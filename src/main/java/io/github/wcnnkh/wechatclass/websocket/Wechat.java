package io.github.wcnnkh.wechatclass.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSONObject;

import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.db.DB;
import io.basc.framework.db.DBManager;
import io.basc.framework.websocket.adapter.standard.StandardContainerConfigurator;
import io.basc.framework.websocket.adapter.standard.StandardSessionManager;
import io.github.wcnnkh.wechatclass.bean.Message;
import io.github.wcnnkh.wechatclass.bean.User;
import io.github.wcnnkh.wechatclass.bean.WebSetting;
import io.github.wcnnkh.wechatclass.enums.WebSettingType;
import io.github.wcnnkh.wechatclass.manager.UserManager;
import io.github.wcnnkh.wechatclass.manager.WebSettingManager;

@ServerEndpoint(value = "/class/{openid}", configurator = StandardContainerConfigurator.class)
public class Wechat {
	public static StandardSessionManager<String> sessionManager = new StandardSessionManager<>("openid");

	@Autowired
	private DB connection;

	private String openid;

	@OnOpen
	public void open(Session session, @PathParam("openid") String openid) {
		sessionManager.remove(openid, (s) -> s.close());
		sessionManager.register(openid, session);

		JSONObject json = new JSONObject();
		json.put("type", 6);
		json.put("data", sessionManager.getGroupSize());
		sessionManager.sendText(json.toJSONString());

		JSONObject json7 = new JSONObject();
		json7.put("type", 7);
		json7.put("user", UserManager.instance.getUser(openid));
		sessionManager.sendText(json7.toJSONString(), UserManager.instance.lecturerMap.keySet());
	}

	@OnMessage
	public void message(Session session, String data) {
		String openid = sessionManager.getGroup(session);
		JSONObject json = null;
		try {
			json = JSONObject.parseObject(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (json == null) {
			return;
		}

		User user = DBManager.getById(User.class, openid);
		json.put("user", user);
		int type = json.getIntValue("type");
		Message message;
		switch (type) {
		case 9:
			long msgId = json.getLongValue("data");
			message = DBManager.getById(Message.class, msgId);
			DBManager.delete(message);
			break;
		case 4:// 禁言/取消 if (user.getUserType() != 1) {// 只有讲师可以禁言 return; }

			WebSetting setting = WebSettingManager.instance.getWebSetting(WebSettingType.classIsShutup);
			if (setting == null) {
				setting = new WebSetting();
				setting.setType(WebSettingType.classIsShutup.getValue());
				setting.setValue(json.getString("data"));
				DBManager.save(setting);
			} else {
				setting.setValue(json.getString("data"));
				DBManager.update(setting);
			}
			sessionManager.sendText(json.toJSONString());
			break;
		case 5:// 举手
			sessionManager.sendText(json.toJSONString(), UserManager.instance.lecturerMap.keySet());
			break;
		default:
			if (user.getUserType() == 0) {
				WebSetting webSetting = WebSettingManager.instance.getWebSetting(WebSettingType.classIsShutup);
				if (webSetting != null && webSetting.getValue().equals("1")) {
					json.put("type", 4);
					json.put("data", "1");
					sessionManager.sendText(json.toJSONString(), openid);
					return;
				}
			}

			message = new Message();
			message.setCts(System.currentTimeMillis());
			message.setData(json.getString("data"));
			message.setOpenid(openid);
			message.setType(json.getIntValue("type"));
			json.put("data", message.getData());
			DBManager.save(message);
			sessionManager.sendText(json.toJSONString());
			break;
		}
	}

	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}

	@OnClose
	public void onclose(Session session) {
		sessionManager.remove(session);
		JSONObject json = new JSONObject();
		json.put("type", 6);
		json.put("data", sessionManager.getGroupSize());
		sessionManager.sendText(json.toJSONString());

		json.put("type", 8);
		json.put("user", UserManager.instance.getUser(openid));
		sessionManager.sendText(json.toJSONString(), UserManager.instance.lecturerMap.keySet());
	}
}
