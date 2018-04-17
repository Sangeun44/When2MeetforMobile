package edu.upenn.cis350.g8.when2meetformobile;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evie on 4/16/18.
 */

public class InternalUser {
    private String name;               // name of the user
    private ArrayList<String> myTimes; // availability as list of datetime strings

    /**
     * Makes a new empty user.
     */
    public InternalUser() {}

    /**
     * Creates a new {@code User} given their availability.
     *
     * @param myTimes availability as a list of datetime strings
     */
    public InternalUser(ArrayList<String> myTimes) {
        this.myTimes = myTimes;
    }


    /**
     * Get the name of this User
     *
     * @return name as String
     */
    @Exclude
    public String getName() {
        return this.name;
    }


    /**
     * Returns true if the user has entered their available times.
     *
     * @return true if the user entered times, false otherwise
     */
    @Exclude
    public boolean enteredTimes() {
        return myTimes != null && !myTimes.isEmpty();
    }

    /**
     * Returns the availability of the user as a list of datetime strings.
     *
     * @return availability as a list of datetime strings
     */
    public List<String> getMyTimes() {
        return myTimes;
    }
}
