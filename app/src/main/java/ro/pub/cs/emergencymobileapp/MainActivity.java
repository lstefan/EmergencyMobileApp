package ro.pub.cs.emergencymobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import ro.pub.cs.emergencymobileapp.utils.Constants;

public class MainActivity extends AppCompatActivity {

    final private static String DEFAULT_SELECT = "Please select";

    private String[] listContent = {"Audiologist", "Allergist", "Cardiologist", "Dermatologist", "Emergency Doctor",
            "Endocrinologist", "Epidemiologist", "General Practitioner", "Hepatologist", "Infectious Disease ",
            "Specialist", "Neurophysiologist", "Obstetrician", "Oncologist", "Orthopedist", "Psychiatrist"};

    private String type;
    private String priority;
    private String people;
    private Set<String> doctorsListContent = new HashSet<String>();

    public Button emergencyButton;
    private Spinner typeSpinner, prioritySpinner, peopleSpinner;
    private ListView doctorsListView;

    private ButtonClickListener buttonClickListener = new ButtonClickListener();
    private SpinnerSelectionListener spinnerSelectionListener = new SpinnerSelectionListener();
    private ListSelectionListener listSelectionListener = new ListSelectionListener();

    private class ButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            Toast.makeText(getApplication(), "Emergency call!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getBaseContext(), MapsActivity.class);
            intent.putExtra(Constants.TYPE_KEY, type);
            intent.putExtra(Constants.NO_OF_PEOPLE_KEY, people);
            intent.putExtra(Constants.PRIORITY_KEY, priority);
            intent.putExtra(Constants.SPECIALIZATION_KEY, (Serializable) doctorsListContent);
            startActivity(intent);
        }
    }

    private class SpinnerSelectionListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long arg) {
            int id = parent.getId();

            switch (id) {
                case R.id.spinnerType:
                    type = parent.getItemAtPosition(pos).toString();
                    break;
                case R.id.spinnerPriority:
                    priority = parent.getItemAtPosition(pos).toString();
                    break;
                case R.id.spinnerNoPeople:
                    people = parent.getItemAtPosition(pos).toString();
                    break;
            }

            if (type != null && priority != null && people != null && !doctorsListContent.isEmpty() && (!type.equals
                    (DEFAULT_SELECT)) && (!priority.equals(DEFAULT_SELECT)) && (!people.equals(DEFAULT_SELECT))) {
                emergencyButton.setEnabled(true);
            } else {
                emergencyButton.setEnabled(false);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

    }

    private class ListSelectionListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
            String item = (String) parent.getItemAtPosition(position);
            doctorsListContent.add(item);

            if (type != null && priority != null && people != null && !doctorsListContent.isEmpty() && (!type.equals
                    (DEFAULT_SELECT)) && (!priority.equals(DEFAULT_SELECT)) && (!people.equals(DEFAULT_SELECT))) {
                emergencyButton.setEnabled(true);
            } else {
                emergencyButton.setEnabled(false);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emergencyButton = (Button) findViewById(R.id.buttonEmergency);
        emergencyButton.setEnabled(false);
        emergencyButton.setOnClickListener(buttonClickListener);

        typeSpinner = (Spinner) findViewById(R.id.spinnerType);
        prioritySpinner = (Spinner) findViewById(R.id.spinnerPriority);
        peopleSpinner = (Spinner) findViewById(R.id.spinnerNoPeople);

        typeSpinner.setOnItemSelectedListener(spinnerSelectionListener);
        prioritySpinner.setOnItemSelectedListener(spinnerSelectionListener);
        peopleSpinner.setOnItemSelectedListener(spinnerSelectionListener);

        doctorsListView = (ListView) findViewById(R.id.doctorsList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_list_item_multiple_choice, listContent);
        doctorsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        doctorsListView.setAdapter(adapter);
        doctorsListView.setOnItemClickListener(listSelectionListener);
    }

}
