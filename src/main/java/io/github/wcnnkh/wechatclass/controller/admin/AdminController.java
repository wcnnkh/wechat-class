package io.github.wcnnkh.wechatclass.controller.admin;

import javax.servlet.http.HttpServletRequest;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.codec.Encoder;
import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.context.result.Result;
import io.basc.framework.context.result.ResultFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.mvc.annotation.Controller;
import io.basc.framework.mvc.model.ModelAndView;
import io.basc.framework.mvc.view.Redirect;
import io.basc.framework.mvc.view.View;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.github.wcnnkh.wechatclass.bean.admin.AdminUser;
import io.github.wcnnkh.wechatclass.manager.AdminManager;

@Controller(value = "admin")
public class AdminController {
	public static final Encoder<String, String> PASSWORD_ENCODE = CharsetCodec.UTF_8.toMD5();
	
	@Autowired
	private ResultFactory resultFactory;

	@Controller
	public View index(UserSession<String> adminUser, ServerHttpRequest request) {
		if (adminUser == null) {
			return new Redirect("/admin/core/toLogin.html");
		}
		return new Redirect("/admin/core/index.html");
	}
	
	@Controller("/core/toLogin.html")
	public ModelAndView login() {
		return new ModelAndView("/ftl/admin/core/toLogin.html");
	}

	@Controller(value = "core/login", methods = HttpMethod.POST)
	public Result login(String user, String pwd, HttpChannel httpChannel) {
		if (StringUtils.isEmpty(user, pwd)) {
			return resultFactory.error("参数错误");
		}

		AdminUser adminUser = AdminManager.instance.getAdminUser(user);
		if (adminUser == null) {
			return resultFactory.error("用户不存在");
		}

		if (!adminUser.getPwd().equals(PASSWORD_ENCODE.encode(pwd))) {
			return resultFactory.error("密码错误");
		}

		httpChannel.createUserSession(adminUser.getUser());
		return resultFactory.success();
	}

	@Controller(value = "core/loginout")
	public View loginout(UserSession<String> userSession) {
		userSession.invalidate();
		return new Redirect("/admin/core/toParentLogin.html");
	}
	
	@Controller(value = "core/toParentLogin.html")
	public ModelAndView toParentLogin(HttpServletRequest request) {
		return new ModelAndView("/ftl/admin/core/toParentLogin.html");
	}
}
