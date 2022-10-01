package io.github.wcnnkh.wechatclass.manager;

import io.basc.framework.context.annotation.Indexed;
import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.db.DBManager;
import io.github.wcnnkh.wechatclass.bean.WebSetting;
import io.github.wcnnkh.wechatclass.enums.WebSettingType;

@Indexed
public class WebSettingManager {
	@Autowired
	public static WebSettingManager instance;

	public WebSetting getWebSetting(int type) {
		return DBManager.getById(WebSetting.class, type);
	}

	public WebSetting getWebSetting(WebSettingType type) {
		return getWebSetting(type.getValue());
	}
}
