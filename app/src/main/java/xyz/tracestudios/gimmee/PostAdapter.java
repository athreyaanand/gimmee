package xyz.tracestudios.gimmee;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.tracestudios.gimmee.firebasemodels.Post;
import xyz.tracestudios.gimmee.firebasemodels.User;
import xyz.tracestudios.gimmee.photocarousel.PhotoCarouselActivity;


public class PostAdapter extends ArrayAdapter<Post> {


    /////////////////////////

    FirebaseUser gimmeeUser;

    //Database
    FirebaseDatabase gimmeeDatabase;
    DatabaseReference gimmeeDatabaseRef;

    private FirebaseAuth gimmeeAuth;

    Context context;

    //private ViewPager postPicturePager;

    String uid;

    private LinearLayout postHeader;
    private LinearLayout postAbout;

    private TextView postDisplayName;
    private TextView postUsername;
    private TextView postTitleText;
    private TextView postConditionText;
    private TextView postDescriptionText;
    private TextView postInterestAmount;
    private TextView postCommentAmount;
    private TextView postLocationAmount;

    private ImageView postConditionCircle;
    private ImageView postInterestIcon;
    private ImageView postCommentIcon;
    private ImageView postLocationIcon;
    private ImageView postPreviewImage;

    private CircleImageView postProfileImage;

    private Button postBuyBttn;

    ArrayList<User> postAuthors;

    public PostAdapter(Context context, ArrayList<Post> posts, ArrayList<User> postAuthors) {
        super(context, 0, posts);
        this.context = context;
        this.postAuthors = postAuthors;
        gimmeeDatabase = FirebaseDatabase.getInstance();
        gimmeeDatabaseRef = gimmeeDatabase.getReference();
        gimmeeAuth = FirebaseAuth.getInstance();
        gimmeeUser = gimmeeAuth.getCurrentUser();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);

        //Grab post

        final Post post = getItem(position);

        uid = gimmeeUser.getUid();

        //TODO: DEBUG _ REMOVE PLS
        System.out.println("~~ USER ID: <" + uid + ">");

        bindViews(view);
        attachListeners(post);
        loadImages(post);
        bindPostData(post);


        if(postAuthors.size() != 0 &&  postAuthors.get(position) != null)
            bindAuthorData(postAuthors.get(position));
        else 
            bindFakeAuthorData();

