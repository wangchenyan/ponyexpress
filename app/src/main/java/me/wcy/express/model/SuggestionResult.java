package me.wcy.express.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wcy on 2016/6/27.
 */
public class SuggestionResult {
    @SerializedName("comCode")
    private String comCode;
    @SerializedName("num")
    private String num;
    @SerializedName("auto")
    private List<AutoBean> auto;

    public String getComCode() {
        return comCode;
    }

    public void setComCode(String comCode) {
        this.comCode = comCode;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public List<AutoBean> getAuto() {
        return auto;
    }

    public void setAuto(List<AutoBean> auto) {
        this.auto = auto;
    }

    public static class AutoBean {
        @SerializedName("comCode")
        private String comCode;
        @SerializedName("id")
        private String id;
        @SerializedName("noCount")
        private int noCount;
        @SerializedName("noPre")
        private String noPre;
        @SerializedName("startTime")
        private String startTime;

        public String getComCode() {
            return comCode;
        }

        public void setComCode(String comCode) {
            this.comCode = comCode;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getNoCount() {
            return noCount;
        }

        public void setNoCount(int noCount) {
            this.noCount = noCount;
        }

        public String getNoPre() {
            return noPre;
        }

        public void setNoPre(String noPre) {
            this.noPre = noPre;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
    }
}
