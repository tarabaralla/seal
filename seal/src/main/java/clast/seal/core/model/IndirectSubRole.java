package clast.seal.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "indirect")
public class IndirectSubRole extends SubRole {
	
	public IndirectSubRole() {
		super();
	}

	public IndirectSubRole(String roleId, String subRoleId) {
		super(roleId, subRoleId);
	}
	
}
