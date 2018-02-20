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

    private ArrayList<String> availability;
    private String name;

    public User() {}

    public User(String name) {
        this.name = name;
        availability = new ArrayList<String>();
    }

    @Exclude
    public String getName() {
        return this.name;
    }

    public User(ArrayList<String> availability) {
        this.availability = availability;
    }

    @Exclude
    public boolean enteredTimes() {
        return (this.availability != null && !this.availability.isEmpty());
    }

    public List<String> getMyTimes() {
       return availability;
    }
}
