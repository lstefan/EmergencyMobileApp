package ro.pub.cs.emergencymobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import ro.pub.cs.emergencymobileapp.dto.IncidentRequestDTO;
import ro.pub.cs.emergencymobileapp.service.SendDataService;
import ro.pub.cs.emergencymobileapp.utils.Constants;

public class ConnectionActivity extends AppCompatActivity {

    private EditText typeText, noOfPeopleText, priorityText;
    private ListView doctorsView;
    private Button sendData;

    private String people;
    private String priority;
    private String type;
    private Set<String> doctorsList = new HashSet<String>();
    private String latitude;
    private String longitude;

    private ButtonClickListener sendButtonListener = new ButtonClickListener();

    private class ButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // create dto
            IncidentRequestDTO incidentRequestDTO = new IncidentRequestDTO();
            incidentRequestDTO.setType(type);
            incidentRequestDTO.setPriority(priority);
            incidentRequestDTO.setInitialLatitude(latitude);
            incidentRequestDTO.setInitialLongitude(longitude);
            incidentRequestDTO.setDateCreated(new Date().getTime());

            // send to server
            SendDataToServerTask runner = new SendDataToServerTask();
            String incidentId = null;
            try {
                incidentId = runner.execute(incidentRequestDTO).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // next screen
            Intent intent = new Intent(getBaseContext(), DataActivity.class);
            intent.putExtra(Constants.INCIDENT_KEY, incidentId);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get data from intent
        Intent intent = getIntent();
        type = intent.getStringExtra(Constants.TYPE_KEY);
        people = intent.getStringExtra(Constants.NO_OF_PEOPLE_KEY);
        priority = intent.getStringExtra(Constants.PRIORITY_KEY);
        doctorsList = (Set<String>) intent.getSerializableExtra(Constants.SPECIALIZATION_KEY);
        latitude = intent.getStringExtra(Constants.LATITUDE_KEY);
        longitude = intent.getStringExtra(Constants.LONGITUDE_KEY);

        typeText = (EditText) findViewById(R.id.typeValue);
        typeText.setText(type);
        typeText.setEnabled(false);
        noOfPeopleText = (EditText) findViewById(R.id.noOfPeopleValue);
        noOfPeopleText.setText(people);
        noOfPeopleText.setEnabled(false);
        priorityText = (EditText) findViewById(R.id.priorityValue);
        priorityText.setText(priority);
        priorityText.setEnabled(false);
        doctorsView = (ListView) findViewById(R.id.doctorsList);

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, new ArrayList<String>(doctorsList));
        doctorsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        doctorsView.setAdapter(adapter);

        sendData = (Button) findViewById(R.id.btnSendToServer);
        sendData.setOnClickListener(sendButtonListener);
    }

    private class SendDataToServerTask extends AsyncTask<IncidentRequestDTO, String, String> {
        private String returnedId;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(IncidentRequestDTO... params) {
            //publishProgress("Sending incident to server..."); // Calls onProgressUpdate()
            returnedId = SendDataService.getSendDataService().sendIncident(params[0]);
            return returnedId;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();

            if (returnedId != null && !returnedId.isEmpty()) {
                Toast.makeText(ConnectionActivity.this, "Incident with id " + returnedId + " was created on the server!", 2000).show();
            }
            else {
                Toast.makeText(ConnectionActivity.this, "Error sending to server. Please try again!", 2000).show();
            }
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ConnectionActivity.this,
                    "ProgressDialog",
                    "Sending incident to server");
        }
    }

}
