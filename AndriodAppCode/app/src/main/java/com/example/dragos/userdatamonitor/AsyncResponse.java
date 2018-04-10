package com.example.dragos.userdatamonitor;

import org.json.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * Created by Dragos on 9/25/17.
 */

// TODO : Rename this interface as it is used in other stuff not just the background thread:

public interface AsyncResponse {

    // for when the background thread for connecting to the https server and GETing the usage data for that one session
    // has finished:
    public void currentUsageDataObtained (JSONArray output);

    // for when the final data from the rest api is returned from the final networking thread:
    public void finalUserDataObtained (Map<String,List<String>> output);

    public void uniqueDataObtained(List<String> ids);

    // for after the user selects their date:
    public void datePicked (String selectedDate);
}
