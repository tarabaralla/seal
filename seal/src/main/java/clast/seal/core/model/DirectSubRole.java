package clast.seal.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "direct")
public class DirectSubRole extends SubRole {
	
	public DirectSubRole() {
		super();
	}

	public DirectSubRole(String roleId, String subRoleId) {
		super(roleId, subRoleId);
	}
	
}
