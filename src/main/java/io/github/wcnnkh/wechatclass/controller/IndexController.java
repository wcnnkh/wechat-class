package io.github.wcnnkh.wechatclass.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.context.result.ResultFactory;
import io.basc.framework.db.DBManager;
import io.basc.framework.mvc.annotation.ActionInterceptors;
import io.basc.framework.mvc.view.Redirect;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.model.ModelAndView;
import io.basc.framework.web.pattern.annotation.RequestMapping;
import io.github.wcnnkh.wechatclass.bean.Message;
import io.github.wcnnkh.wechatclass.bean.User;
import io.github.wcnnkh.wechatclass.bean.WebSetting;
import io.github.wcnnkh.wechatclass.enums.WebSettingType;
import io.github.wcnnkh.wechatclass.manager.UserManager;
import io.github.wcnnkh.wechatclass.manager.WebSettingManager;

@RequestMapping
public class IndexController {
	@Autowired
	private ResultFactory resultFactory;
	@Autowired
	private UserManager userManager;
	@Autowired
	private WebSettingManager webSettingManager;

	@RequestMapping(value = "userinfo")
	public void userinfo(String openid, String headimgurl, String nickname, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (openid != null) {
			User user = userManager.getUser(openid);
			if (user == null) {
				user = new User();
				user.setOpenid(openid);
				user.setTitleImg(headimgurl);
				user.setNickName(nickname);
				user.setRegisterTime(System.currentTimeMillis());
				user.setUserType(0);
				userManager.save(user);
			} else {
				user.setTitleImg(headimgurl);
				user.setNickName(nickname);
				userManager.update(user);
			}

			HttpSession httpSession = request.getSession(true);
			userManager.setOpenId(httpSession, openid);
		}

		response.sendRedirect(request.getContextPath() + "/index.html");
	}

	@RequestMapping(value = "index.html")
	public Object index(ServerHttpRequest request) {
		String openid = userManager.getOpenId(request.getSession());
		if (openid == null) {
			return new Redirect(LoginFilter.LOGIN_URL);
		}

		ModelAndView view = new ModelAndView("/ftl/index.html");
		view.put("user", userManager.getUser(openid));
		view.put("className", webSettingManager.getWebSetting(WebSettingType.className));
		view.put("classPopularity", webSettingManager.getWebSetting(WebSettingType.classPopularity));
		view.put("classTime", webSettingManager.getWebSetting(WebSettingType.classTime));
		view.put("classCts", webSettingManager.getWebSetting(WebSettingType.classCts));
		view.put("classIsShutup", webSettingManager.getWebSetting(WebSettingType.classIsShutup));
		view.put("guestList", userManager.guestMap.values());
		view.put("lecturerList", userManager.lecturerMap.values());
		return view;
	}

	@ActionInterceptors(LoginFilter.class)
	@RequestMapping(value = "chart.html")
	public ModelAndView chat(ServerHttpRequest request) {
		String openid = userManager.getOpenId(request.getSession());
		request.setAttribute("user", userManager.getUser(openid));
		request.setAttribute("classIsShutup", webSettingManager.getWebSetting(WebSettingType.classIsShutup));
		request.setAttribute("lineCount", 0);
		// request.setAttribute("lineCount", Wechat.sessionManager.getGroupSize());
		request.setAttribute("msgList", DBManager.getByIdList(Message.class));
		request.setAttribute("className", webSettingManager.getWebSetting(WebSettingType.className));
		WebSetting webSetting = webSettingManager.getWebSetting(WebSettingType.classPopularity);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setValue("1");
			webSetting.setType(WebSettingType.classPopularity.getValue());
			DBManager.update(webSetting);
		} else {
			webSetting.setValue(Integer.parseInt(webSetting.getValue()) + 1 + "");
			DBManager.update(webSetting);
		}
		return new ModelAndView("/ftl/chart.html");
	}
}
