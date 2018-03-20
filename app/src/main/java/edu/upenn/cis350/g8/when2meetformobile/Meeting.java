package edu.upenn.cis350.g8.when2meetformobile;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Saniyah on 2/18/2018.
 * simple object to represent a meeting
 */


public class Meeting {

    private Map<String, User> users;
    private List<String> dates;
    private int high_time;
    private int low_time;
    private String name;
    private String owner;

    /**
     * Generic constructor
     */
    public Meeting() {}

    /**
     * Constructs a meeting with all parameters
     * @param users the Map of User objects
     * @param dates the List of dates as Strings
     * @param high_time the max times
     * @param low_time the min time
     * @param name the name associated with the meeting
     * @param owner the owner's ID
     */
    public Meeting(Map<String, User> users, List<String> dates, int high_time, int low_time,
                   String name, String owner) {
        this.users = users;
        this.dates = dates;
        this.high_time = high_time;
        this.low_time = low_time;
        this.name = name;
        this.owner = owner;
    }

    /**
     * Gets the dates
     * @return dates
     */
    public List<String> getDates() {
        return dates;
    }

    /**
     * gets the High time
     * @return high_time
     */
    public int getHigh_time() {
        return high_time;
    }

    /**
     * gets the low_time
     * @return low_time
     */
    public int getLow_time() {
        return low_time;
    }

    /**
     * gets the Name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * returns the Owner
     * @return owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * gets the Map of Users
     * @return users
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Adds a user to the map
     */
    public void addUsers(String id) {
        if(users == null) {
            users = new HashMap<String, User>();
        }
        users.put(id, new User());
    }

    public void addUsers(String id, User use) {
        if (users == null) {
            users = new HashMap<String, User>();
            users.put(id, use);
        }
        users.put(id, use);
    }

    /**
     * determines if the Meeting contains a particular user when the user is not the owner
     * @param userID the ID of the user to find
     * @return true if the user has joined the meeting but isn't the creator
     */
    @Exclude
    public boolean containsUserNotAsOwner(String userID) {

        if (userID.equals(owner)) {
            return false;
        }

        if (users.keySet().contains(userID)) {
            return true;
        }

        return false;
    }

    /**
     * finds the number of uses
     * @return int representing the number of users
     */
    @Exclude
    public int getNumUsers() {
        return users.size();
    }

    /**
     * gets a Map that helps to determine the best times to meet
     * by showing how many people are available at each time
     * @return Map<Integer, HashSet<String>> of the number of appearances to all dates that appear
     * that many times in the database as free
     */
    @Exclude
    public Map<Integer, HashSet<String>> getBestTimes() {
        Map<String, Integer> allTimes = new HashMap<String, Integer>();
        for (String date : dates) {
            for (int i = low_time; i < high_time; i++) {
                allTimes.put(date + " " + i, 0);
            }
        }

        for (User u: users.values()) {
            List<String> times = u.getMyTimes();
            if (times != null) {
                for (String time : times) {
                    allTimes.put(time, allTimes.get(time) + 1);
                }
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
