package clast.census.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "direct")
public class DirectSubRoleRelation extends SubRoleRelation {
	
	public DirectSubRoleRelation() {
		super();
	}

	public DirectSubRoleRelation(String roleId, String subRoleId) {
		super(roleId, subRoleId);
	}
	
}
