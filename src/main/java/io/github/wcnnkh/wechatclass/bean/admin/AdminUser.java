package io.github.wcnnkh.wechatclass.bean.admin;

import io.basc.framework.db.BaseBean;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;

@Table(name="admin_user")
public class AdminUser extends BaseBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String user;
	private String pwd;
	private String realName;
	private long groupId;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
}
