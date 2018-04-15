package edu.upenn.cis350.g8.when2meetformobile;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;               // name of the user
    private String userName;
    private String phoneNumber;
    private String description;
    private String image;
    private ArrayList<String> myTimes; // availability as list of datetime strings

    /**
     * Makes a new empty user.
     */
    public User() {}

    /**
     * Makes a new user given their name.
     *
     * @param name name of the user
     */
    public User(String name) {
        this.name = name;
        myTimes = new ArrayList<>();
        phoneNumber = "";
        description = "";
        image = "";
    }

    /**
     * Creates a new {@code User} given their availability.
     *
     * @param myTimes availability as a list of datetime strings
     */
    public User(ArrayList<String> myTimes) {
        this.myTimes = myTimes;
    }

    /**
     * Creates a new {@code User} given a name and availability.
     *
     * @param name name of the user
     * @param myTimes availability as a list of datetime strings
     */
    public User(String name, ArrayList<String> myTimes) {
        this.name = name;
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

    public String getUserName() {
        return this.userName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImage() {return this.image;}

    public void setName(String name) {this.name = name;}

    public void setUserName(String name) {this.userName = name;}

    public void setNumber(String number) {phoneNumber = number;}

    public void setDescription(String description) {this.description = description;}

    public void setImage(String image) {this.image = image;}

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
