package com.example.dragos.userdatamonitor.AsyncNetworkingThreads;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dragos.userdatamonitor.AsyncResponse;
import com.example.dragos.userdatamonitor.JSONReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * This is a class that represents a background thread that will connect to the rest api
 *
 * depending on its task, it will either get the unique ids from the rest api, or obtain all the data and
 * put it into a hash map so that user stats can be displayed
 *
 * Created by Dragos on 10/1/17.
 */

public class ConnectToRestAPI extends AsyncTask<String,Void,String>{

    private String FINAL_DATA = "";

    private String task = "";

    AsyncResponse delegate = null;

    @Override
    protected void onPostExecute (String result) {

        // parse the returned JSON Data here:
        JSONReader jsonReader = new JSONReader(result);
        jsonReader.loadDataFromRestAPI();

        // pass the map to string data back to the main activity, to display it on the text view:
        if (this.task.equals("obtainFinal"))
            delegate.finalUserDataObtained(jsonReader.getFinalUserDataFromRestAPI());

        else
            delegate.uniqueDataObtained(jsonReader.getUniqueIdsFromRestAPI());
    }

    @Override
    protected String doInBackground(String... params) {

        this.task = params[1];

        obtainFinalData(params[0]);
            return FINAL_DATA;
    }

    public void obtainFinalData (String urlForServer) {

        try {

            // open a connection to the server storing the data:
            URL url = new URL (urlForServer);
            URLConnection connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            // TODO: maybe put this in a different method:

            // set up the buffered reader by using an input stream reader that utilizes the parameter
            InputStreamReader isr = new InputStreamReader(httpConnection.getInputStream(),"UTF-8");
            BufferedReader br = new BufferedReader(isr);

            // read the json data
            String currentJSONDataLine = br.readLine();

            while (currentJSONDataLine != null) {
                this.FINAL_DATA += currentJSONDataLine + "\n";
                currentJSONDataLine = br.readLine();
            }

        }

        catch (Exception e) {
            Log.w("ERROR: ","Problem Reading from JSON Server");
            e.printStackTrace();
        }
    }


    public void setDelegate (AsyncResponse del) {
        this.delegate = del;
    }

}
