/**
 * 2015-4-2
 */
package me.wcy.express.model;

import java.io.Serializable;

/**
 * @author wcy
 */
@SuppressWarnings("serial")
public class QueryResult implements Serializable {
    // json
    String status;
    // json
    String message;
    // json 快递单号
    String nu;
    // json 请求参数
    String com;
    // json
    String ischeck;
    // json
    ResultItem data[];
    // custom
    String company_icon;
    // custom
    String company_name;

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

    public String getCompany_icon() {
        return company_icon;
    }

    public void setCompany_icon(String company_icon) {
        this.company_icon = company_icon;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

}
