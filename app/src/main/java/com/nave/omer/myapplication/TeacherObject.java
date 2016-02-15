package com.nave.omer.myapplication;

import java.util.List;

/**
 * Created by omer on 14/02/16.
 */
public class TeacherObject {
    private String fullName;
    private byte[] profile;
    private List<String> teaches;
    private List<String> learns;
    private int rating;
    private String id;

    public TeacherObject(String fullName, byte[] image, List<String> teaches, List<String> learns, int rating, String id) {
        this.fullName = fullName;
        this.profile = image;
        this.teaches = teaches;
        this.learns = learns;
        this.rating = rating;
        this.id = id;
    }

    public String getName() {
        return this.fullName;
    }
    public byte[] getProfile() {
        return this.profile;
    }
    public List<String> getTeaches() {
        return this.teaches;
    }
    public List<String> getLearns() {
        return this.learns;
    }
    public int getRating() {
        return this.rating;
    }
    public String getId() {
        return this.id;
    }
}
