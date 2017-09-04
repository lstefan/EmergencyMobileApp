package ro.pub.cs.emergencymobileapp.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class IncidentRequest implements Serializable {
	private String type;
	private String priority;
	private Long dateCreated;
	private String initialLatitude;
	private String initialLongitude;
	private String noOfPeople;
	private Set<String> specializationList;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getInitialLatitude() {
		return initialLatitude;
	}

	public void setInitialLatitude(String initialLatitude) {
		this.initialLatitude = initialLatitude;
	}

	public String getInitialLongitude() {
		return initialLongitude;
	}

	public void setInitialLongitude(String initialLongitude) {
		this.initialLongitude = initialLongitude;
	}

	public String getNoOfPeople() {
		return noOfPeople;
	}

	public void setNoOfPeople(String noOfPeople) {
		this.noOfPeople = noOfPeople;
	}

	public Set<String> getSpecializationList() {
		return specializationList;
	}

	public void setSpecializationList(Set<String> specializationList) {
		this.specializationList = specializationList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IncidentRequest that = (IncidentRequest) o;
		return Objects.equals(type, that.type) && Objects.equals(priority, that.priority) && Objects.equals
				(dateCreated, that.dateCreated) && Objects.equals(initialLatitude, that.initialLatitude) && Objects
				.equals(initialLongitude, that.initialLongitude) && Objects.equals(noOfPeople, that.noOfPeople) &&
				Objects.equals(specializationList, that.specializationList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, priority, dateCreated, initialLatitude, initialLongitude, noOfPeople, specializationList);
	}

	@Override
	public String toString() {
		return "IncidentRequest{" + "type=" + type + ", priority=" + priority + ", dateCreated=" + dateCreated + ", initialLatitude=" + initialLatitude + ", initialLongitude=" + initialLongitude + '}';
	}
}
