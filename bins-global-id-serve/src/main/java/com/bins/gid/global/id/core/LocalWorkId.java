package com.bins.gid.global.id.core;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc: 将workId存储在本地
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
class LocalWorkId {

    private static final Logger logger = LoggerFactory.getLogger(LocalWorkId.class);

    static void updateLocalWorkerId(int workerId, String filePath) {
        File localConfFile = new File(filePath);
        logger.info("LocalWorkId:local file path is:{}", localConfFile);
        boolean exists = localConfFile.exists();
        logger.info("file exists status is {}", exists);
        if (exists) {
            try {
                FileUtils.writeStringToFile(localConfFile, "workerId=" + workerId, false);
                logger.info("update file cache workerId is {}", workerId);
            } catch (IOException e) {
                logger.error("update file cache error ", e);
            }
        } else {
            try {
                boolean mkdirs = localConfFile.getParentFile().mkdirs();
                logger.info("init local file cache create parent dis status is {}, worker id is {}", mkdirs, workerId);
                if (mkdirs) {
                    if (localConfFile.createNewFile()) {
                        FileUtils.writeStringToFile(localConfFile, "workerId=" + workerId, false);
                        logger.info("local file cache workerId is {}", workerId);
                    }
                } else {
                    logger.warn("create parent dir error===");
                }
            } catch (IOException e) {
                logger.warn("craete workerId conf file error", e);
            }
        }
    }
}
