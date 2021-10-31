package io.github.wcnnkh.wechatclass.bean;

import io.basc.framework.db.BaseBean;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;

@Table(name="sendcode_info")
public class SendCodeInfo extends BaseBean{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String phone;
	@PrimaryKey
	private int type;
	private String code;
	private long cts;
	private int count;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getCts() {
		return cts;
	}
	public void setCts(long cts) {
		this.cts = cts;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
