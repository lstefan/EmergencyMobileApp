package ro.pub.cs.emergencymobileapp.service;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import ro.pub.cs.emergencymobileapp.utils.Constants;

public class AudioRecorder extends Thread {

    public byte[] byteAudioBuffer;

    AudioRecord recorder;

    private int sampleRate = 8000;//8000; 16000 for voice
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;

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
            //if (DataActivity.pictureFlag) {
            //reading data from MIC into byteAudioBuffer
            minBufSize = recorder.read(shortsAudioBuffer, 0, shortsAudioBuffer.length);
            byteAudioBuffer = short2byte(shortsAudioBuffer);

            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName(Constants.HOST);
                DatagramPacket sendPacket = new DatagramPacket(byteAudioBuffer, byteAudioBuffer.length, IPAddress, Constants.UDP_PORT);
                Log.d(Constants.TAG, "Am trimis " + byteAudioBuffer.length);
                clientSocket.send(sendPacket);
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("MinBufferSize: " + minBufSize);
            shortsAudioBuffer = new short[minBufSize];

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //}
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
 