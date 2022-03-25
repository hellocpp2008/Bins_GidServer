package com.bins.gid.global.id.health;

import com.bins.id.global.api.ResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Desc: 
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@RestController
public class PingController {

    @GetMapping("/ping")
    public ResultVO ping() {
        return ResultVO.createSuccess(null);
    }

}
