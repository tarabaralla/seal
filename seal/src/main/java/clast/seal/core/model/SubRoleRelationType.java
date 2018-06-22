package clast.seal.core.model;

public enum SubRoleRelationType {
	
	DIRECT(DirectSubRoleRelation.class, "DirectSubRoleRelation", "direct"), INDIRECT(IndirectSubRoleRelation.class, "IndirectSubRoleRelation", "indirect");
	
	private final Class<? extends SubRoleRelation> modelValue;
	private final String queryValue;
	private final String dbValue;
	
	private SubRoleRelationType(Class<? extends SubRoleRelation> modelValue, String queryValue, String dbValue) {
		this.modelValue = modelValue;
		this.queryValue = queryValue;
		this.dbValue = dbValue;
	}

	public Class<? extends SubRoleRelation> getModelValue() {
		return modelValue;
	}
	
	public String getQueryValue() {
		return queryValue;
	}

	public String getDbValue() {
		return dbValue;
	}
	
}
