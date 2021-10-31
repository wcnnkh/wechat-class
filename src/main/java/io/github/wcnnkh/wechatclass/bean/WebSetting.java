package io.github.wcnnkh.wechatclass.bean;

import io.basc.framework.db.BaseBean;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;

@Table(name = "web_setting")
public class WebSetting extends BaseBean {
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private int type;
	private String value;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
