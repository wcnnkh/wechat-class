package io.github.wcnnkh.wechatclass.enums;

public enum WebSettingType {
	className(1),//名称
	classPopularity(2),//人气
	classTime(3),//时长
	classCts(4),//创建时间
	classIsShutup(5);//是否禁言
	
	private int value;

	WebSettingType(int v) {
		value = v;
	}

	public int getValue() {
		return value;
	}
}
