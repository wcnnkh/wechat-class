package io.github.wcnnkh.wechatclass.controller.admin;

import javax.servlet.http.HttpServletRequest;

import io.basc.framework.codec.Encoder;
import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.context.transaction.Result;
import io.basc.framework.context.transaction.ResultFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.view.Redirect;
import io.basc.framework.mvc.view.View;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.model.ModelAndView;
import io.basc.framework.web.pattern.annotation.RequestMapping;
import io.github.wcnnkh.wechatclass.bean.admin.AdminUser;
import io.github.wcnnkh.wechatclass.manager.AdminManager;

@RequestMapping(value = "admin")
public class AdminController {
	public static final Encoder<String, String> PASSWORD_ENCODE = CharsetCodec.UTF_8.toMD5();
	
	@Autowired
	private ResultFactory resultFactory;
	@Autowired
	private AdminManager adminManager;

	@RequestMapping
	public View index(UserSession<String> adminUser, ServerHttpRequest request) {
		if (adminUser == null) {
			return new Redirect("/admin/core/toLogin.html");
		}
		return new Redirect("/admin/core/index.html");
	}
	
	@RequestMapping("/core/toLogin.html")
	public ModelAndView login() {
		return new ModelAndView("/ftl/admin/core/toLogin.html");
	}

	@RequestMapping(value = "core/login", methods = HttpMethod.POST)
	public Result login(String user, String pwd, HttpChannel httpChannel) {
		if (StringUtils.isEmpty(user, pwd)) {
			return resultFactory.error("参数错误");
		}

		AdminUser adminUser = adminManager.getAdminUser(user);
		if (adminUser == null) {
			return resultFactory.error("用户不存在");
		}

		if (!adminUser.getPwd().equals(PASSWORD_ENCODE.encode(pwd))) {
			return resultFactory.error("密码错误");
		}

		httpChannel.createUserSession(adminUser.getUser());
		return resultFactory.success();
	}

	@RequestMapping(value = "core/loginout")
	public View loginout(UserSession<String> userSession) {
		userSession.invalidate();
		return new Redirect("/admin/core/toParentLogin.html");
	}
	
	@RequestMapping(value = "core/toParentLogin.html")
	public ModelAndView toParentLogin(HttpServletRequest request) {
		return new ModelAndView("/ftl/admin/core/toParentLogin.html");
	}
}
