package xyz.tracestudios.gimmee.firebasemodels;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Comment {

    String key;
    String text;
    String authorID;

    public Comment() {
    }

    public Comment(String key, String text, String authorID) {
        this.key = key;
        this.text = text;
        this.authorID = authorID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }
}
