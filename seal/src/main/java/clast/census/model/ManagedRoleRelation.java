package clast.census.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "managed_roles")
public class ManagedRoleRelation {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@NotNull
	@Column(name = "role_id")
	private String roleId;

	@NotNull
	@Column(name = "managed_role_id")
	private String managedRoleId;

	public ManagedRoleRelation() {
	}

	public ManagedRoleRelation(String roleId, String managedRoleId) {
		this.roleId = roleId;
		this.managedRoleId = managedRoleId;
	}

	public String getId() {
		return id;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getManagedRoleId() {
		return managedRoleId;
	}

	public void setManagedRoleId(String managedRoleId) {
		this.managedRoleId = managedRoleId;
	}
	
	@Override
	public String toString() {
		return roleId + "-" + managedRoleId;
	}

}
