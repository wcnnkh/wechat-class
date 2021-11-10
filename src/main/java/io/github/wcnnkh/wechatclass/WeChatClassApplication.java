package io.github.wcnnkh.wechatclass;

import io.basc.framework.beans.annotation.Bean;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.db.DB;
import io.basc.framework.sqlite.SQLiteDB;
import io.basc.framework.web.message.model.ModelAndViewRegistry;
import io.basc.framework.web.resource.StaticResourceRegistry;

public class WeChatClassApplication {
	public static void main(String[] args) {
		MainApplication.run(WeChatClassApplication.class, args);
	}

	@Bean
	public DB getDB() {
		DB db = SQLiteDB.create("message-wechat");
		db.createTables(WeChatClassApplication.class.getPackage().getName() + ".bean");
		return db;
	}

	@Bean
	public StaticResourceRegistry getStaticResourceRegistry() {
		StaticResourceRegistry staticResourceRegistry = new StaticResourceRegistry();
		staticResourceRegistry.add("/static/**", "/");
		return staticResourceRegistry;
	}
	
	@Bean
	public ModelAndViewRegistry getModelAndViewRegistry() {
		ModelAndViewRegistry registry = new ModelAndViewRegistry();
		registry.add("/admin/**/*.html", "/ftl/");
		return registry;
	}
}
