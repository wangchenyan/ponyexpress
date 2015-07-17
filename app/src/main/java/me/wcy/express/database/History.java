package me.wcy.express.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "History")
public class History {

	/**
	 * 运单号
	 */
	@DatabaseField(id = true)
	public String post_id;

	/**
	 * 快递公司请求参数
	 */
	@DatabaseField
	public String type;

	/**
	 * 快递公司
	 */
	@DatabaseField
	public String com;

	/**
	 * 公司Logo
	 */
	@DatabaseField
	public String icon;

	/**
	 * 签收状态
	 */
	@DatabaseField
	public String is_check;

	public History() {
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIs_check() {
		return is_check;
	}

	public void setIs_check(String is_check) {
		this.is_check = is_check;
	}

}
