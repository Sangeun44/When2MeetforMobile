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
    private ArrayList<String> availability;


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
        availability = new ArrayList<String>();
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
     * @param availability Availability as a List of Strings
     */
    public User(ArrayList<String> availability) {
        this.availability = availability;
    }

    /**
     * Determines whether times have been entered for this user
     * @return true if times were entered
     */
    @Exclude
    public boolean enteredTimes() {
        return (this.availability != null && !this.availability.isEmpty());
    }

    /**
     * Gets the Availability
     * @return availability as a List of Strings
     */
    public List<String> getMyTimes() {
       return availability;
    }
}
