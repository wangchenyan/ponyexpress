/**
 * 2015-4-2
 */
package me.wcy.express.model;

import java.io.Serializable;

/**
 * @author wcy
 * 
 */
@SuppressWarnings("serial")
public class QueryResult implements Serializable {
	String status;
	String message;
	// 快递单号
	String nu;
	// 请求参数
	String com;
	String ischeck;
	ResultItem data[];
	String companyIcon;
	String companyName;

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

	public String getCompanyIcon() {
		return companyIcon;
	}

	public void setCompanyIcon(String companyIcon) {
		this.companyIcon = companyIcon;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
