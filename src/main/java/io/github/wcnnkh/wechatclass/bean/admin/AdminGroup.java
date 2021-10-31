package io.github.wcnnkh.wechatclass.bean.admin;

import io.basc.framework.db.BaseBean;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.orm.annotation.Table;

@Table(name="admin_group")
public class AdminGroup extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
	private long groupId;
	private String groupName;
	private String actionIds;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public String getActionIds() {
		return actionIds;
	}
	public void setActionIds(String actionIds) {
		this.actionIds = actionIds;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
