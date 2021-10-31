package io.github.wcnnkh.wechatclass.controller.admin;

import javax.servlet.http.HttpServletRequest;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.context.result.Result;
import io.basc.framework.context.result.ResultFactory;
import io.basc.framework.db.DB;
import io.basc.framework.db.DBManager;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.mvc.annotation.Controller;
import io.basc.framework.mvc.model.ModelAndView;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.WebSqlExcelExport;
import io.github.wcnnkh.wechatclass.bean.User;
import io.github.wcnnkh.wechatclass.bean.WebSetting;
import io.github.wcnnkh.wechatclass.enums.WebSettingType;
import io.github.wcnnkh.wechatclass.manager.UserManager;
import io.github.wcnnkh.wechatclass.manager.WebSettingManager;

@Controller(value = "admin", interceptors = AdminFilter.class)
public class ClassController {
	@Autowired
	private static DB datasource;
	@Autowired
	private ResultFactory resultFactory;

	@Controller(value = "classInfo.html")
	public ModelAndView classInfo(HttpServletRequest request) {
		request.setAttribute("className", WebSettingManager.instance
				.getWebSetting(WebSettingType.className));
		request.setAttribute("classPopularity", WebSettingManager.instance
				.getWebSetting(WebSettingType.classPopularity));
		request.setAttribute("classTime", WebSettingManager.instance
				.getWebSetting(WebSettingType.classTime));
		request.setAttribute("classCts", WebSettingManager.instance
				.getWebSetting(WebSettingType.classCts));
		request.setAttribute("classIsShutup", WebSettingManager.instance
				.getWebSetting(WebSettingType.classIsShutup));
		return new ModelAndView("/ftl/admin/classinfo.html");
	}

	@Controller(value = "updateClassInfo", methods = HttpMethod.POST)
	public Result updateClassInfo(String className, String classCts,
			int classTime, String classIsShutup, int classPopularity) {
		WebSetting webSetting = WebSettingManager.instance
				.getWebSetting(WebSettingType.className);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.className.getValue());
			webSetting.setValue(className);
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(className);
			DBManager.update(webSetting);
		}

		webSetting = WebSettingManager.instance
				.getWebSetting(WebSettingType.classCts);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.classCts.getValue());
			webSetting.setValue(classCts);
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(classCts);
			DBManager.update(webSetting);
		}

		webSetting = WebSettingManager.instance
				.getWebSetting(WebSettingType.classTime);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.classTime.getValue());
			webSetting.setValue(classTime + "");
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(classTime + "");
			DBManager.update(webSetting);
		}

		webSetting = WebSettingManager.instance
				.getWebSetting(WebSettingType.classPopularity);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.classPopularity.getValue());
			webSetting.setValue(classPopularity + "");
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(classPopularity + "");
			DBManager.update(webSetting);
		}

		webSetting = WebSettingManager.instance
				.getWebSetting(WebSettingType.classIsShutup);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.classIsShutup.getValue());
			webSetting.setValue(classIsShutup);
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(classIsShutup);
			DBManager.update(webSetting);
		}
		return resultFactory.success();
	}

	@Controller(value = "lecturerList.html")
	public ModelAndView lecturerList(HttpServletRequest request) {
		request.setAttribute("userList",
				UserManager.instance.lecturerMap.values());
		return new ModelAndView("/ftl/admin/lecturerList.html");
	}

	@Controller(value = "guestList.html")
	public ModelAndView guestList(HttpServletRequest request) {
		request.setAttribute("userList", UserManager.instance.guestMap.values());
		return new ModelAndView("/ftl/admin/guestList.html");
	}

	@Controller(value = "userList.html")
	public ModelAndView userList(HttpServletRequest request, Integer page,
			String content) {
		if (page == null) {
			page = 1;
		}

		long count;
		if (content == null || content.length() == 0) {
			count = datasource.query(long.class,
					new SimpleSql("select count(0) from user")).first();
		} else {
			count = datasource
					.query(long.class,
							new SimpleSql(
									"select count(0) from user where phone like ? or nickName like ?",
									"%" + content + "%", "%" + content + "%"))
					.first();
		}
		int maxPage = (int) (count / 20 + 1);
		if (page > maxPage) {
			page = maxPage;
		}

		if (page <= 0) {
			page = 1;
		}

		int begin = (page - 1) * 20;
		if (content == null || content.length() == 0) {
			request.setAttribute(
					"userList",
					datasource
							.query(User.class,
									new SimpleSql(
											"select * from user order by registerTime desc limit ?,20",
											begin)).shared());
		} else {
			request.setAttribute(
					"userList",
					datasource
							.query(User.class,
									new SimpleSql(
											"select * from user where phone like ? or nickName like ?",
											"order by registerTime desc limit ?,20",
											"%" + content + "%", "%" + content
													+ "%", begin)).shared());
		}

		request.setAttribute("content", content);
		request.setAttribute("maxPage", count / 20 + 1);
		request.setAttribute("page", page);
		return new ModelAndView("/ftl/admin/userList.html");
	}

	@Controller(value = "updateClassUser", methods = HttpMethod.POST)
	public Result updateUser(String openid, int userType, String desc) {
		User user = UserManager.instance.getUser(openid);
		if (user == null) {
			return resultFactory.error("用户不存在");
		}

		user.setUserType(userType);
		user.setUserDesc(desc);
		UserManager.instance.update(user);
		return resultFactory.success();
	}

	@Controller(value = "downMessage")
	public void downMessage(HttpServletRequest request,
			ServerHttpResponse httpServletResponse) throws Exception {
		WebSqlExcelExport.create(httpServletResponse, "聊天记录").export(new String[] { "openid", "昵称", "时间", "类型", "内容" }, datasource, new SimpleSql(
				"select m.openid, u.nickName, FROM_UNIXTIME(m.cts,'%Y-%m-%d %H:%m:%s'), m.type, m.`data` from user as u,message as m where u.openid=m.openid"));
	}

	@Controller(value = "notification")
	public Result notification() {
		return resultFactory.success();
	}
}