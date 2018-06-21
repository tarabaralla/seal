package clast.seal.core.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING, name="type")
@Table(name = "sub_roles")
public abstract class SubRole {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@NotNull
	@Column( name = "role_id" )
	private String roleId;

	@NotNull
	@Column( name = "sub_role_id" )
	private String subRoleId;
	
	public SubRole() {}

	public SubRole(String roleId, String subRoleId) {
		this.roleId = roleId;
		this.subRoleId = subRoleId;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getSubRoleId() {
		return subRoleId;
	}

	public void setSubRoleId(String subRoleId) {
		this.subRoleId = subRoleId;
	}
	
	@Override
	public String toString() {
		return roleId + "-" + subRoleId;
	}
	
}
