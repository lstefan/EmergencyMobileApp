package ro.pub.cs.emergencymobileapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import message.Message;

public class ServiceUtils {
    // Serialize to a byte array

    public static byte[] serializeMessage(Message message) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        try {
            ObjectOutput objectOutput = new ObjectOutputStream(bStream);
            objectOutput.writeObject(message);
            objectOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bStream.toByteArray();
    }
}
