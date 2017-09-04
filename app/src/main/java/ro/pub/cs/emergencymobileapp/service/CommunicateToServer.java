package ro.pub.cs.emergencymobileapp.service;


import android.util.Log;

import java.io.IOException;

import message.Message;
import ro.pub.cs.emergencymobileapp.utils.GlobalParams;
import ro.pub.cs.emergencymobileapp.dto.IncidentRequest;
import ro.pub.cs.emergencymobileapp.utils.Constants;

public class CommunicateToServer {

    public static void sendIncidentRequest(IncidentRequest incidentRequest) {
        Log.d(Constants.TAG, "Requesting doctor...");
        sendRequestMessage(incidentRequest);
    }

    private static void sendRequestMessage(IncidentRequest incidentRequest) {
        Message message = new Message();
        message.setRequesterID(GlobalParams.requesterID);
        message.setType(Message.REQUEST_DOCTOR);
        message.setIncidentType(incidentRequest.getType());
        message.setDateCreated(incidentRequest.getDateCreated());
        message.setInitialLatitude(incidentRequest.getInitialLatitude());
        message.setInitialLongitude(incidentRequest.getInitialLongitude());
        message.setPriority(incidentRequest.getPriority());
        message.setNoOfPeople(incidentRequest.getNoOfPeople());
        message.setSpecializationList(incidentRequest.getSpecializationList());

        try {
            GlobalParams.out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GlobalParams.sentRequest = true;
    }

    public static void sendMessageToServer(String chatMessage) {
        Message message = new Message();
        message.setType(Message.SEND_MESSAGE);
        message.setRequesterID(GlobalParams.requesterID);
        message.setDoctorID(GlobalParams.doctorID);
        message.setMessage(chatMessage);

        try {
            GlobalParams.out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
