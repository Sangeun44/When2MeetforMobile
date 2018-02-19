package edu.upenn.cis350.g8.when2meetformobile;


import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Saniyah on 2/18/2018.
 */

public class User {

    private Map<String, ArrayList<Integer>> times;

    public User() {}

    public User(Map<String, ArrayList<Integer>> times) {
        this.times = times;
    }

    public boolean enteredTimes() {
        return (this.times != null && !this.times.isEmpty());
    }
}
