package message;

import java.io.Serializable;
import java.util.Set;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    //message types
    public static final int AUTHENTICATE_USER = 101;
    public static final int SEND_AUDIO = 501;
    public static final int SEND_IMAGE = 502;
    public static final int REQUEST_DOCTOR = 503;
    public static final int ACCEPT_REQUEST = 504;
    public static final int REJECT_REQUEST = 505;
    public static final int SEND_MESSAGE = 506;

    //user ids
    private String requesterID;
    private String doctorID;

    //incident details
    private int type;
    private byte[] picture;
    private byte[] audio;
    private String incidentType;
    private String priority;
    private Long dateCreated;
    private String initialLatitude;
    private String initialLongitude;
    private String noOfPeople;
    private Set<String> specializationList;

    //chat message
    private String message;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }
}
