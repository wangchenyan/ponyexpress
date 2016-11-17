/**
 * 2015-4-2
 */
package me.wcy.express.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author wcy
 */
public class SearchResult implements Serializable {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    // 快递单号
    @SerializedName("nu")
    private String nu;
    // 请求参数
    @SerializedName("com")
    private String com;
    @SerializedName("ischeck")
    private String ischeck;
    @SerializedName("data")
    private ResultItem data[];

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getIscheck() {
        return ischeck;
    }

    public void setIscheck(String ischeck) {
        this.ischeck = ischeck;
    }

    public ResultItem[] getData() {
        return data;
    }

    public void setData(ResultItem[] data) {
        this.data = data;
    }

    public static class ResultItem implements Serializable {
        @SerializedName("time")
        private String time;
        @SerializedName("context")
        private String context;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }
    }
}
