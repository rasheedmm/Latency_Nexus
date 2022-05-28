package com.live.latency_nexus.Models;

public class User {
    private String id;
    private String username;
    private String imageurl;
    private String bio;
    private String name;
    private Boolean usertype;
    public String lati;
    public String longi;
    public String location;
    public String recommended;
    public String popular;
    public String userposts;
    public User(String id, String username, String imageurl, String bio, String name, Boolean usertype , String lati, String longi, String location, String recommended, String popular, String userposts) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.bio = bio;
        this.name = name;
        this.usertype=usertype;
        this.lati = lati;
        this.longi = longi;
        this.location=location;
        this.recommended=recommended;
        this.popular=popular;
        this.userposts=userposts;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getUsertype() {
        return usertype;
    }

    public void setUsertype(Boolean usertype) {
        this.usertype = usertype;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRecommended() {
        return recommended;
    }

    public void setRecommended(String recommended) {
        this.recommended = recommended;
    }

    public String getPopular() {
        return popular;
    }

    public void setPopular(String popular) {
        this.popular = popular;
    }

    public String getUserposts() {
        return userposts;
    }

    public void setUserposts(String userposts) {
        this.userposts = userposts;
    }
}
