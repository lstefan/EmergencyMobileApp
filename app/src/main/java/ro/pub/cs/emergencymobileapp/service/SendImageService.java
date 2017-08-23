package ro.pub.cs.emergencymobileapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import message.Message;
import ro.pub.cs.emergencymobileapp.DataActivity;
import ro.pub.cs.emergencymobileapp.utils.Constants;


public class SendImageService extends IntentService {

    public SendImageService() {
        super("SendImageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean startTransmission = intent.getBooleanExtra("START", false);
        boolean endTransmission = intent.getBooleanExtra("END", false);
        boolean sendPicture = intent.getBooleanExtra("PICTURE", false);

        if(endTransmission) {
            Log.d(Constants.TAG, "Stopping transmission...");
            try {
                DataActivity.out.close();
                DataActivity.in.close();
                DataActivity.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;

        }

        if(startTransmission) {
            Log.d(Constants.TAG, "Starting transmission...");
            Log.d(Constants.TAG, "START = " + startTransmission);
            Log.d(Constants.TAG, "PICTURE = " + sendPicture);
            openSocket();
            sendAuthenticationMessage();
        }

        if(sendPicture) {
            Log.d(Constants.TAG, "Sending picture...");

            byte[] temp = intent.getByteArrayExtra("picture");
            if(temp == null || temp.length == 0) {
                Log.d(Constants.TAG, "Cannot send empty picture!");
                return;
            }
            Message message = new Message();
            message.setPicture(temp);
            message.setType(Message.PICTURE_FILE);

            Log.d(Constants.TAG, "Image size =  " + temp.length);

            try {
                DataActivity.out.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void sendAuthenticationMessage() {
        Message message = new Message();
        message.setType(Message.AUTHENTICATE_USER);
        try {
            DataActivity.out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSocket() {
        DataActivity.socket = new Socket();
        try {
            DataActivity.socket.connect(new InetSocketAddress(Constants.HOST, Constants.TCP_PORT));
            Log.d(Constants.TAG, "Connecting to server...");
            DataActivity.out = new ObjectOutputStream(DataActivity.socket.getOutputStream());
            DataActivity.in = new ObjectInputStream(DataActivity.socket.getInputStream());
        } catch (IOException e) {
            Log.d(Constants.TAG, "Error connecting. Exiting...");
            e.printStackTrace();
        }
    }

}
