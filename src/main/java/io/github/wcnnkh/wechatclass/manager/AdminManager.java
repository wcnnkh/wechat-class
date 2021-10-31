package io.github.wcnnkh.wechatclass.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.openmbean.KeyAlreadyExistsException;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.beans.annotation.InitMethod;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.codec.Encoder;
import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.db.DBManager;
import io.basc.framework.util.StringUtils;
import io.github.wcnnkh.wechatclass.bean.admin.AdminGroup;
import io.github.wcnnkh.wechatclass.bean.admin.AdminUser;
import io.github.wcnnkh.wechatclass.conf.AdminAction;
import io.github.wcnnkh.wechatclass.conf.AdminMenu;

public class AdminManager {
	@Autowired
	public static AdminManager instance;

	private static final Encoder<String, String> PASSWORD_ENCODE = CharsetCodec.UTF_8.toMD5();

	@Value(value = "/admin/admin_menu.xml")
	private Map<Integer, AdminMenu> menuMap = new HashMap<Integer, AdminMenu>();

	@Value(value = "/admin/admin_action.xml")
	private List<AdminAction> actionList;
	private Map<Integer, AdminAction> actionMap = new HashMap<>();

	private Map<String, Map<String, Integer>> adminPathMap = new HashMap<>();

	@InitMethod
	private void init() {
		for (AdminAction adminAction : actionList) {
			Map<String, Integer> map = adminPathMap.get(adminAction.getServletPath());
			if (map == null) {
				map = new HashMap<>();
			}
			map.put(adminAction.getMethod(), adminAction.getId());
			adminPathMap.put(adminAction.getServletPath(), map);

			if (actionMap.containsKey(adminAction.getId())) {
				throw new KeyAlreadyExistsException("admin_action.xml存在重复的id, actionId=" + adminAction.getId());
			}
			actionMap.put(adminAction.getId(), adminAction);
		}

		AdminUser adminUser = getAdminUser("admin");
		if (adminUser == null) {
			create("admin", "admin", "超级管理员", 0L);
		}
	}

	public AdminUser getAdminUser(String username) {
		return DBManager.getById(AdminUser.class, username);
	}

	public AdminUser create(String username, String pwd, String realName, long groupId) {
		AdminUser adminUser = new AdminUser();
		adminUser.setUser(username);
		adminUser.setPwd(PASSWORD_ENCODE.encode(pwd));
		adminUser.setRealName(realName);
		adminUser.setGroupId(groupId);
		DBManager.save(adminUser);
		return adminUser;
	}

	public boolean checkPath(String method, String path, long groupId) {
		Map<String, Integer> map = adminPathMap.get(path);
		if (map == null) {
			return true;
		}

		Integer id = map.get(method);
		if (id == null) {
			return true;
		}

		AdminGroup adminGroup = DBManager.getById(AdminGroup.class, groupId);
		if (adminGroup == null) {
			return false;
		}

		if (adminGroup.getActionIds() == null) {
			return false;
		}

		return adminGroup.getActionIds().indexOf("," + id + ",") != -1;
	}

	public Map<String, List<AdminAction>> getAllMenu() {
		Map<String, List<AdminAction>> map = new HashMap<String, List<AdminAction>>();
		for (AdminAction action : actionList) {
			String menuName = menuMap.get(action.getMenuId()).getMenuName();
			List<AdminAction> l = map.get(menuName);
			if (l == null) {
				l = new ArrayList<AdminAction>();
			}
			l.add(action);
			map.put(menuName, l);
		}
		return map;
	}

	public Map<String, List<AdminAction>> getUserShowMenu(AdminUser adminUser) {
		Map<String, List<AdminAction>> map = new HashMap<String, List<AdminAction>>();
		if ("admin".equals(adminUser.getUser())) {
			for (AdminAction action : actionList) {
				String menuName = menuMap.get(action.getMenuId()).getMenuName();
				List<AdminAction> l = map.get(menuName);
				if (l == null) {
					l = new ArrayList<AdminAction>();
				}
				l.add(action);
				map.put(menuName, l);
			}
		} else {
			AdminGroup adminGroup = DBManager.getById(AdminGroup.class, adminUser.getGroupId());
			if (adminGroup != null) {
				String actionIds = adminGroup.getActionIds();
				if (actionIds != null) {
					String[] idsArr = actionIds.split(",");
					for (int i = 0; i < idsArr.length; i++) {
						if (StringUtils.isEmpty(idsArr[i])) {
							continue;
						}

						int id = Integer.parseInt(idsArr[i]);
						AdminAction action = actionMap.get(id);
						if (action == null) {
							continue;
						}

						String menuName = menuMap.get(action.getMenuId()).getMenuName();
						List<AdminAction> l = map.get(menuName);
						if (l == null) {
							l = new ArrayList<AdminAction>();
						}
						l.add(action);
						map.put(menuName, l);
					}
				}
			}
		}
		return map;
	}
}
