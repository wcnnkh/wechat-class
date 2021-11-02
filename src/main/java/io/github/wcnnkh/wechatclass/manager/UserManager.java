package io.github.wcnnkh.wechatclass.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import io.basc.framework.beans.annotation.Autowired;
import io.basc.framework.beans.annotation.InitMethod;
import io.basc.framework.db.DB;
import io.basc.framework.db.DBManager;
import io.basc.framework.security.session.Session;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.timer.boot.annotation.Schedule;
import io.basc.framework.util.TimeUtils;
import io.github.wcnnkh.wechatclass.bean.User;

public class UserManager {
	@Autowired
	public static UserManager instance;
	
	@Autowired
	private DB datasource;
	
	public Map<String, User> guestMap = new HashMap<>();
	public Map<String, User> lecturerMap = new HashMap<>();
	
	@InitMethod
	private void init(){
		List<User> list = datasource.query(User.class, new SimpleSql("select * from `user` where userType=?", 1)).shared();
		if(list != null){
			for(User user : list){
				lecturerMap.put(user.getOpenid(), user);
			}
		}
		
		list = datasource.query(User.class, new SimpleSql("select * from `user` where userType=?", 2)).shared();
		if(list != null){
			for(User user : list){
				guestMap.put(user.getOpenid(), user);
			}
		}
	}
	
	@Schedule(period = TimeUtils.ONE_DAY, name = "清理过期消息")
	private void deleteMessageTask(){
		datasource.update(new SimpleSql("delete from message where cts<?", System.currentTimeMillis() - 2 * TimeUtils.ONE_DAY));
	}
	
	public String getOpenId(Session session){
		return session==null? null:(String)session.getAttribute("openid");
	}
	
	public void setOpenId(HttpSession httpSession, String openid){
		httpSession.setAttribute("openid", openid);
	}
	
	public User getUser(String openid){
		User user = guestMap.get(openid);
		if(user == null){
			user = lecturerMap.get(openid);
			
			if(user == null){
				user = DBManager.getById(User.class, openid);
			}
		}
		return user;
	}
	
	public void update(User user){
		if(user.getUserType() == 1){
			synchronized(guestMap){
				guestMap.remove(user.getOpenid());
			}
			
			synchronized (lecturerMap) {
				lecturerMap.put(user.getOpenid(), user);
			}
		}else if(user.getUserType() == 2){
			synchronized(lecturerMap){
				lecturerMap.remove(user.getOpenid());
			}
			
			synchronized (guestMap) {
				guestMap.put(user.getOpenid(), user);
			}
		}
		
		DBManager.update(user);
	}
	
	public void save(User user){
		if(user.getUserType() == 1){
			synchronized(guestMap){
				guestMap.remove(user.getOpenid());
			}
			
			synchronized (lecturerMap) {
				lecturerMap.put(user.getOpenid(), user);
			}
			lecturerMap.put(user.getOpenid(), user);
		}else if(user.getUserType() == 2){
			synchronized(lecturerMap){
				lecturerMap.remove(user.getOpenid());
			}
			
			synchronized (guestMap) {
				guestMap.put(user.getOpenid(), user);
			}
		}
		DBManager.save(user);
	}
}
