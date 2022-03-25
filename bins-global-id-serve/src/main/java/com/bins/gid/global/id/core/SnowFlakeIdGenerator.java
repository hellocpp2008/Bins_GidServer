package com.bins.gid.global.id.core;

import com.bins.gid.global.id.common.GidResult;
import com.google.common.base.Preconditions;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Desc: 负责连接zookeeper、处理zookeeper节点相关信息
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@Component
public class SnowFlakeIdGenerator {

    @Autowired
    private ZkHolder holder;

    private static final Logger logger = LoggerFactory.getLogger(SnowFlakeIdGenerator.class);

    private final long timeStampBeginning = 1288834974657L;
    /**
     * 生成的分布式id中，10个比特位存放机器id
     */
    private final long workerIdBits = 10L;
    /**
     * 最大能够分配的workerid = 1023
     */
    private final long maxWorkerId = ~(-1L << workerIdBits);
    /**
     * 生成的分布式id中，最后12个比特位是自增序列
     */
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);
    /**
     * 生成的分布式id中的workId
     */
    private long workerId;
    /**
     * 生成的分布式id中的自增序列
     */
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static final Random RANDOM = new Random();
    private static final int MAX_OFFSET = 5;

    public SnowFlakeIdGenerator() {
    }

    @PostConstruct
    public void init() {
        Preconditions.checkArgument(currentTimeMillis() > timeStampBeginning, "Snowflake not support timeStampBeginning gt currentTime");
        boolean initFlag = holder.nodeInit();
        if (initFlag) {
            workerId = holder.getWorkerID();
            logger.info("START SUCCESS USE ZK WORKERID-{}", workerId);
        } else {
            throw new IllegalArgumentException(String.valueOf("Snowflake Id Generator is not init ok"));
        }
        Preconditions.checkArgument(workerId >= 0 && workerId <= maxWorkerId, "workerID must gte 0 and lte 1023");
    }

    public synchronized GidResult get(String key) {
        long timestamp = currentTimeMillis();
        if (timestamp < lastTimestamp) {
            logger.info("SnowFlakeIdGenerator:get:timestamp<lastTimestamp");
            long offset = lastTimestamp - timestamp;
            if (offset <= MAX_OFFSET) {
                try {
                    wait(offset << 1);
                    timestamp = currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        return GidResult.error(-1, "timestamp has problem,after waiting still compared fail");
                    }
                } catch (InterruptedException e) {
                    logger.error("wait interrupted");
                    return GidResult.error(-2, "timestamp has problem,wait interrupted");
                }
            } else {
                return GidResult.error(-3, "timestamp has problem,too much interval,error");
            }
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - timeStampBeginning) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return GidResult.success(id);
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
