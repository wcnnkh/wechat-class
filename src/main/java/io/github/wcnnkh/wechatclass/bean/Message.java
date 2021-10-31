package io.github.wcnnkh.wechatclass.bean;

import io.basc.framework.db.BaseBean;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;
import io.github.wcnnkh.wechatclass.manager.UserManager;

@Table
public class Message extends BaseBean{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long cts;
	private String openid;
	private int type;
	private String data;
	
	public User getUser() {
		return UserManager.instance.getUser(openid);
	}

	public long getCts() {
		return cts;
	}
	
	public void setCts(long cts) {
		this.cts = cts;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
}
