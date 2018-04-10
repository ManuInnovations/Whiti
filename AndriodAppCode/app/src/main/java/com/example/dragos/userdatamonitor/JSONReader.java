package com.example.dragos.userdatamonitor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * This class will take one entire JSON string from the ReadFromWhiti
 * and parse it, storing the data in collection(s)
 * <p>
 * Created by Dragos on 9/25/17.
 */

public class JSONReader {

    private String JSON_DATA;

    //TODO: may change this later, if Vini wants more detailed info other than just which users use the app on which day
    private Map<String, List<String>> finalUserDataFromRestAPI = new HashMap<String, List<String>>();

    // the unique ids for JSON users:
    private List<String> ids = new ArrayList<>();

    public JSONReader(String data) {

        this.JSON_DATA = data;
    }


    /**
     * For loading the hashmap with the user data obtained from the rest api:
     *
     */
    public void loadDataFromRestAPI() {

        try {

            // this will be the JSON array containing the most up to date data from the rest api:
            JSONArray jsonArray = new JSONArray(this.JSON_DATA);

            for (int i = 0; i < jsonArray.length(); i++) {
                // every object from the rest api will be an array of JSON objects:
                JSONObject currentObject = jsonArray.getJSONObject(i);
                // obtain the current array of JSON objects:
                JSONArray arr = currentObject.getJSONArray("dataFromWhitiServer");
                // now load the JSON objects in that array, into the hash map:
                loadJsonArray(arr);
            }

        } catch (JSONException e) {
            Log.w("JSON ERROR", "Problem parsing the JSON Data");
            e.printStackTrace();
        }
    }

    /**
     * Takes in a JSON array, and loads its contents into this objects HashMap
     *
     * @param arr
     */
    public void loadJsonArray(JSONArray arr) {

        try {
            for (int i = 0; i < arr.length(); i++) {

                JSONObject currentObject = arr.getJSONObject(i);

                // first get the unique id for this user and add it to the list:
                this.ids.add(currentObject.getString("id"));

                String userType = currentObject.getString("serType");
                String dateInformation = currentObject.getString("dateInformation");

                // get all the users who have also used the app on this date:
                List<String> usersForCurrentDate = this.finalUserDataFromRestAPI.get(dateInformation);


                // if there are no other users for this date, make a list with one user, and
                if (usersForCurrentDate == null) {
                    List<String> user = new ArrayList<>();
                    user.add(userType);
                    this.finalUserDataFromRestAPI.put(dateInformation, user);
                }

                // otherwise add the user to the list of users who have uded the app on this date:
                else {
                    usersForCurrentDate.add(userType);
                    this.finalUserDataFromRestAPI.put(dateInformation, usersForCurrentDate);
                }
            }
        } catch (JSONException e) {
            Log.w("JSON ERROR", "Problem parsing the JSON Data");
            e.printStackTrace();
        }

    }

    // CODE FOR ENSURING THAT ONLY THE NEWEST USER DATA IS SAVED, SO PREVIOUSLY SAVED DATA IS NOT SAVED TWICE:

    /**
     * Takes in a JSON array representing the newest state of the JSON data retrieved from the Whiti server
     * and filters out all the JSON objects that have already been saved.
     * <p>
     * The result is a JSON array with only the newest data
     *
     * @param userData
     * @param ids
     * @return
     */
    public static JSONArray obtainNewestObjects(JSONArray userData, List<String> ids) {

        JSONArray newestData = new JSONArray();

        try {
            for (int i = 0; i < userData.length(); i++) {

                JSONObject obj = userData.getJSONObject(i);
                String idForCurrentObject = obj.getString("id");

                if (!ids.contains(idForCurrentObject))
                    newestData.put(obj);
            }
        } catch (JSONException e) {
            Log.w("WARNING", "problem with obtainNewestObjects()");
        }

        return newestData;
    }

    // GETTERS

    public Map<String, List<String>> getFinalUserDataFromRestAPI() {
        return this.finalUserDataFromRestAPI;
    }

    public List<String> getUniqueIdsFromRestAPI() {
        return this.ids;
    }


}
