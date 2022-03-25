package com.bins.gid.global.id.core;

import com.bins.gid.global.id.entity.NodeData;
import com.bins.gid.global.id.exception.CheckLastTimeException;
import com.bins.gid.global.id.utils.IpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Desc: 负责连接zookeeper、处理zookeeper节点相关信息
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@Component
public class ZkHolder {

    private static final Logger logger = LoggerFactory.getLogger(ZkHolder.class);

    @Value("${gid.name}")
    private String gidName;

    @Value("${gid.snowflake.enable}")
    private String gidEnable;

    @Value("${gid.snowflake.zk.address}")
    private String connectionString;

    @Value("${gid.snowflake.port}")
    private String port;

    private int workerID;
    private String zkAddressNode = null;
    private String listenAddress = null;
    private String prefixZkPath = null;
    private String pathForever = null;
    private String propPath = null;
    private final String ip = IpUtils.getIp();
    private long lastUpdateTime;

    public ZkHolder() {
    }

    @PostConstruct
    void init() {
        listenAddress = IpUtils.getIp() + ":" + port;
        prefixZkPath = "/snowflake/" + gidName;
        pathForever = prefixZkPath + "/forever";
        propPath = System.getProperty("java.io.tmpdir") + File.separator + gidName + "/gidconf/{port}/workerID.properties";
        logger.info("ZkHolder.init():listenAddress:{},prefixZkPath:{},pathForever:{},propPath:{}", listenAddress, prefixZkPath, pathForever, propPath);
    }

    boolean nodeInit() {
        try {
            CuratorFramework curator = ZkInstance.getInstance(connectionString);
            Stat stat = curator.checkExists().forPath(pathForever);
            if (stat == null) {
                zkAddressNode = createNode(curator);
                //worker id 默认是0
                LocalWorkId.updateLocalWorkerId(workerID, propPath.replace("{port}", port));
                scheduledUploadData(curator, zkAddressNode);
                return true;
            } else {
                Map<String, Integer> nodeMap = Maps.newHashMap();
                Map<String, String> realNode = Maps.newHashMap();
                List<String> keys = curator.getChildren().forPath(pathForever);
                for (String key : keys) {
                    String[] nodeKey = key.split("-");
                    realNode.put(nodeKey[0], key);
                    nodeMap.put(nodeKey[0], Integer.parseInt(nodeKey[1]));
                }
                Integer workerid = nodeMap.get(listenAddress);
                if (workerid != null) {
                    zkAddressNode = pathForever + "/" + realNode.get(listenAddress);
                    workerID = workerid;
                    if (!checkInitTimeStamp(curator, zkAddressNode)) {
                        throw new CheckLastTimeException("init timestamp check error,forever node timestamp gt this node time");
                    }
                    doService(curator);
                    LocalWorkId.updateLocalWorkerId(workerID, propPath.replace("{port}", port));
                    logger.info("[Old NODE]find forever node have this nodeData ip-{} port-{} workid-{} childnode and start SUCCESS", ip, port, workerID);
                } else {
                    //表示新启动的节点,创建持久节点 ,不用check时间
                    String newNode = createNode(curator);
                    zkAddressNode = newNode;
                    String[] nodeKey = newNode.split("-");
                    workerID = Integer.parseInt(nodeKey[1]);
                    doService(curator);
                    LocalWorkId.updateLocalWorkerId(workerID, propPath.replace("{port}", port));
                    logger.info(
                        "[New NODE]can not find node on forever node that nodeData ip-{} port-{} workid-{},create own node on forever node and start SUCCESS ",
                        ip, port, workerID);
                }
            }
        } catch (Exception e) {
            logger.error("Start node ERROR {}", e);
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(new File(propPath.replace("{port}", port + ""))));
                workerID = Integer.parseInt(properties.getProperty("workerID"));
                logger.warn("START FAILED ,use local node file properties workerID-{}", workerID);
            } catch (Exception e1) {
                logger.error("Read file error ", e1);
                return false;
            }
        }
        return true;
    }

    private void doService(CuratorFramework curator) {
        scheduledUploadData(curator, zkAddressNode);
    }

    private void scheduledUploadData(final CuratorFramework curator, final String zkAddressNode) {
        Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "schedule-upload-time");
                thread.setDaemon(true);
                return thread;
            }
        }).scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                updateNewData(curator, zkAddressNode);
            }
        }, 1L, 3L, TimeUnit.SECONDS);

    }

    private boolean checkInitTimeStamp(CuratorFramework curator, String zkAddressNode) throws Exception {
        byte[] bytes = curator.getData().forPath(zkAddressNode);
        NodeData nodeData = deBuildData(new String(bytes));
        //该节点的时间不能小于最后一次上报的时间
        return !(nodeData.getTimestamp() > System.currentTimeMillis());
    }

    private String createNode(CuratorFramework curator) throws Exception {
        // 创建持久顺序节点 ,并把节点数据放入 value
        return ZkInstance.createNode(curator, CreateMode.PERSISTENT_SEQUENTIAL, pathForever + "/" + listenAddress + "-", buildData().getBytes());
    }

    private void updateNewData(CuratorFramework curator, String path) {
        try {
            if (System.currentTimeMillis() < lastUpdateTime) {
                logger.info("ZkHolder.updateNewData:currentTimeMillis<lastUpdateTime,return");
                return;
            }
            curator.setData().forPath(path, buildData().getBytes());
            lastUpdateTime = System.currentTimeMillis();
            //logger.info("ZkHolder.updateNewData:end,data:{}", buildData());
        } catch (Exception e) {
            logger.info("ZkHolder.updateNewData:Exception:update init data error path is {} error is {}", path, e);
        }
    }

    /**
     * 构建需要上传的数据
     *
     * @return
     */
    private String buildData() throws JsonProcessingException {
        NodeData nodeData = new NodeData(ip, port, System.currentTimeMillis());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(nodeData);
    }

    private NodeData deBuildData(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, NodeData.class);
    }

    public int getWorkerID() {
        return workerID;
    }

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }

}