        System.out.println("RETURNED THE VIEW MANNNNNNNNNNNNN");
        // return completed view to render
        return view;
    }

    private void bindFakeAuthorData() {

        //Update name view
        postDisplayName.setText("John Doe");

        //Update @handle view
        postUsername.setText("@johndoe");

        //update pci view
        postProfileImage.setImageResource(R.drawable.avatar);

    }

    private void bindAuthorData(User author) {

        //Update name view
        postDisplayName.setText(author.getName());

        //Update @handle view
        String handle = "@" + author.getUsername();
        postUsername.setText(handle);

        //update pci view
        //TODO: set profile image to dl one
        Picasso.with(context)
                .load(author.getProfileImageURL())
                .placeholder(R.drawable.arrow_left_black)
                .error(R.drawable.arrow_right_black)
                .into(postProfileImage);

    }

    private void bindPostData(Post post) {

        //Bind data to views
        postTitleText.setText(post.getTitle());
        postDescriptionText.setText(post.getDescription());
        postInterestAmount.setText(String.valueOf(post.getLikedBy().size() - 1));
        postCommentAmount.setText(String.valueOf(post.getComments()));

        //TODO: GET RID OF TEMP LOCAION
        postLocationAmount.setText(findDistance(post.getLocation(), getCurrentLocation()));

        //TODO: chnage color when liked/commented
        if(post.getLikedBy().contains(uid))
            postInterestIcon.setBackgroundResource(R.drawable.ic_interest_active);
        else
            postInterestIcon.setBackgroundResource(R.drawable.ic_interest_normal);


        Double price = post.getPrice() / 100.0;
        String priceText = "$" + String.format("%.2f", price);
        postBuyBttn.setText(priceText);

        switch (post.getCondition()){
            case 0: {
                postConditionText.setText("New");
                postConditionCircle.setImageResource(R.drawable.condition_circle_new);
                break;
            }
            case 2: {
                postConditionText.setText("Like New");
                postConditionCircle.setImageResource(R.drawable.condition_circle_like_new);
                break;
            }
            case 4: {
                postConditionText.setText("Slightly Used");
                postConditionCircle.setImageResource(R.drawable.condition_circle_slightly_used);
                break;
            }
            case 6: {
                postConditionText.setText("Used");
                postConditionCircle.setImageResource(R.drawable.condition_circle_used);
                break;
            }
            default: {
                postConditionText.setText("UKWN");
                postConditionCircle.setImageResource(R.drawable.condition_circle_unknown);
            }
        }

    }

    private List<Double> getCurrentLocation() {
        ArrayList<Double> cords = new ArrayList<>();
            cords.add(34.095245);
            cords.add(-118.332367);
        return cords;
    }

    private void loadImages(final Post post) {

        //Load images
        //Async Task so load first

        //load image
        Picasso.with(context)
                .load(post.getPhotoURLs().get(0))
                .placeholder(R.drawable.arrow_left_black)
                .error(R.drawable.arrow_right_black)
                .into(postPreviewImage);

    }

    private void attachListeners(final Post post) {

        // Create & Attach Listeners

        // Buy
        postBuyBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "BUY BUTTON PR33S3D",
                        Toast.LENGTH_SHORT).show();

                Intent postDetails = new Intent(context, PostDetailsActivity.class);
                postDetails.putExtra("postID", post.getKey());
                context.startActivity(postDetails);
            }
        });

        // Interest (Like)
        postInterestIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postInterestIcon.setBackgroundResource(R.drawable.ic_interest_active);

                if(post.getLikedBy().contains(uid))
                    actionUnlikePost(post);
                else
                    actionLikePost(post);
            }
        });

        // Comment
        postCommentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "comment PR33S3D",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Header | Profile
        postHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, "header profile PR33S3D",
                        Toast.LENGTH_SHORT).show();

                int[] startingLocation = new int[2];
                UserProfileActivity.startUserProfileFromLocation(startingLocation, (Activity) context);
            }
        });

        // About
        postAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "post about PR33S3D",
                        Toast.LENGTH_SHORT).show();

                Intent postDetails = new Intent(context, PostDetailsActivity.class);
                postDetails.putExtra("postID", post.getKey());
                context.startActivity(postDetails);
            }
        });

        // preview Images
        postPreviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoCarousel(post.getPhotoURLs());
            }
        });
    }

    private void bindViews(View view) {

        //Bind TextViews
        postDisplayName = (TextView) view.findViewById(R.id.postDisplayName);
        postUsername = (TextView) view.findViewById(R.id.postUsername);
        postTitleText = (TextView) view.findViewById(R.id.postTitleText);
        postConditionText = (TextView) view.findViewById(R.id.postConditionText);
        postDescriptionText = (TextView) view.findViewById(R.id.postDescriptionText);
        postInterestAmount = (TextView) view.findViewById(R.id.postInterestAmount);
        postCommentAmount = (TextView) view.findViewById(R.id.postCommentAmount);
        postLocationAmount = (TextView) view.findViewById(R.id.postLocationAmount);

        //Bind ImageViews
        postPreviewImage = (ImageView) view.findViewById(R.id.postPreviewImage);
        postConditionCircle = (ImageView) view.findViewById(R.id.postConditionCircle);
        postInterestIcon = (ImageView) view.findViewById(R.id.postInterestIcon);
        postCommentIcon = (ImageView) view.findViewById(R.id.postCommentIcon);
        postLocationIcon = (ImageView) view.findViewById(R.id.postLocationIcon);

        //Bind CircleImageView
        postProfileImage = (CircleImageView) view.findViewById(R.id.postProfileImage);

        //Bind Button
        postBuyBttn =(Button) view.findViewById(R.id.postBuyBttn);

        //Bind LinearLayout
        postHeader = (LinearLayout) view.findViewById(R.id.header);
        postAbout = (LinearLayout) view.findViewById(R.id.about);

    }

    private void actionUnlikePost(Post post){

        List<String> likedBy = post.getLikedBy();
        likedBy.remove(likedBy.indexOf(uid));
        gimmeeDatabaseRef.child("posts").child(post.getKey()).child("likedBy").setValue(likedBy);

        System.out.println("LIKE REMOVED FROM: " + post.getKey());

    }

    private void actionLikePost(Post post){

        List<String> likedBy = post.getLikedBy();
        likedBy.add(0, uid);
        gimmeeDatabaseRef.child("posts").child(post.getKey()).child("likedBy").setValue(likedBy);

        System.out.println("LIKE ADDED TO: " + post.getKey());

    }

    private void openPhotoCarousel(List<String> photoURLS){

        ArrayList<String> photoArrayList = new ArrayList<>();
        photoArrayList.addAll(photoURLS);

        Intent photoCarousel = new Intent(context, PhotoCarouselActivity.class);
        photoCarousel.putStringArrayListExtra("photoURLs", photoArrayList);
        context.startActivity(photoCarousel);

    }

    private String findDistance(List<Double> myCords, List<Double> postCords){

        double dist = kmToMiles(haversine(myCords, postCords));

        return String.format("%.2f", dist) + " miles";

    }

    private double kmToMiles(double km){
        return km * 0.621371;
    }
    /**
     * Calculates the distance in km between two lat/long points
     * using the haversine formula
     *
     * http://stackoverflow.com/a/18862550
     */
    private static double haversine(List<Double> myCords, List<Double> postCords) {

        double lat1 = myCords.get(0);
        double lat2 = postCords.get(0);

        double lng1 = myCords.get(1);
        double lng2 = postCords.get(1);

        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }


}
