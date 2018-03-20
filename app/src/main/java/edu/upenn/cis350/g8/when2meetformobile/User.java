package edu.upenn.cis350.g8.when2meetformobile;


import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Saniyah on 2/18/2018.
 */

public class User {

    private String name;
    private ArrayList<String> myTimes;


    /**
     * basic constructor
     */
    public User() {}

    /**
     * Makes a new user given their name
     * @param name the String of the name of the user
     */
    public User(String name) {
        this.name = name;
        myTimes = new ArrayList<String>();
    }

    /**
     * Get the name of this User
     * @return name as String
     */
    @Exclude
    public String getName() {
        return this.name;
    }

    /**
     * Creates a User given their availability
     * Used for reading data from the database
     * @param myTimes Availability as a List of Strings
     */
    public User(ArrayList<String> myTimes) {
        this.myTimes = myTimes;
    }

    public User(String name, ArrayList<String> myTimes) {
        this.name = name;
        this.myTimes = myTimes;
    }

    /**
     * Determines whether times have been entered for this user
     * @return true if times were entered
     */
    @Exclude
    public boolean enteredTimes() {
        return (this.myTimes != null && !this.myTimes.isEmpty());
    }


    /**
     * Gets the Availability
     * @return availability as a List of Strings
     */
    public List<String> getMyTimes() {
        /*List<String> times = new ArrayList<String>();

        String start = "";

        for (String s: myTimes) {
            String[] parts = s.split(" ");
            if (parts[1].equals("S")) {
                start = s;
            }

            if (parts[1].equals("E")) {
                String date = parts[0];

                String[] startParts = start.split(" ");
                for (int i = Integer.parseInt(startParts[2].trim());
                        i < Integer.parseInt(parts[2].trim()); i++) {
                    times.add(date + " " + i);
                }

                start = "";
            }
        }*/

        return myTimes;
    }
}
