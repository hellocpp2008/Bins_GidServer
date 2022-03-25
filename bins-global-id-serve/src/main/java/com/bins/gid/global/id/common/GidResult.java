package com.bins.gid.global.id.common;

import java.io.Serializable;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Desc:
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@Data
public class GidResult implements Serializable {

    private static final long serialVersionUID = 1;

    private long id;

    private String gidMsg;

    private GidStatus gidStatus;

    protected GidResult(long id, String gidMsg, GidStatus gidStatus) {
        this.id = id;
        this.gidMsg = gidMsg;
        this.gidStatus = gidStatus;
    }

    public static GidResult create(long id, GidStatus gidStatus, String gidMsg) {
        return new GidResult(id, gidMsg, gidStatus);
    }

    public static GidResult success(long id) {
        return create(id, GidStatus.SUCCESS, GidStatus.SUCCESS.toString());
    }

    public static GidResult error(long id, String gidMsg) {
        return create(id, GidStatus.EXCEPTION, StringUtils.isEmpty(gidMsg) ? GidStatus.EXCEPTION.toString() : gidMsg);
    }
}
