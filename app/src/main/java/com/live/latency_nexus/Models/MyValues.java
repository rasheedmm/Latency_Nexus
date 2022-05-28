package com.live.latency_nexus.Models;

public class MyValues {
    private int count;
    private int posts;
    public MyValues(int count, int posts) {
        this.count = count;
        this.posts=posts;
    }

    public MyValues() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }
}
