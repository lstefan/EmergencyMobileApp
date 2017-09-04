package ro.pub.cs.emergencymobileapp.service;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import message.Message;
import ro.pub.cs.emergencymobileapp.utils.Constants;
import ro.pub.cs.emergencymobileapp.utils.ServiceUtils;

public class AudioRecorder extends Thread {

    private byte[] byteAudioBuffer;
    private AudioRecord recorder;
    private int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;

    private DatagramSocket audioSocket;
    private InetAddress ipAddress;

    public AudioRecorder() {
        try {
            audioSocket = new DatagramSocket();
            ipAddress = InetAddress.getByName(Constants.HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        short[] shortsAudioBuffer = new short[minBufSize];

        Log.d(Constants.TAG, "Buffer created of size " + minBufSize);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
        Log.d(Constants.TAG, "Recorder initialized");

        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(Constants.TAG, "Audio Record can't initialize!");
            return;
        }

        Log.e(Constants.TAG, "Start recording!");
        recorder.startRecording();

        while (status) {
            minBufSize = recorder.read(shortsAudioBuffer, 0, shortsAudioBuffer.length);
            byteAudioBuffer = short2byte(shortsAudioBuffer);

            //Create message
            Message message = new Message();
            message.setType(Message.SEND_AUDIO);
            message.setAudio(byteAudioBuffer);

            //serialize message
            byte[] serializedMessage = ServiceUtils.serializeMessage(message);

            try {
                DatagramPacket sendPacket = new DatagramPacket(serializedMessage, serializedMessage.length, ipAddress, Constants.UDP_AUDIO_PORT);
                Log.d(Constants.TAG, "Am trimis " + byteAudioBuffer.length);
                audioSocket.send(sendPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
            shortsAudioBuffer = new short[minBufSize];

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        recorder.release();
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }
}
 