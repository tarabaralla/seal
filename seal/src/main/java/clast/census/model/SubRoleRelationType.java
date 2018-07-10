package clast.census.model;

public enum SubRoleRelationType {
	
	DIRECT("DirectSubRoleRelation"), INDIRECT("IndirectSubRoleRelation");
	
	private final String queryValue;
	
	private SubRoleRelationType(String queryValue) {
		this.queryValue = queryValue;
	}

	public String getQueryValue() {
		return queryValue;
	}
	
}
