package xyz.tracestudios.gimmee;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.tracestudios.gimmee.firebasemodels.Comment;
import xyz.tracestudios.gimmee.firebasemodels.Post;
import xyz.tracestudios.gimmee.firebasemodels.User;
import xyz.tracestudios.gimmee.photocarousel.PhotoCarouselActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PostDetailsActivity extends AppCompatActivity {


    //Bind Views using ButterKnife

    //Toolbar
    @BindView(R.id.postDetailsToolbar)
    Toolbar toolbar;

    //Root View
    @BindView(R.id.postDetailsScrollRoot)
    ScrollView scrollRoot;

    //Header View
    @BindView(R.id.postDetailsHeaderPreviewImage)
    ImageView headerPreviewImage;
    @BindView(R.id.namecardProfileImage)
    CircleImageView headerNamecardProfileImage;
    @BindView(R.id.namecardUsername)
    TextView headerNamecardUsername;
    @BindView(R.id.namecardName)
    TextView headerNamecardName;

    //About View
    @BindView(R.id.postDetailsAboutConditionCircle)
    ImageView aboutConditionCircle;
    @BindView(R.id.postDetailsAboutConditionText)
    TextView aboutConditionText;
    @BindView(R.id.postDetailsAboutTitle)
    TextView aboutTitle;
    @BindView(R.id.postDetailsAboutDescription)
    TextView aboutDescription;

    //Meta - Interest View
    @BindView(R.id.postDetailsMetaInterestIcon)
    ImageView metaInterestIcon;
    @BindView(R.id.postDetailsMetaInterestAmount)
    TextView metaInterestAmount;

    //Meta - Distance View
    @BindView(R.id.postDetailsMetaDistanceIcon)
    ImageView metaDistanceIcon;
    @BindView(R.id.postDetailsMetaDistanceAmount)
    TextView metaDistanceAmount;

    //Meta - Photo View
    @BindView(R.id.postDetailsMetaPhotosIcon)
    ImageView metaPhotosIcon;
    @BindView(R.id.postDetailsMetaPhotosAmount)
    TextView metaPhotosAmount;

    //Buy View
    @BindView(R.id.postDetailsBuyButton)
    Button buyButton;
    @BindView(R.id.postDetailsBuyPrice)
    TextView buyPrice;

    //Comment
    @BindView(R.id.postDetailsCommentHolder)
    ListView commentHolder;
    @BindView(R.id.postDetailsCommentEdit)
    EditText commentEdit;
    @BindView(R.id.postDetailsCommentFAB)
    FloatingActionButton commentFAB;
    @BindView(R.id.postDetailsCommentAmount)
    TextView commentAmount;

    //Firebase - Database & Ref & Auth
    private FirebaseDatabase gimmeeDatabase;
    private DatabaseReference gimmeeDatabaseRef;
    private FirebaseAuth gimmeeAuth;

    LocationManager locationManager;

    //List of comments w/ authors
    ArrayList<Comment> comments;
    ArrayList<User> commentAuthors;

    //Custom ArrayAdapter
    CommentAdapter commentAdapter;

    //ID of current post
    String postID;

    //Context as needed
    Context context;

    //ID of current user (you)
    String myUserID;

    LocationListener locationListener;

    List<Double> cords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);

        //Bind Views in class header
        ButterKnife.bind(this);

        //Set context
        context = getApplicationContext();

        //Get PostID from calling activity
        postID = getIntent().getStringExtra("postID");

        //Update Toolbar
        toolbar.setTitle("Posts");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Grab Firebase Instances
        gimmeeDatabase = FirebaseDatabase.getInstance();
        gimmeeDatabaseRef = gimmeeDatabase.getReference();
        gimmeeAuth = FirebaseAuth.getInstance();

        //Get current user ID
        myUserID = gimmeeAuth.getCurrentUser().getUid();

        //Create arrays
        comments = new ArrayList<>();
        commentAuthors = new ArrayList<>();

        cords = new ArrayList<>();
        cords.add(34.402332);
        cords.add(-118.597579);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                cords.clear();
                cords.add(location.getLatitude());
                cords.add(location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        //Bind fake data while real data loads
        bindFakePostData();
        bindFakeProfileData();

        //Prepare Comment Adapter
        readyCommentAdapter();

        //Get real post data
        getPostData(postID);

        //Get real comment data
        getCommentData(postID);
    }

    private void readyCommentAdapter() {
        //Create new comment adapter w/ above arrays
        commentAdapter = new CommentAdapter(this, comments, commentAuthors);

        //Bind adapter to layout
        commentHolder.setAdapter(commentAdapter);

        //Attach Listener to enable scrolling
        commentHolder.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    private void bindFakeProfileData() {
        //Set views w/ "fake" data while we grab the "real" data from Firebase
        headerNamecardProfileImage.setImageResource(R.drawable.placeholder_avatar);
        headerNamecardName.setText("John Doe");
        headerNamecardUsername.setText("@username");
    }

    private void bindFakePostData() {
        //Set views w/ "fake" data while we grab the "real" data from Firebase

        //About
        aboutConditionCircle.setImageResource(R.drawable.condition_circle_unknown);
        aboutConditionText.setText("UNKWN");
        aboutTitle.setText("Title");
        aboutDescription.setText("About this item...");

        //Meta - Interest
        metaInterestIcon.setImageResource(R.drawable.ic_interest_normal);
        metaInterestAmount.setText("3 Interested");

        //Meta - Distance
        metaDistanceIcon.setImageResource(R.drawable.ic_map_pin);
        metaDistanceAmount.setText("0.0 miles");

        //Meta - Photos
        metaPhotosIcon.setImageResource(R.drawable.ic_menu_gallery);
        metaPhotosAmount.setText("View 0 photos");

        //Buy
        buyButton.setText("Gimmee That!");
        buyPrice.setText("$0.00");
    }

    //Create and attach listeners for various elements
    private void attachListeners(final Post post) {

        //Action - Interest (Like)
        metaInterestIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Like or Unlike
                if (post.getLikedBy().contains(myUserID))
                    actionUnlikePost(post);
                else
                    actionLikePost(post);
            }
        });

        //Action - Map Intent
        metaDistanceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapsIntent(post.getLocation(), post);
            }
        });

        //Action - Open PhotoCarousel
        metaPhotosIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoCarousel(post.getPhotoURLs());
            }
        });

        //Action - Buy Item
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PostDetailsActivity.this)
                        .setTitle("Buy Item")
                        .setMessage("Are you sure you want to buy this??")
                        .setIcon(R.drawable.ic_logout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                new AlertDialog.Builder(PostDetailsActivity.this)
                                        .setTitle("Congrats!")
                                        .setMessage("The seller has reserved the item for you, go to his location and complete the transaction!")
                                        .setIcon(R.drawable.ic_logout)
                                        .setPositiveButton("Sounds Good!", null)
                                        .show();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        //Action - Leave Comment
        commentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionMakeComment(commentEdit.getText().toString(), post.getKey());
                commentEdit.setText("");
                commentEdit.clearFocus();
                commentHolder.smoothScrollToPosition(0);

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Hide:
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });

        //Action - Open PhotoCarousel
        headerPreviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoCarousel(post.getPhotoURLs());
            }
        });


    }

    private void actionMakeComment(String text, String postKey) {

        //Make sure comment is not empty
        if (text.length() == 0) return;

        //Generate "push" key
        String pushKey = gimmeeDatabaseRef.child("post_comments").child(postKey).push().getKey();

        //Create new comment w/ data
        Comment comment = new Comment(pushKey, text, myUserID);

        //Make comment @ pushkey location
        gimmeeDatabaseRef.child("post_comments").child(postKey).child(pushKey).setValue(comment);

        //Notify view to update
        //commentAdapter.notifyDataSetChanged();

        //Update "comment" count
        updatePostCommentCount(postKey);
    }

    //Increments comment amount for given post
    private void updatePostCommentCount(String postKey) {

        //
        gimmeeDatabaseRef.child("posts").child(postKey).child("comments").setValue(comments.size() + 1);
        commentAdapter.notifyDataSetChanged();

    }

    private void openMapsIntent(List<Double> location, Post post) {

        Toast.makeText(context, "Opening Maps @ " + location.get(0), Toast.LENGTH_SHORT).show();

        Double[] cords = {location.get(0), location.get(1)};

        Intent mapsIntent = new Intent(this, MapsActivity.class);
        mapsIntent.putExtra("title", post.getTitle());
        mapsIntent.putExtra("cords", cords);

        startActivity(mapsIntent);

    }

    private void actionUnlikePost(Post post) {

        List<String> likedBy = post.getLikedBy();
        likedBy.remove(likedBy.indexOf(myUserID));
        gimmeeDatabaseRef.child("posts").child(post.getKey()).child("likedBy").setValue(likedBy);

        System.out.println("LIKE REMOVED FROM: " + post.getKey());

    }

    private void actionLikePost(Post post) {

        List<String> likedBy = post.getLikedBy();
        likedBy.add(0, myUserID);
        gimmeeDatabaseRef.child("posts").child(post.getKey()).child("likedBy").setValue(likedBy);

        System.out.println("LIKE ADDED TO: " + post.getKey());

    }

    private void openPhotoCarousel(List<String> photoURLS) {

        ArrayList<String> photoArrayList = new ArrayList<>();
        photoArrayList.addAll(photoURLS);

        Intent photoCarousel = new Intent(this, PhotoCarouselActivity.class);
        photoCarousel.putStringArrayListExtra("photoURLs", photoArrayList);
        photoCarousel.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(photoCarousel);

    }

    private void getPostData(String postID) {

        DatabaseReference postRef = gimmeeDatabase.getReference("posts").child(postID);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("~~~~~~~~Data: " + dataSnapshot.toString());

                Post post = dataSnapshot.getValue(Post.class);
                updatePostData(post);
                attachListeners(post);
                getPostAuthorData(post.getSellerUID());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getPostAuthorData(String UID) {

        System.out.println("Grabbing Post Author Data For: " + UID);

        DatabaseReference userRef = gimmeeDatabase.getReference("users").child(UID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Post Author Data: " + dataSnapshot.toString());
                updatePostAuthorData(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updatePostAuthorData(User author) {

        Picasso.with(context)
                .load(author.getProfileImageURL())
                .placeholder(R.drawable.arrow_left_black)
                .error(R.drawable.arrow_right_black)
                .into(headerNamecardProfileImage);

        headerNamecardName.setText(author.getName());
        headerNamecardUsername.setText(author.getUsername());
    }

    private void updatePostData(Post post) {

        Picasso.with(context)
                .load(post.getPhotoURLs().get(0))
                .placeholder(R.drawable.arrow_left_black)
                .error(R.drawable.arrow_right_black)
                .into(headerPreviewImage);

        toolbar.setTitle(post.getTitle());

        switch (post.getCondition()) {
            case 0: {
                aboutConditionText.setText("New");
                aboutConditionCircle.setImageResource(R.drawable.condition_circle_new);
                break;
            }
            case 2: {
                aboutConditionText.setText("Like New");
                aboutConditionCircle.setImageResource(R.drawable.condition_circle_like_new);
                break;
            }
            case 4: {
                aboutConditionText.setText("Slightly Used");
                aboutConditionCircle.setImageResource(R.drawable.condition_circle_slightly_used);
                break;
            }
            case 6: {
                aboutConditionText.setText("Used");
                aboutConditionCircle.setImageResource(R.drawable.condition_circle_used);
                break;
            }
            default: {
                aboutConditionText.setText("UKWN");
                aboutConditionCircle.setImageResource(R.drawable.condition_circle_unknown);
            }
        }

        aboutTitle.setText(post.getTitle());
        aboutDescription.setText(post.getDescription());

        if (post.getLikedBy().contains(myUserID))
            metaInterestIcon.setBackgroundResource(R.drawable.ic_interest_active);
        else
            metaInterestIcon.setBackgroundResource(R.drawable.ic_interest_normal);


        String likeText = (post.getLikedBy().size() - 1) + " likes";
        metaInterestAmount.setText(likeText);


        metaDistanceIcon.setBackgroundResource(R.drawable.arrow_right_black);

        String mileText = findDistance(getCurrentLocation(), post.getLocation()) + "miles";
        metaDistanceAmount.setText(mileText);


        metaPhotosIcon.setBackgroundResource(R.drawable.ic_menu_gallery);


        String photoText = post.getPhotoURLs().size() + " photos";
        metaPhotosAmount.setText(photoText);


        buyPrice.setText(formatPrice(post.getPrice()));
        buyButton.setText("Gimmee That!");

        System.out.println(post.getComments() + " SIZE");

        commentAmount.setText(String.valueOf(post.getComments()));

    }

    private List<Double> getCurrentLocation() {
        return cords;
    }

    private String formatPrice(int price) {
        return "$" + String.format("%.2f", price / 100.0);
    }

    private void getCommentData(String postID) {
        // Read from the database
        DatabaseReference postRef = gimmeeDatabase.getReference("post_comments").child(postID);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();

                System.out.println("=================");

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    System.out.println("Comment Data: " + data.toString());

                    Comment comment = data.getValue(Comment.class);
                    comments.add(0, comment);

                    System.out.println("CURRENT AUTHOR ID: " + comment.getAuthorID());

                    getCommentAuthorData(comment.getAuthorID());
                    commentAdapter.notifyDataSetChanged();
                }

                System.out.println("=================");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCommentAuthorData(String UID) {

        System.out.println("Grabbing Comment Author Data For: " + UID);

        DatabaseReference userRef = gimmeeDatabase.getReference("users").child(UID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Comment Author Data: " + dataSnapshot.toString());
                commentAuthors.add(0, dataSnapshot.getValue(User.class));
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private String findDistance(List<Double> myCords, List<Double> postCords) {
        return String.format("%.2f", kmToMiles(haversine(myCords, postCords)));
    }

    private double kmToMiles(double km) {
        return km * 0.621371;
    }

    /**
     * Calculates the distance in km between two lat/long points
     * using the haversine formula
     * <p>
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
