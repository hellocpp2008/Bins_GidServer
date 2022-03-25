package com.bins.gid.global.id.provider.dubbo;

import ch.qos.logback.core.status.Status;
import com.bins.gid.global.id.common.GidResult;
import com.bins.gid.global.id.common.GidStatus;
import com.bins.gid.global.id.core.SnowFlakeIdGenerator;
import com.bins.gid.global.id.exception.GlobalIdServerException;
import com.bins.gid.global.id.exception.KeyEmptyException;
import com.bins.id.global.api.GlobalIdDubboRemote;
import com.bins.id.global.api.ResultCodeEnum;
import com.bins.id.global.api.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * Desc: 
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@Slf4j
@DubboService(version = "0.0.1", group = "${spring.dubbo.gra.group}")
public class GlobalIdDubboProvider implements GlobalIdDubboRemote {

    @Autowired
    private SnowFlakeIdGenerator snowFlakeIdGenerator;

    @Override
    public ResultVO<Long> getGid(String key) {
        try {
            log.info("GlobalIdDubboProvider:getGid:start:key:{}", key);
            if (StringUtils.isEmpty(key)) {
                throw new KeyEmptyException("key is empty");
            }
            GidResult gidResult = snowFlakeIdGenerator.get(key);
            if (gidResult.getGidStatus().equals(GidStatus.EXCEPTION)) {
                throw new GlobalIdServerException(gidResult.toString());
            }
            log.info("GlobalIdDubboProvider:getGid:end:key:{},gid:{}", key, gidResult.getId());
            return ResultVO.createSuccess(gidResult.getId());
        } catch (Exception e) {
            log.info("GlobalIdDubboProvider:getGid:Exception:key:{},Exception:{}", key, e.getMessage());
            return ResultVO.createError(ResultCodeEnum.ERROR, e.getMessage());
        }
    }
}
