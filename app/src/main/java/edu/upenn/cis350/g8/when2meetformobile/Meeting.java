package edu.upenn.cis350.g8.when2meetformobile;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Meeting {
    private Map<String, User> users;
    private List<String> dates;

    private int high_time;
    private int low_time;

    private String name;
    private String owner;

    /**
     * Makes a new empty meeting.
     */
    public Meeting() {}

    /**
     * Constructs a new meeting from all required parameters.
     *
     * @param users collection of participating users
     * @param dates list of meeting times as datetime strings
     * @param high_time latest meeting time
     * @param low_time earliest meeting time
     * @param name meeting name
     * @param owner userID of the owner
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
     * Returns all possible dates for the meeting as a list of datetime strings.
     *
     * @return dates list of possible dates for the meeting
     */
    public List<String> getDates() {
        return dates;
    }

    /**
     * Returns the latest time the meeting may go until.
     *
     * @return high_time latest time for the meeting
     */
    public int getHigh_time() {
        return high_time;
    }

    /**
     * Returns the earliest time the meeting may start.
     *
     * @return low_time earliest time for the meeting
     */
    public int getLow_time() {
        return low_time;
    }

    /**
     * Returns the name of the meeting.
     *
     * @return name meeting name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns userID of the owner of this meeting.
     *
     * @return owner userID of the meeting's owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the collection of users.
     *
     * @return users map of users participating in this meeting
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Adds a user to the collection of participating users by creating
     * a new {@code User} object.
     *
     * @param id userID of the new user
     */
    public void addUsers(String id) {
        if(users == null) {
            users = new HashMap<String, User>();
        }
        users.put(id, new User());
    }

    /**
     * Adds a user to the collection of participating users from an
     * existing {@code User} object.
     *
     * @param id userID of the new user
     * @param use {@code User} object associated with the user
     */
    public void addUsers(String id, User use) {
        if (users == null) {
            users = new HashMap<String, User>();
            users.put(id, use);
        }
        users.put(id, use);
    }

    /**
     * Determines if the user is a participant of the meeting, but is not the owner.
     *
     * @param userID ID of the user to lookup
     * @return true if the user has joined the meeting but isn't the owner
     */
    @Exclude
    public boolean containsUserNotAsOwner(String userID) {
        return !userID.equals(owner) && users.keySet().contains(userID);
    }

    /**
     * Returns the number of participating users for this meeting.
     *
     * @return number of users
     */
    @Exclude
    public int getNumUsers() {
        return users.size();
    }

    /**
     * Gets the best times to meet based on all user responses and preferences.
     *
     * @return mapping of datetime preference counts to datetime strings
     */
    @Exclude
    public Map<Integer, HashSet<String>> getBestTimes() {
        Map<String, Integer> allTimes = new HashMap<String, Integer>();
        if (low_time == 24) {
            low_time = 0;
        }

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
