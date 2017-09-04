package ro.pub.cs.emergencymobileapp.utils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ro.pub.cs.emergencymobileapp.dto.IncidentRequest;
import ro.pub.cs.emergencymobileapp.service.AudioRecorder;

public class GlobalParams {

    public static Socket socket;
    public static ObjectOutputStream out = null;
    public static ObjectInputStream in = null;
    public static AudioRecorder audioRecorder = new AudioRecorder();

    public static boolean sentRequest = false;
    public static boolean acceptedRequest = false;

    public static String requesterID;
    public static String doctorID;

    public static IncidentRequest incidentRequest;

}
