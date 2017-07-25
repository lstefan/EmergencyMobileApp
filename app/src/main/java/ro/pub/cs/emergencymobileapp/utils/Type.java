package ro.pub.cs.emergencymobileapp.utils;

public enum Type {
	CAR_CRASH("Car crash"),
	FIRE("Fire"),
	EXPLOSION("Explosion"),
	DROWNING("Drowning"),
	SICKNESS("Sickness"),
	OTHER("Other");
	
	private final String typeCode;
	
	private Type(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeCode() {
		return typeCode;
	}
	
	public static Type getByName(String name){
		return Type.valueOf(name);
	}
	
}
