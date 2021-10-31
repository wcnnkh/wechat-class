package io.github.wcnnkh.wechatclass.conf;

import io.basc.framework.orm.annotation.PrimaryKey;

public class AdminMenu{
	@PrimaryKey
	private int menuId;
	private String menuName;
	public int getMenuId() {
		return menuId;
	}
	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
}
