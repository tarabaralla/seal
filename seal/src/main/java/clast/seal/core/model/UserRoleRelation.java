package clast.seal.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_roles")
public class UserRoleRelation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@NotNull
	@Column(name = "user_id")
	private String userId;

	@NotNull
	@Column(name = "role_id")
	private String roleId;

	public UserRoleRelation() {
	}

	public UserRoleRelation(String userId, String roleId) {
		this.userId = userId;
		this.roleId = roleId;
	}
	
	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	@Override
	public String toString() {
		return userId + "-" + roleId;
	}

}
