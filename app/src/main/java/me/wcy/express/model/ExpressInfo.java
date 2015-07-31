package me.wcy.express.model;

import java.io.Serializable;

/**
 * Created by chenyan.wang on 2015/7/31.
 */
public class ExpressInfo implements Serializable {
    String comName;
    String comIcon;
    String comParam;
    String postId;

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getComIcon() {
        return comIcon;
    }

    public void setComIcon(String comIcon) {
        this.comIcon = comIcon;
    }

    public String getComParam() {
        return comParam;
    }

    public void setComParam(String comParam) {
        this.comParam = comParam;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
