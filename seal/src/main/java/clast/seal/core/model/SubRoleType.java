package clast.seal.core.model;

public enum SubRoleType {
	
	DIRECT(DirectSubRole.class, "DirectSubRole", "direct"), INDIRECT(IndirectSubRole.class, "IndirectSubRole", "indirect");
	
	private final Class<? extends SubRole> modelValue;
	private final String queryValue;
	private final String dbValue;
	
	private SubRoleType(Class<? extends SubRole> modelValue, String queryValue, String dbValue) {
		this.modelValue = modelValue;
		this.queryValue = queryValue;
		this.dbValue = dbValue;
	}

	public Class<? extends SubRole> getModelValue() {
		return modelValue;
	}
	
	public String getQueryValue() {
		return queryValue;
	}

	public String getDbValue() {
		return dbValue;
	}
	
}
