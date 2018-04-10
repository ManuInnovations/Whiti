package com.example.dragos.userdatamonitor.DataDisplaying;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * This class will be responsible for the logic behind taking the hash map (with all the user data)
 * and creating the formatted display sting that shows how many users there are etc
 *
 * Created by Dragos on 10/5/17.
 */

public class DataDisplayer {

    /**
     * Take in the hash map containing all the user data (loaded from the JSON data on the server)
     * and a user selected date, and return a formatted string displaying the user data stats
     *
     * @param userData
     * @param selectedDate
     * @return
     */
    public String getDataDisplay (Map<String,List<String>> userData,String selectedDate) {
        String result = "";

        int interpreters = 0;
        int listeners = 0;

        // get the list of users who used the app on the selected date:
        List<String> users = userData.get(selectedDate);

        // if no users used the app on this date:
        if (users == null) {
            result = "No users used Whiti on " + selectedDate;
        }

        else {
            for (String currentUser : users) {
                if (currentUser.equals("listener"))
                    listeners++;
                if (currentUser.equals("interpreter"))
                    interpreters++;
            }

            result += "User Data for " + selectedDate + "\n\n";
            result += "Listeners : " + listeners + "\n\n";
            result += "Interpreters : " + interpreters + "\n\n";
        }


        result += displayMonthlyStatistic(userData,selectedDate);

        return result;
    }

    /**
     * Iterate over the HashMap to find the total number of users (listeners and Interpreters)
     * who used the app on the same month and year of the currently selected date (from the calander)
     *
     * @param selectedDate
     * @return
     */
    public String displayMonthlyStatistic (Map<String,List<String>> userData,String selectedDate) {

        String result = "";

        DateFormatSymbols dateFormatter = new DateFormatSymbols();
        // the month and year for the currently selected date:
        int month = Integer.parseInt(selectedDate.split("/")[0]);
        int year = Integer.parseInt(selectedDate.split("/")[2]);

        result += "\n\n User Data for " + dateFormatter.getMonths()[month-1] + " " + year + "\n\n";

        // calculate the total number of listeners and interpreters for this dates month and year:
        int interpForMonth = 0;
        int listnForMonth = 0;

        for (String currentDate : userData.keySet()) {

            // if we have a date with the same month and year as the month/year for the currently selected date:

            int currentDateMonth = Integer.parseInt(currentDate.split("/")[0]);
            int currentDateYear = Integer.parseInt(currentDate.split("/")[2]);

            if (currentDateMonth == month && currentDateYear == year) {

                List<String> usersForCurrentDate = userData.get(currentDate);

                for (String user: usersForCurrentDate) {
                    if (user.equals("listener"))
                        listnForMonth++;
                    if (user.equals("interpreter"))
                        interpForMonth++;
                }
            }
        }

        result += "Listeners : " + listnForMonth + "\n\n";
        result += "Interpreters : " + interpForMonth + "\n\n";

        return result;
    }


}
