package edu.upenn.cis350.g8.when2meetformobile;

import java.util.ArrayList;
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
}
