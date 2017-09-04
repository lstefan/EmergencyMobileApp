package ro.pub.cs.emergencymobileapp.dto;

public enum People {
	ONE ("1"),
	TWO ("2"),
	THREE_FIVE ("3-5"),
	SIX_TEN("6-10"),
	TEN_PLUS(">10");
	
	private final String peopleCode;
	
	private People(String peopleCode) {
		this.peopleCode = peopleCode;
	}

	public String getPeopleCode() {
		return peopleCode;
	}
	
	public static People getByName(String name){
		return People.valueOf(name);
	}
}
