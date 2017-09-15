package xyz.tracestudios.gimmee.firebasemodels;

import java.util.List;


public class User {

    String username;
    String key;
    String email;
    String name;
    String profileImageURL;
    String bio;

    int goal;
    int raised;

    List<String> posts;
    //List<String> likes;

    //Empty Constructor
    public User() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getRaised() {
        return raised;
    }

    public void setRaised(int raised) {
        this.raised = raised;
    }

//    public List<String> getLikes() {
//        return likes;
//    }

//    public void setLikes(List<String> likes) {
//        this.likes = likes;
//    }

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

}
