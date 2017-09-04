package ro.pub.cs.emergencymobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import message.Message;
import ro.pub.cs.emergencymobileapp.service.CommunicateToServer;
import ro.pub.cs.emergencymobileapp.service.SendImageService;
import ro.pub.cs.emergencymobileapp.utils.Constants;
import ro.pub.cs.emergencymobileapp.utils.GlobalParams;

public class DataActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Boolean pictureFlag = false;
    private Context mContext;
    private int width = 640;
    private int height = 480;
    private long getFrameTime = 0;

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera.PreviewCallback previewCallback;
    private Button sendPictureButton;

    private ButtonClickListener sendButtonListener = new ButtonClickListener();

    private class ButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (pictureFlag == false) {
                Log.d(Constants.TAG, "Set pictureFlag = true");
                pictureFlag = true;
                sendPictureButton.setText("Stop video");
                refreshCamera();
                return;
            }
            if (pictureFlag == true) {
                Log.d(Constants.TAG, "Set pictureFlag = false");
                sendPictureButton.setText("Start video");
                pictureFlag = false;
                refreshCamera();
                return;
            }
        }
    }


    //chat
    private EditText messageEditText;
    private ListView messageListView;
    private Button sendMessageButton;
    private ArrayAdapter<String> chatAdapter;

    private SendMessageButtonClickListener sendMessageButtonClickListener = new SendMessageButtonClickListener();

    private class SendMessageButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            sendChatMessage();
        }
    }

    private void sendChatMessage() {
        String enteredMessage = messageEditText.getText().toString();
        if(!enteredMessage.isEmpty()) {
            String chatMessage = GlobalParams.requesterID + ":" + enteredMessage;
            chatAdapter.add(chatMessage);
            //TODO: send to server
            CommunicateToServer.sendMessageToServer(enteredMessage);
            messageEditText.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pictureFlag = savedInstanceState.getBoolean("PICTURE_FLAG");
        }

        setContentView(R.layout.activity_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();

//        TextView textView = (TextView)findViewById(R.id.textVideo);
//        textView.setText("In call with user <b>" + GlobalParams.doctorID + "</b>");

//        Intent intent = getIntent();

        //chat
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        messageListView = (ListView) findViewById(R.id.msgListView);
        sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(sendMessageButtonClickListener);
        chatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        messageListView.setAdapter(chatAdapter);

        // ----Set autoscroll of listview when a new message arrives----//
        messageListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        messageListView.setStackFromBottom(true);

        ReadMessageFromServerTask readMessageTask = new ReadMessageFromServerTask();
        readMessageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//        new Thread(new Runnable() {
//            public void run() {
//                Message message = null;
//                while(true) {
//                    try {
//                        message = (Message) GlobalParams.in.readObject();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    if(message != null && message.getType() == Message.SEND_MESSAGE) {
//                        String chatMessage = message.getDoctorID() + ":" + message.getMessage();
//                        chatAdapter.add(chatMessage);
//                    }
//                }
//            }
//        }).start();
        //end chat

        sendPictureButton = (Button) findViewById(R.id.capture_button);
        sendPictureButton.setOnClickListener(sendButtonListener);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if(!GlobalParams.audioRecorder.isAlive()) {
            GlobalParams.audioRecorder.start();
        }

//        Intent i = new Intent(this, SendImageService.class);
//        i.putExtra("START", true);
//        startService(i);




        previewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Parameters parameters = camera.getParameters();
                int imageFormat = parameters.getPreviewFormat();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (imageFormat == ImageFormat.NV21) {
                    Rect rect = new Rect(0, 0, width, height);
                    YuvImage img = new YuvImage(data, ImageFormat.NV21, width, height, null);
                    img.compressToJpeg(rect, 100, stream);
                }

                byte[] byteArray = stream.toByteArray();
                long newFraneTimestamp = System.currentTimeMillis();
                if(newFraneTimestamp - getFrameTime > 1000) {
                    Intent i = new Intent(mContext, SendImageService.class);
                    i.putExtra("PICTURE", true);
                    i.putExtra("picture", byteArray);
                    startService(i);
                    getFrameTime = System.currentTimeMillis();
                }
            }
        };

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Log.d(Constants.TAG, "surfaceChanged");
        refreshCamera();
    }

    private void refreshCamera() {
        Log.d(Constants.TAG, "refreshCamera");
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            if (pictureFlag) {
                camera.stopPreview();

                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(width, height);
                parameters.setPreviewFormat(ImageFormat.NV21);
                camera.setParameters(parameters);
                //CameraUtils.setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, camera);

                camera.setPreviewDisplay(surfaceHolder);
                camera.setPreviewCallback(previewCallback);
                camera.startPreview();
            } else {
                camera.stopPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(Constants.TAG, "surfaceCreated");
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }

        //
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            if (pictureFlag) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(width, height);
                parameters.setPreviewFormat(ImageFormat.NV21);
                camera.setParameters(parameters);
                //CameraUtils.setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
                camera.setPreviewDisplay(surfaceHolder);
                camera.setPreviewCallback(previewCallback);
                camera.startPreview();
            }
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(Constants.TAG, "surfaceDestroyed");
        // stop preview and release camera
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        Log.d(Constants.TAG, "onSavedInstanceState");
        outState.putBoolean("PICTURE_FLAG", pictureFlag);
        super.onSaveInstanceState(outState);
    }

    private class ReadMessageFromServerTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onProgressUpdate(String... item) {
            chatAdapter.add(item[0]);
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.d(Constants.TAG, "Waiting for messages...");
            Message message = null;

            while(true) {
                try {
                    message = (Message) GlobalParams.in.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(message != null && message.getType() == Message.SEND_MESSAGE) {
                    String chatMessage = message.getDoctorID() + ":" + message.getMessage();
                    publishProgress(chatMessage);
                }
            }
        }
    }
}
