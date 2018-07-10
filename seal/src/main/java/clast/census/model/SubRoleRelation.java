package clast.census.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING, name="type")
@Table(name = "sub_roles")
public abstract class SubRoleRelation {
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;

	@NotNull
	@Column( name = "role_id" )
	private String roleId;

	@NotNull
	@Column( name = "sub_role_id" )
	private String subRoleId;
	
	public SubRoleRelation() {}

	public SubRoleRelation(String roleId, String subRoleId) {
		this.roleId = roleId;
		this.subRoleId = subRoleId;
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
