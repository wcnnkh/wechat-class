package io.github.wcnnkh.wechatclass.bean;

import io.basc.framework.db.BaseBean;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;
import io.basc.framework.util.TimeUtils;

@Table
public class User extends BaseBean{
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private String openid;
	private String phone;
	private String titleImg;
	private String nickName;
	private long registerTime;
	
	private int userType;//0是默认 1是讲师  2是嘉宾
	
	private String userDesc;//描述
	
	public String getRegisterTimeStr() {
		return TimeUtils.format(registerTime, "yyyy-MM-dd HH:mm:ss");
	}
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public long getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTitleImg() {
		return titleImg;
	}

	public void setTitleImg(String titleImg) {
		this.titleImg = titleImg;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getUserDesc() {
		return userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}
}
