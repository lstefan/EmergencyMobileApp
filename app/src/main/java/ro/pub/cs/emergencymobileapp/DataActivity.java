package ro.pub.cs.emergencymobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ro.pub.cs.emergencymobileapp.service.SendDataService;
import ro.pub.cs.emergencymobileapp.utils.Constants;

public class DataActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = DataActivity.class.getSimpleName();

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    private Button btnCapturePicture, btnRecordVideo;

    //new
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;

    String incidentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        incidentId = intent.getStringExtra(Constants.INCIDENT_KEY);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                DataActivity.SendDataToServerTask runner = new DataActivity.SendDataToServerTask();
                String status = null;
                try {
                    status = runner.execute(data).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
//                FileOutputStream outStream = null;
//                try {
//                    outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
//                    outStream.write(data);
//                    outStream.close();
//                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                }
//                Toast.makeText(getApplicationContext(), "Picture Saved", 2000).show();
                refreshCamera();
            }
        };

    }

    public void captureImage(View v) throws IOException {
        //take the picture
        camera.takePicture(null, null, jpegCallback);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        refreshCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        param.setPreviewSize(352, 288);
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            param.set("orientation", "portrait");
            param.setRotation(90);
            camera.setDisplayOrientation(90);
        }

        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private class SendDataToServerTask extends AsyncTask<byte[], String, String> {
        private String returnStatus;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(byte[]... params) {
            //publishProgress("Sending incident to server..."); // Calls onProgressUpdate()
            returnStatus = SendDataService.getSendDataService().sendFrame(params[0]);
            return returnStatus;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();

//            if (returnedId != null && !returnedId.isEmpty()) {
//                Toast.makeText(DataActivity.this, "Incident with id " + returnedId + " was created on the server!", 2000).show();
//            }
//            else {
//                Toast.makeText(DataActivity.this, "Error sending to server. Please try again!", 2000).show();
//            }
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(DataActivity.this,
                    "ProgressDialog",
                    "Sending incident to server");
        }
    }

}
