package clast.census.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "indirect")
public class IndirectSubRoleRelation extends SubRoleRelation {
	
	public IndirectSubRoleRelation() {
		super();
	}

	public IndirectSubRoleRelation(String roleId, String subRoleId) {
		super(roleId, subRoleId);
	}
	
}
