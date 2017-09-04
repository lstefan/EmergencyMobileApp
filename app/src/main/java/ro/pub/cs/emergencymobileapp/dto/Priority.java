package ro.pub.cs.emergencymobileapp.dto;

public enum Priority {
	HIGH ("High"),
	MEDIUM ("Medium"),
	LOW("Low");
	
	private final String priorityCode;
	
	private Priority(String priorityCode) {
		this.priorityCode = priorityCode;
	}

	public String getPriorityCode() {
		return priorityCode;
	}
	
	public static Priority getByName(String name){
		return Priority.valueOf(name);
	}
	

}
