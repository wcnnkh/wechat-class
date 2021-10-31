package io.github.wcnnkh.wechatclass.controller.admin;

import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.action.Action;
import io.basc.framework.mvc.action.ActionInterceptor;
import io.basc.framework.mvc.action.ActionInterceptorChain;
import io.basc.framework.mvc.action.ActionParameters;
import io.basc.framework.security.session.UserSession;
import io.github.wcnnkh.wechatclass.bean.admin.AdminUser;
import io.github.wcnnkh.wechatclass.manager.AdminManager;

public class AdminFilter implements ActionInterceptor{

	@Override
	public Object intercept(HttpChannel httpChannel, Action action,
			ActionParameters parameters, ActionInterceptorChain chain)
			throws Throwable {
		UserSession<String> userSession = httpChannel.getUserSession(String.class);
		if(userSession == null) {
			httpChannel.getResponse().sendRedirect(httpChannel.getRequest().getContextPath() + "/admin/core/toParentLogin.html");
			return null;
		}
		
		if(userSession.getUid().equals("admin")){
			return chain.intercept(httpChannel, action, parameters);
		}
		
		AdminUser user = AdminManager.instance.getAdminUser(userSession.getUid());
		if(user == null){
			httpChannel.getResponse().sendRedirect(httpChannel.getResponse().getContentLength() + "/admin/core/toParentLogin.html");
			return null;
		}
		
		boolean b = AdminManager.instance.checkPath(httpChannel.getRequest().getRawMethod(), httpChannel.getRequest().getPath(), user.getGroupId());
		if(!b){
			httpChannel.getResponse().sendError(403, "Insufficient user permissions");
			return null;
		}
		return chain.intercept(httpChannel, action, parameters);
	}

}
