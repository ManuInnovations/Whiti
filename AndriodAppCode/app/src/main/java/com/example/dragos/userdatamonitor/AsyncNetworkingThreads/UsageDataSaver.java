package com.example.dragos.userdatamonitor.AsyncNetworkingThreads;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class will represent yet another background thread that makes a POST request to the RESTful
 * API Server, which will permanently save the usage data, so that usage data from previous dates can
 * be viewed whenever
 *
 * Created by Dragos on 10/1/17.
 */

public class UsageDataSaver extends AsyncTask<JSONArray,Void,Void> {

    @Override
    protected Void doInBackground(JSONArray[] params) {
        // we will only give one map (not an array of them) to this thread, so lets obtain that one:
        JSONArray userDataFromWhitiServer = params[0];

        // now connect to the restful API and make a POST request, sending the data obtained from the Whiti server:
        try {

            // if the Whiti Server wasn't running, or no users have used the app, make no request:
            if (userDataFromWhitiServer.length() == 0)
                return null;

            // open a connection to the server storing the data:
            URL url = new URL ("http://rest.learncode.academy/api/engrwhitiapp/userlogs");
            URLConnection connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setDoOutput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setRequestMethod("POST");

            httpConnection.setRequestProperty("Content-Type","application/json");
            httpConnection.setRequestProperty("Accept", "application/json");

            httpConnection.connect();


            // now write the data:
            JSONObject root = new JSONObject();
            root.put("dataFromWhitiServer",userDataFromWhitiServer);

            DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
            os.writeBytes(root.toString());

            os.flush();
            os.close();


            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                Log.d("ALL GOOD MATE","Connection was ok");
            else
                Log.w("BAD NEWS","Connection was bad");

        }

        catch (Exception e) {
            Log.w("ERROR: ","Problem posting data to JSON Server");
            e.printStackTrace();
        }

        return null;
    }



}
