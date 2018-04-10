package com.example.dragos.userdatamonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dragos.userdatamonitor.AsyncNetworkingThreads.ConnectToRestAPI;
import com.example.dragos.userdatamonitor.AsyncNetworkingThreads.ReadFromWhiti;
import com.example.dragos.userdatamonitor.AsyncNetworkingThreads.UsageDataSaver;
import com.example.dragos.userdatamonitor.DataDisplaying.DataDisplayer;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Activity that will start after the Vini has specified the IP Address that the server is running on
 * This will then start the background thread, connect to the server, etc ...
 *
 */
public class SelectUserDataActivity extends AppCompatActivity implements AsyncResponse {

    // this will contain the final overall user data, obtained from the rest api:
    private Map<String,List<String>> finalDataFromRestApi = new HashMap<String,List<String>>();

    // this is a list of all the id's for JSON objects that have already been saved to the rest api:
    private List<String> idValuesForAllPreviouslySavedUsers = new ArrayList<>();

    // A global variable/flag that will determine what opperation is intended by Vini:
    private String saveOrBrowse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_data);

        // obtain the intent that started this activity:
        Intent intent = getIntent();

        // obtain the bundle of data sent from the main:
        Bundle bundle = intent.getExtras();

        // save or browse
        String typeOfOpp = bundle.getString("saveOrBrowse");

        // if the user wanted to save data:
        if (typeOfOpp.equals("save")) {
            this.saveOrBrowse = "save";

            String ipAddress = bundle.getString("ipAdd");

            getDataFromRestAPI("getIDS");

            // now that the ipAddress from the first activity (where Vini has inputed the ip)
            // has been retrieved, start the background thread to connect to the server, and get the JSON Data:
            ReadFromWhiti sr = new ReadFromWhiti();

            sr.setDelegate(this);

            sr.execute("https://" + ipAddress + ":5000/usageData");
        }

        // if Vini wants to browse then only execute the thread that gets the data from the rest API:
        else if (typeOfOpp.equals("browse")) {
            this.saveOrBrowse = "browse";
            getDataFromRestAPI("obtainFinal");
        }


    }


    @Override
    public void currentUsageDataObtained (JSONArray output) {

        // obtain the filtered (data not already saved) data from the whiti JSON:
        JSONArray recentData = JSONReader.obtainNewestObjects(output,this.idValuesForAllPreviouslySavedUsers);

        UsageDataSaver saveToAPI = new UsageDataSaver();
        saveToAPI.execute(recentData);

        // Now start the final thread that will read from the (possibly updated) REST API
        // and obtain the final user data
        getDataFromRestAPI("obtainFinal");

    }

    @Override
    public void uniqueDataObtained(List<String> ids) {
        this.idValuesForAllPreviouslySavedUsers = ids;
    }

    @Override
    public void finalUserDataObtained (Map<String,List<String>> output) {
        // after the data is returned from the api:
        this.finalDataFromRestApi = output;

        // now that the final data from the rest api has been loaded onto the application,
        // display a toast message to Vini, so that he knows he can pick the date:

        String toastDisplay = "";

        if (this.saveOrBrowse.equals("save"))
            toastDisplay = "Data Saved, you may proceed to browse :)";
        else
            toastDisplay = "Data loaded, you may proceed to browse :)";

        // obtain the text field, and update it to let Vini know that the data has been loaded and can now be viewed:
        TextView mainView = (TextView)findViewById(R.id.mainView);
        mainView.setText(R.string.dataLoaded);

        Toast.makeText(SelectUserDataActivity.this,toastDisplay,
                Toast.LENGTH_LONG).show();
    }

    public void getDataFromRestAPI (String task) {
        ConnectToRestAPI finalNetworkingThread = new ConnectToRestAPI();
        finalNetworkingThread.setDelegate(this);
        finalNetworkingThread.execute(new String []{"http://rest.learncode.academy/api/engrwhitiapp/userlogs",task});
    }



    @Override
    public void datePicked (String selectedDate) {

        DataDisplayer dataDisplayer = new DataDisplayer();

        // display text here:
        TextView mainView = (TextView) findViewById(R.id.mainView);
        mainView.setTextSize(25);
        String displayData = dataDisplayer.getDataDisplay(this.finalDataFromRestApi,selectedDate);
        mainView.setText(displayData);
    }


    public void showDatePickerDialog (View view) {

        UserLogDatePicker datePicker = new UserLogDatePicker();
        datePicker.setDelegate(this);

        datePicker.show(getFragmentManager(),"date_picker_button");
    }

}
