package ro.pub.cs.emergencymobileapp.dto;

import java.io.Serializable;

/**
 *
 */
public class IncidentRequestDTO implements Serializable {
	private String type;
	private String priority;
	private Long dateCreated;
	private String initialLatitude;
	private String initialLongitude;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		IncidentRequestDTO that = (IncidentRequestDTO) o;

		if (type != that.type) {
			return false;
		}
		if (priority != that.priority) {
			return false;
		}
		if (dateCreated != null ? !dateCreated.equals(that.dateCreated) : that.dateCreated != null) {
			return false;
		}
		if (initialLatitude != null ? !initialLatitude.equals(that.initialLatitude) : that.initialLatitude != null) {
			return false;
		}
		return initialLongitude != null ? initialLongitude.equals(that.initialLongitude) : that.initialLongitude == null;
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (priority != null ? priority.hashCode() : 0);
		result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
		result = 31 * result + (initialLatitude != null ? initialLatitude.hashCode() : 0);
		result = 31 * result + (initialLongitude != null ? initialLongitude.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "IncidentRequestDTO{" + "type=" + type + ", priority=" + priority + ", dateCreated=" + dateCreated + ", initialLatitude=" + initialLatitude + ", initialLongitude=" + initialLongitude + '}';
	}
}
