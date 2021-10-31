package io.github.wcnnkh.wechatclass.controller;

import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.action.ActionInterceptor;
import io.basc.framework.mvc.action.ActionInterceptorChain;
import io.basc.framework.mvc.action.ActionParameters;
import io.github.wcnnkh.wechatclass.manager.UserManager;

public class LoginFilter implements ActionInterceptor{
	public static final String LOGIN_URL = "http://qrcode.yzan.net/weixin/authorize?connectId=2";
	

	@Override
	public Object intercept(HttpChannel httpChannel, Action action, ActionParameters parameters,
			ActionInterceptorChain chain) throws Throwable {
		String openId = UserManager.instance.getOpenId(httpChannel.getRequest().getSession());
		if(openId == null){
			httpChannel.getResponse().sendRedirect(LOGIN_URL);
			return null;
		}
		return chain.intercept(httpChannel, action, parameters);
	}

}
