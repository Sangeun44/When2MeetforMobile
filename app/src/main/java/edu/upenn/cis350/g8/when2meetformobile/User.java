package edu.upenn.cis350.g8.when2meetformobile;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public Set<String> getMyTimes() {
        Set<String> allTimes = new HashSet<String>();

        for (String date : times.keySet()) {
            for (int i : times.get(date)) {
                allTimes.add(date + " " + i);
            }
        }

        return allTimes;
    }
}
