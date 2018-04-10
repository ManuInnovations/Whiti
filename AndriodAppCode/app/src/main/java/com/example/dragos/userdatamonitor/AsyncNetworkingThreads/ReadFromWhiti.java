package com.example.dragos.userdatamonitor.AsyncNetworkingThreads;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dragos.userdatamonitor.AsyncResponse;
import com.example.dragos.userdatamonitor.TrustAllCertifcates;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

// for the https stuff:


/**
 * This class will be responsible for reading the JSON user log data from the Whiti server
 * As one entire String
 *
 *
 * Created by Dragos on 9/25/17.
 */

public class ReadFromWhiti extends AsyncTask <String,Void,String>{

    // To be set as the String representing the JSON data obtained from the server:
    private String JSON_DATA = "";

    private AsyncResponse delegate = null;

    // TODO: remove once https certificate is signed externally:
    private TrustAllCertifcates trustAll = new TrustAllCertifcates();


    // before this thread starts executing, create a trust manager that trusts all https
    // certificates
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // TODO: remove once https certificate is signed externally:
        this.trustAll.trustAllCertificates();
    }

    /**
     * This method will be called with one URL (the one for the JSON Server)
     * and will execute readDataFromServer(), in the background with the JSON Server URL
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(String[] params) {
        readDataFromServer(params[0]);
        return this.JSON_DATA;
    }

    /**
     * This runs on the UI thread after the thread in the background has finished executing
     * I will print out the JSON data obtained:
     * @param result
     */
    @Override
    protected void onPostExecute (String result) {

        // once this thread has finished obtaining the json data from the server, create a JSONArray
        // holding that data.
        // If there was no data obtained from the server, an expetion should be thrown, and an empty JSON array
        // should be returned:

        JSONArray jsonArray = new JSONArray();

        try {
             jsonArray = new JSONArray(result.replace("'","\"").replace("u",""));
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        finally {
            delegate.currentUsageDataObtained(jsonArray);
        }

    }

    // ACTUALL BEHAVIOUR THAT GOES ON IN THIS BACKGROUND THREAD:

    /**
     * read the JSON data from the server:
     * @param urlForJSONServer
     */
    public void readDataFromServer (String urlForJSONServer) {

        try {

            // open a connection to the server storing the data:
            URL url = new URL (urlForJSONServer);
            URLConnection connection = url.openConnection();

            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            // TODO: remove once https certificate is signed externally:
            httpsConnection.setHostnameVerifier(this.trustAll.getDummyVerifier());

            // obtain the JSON Data from that sever here:
            InputStream inputStream = httpsConnection.getInputStream();
            parseJSONData(inputStream);
        }

        catch (Exception e) {
            Log.w("ERROR: ","Problem Reading from JSON Server");
            e.printStackTrace();
        }
    }

    /**
     * Take in an input stream from an httpsConnection, and read obtain the JSON string from the
     * input string, and build up the JSON_DATA
     * @param is
     */
    public void parseJSONData (InputStream is) {

        try {
            // set up the buffered reader by using an input stream reader that utilizes the parameter
            InputStreamReader isr = new InputStreamReader(is,"UTF-8");
            BufferedReader br = new BufferedReader(isr);

            // read the json data
            String currentJSONDataLine = br.readLine();

            while (currentJSONDataLine != null) {
                this.JSON_DATA += currentJSONDataLine + "\n";
                currentJSONDataLine = br.readLine();
            }
        }

        catch (Exception e) {
            Log.w("ERROR: ","Problem Parsing JSON Data");
            e.printStackTrace();
        }
    }

    // SETTER:

    public void setDelegate (AsyncResponse del) {
        this.delegate = del;
    }

}
