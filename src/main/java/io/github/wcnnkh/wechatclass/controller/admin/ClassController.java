package io.github.wcnnkh.wechatclass.controller.admin;

import javax.servlet.http.HttpServletRequest;

import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.context.transaction.Result;
import io.basc.framework.context.transaction.ResultFactory;
import io.basc.framework.db.DB;
import io.basc.framework.db.DBManager;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.microsoft.ExcelTemplate;
import io.basc.framework.mvc.annotation.ActionInterceptors;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.util.ResultSet;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.message.model.ModelAndView;
import io.basc.framework.web.pattern.annotation.RequestMapping;
import io.github.wcnnkh.wechatclass.bean.User;
import io.github.wcnnkh.wechatclass.bean.WebSetting;
import io.github.wcnnkh.wechatclass.enums.WebSettingType;
import io.github.wcnnkh.wechatclass.manager.UserManager;
import io.github.wcnnkh.wechatclass.manager.WebSettingManager;

@ActionInterceptors(AdminFilter.class)
@RequestMapping(value = "admin")
public class ClassController {
	@Autowired
	private static DB datasource;
	@Autowired
	private ResultFactory resultFactory;
	@Autowired
	private UserManager userManager;
	@Autowired
	private WebSettingManager webSettingManager;

	@RequestMapping(value = "classInfo.html")
	public ModelAndView classInfo(HttpServletRequest request) {
		request.setAttribute("className", webSettingManager.getWebSetting(WebSettingType.className));
		request.setAttribute("classPopularity", webSettingManager.getWebSetting(WebSettingType.classPopularity));
		request.setAttribute("classTime", webSettingManager.getWebSetting(WebSettingType.classTime));
		request.setAttribute("classCts", webSettingManager.getWebSetting(WebSettingType.classCts));
		request.setAttribute("classIsShutup", webSettingManager.getWebSetting(WebSettingType.classIsShutup));
		return new ModelAndView("/ftl/admin/classInfo.html");
	}

	@RequestMapping(value = "updateClassInfo", methods = HttpMethod.POST)
	public Result updateClassInfo(String className, String classCts, int classTime, String classIsShutup,
			int classPopularity) {
		WebSetting webSetting = webSettingManager.getWebSetting(WebSettingType.className);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.className.getValue());
			webSetting.setValue(className);
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(className);
			DBManager.update(webSetting);
		}

		webSetting = webSettingManager.getWebSetting(WebSettingType.classCts);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.classCts.getValue());
			webSetting.setValue(classCts);
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(classCts);
			DBManager.update(webSetting);
		}

		webSetting = webSettingManager.getWebSetting(WebSettingType.classTime);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.classTime.getValue());
			webSetting.setValue(classTime + "");
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(classTime + "");
			DBManager.update(webSetting);
		}

		webSetting = webSettingManager.getWebSetting(WebSettingType.classPopularity);
		if (webSetting == null) {
			webSetting = new WebSetting();
			webSetting.setType(WebSettingType.classPopularity.getValue());
			webSetting.setValue(classPopularity + "");
			DBManager.save(webSetting);
		} else {
			webSetting.setValue(classPopularity + "");
			DBManager.update(webSetting);
		}

		webSetting = webSettingManager.getWebSetting(WebSettingType.classIsShutup);
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

	@RequestMapping(value = "lecturerList.html")
	public ModelAndView lecturerList(HttpServletRequest request) {
		request.setAttribute("userList", userManager.lecturerMap.values());
		return new ModelAndView("/ftl/admin/lecturerList.html");
	}

	@RequestMapping(value = "guestList.html")
	public ModelAndView guestList(HttpServletRequest request) {
		request.setAttribute("userList", userManager.guestMap.values());
		return new ModelAndView("/ftl/admin/guestList.html");
	}

	@RequestMapping(value = "userList.html")
	public ModelAndView userList(HttpServletRequest request, Integer page, String content) {
		if (page == null) {
			page = 1;
		}

		long count;
		if (content == null || content.length() == 0) {
			count = datasource.query(long.class, new SimpleSql("select count(0) from user")).first();
		} else {
			count = datasource
					.query(long.class, new SimpleSql("select count(0) from user where phone like ? or nickName like ?",
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
			request.setAttribute("userList",
					datasource
							.query(User.class,
									new SimpleSql("select * from user order by registerTime desc limit ?,20", begin))
							.toList());
		} else {
			request.setAttribute("userList", datasource.query(User.class, new SimpleSql(
					"select * from user where phone like ? or nickName like ? order by registerTime desc limit ?,20",
					"%" + content + "%", "%" + content + "%", begin)).toList());
		}

		request.setAttribute("content", content);
		request.setAttribute("maxPage", count / 20 + 1);
		request.setAttribute("page", page);
		return new ModelAndView("/ftl/admin/userList.html");
	}

	@RequestMapping(value = "updateClassUser", methods = HttpMethod.POST)
	public Result updateUser(String openid, int userType, String desc) {
		User user = userManager.getUser(openid);
		if (user == null) {
			return resultFactory.error("用户不存在");
		}

		user.setUserType(userType);
		user.setUserDesc(desc);
		userManager.update(user);
		return resultFactory.success();
	}

	@RequestMapping(value = "downMessage")
	public void downMessage(HttpServletRequest request, ServerHttpResponse httpServletResponse) throws Exception {
		// TODO 这里是sqlite特有的语法datetime
		datasource.query(ResultSet.class, new SimpleSql(
				"select m.openid as 'openid', u.nickName as '昵称', datetime(m.cts/1000, 'unixepoch', 'localtime') as '时间', m.type as '类型', m.`data` as '内容' from user as u,message as m where u.openid=m.openid"))
				.transfer((e) -> {
					new ExcelTemplate().process(e.iterator(), httpServletResponse, "聊天记录.xls");
				});
	}

	@RequestMapping(value = "notification")
	public Result notification() {
		return resultFactory.success();
	}
}
