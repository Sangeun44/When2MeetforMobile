package edu.upenn.cis350.g8.when2meetformobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Saniyah on 2/18/2018.
 * simple object to represent a meeting
 */


public class Meeting {

    private Set<User> users;
    private ArrayList<String> dates;
    private int high_time;
    private int low_time;
    private String name;
    private int owner;

    public Meeting() {}

    public Meeting(Set<User> users, ArrayList<String> dates, int high_time, int low_time, String name, int owner) {
        this.users = users;
        this.dates = dates;
        this.high_time = high_time;
        this.low_time = low_time;
        this.name = name;
        this.owner = owner;
    }

    public Set<User> getUsers() {
        return users;
    }

    public int getNumUsers() {
        return users.size();
    }

    public Map<Integer, HashSet<String>> getBestTimes() {
        Map<String, Integer> allTimes = new HashMap<String, Integer>();
        for (String date : dates) {
            for (int i = low_time; i < high_time; i++) {
                allTimes.put(date + " " + i, 0);
            }
        }

        for (User u: users) {
            for (String time : u.getMyTimes()) {
                allTimes.put(time, allTimes.get(time) + 1);
            }
        }

        Map<Integer, HashSet<String>> invertedTimes = new HashMap<Integer, HashSet<String>>();
        for (String key : allTimes.keySet()) {
            Integer value = allTimes.get(key);
            if (invertedTimes.containsKey(value)) {
                HashSet<String> times = invertedTimes.get(value);
                times.add(key);
            } else {
                HashSet<String> newSet = new HashSet<String>();
                newSet.add(key);
                invertedTimes.put(value, newSet);
            }
        }

        return invertedTimes;
    }
}
