package io.github.wcnnkh.wechatclass.bean;

import io.basc.framework.db.BaseBean;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;

@Table(name="phone_user")
public class PhoneUser extends BaseBean{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String phone;
	private String openid;//为空时说明还没有注册

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
}
