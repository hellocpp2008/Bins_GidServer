package com.bins.gid.global.id.core;

import java.util.Objects;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc: 
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
public class ZkInstance {

    private static final Logger logger = LoggerFactory.getLogger(ZkInstance.class);

    private static CuratorFramework curatorFramework;

    static int connectionTimeoutMs = 10000;
    static int sessionTimeoutMs = 6000;

    static synchronized CuratorFramework getInstance(String connectionString) {
        RetryPolicy retryPolicy = new RetryUntilElapsed(1000, 4);
        if (Objects.isNull(curatorFramework)) {
            curatorFramework = CuratorFrameworkFactory.builder().connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
        }
        curatorFramework.start();
        return curatorFramework;
    }

    static String createNode(CuratorFramework curator, CreateMode createMode, String path, byte[] bytes) throws Exception {
        try {
            return curator.create().creatingParentsIfNeeded().withMode(createMode).forPath(path, bytes);
        } catch (Exception e) {
            logger.error("ZkInstance:createNode:create node error msg {} ", e.getMessage());
            throw e;
        }
    }

}
