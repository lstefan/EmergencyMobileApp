package ro.pub.cs.emergencymobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ro.pub.cs.emergencymobileapp.service.AudioRecorder;
import ro.pub.cs.emergencymobileapp.service.SendDataAndroidService;
import ro.pub.cs.emergencymobileapp.utils.Constants;

public class DataActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = DataActivity.class.getSimpleName();

    private Button sendPictureButton;

    private int width = 640;
    private int height = 480;
    //new
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Camera.PictureCallback jpegCallback;
    Camera.PreviewCallback previewCallback;

    String incidentId;
    public static Boolean pictureFlag = false;
    static Context mContext;

    public static Socket socket;
    public static ObjectOutputStream out = null;
    public static ObjectInputStream in = null;
    public static AudioRecorder audioRecorder = new AudioRecorder();

    private ButtonClickListener sendButtonListener = new ButtonClickListener();

    private class ButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (pictureFlag == false) {
                Log.d(Constants.TAG, "Set pictureFlag = true");
                pictureFlag = true;
                refreshCamera();
                return;
            }
            if (pictureFlag == true) {
                Log.d(Constants.TAG, "Set pictureFlag = false");
                pictureFlag = false;
                refreshCamera();
                return;
            }
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

        Intent intent = getIntent();
        incidentId = intent.getStringExtra(Constants.INCIDENT_KEY);

        sendPictureButton = (Button) findViewById(R.id.capture_button);
        sendPictureButton.setOnClickListener(sendButtonListener);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Intent i = new Intent(this, SendDataAndroidService.class);
        i.putExtra("START", true);
        startService(i);

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
                Intent i = new Intent(mContext, SendDataAndroidService.class);
                i.putExtra("PICTURE", true);
                i.putExtra("picture", byteArray);
                startService(i);
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

    public int[] decodeYUV(byte[] fg, int width, int height) throws NullPointerException, IllegalArgumentException {
        int[] out = new int[width * height];
        int sz = width * height;
        if (out == null) throw new NullPointerException("byteAudioBuffer out is null");
        if (out.length < sz) throw new IllegalArgumentException("byteAudioBuffer out size " + out.length + " < minimum " + sz);
        if (fg == null) throw new NullPointerException("byteAudioBuffer 'fg' is null");
        if (fg.length < sz)
            throw new IllegalArgumentException("byteAudioBuffer fg size " + fg.length + " < minimum " + sz * 3 / 2);
        int i, j;
        int Y, Cr = 0, Cb = 0;
        for (j = 0; j < height; j++) {
            int pixPtr = j * width;
            final int jDiv2 = j >> 1;
            for (i = 0; i < width; i++) {
                Y = fg[pixPtr];
                if (Y < 0) Y += 255;
                if ((i & 0x1) != 1) {
                    final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
                    Cb = fg[cOff];
                    if (Cb < 0) Cb += 127;
                    else Cb -= 128;
                    Cr = fg[cOff + 1];
                    if (Cr < 0) Cr += 127;
                    else Cr -= 128;
                }
                int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if (R < 0) R = 0;
                else if (R > 255) R = 255;
                int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                if (G < 0) G = 0;
                else if (G > 255) G = 255;
                int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if (B < 0) B = 0;
                else if (B > 255) B = 255;
                out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
            }
        }

        return out;

    }


}
