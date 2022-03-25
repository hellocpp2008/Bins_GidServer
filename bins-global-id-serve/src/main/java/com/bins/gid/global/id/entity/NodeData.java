package com.bins.gid.global.id.entity;

import lombok.Data;

/**
 * Desc: 
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@Data
public class NodeData {

    private String ip;
    private String port;
    private long timestamp;

    public NodeData() {
    }

    public NodeData(String ip, String port, long timestamp) {
        this.ip = ip;
        this.port = port;
        this.timestamp = timestamp;
    }
}
