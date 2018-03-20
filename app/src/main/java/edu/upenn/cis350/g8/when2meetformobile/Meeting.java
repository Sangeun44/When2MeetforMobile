package edu.upenn.cis350.g8.when2meetformobile;

import com.google.firebase.firestore.Exclude;

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

    private HashMap<String, User> users;
    private ArrayList<String> dates;
    private int high_time;
    private int low_time;
    private String name;
    private String owner;

    public Meeting() {}

    public Meeting(HashMap<String, User> users, ArrayList<String> dates, int high_time, int low_time, String name, String owner) {
        this.users = users;
        this.dates = dates;
        this.high_time = high_time;
        this.low_time = low_time;
        this.name = name;
        this.owner = owner;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public int getHigh_time() {
        return high_time;
    }

    public int getLow_time() {
        return low_time;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public void addUsers(String id) {
        if(users == null) {
            users = new HashMap<String, User>();
        }
        users.put(id, new User());
    }

    @Exclude
    public int getNumUsers() {
        return users.size();
    }

    @Exclude
    public Map<Integer, HashSet<String>> getBestTimes() {
        Map<String, Integer> allTimes = new HashMap<String, Integer>();
        for (String date : dates) {
            for (int i = low_time; i < high_time; i++) {
                allTimes.put(date + " " + i, 0);
            }
        }

        for (User u: users.values()) {
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
