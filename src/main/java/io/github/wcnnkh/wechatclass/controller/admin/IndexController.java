package io.github.wcnnkh.wechatclass.controller.admin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.basc.framework.context.ioc.annotation.Autowired;
import io.basc.framework.context.transaction.Result;
import io.basc.framework.context.transaction.ResultFactory;
import io.basc.framework.db.DBManager;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.mvc.annotation.ActionInterceptors;
import io.basc.framework.security.login.UserToken;
import io.basc.framework.security.session.UserSession;
import io.basc.framework.util.StringUtils;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.message.model.ModelAndView;
import io.basc.framework.web.pattern.annotation.RequestMapping;
import io.github.wcnnkh.wechatclass.bean.admin.AdminGroup;
import io.github.wcnnkh.wechatclass.bean.admin.AdminUser;
import io.github.wcnnkh.wechatclass.manager.AdminManager;

@ActionInterceptors(AdminFilter.class)
@RequestMapping(value = "admin")
public class IndexController {
	@Autowired
	private ResultFactory resultFactory;
	@Autowired
	private AdminManager adminManager;

	@RequestMapping(value = "core/top.html")
	public ModelAndView top(UserToken<String> adminUser, HttpServletRequest request) {
		request.setAttribute("user", adminManager.getAdminUser(adminUser.getUid()));
		return new ModelAndView("/ftl/admin/core/top.html");
	}

	@RequestMapping(value = "core/left.html")
	public ModelAndView left(UserToken<String> adminUser, HttpServletRequest request) {
		request.setAttribute("menuMap", adminManager.getUserShowMenu(adminManager.getAdminUser(adminUser.getUid())));
		return new ModelAndView("/ftl/admin/core/left.html");
	}

	@RequestMapping(value = "core/updatePwd", methods = HttpMethod.POST)
	public Result updatePwd(String oldPwd, String newPwd, UserSession<String> adminUser) {
		if (StringUtils.isEmpty(oldPwd, newPwd)) {
			return resultFactory.error("参数错误");
		}

		AdminUser user = adminManager.getAdminUser(adminUser.getUid());
		if (!user.getPwd().equals(AdminController.PASSWORD_ENCODE.encode(oldPwd))) {
			return resultFactory.error("旧密码错误");
		}

		user.setPwd(AdminController.PASSWORD_ENCODE.encode(newPwd));
		DBManager.update(user);
		return resultFactory.success();
	}

	@RequestMapping(value = "core/admin_list.html")
	public ModelAndView admin_list(HttpServletRequest request) {
		request.setAttribute("adminList", DBManager.getByIdList(AdminUser.class));
		request.setAttribute("groupList", DBManager.getByIdList(AdminGroup.class));
		return new ModelAndView("/ftl/admin/core/admin_list.html");
	}

	@RequestMapping(value = "core/add_admin_user.html")
	public ModelAndView add_admin(HttpServletRequest request) {
		request.setAttribute("groupList", DBManager.getByIdList(AdminGroup.class));
		return new ModelAndView("/ftl/admin/core/add_admin_user.html");
	}

	@RequestMapping(value = "core/add_admin_user", methods = HttpMethod.POST)
	public Result add_user(String user, String pwd, String realName, long groupId) {
		AdminUser adminUser = DBManager.getById(AdminUser.class, user);
		if (adminUser != null) {
			return resultFactory.error("账号已经存在");
		}

		adminUser = new AdminUser();
		adminUser.setUser(user);
		adminUser.setPwd(AdminController.PASSWORD_ENCODE.encode(pwd));
		adminUser.setGroupId(groupId);
		adminUser.setRealName(realName);
		DBManager.save(adminUser);
		return resultFactory.success();
	}

	@RequestMapping(value = "core/update_admin_user", methods = HttpMethod.POST)
	public Result updateUser(String user, String pwd, String realName, long groupId) {
		AdminUser adminUser = DBManager.getById(AdminUser.class, user);
		if (adminUser == null) {
			return resultFactory.error("用户不存在");
		}

		adminUser.setRealName(realName);
		adminUser.setGroupId(groupId);
		if (!StringUtils.isEmpty(pwd)) {
			adminUser.setPwd(AdminController.PASSWORD_ENCODE.encode(pwd));
		}
		DBManager.update(adminUser);
		return resultFactory.success();
	}

	@RequestMapping(value = "core/delete_admin_user", methods = HttpMethod.POST)
	public Result deleteUser(String user) {
		AdminUser adminUser = DBManager.getById(AdminUser.class, user);
		if (adminUser == null) {
			return resultFactory.error("账号不存在");
		}

		DBManager.delete(adminUser);
		return resultFactory.success();
	}

	@RequestMapping(value = "core/group_list.html")
	public ModelAndView group_list(HttpServletRequest request) {
		request.setAttribute("groupList", DBManager.getByIdList(AdminGroup.class));
		return new ModelAndView("/ftl/admin/core/group_list.html");
	}

	@RequestMapping(value = "core/add_admin_group", methods = HttpMethod.POST)
	public Result add_group(String groupName) {
		AdminGroup adminGroup = new AdminGroup();
		adminGroup.setGroupId(System.currentTimeMillis());
		adminGroup.setGroupName(groupName);
		DBManager.save(adminGroup);
		return resultFactory.success();
	}

	@RequestMapping(value = "core/update_admin_group_name", methods = HttpMethod.POST)
	public Result update_group_name(long groupId, String groupName) {
		AdminGroup adminGroup = DBManager.getById(AdminGroup.class, groupId);
		if (adminGroup == null) {
			return resultFactory.error("分组不存在");
		}

		adminGroup.setGroupName(groupName);
		DBManager.update(adminGroup);
		return resultFactory.success();
	}

	@RequestMapping(value = "core/updateGroup.html")
	public ModelAndView updateGroup(long groupId, HttpServletRequest request) {
		AdminGroup adminGroup = DBManager.getById(AdminGroup.class, groupId);
		Map<String, Boolean> map = new HashMap<>();
		if (adminGroup.getActionIds() != null) {
			String[] ids = adminGroup.getActionIds().split(",");
			for (int i = 0; i < ids.length; i++) {
				map.put(ids[i], true);
			}
		}
		request.setAttribute("idMap", map);
		request.setAttribute("group", adminGroup);
		request.setAttribute("actionMap", adminManager.getAllMenu());
		return new ModelAndView("/ftl/admin/core/updateGroup.html");
	}

	@RequestMapping(value = "core/update_admin_group_actionIds", methods = HttpMethod.POST)
	public Result update_group_actionIds(long groupId, String actionIds) {
		AdminGroup adminGroup = DBManager.getById(AdminGroup.class, groupId);
		if (adminGroup == null) {
			return resultFactory.error("分组不存在");
		}

		adminGroup.setActionIds(actionIds);
		DBManager.update(adminGroup);
		return resultFactory.success();
	}

	@RequestMapping(value = "core/updateAdminPwd", methods = HttpMethod.POST)
	public Result updatePwd(UserSession<String> adminUser, String oldPwd, String newPwd) {
		AdminUser user = DBManager.getById(AdminUser.class, adminUser.getUid());
		if (!user.getPwd().equals(AdminController.PASSWORD_ENCODE.encode(oldPwd))) {
			return resultFactory.error("旧密码错误");
		}

		user.setPwd(AdminController.PASSWORD_ENCODE.encode(newPwd));
		DBManager.update(user);
		return resultFactory.success();
	}

	@RequestMapping("/core/index.html")
	public ModelAndView index(ServerHttpRequest request) {
		return new ModelAndView("/ftl/admin/core/index.html");
	}
}
