package com.example.dragos.userdatamonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // the ip address that the whiti server is running on:
    private String IP_ADDRESS = "";

    private boolean wifiIsUp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // remove the tool bar, since it isn't needed:
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        //TODO: Add toast messages if Wifi is down, this still needs some work


        WifiManager wifiInf = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiInf.isWifiEnabled()) {
            this.wifiIsUp = false;
            showWifiIsDownToast();
        }


    }

    public void showWifiIsDownToast () {
        Toast.makeText(MainActivity.this,"Hey Vini, your Wifi is off, please turn it on",
                Toast.LENGTH_LONG).show();
    }

    /**
     * This method will start the next activity, with the IP address that was provided (
     * in the case of a save selection)
     *
     * @param type : either a browse or a save
     */
    public void startBrowseDataActivity (String type) {

        // the intent that will start the next activity:
        Intent intent = new Intent(this,SelectUserDataActivity.class);

        // create a bundle to send two strings to the next activity:
        Bundle dataSentToNextActivity = new Bundle();
        // add the type of opp to perform: save or browse:
        dataSentToNextActivity.putString("saveOrBrowse",type);
        // only send the IP address if a save opp was selected and the IP_ADDRESS was actually set:
        if (type.equals("save"))
            dataSentToNextActivity.putString("ipAdd",this.IP_ADDRESS);

        // send the bundle of data to the next activity:
        intent.putExtras(dataSentToNextActivity);

        // start the activity:
        startActivity(intent);
    }



    public void save (View view) {

        // don't allow networking opps if the wifi is down:
//        if (!this.wifiIsUp) {
//            showWifiIsDownToast();
//            return;
//        }

        // create a pop up that will prompt Vini for the IP Address of the server:
        AlertDialog.Builder popUp = new AlertDialog.Builder(this);
        popUp.setTitle("Please Enter Server IP Address");

        // set up the input text field:
        final EditText input = new EditText(MainActivity.this);
        // specify the type of text inserted in the input field, right now it's normal text:
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        popUp.setView(input);

        // now for the buttons in the pop up:

        // for pressing OK
        popUp.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            public void onClick (DialogInterface dialog,int which) {
                // if Vini has entered his IP Address, then set it, and start the next activity:
                IP_ADDRESS = input.getText().toString();
                // Vini wants to save user data:
                startBrowseDataActivity("save");
            }
        });

        // for pressing cancel:
        popUp.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            public void onClick (DialogInterface dialog,int which) {
                dialog.cancel();
            }
        });

        popUp.show();
    }


    public void browse (View view) {

        // don't allow networking opps if the wifi is down:
//        if (!this.wifiIsUp) {
//            showWifiIsDownToast();
//            return;
//        }

        // vini wants to browse user date previously saved:
        startBrowseDataActivity("browse");
    }

}
