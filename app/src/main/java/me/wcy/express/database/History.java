package me.wcy.express.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "History")
public class History {
    // 运单号
    @DatabaseField(columnName = "post_id", id = true, canBeNull = false)
    private String post_id;

    // 快递公司请求参数
    @DatabaseField(columnName = "company_param", canBeNull = false)
    private String company_param;

    // 快递公司
    @DatabaseField(columnName = "company_name", canBeNull = false)
    private String company_name;

    // 快递公司Logo
    @DatabaseField(columnName = "company_icon", canBeNull = false)
    private String company_icon;

    // 签收状态
    @DatabaseField(columnName = "is_check", canBeNull = false)
    private String is_check;

    // 运单备注
    @DatabaseField(columnName = "remark")
    private String remark;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getCompany_param() {
        return company_param;
    }

    public void setCompany_param(String company_param) {
        this.company_param = company_param;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_icon() {
        return company_icon;
    }

    public void setCompany_icon(String company_icon) {
        this.company_icon = company_icon;
    }

    public String getIs_check() {
        return is_check;
    }

    public void setIs_check(String is_check) {
        this.is_check = is_check;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
