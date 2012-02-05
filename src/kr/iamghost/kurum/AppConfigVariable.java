package kr.iamghost.kurum;

public class AppConfigVariable {
	public enum VarType {
		DIRECTORY, FILE;
	}
	
	private VarType type;
	private String name;
	private String message;
	private String[] filters = null;
	
	public VarType getType() {
		return type;
	}
	
	public void setType(VarType type) {
		this.type = type;
	}
	
	public void setType(String typeString) {
		if (typeString.equalsIgnoreCase("directory")) {
			type = VarType.DIRECTORY;
		}
		else {
			type = VarType.FILE;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String[] getFilters() {
		return filters;
	}

	public void setFilter(String filterString) {
		if (filterString != null) {
			filters = filterString.split(";");
		}
	}
}
