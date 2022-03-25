package com.bins.gid.global.id.provider.http;

import ch.qos.logback.core.status.Status;
import com.bins.gid.global.id.common.GidResult;
import com.bins.gid.global.id.common.GidStatus;
import com.bins.gid.global.id.core.SnowFlakeIdGenerator;
import com.bins.gid.global.id.exception.GlobalIdServerException;
import com.bins.gid.global.id.exception.KeyEmptyException;
import com.bins.id.global.api.ResultCodeEnum;
import com.bins.id.global.api.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Desc:
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@Slf4j
@RestController
public class GlobalIdController {

    @Autowired
    private SnowFlakeIdGenerator snowFlakeIdGenerator;

    @RequestMapping(value = "/api/gid/get")
    public ResultVO getGid(@RequestParam(required = false) String key) {
        try {
            log.info("GlobalIdController:getGid:start:key:{}", key);
            if (StringUtils.isEmpty(key)) {
                throw new KeyEmptyException("key is empty");
            }
            GidResult gidResult = snowFlakeIdGenerator.get(key);
            if (gidResult.getGidStatus().equals(GidStatus.EXCEPTION)) {
                throw new GlobalIdServerException(gidResult.toString());
            }
            log.info("GlobalIdController:getGid:end:key:{},gid:{}", key, gidResult.getId());
            return ResultVO.createSuccess(gidResult.getId());
        } catch (Exception e) {
            log.info("GlobalIdController:getGid:Exception:key:{},Exception:{}", key, e.getMessage());
            return ResultVO.createError(ResultCodeEnum.ERROR, e.getMessage());
        }
    }

}
