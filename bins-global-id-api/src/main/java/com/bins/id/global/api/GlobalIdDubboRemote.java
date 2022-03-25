package com.bins.id.global.api;

/**
 * Desc: 
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
public interface GlobalIdDubboRemote {

    ResultVO<Long> getGid(String key);

}
