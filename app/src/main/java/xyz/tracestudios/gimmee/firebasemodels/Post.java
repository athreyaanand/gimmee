package xyz.tracestudios.gimmee.firebasemodels;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Post {

    String sellerUID;
    String key;
    String title;
    String description;

    int condition;
    int price;
    List<String> likedBy;
    int comments;

    long timestamp;

    List<String> photoURLs;
    List<Double> location;

    boolean isSold;

    public Post() {}

    public String getSellerUID() {
        return sellerUID;
    }

    public void setSellerUID(String sellerUID) {
        this.sellerUID = sellerUID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getPhotoURLs() {
        return photoURLs;
    }

    public void setPhotoURLs(List<String> photoURLs) {
        this.photoURLs = photoURLs;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }
}
