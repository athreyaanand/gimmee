package xyz.tracestudios.gimmee;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.prefs.Prefs;


import butterknife.BindDimen;
import butterknife.BindString;
import xyz.tracestudios.gimmee.firebasemodels.Post;
import xyz.tracestudios.gimmee.firebasemodels.User;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //Initializing various UI elements and variables
    FloatingActionButton fabCreate;

    ImageView ivMenuUserProfilePhoto;

    private SwipeRefreshLayout swipeContainer;

    @BindDimen(R.dimen.global_menu_avatar_size)//Butterknife allows us to set the values of vars outside of methods to remove clutter
    int avatarSize;
    @BindString(R.string.user_profile_photo)
    String profilePhoto;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.vNavigation)
    NavigationView vNavigation;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    ValueEventListener postListener;

    //Database
    FirebaseDatabase gimmeeDatabase;

    DatabaseReference gimmeeDatabaseRef;

    ArrayList<Post> posts;

    ArrayList<User> postAuthors;

    ListView postListView;

    PostAdapter postAdapter;

    private boolean refreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sets up and implements toolbar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(null); //removes activity name from toolbar title
        setSupportActionBar(toolbar);
        setupHeader(); //calls to setup navigation drawer

        //Permissions listener in case the user doesn't allow permissions
        MultiplePermissionsListener dialogMultiplePermissionsListener = //
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle("Camera & Locations permission")
                        .withMessage("Both camera and location permissions are required for you to sell and but items close to you!")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_launcher)
                        //TODO: Implement warning icon
                        .build();

        //Dexter allows is for easy and efficient requests of permissions
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new CompositeMultiplePermissionsListener(dialogMultiplePermissionsListener, new MultiplePermissionsListener() { //combines two listeners: one for on no pressed and one normal
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
            })).check();

        // sets up fab and allows to implement onClick activity
        fabCreate = (FloatingActionButton) findViewById(R.id.fab);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gets position event is originating and begins animation from that point
                int[] startingLocation = new int[2];
                fabCreate.getLocationOnScreen(startingLocation);
                startingLocation[0] += fabCreate.getWidth() / 2;
                dispatchTakePictureIntent();
                overridePendingTransition(0, 0);
            }
        });

        // initiates Drawer and implements toggle reactions
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        vNavigation.setNavigationItemSelectedListener(this);

        //Arraylist of posts and their publishers we will soon get from the firebase database
        posts = new ArrayList<>();
        postAuthors = new ArrayList<>();

        gimmeeDatabase = FirebaseDatabase.getInstance();
        gimmeeDatabaseRef = gimmeeDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        //postAdapter retrieves and sets posts to the list
        postAdapter = new PostAdapter(this, posts, postAuthors);

        // Attach the adapter to a ListView
        postListView = (ListView) findViewById(R.id.postListView);
        postListView.setAdapter(postAdapter);

        //retrieve post data from firebase
        getPostData();

        //Pull down to refresh portion
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // sets a boolean to true so the method knows if a swipeContainer is still refreshing
                refreshing = true;

                swipeContainer.setRefreshing(true);

                getPostData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    private void getPostData(){
        // Read from the database
        DatabaseReference postRef = gimmeeDatabase.getReference("posts");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                swipeContainer.setRefreshing(false);

                System.out.println("=================");

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    System.out.println("Post Data: " + data.toString());
                    Post post = data.getValue(Post.class);
                    posts.add(0, post);
                    getAuthorData(post.getSellerUID());
                    postAdapter.notifyDataSetChanged();
                }

                System.out.println("=================");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAuthorData(final String UID) {

        System.out.println("Grabbing Author Data For: " + UID);

        //gets poster information
        DatabaseReference userRef = gimmeeDatabase.getReference("users").child(UID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("Author Data: " + dataSnapshot.toString());
                postAuthors.add(0, dataSnapshot.getValue(User.class));
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody") //just incase we get empty warnings
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_post) {
            //new post intent
            Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            //user profile intent
            int[] startingLocation = new int[2];
            UserProfileActivity.startUserProfileFromLocation(startingLocation, this);
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_settings) {
            //profile settings intent
            Intent intent = new Intent(MainActivity.this, ProfileSetupActivity.class);
                intent.putExtra("fromRegister", false);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            //share a share link to various platforms
            //TODO: change uri to app icon
            Uri pictureUri = Uri.parse("android.resource://xyz.tracestudios.gimmee/drawable/avatar");
            String text = "Want to get rid of the things laying around your house doing nothing but collecting dust. Download Gimmee now and sell whatever you want with a few simple clicks! The possibilities are endless; make money from the comfort of your own home!";

            Intent customShareIntent = new Intent();
                customShareIntent.setAction(Intent.ACTION_SEND);
                customShareIntent.putExtra(Intent.EXTRA_TEXT, text);
                customShareIntent.putExtra(Intent.EXTRA_STREAM, pictureUri);
                customShareIntent.setType("image/*");
                customShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(customShareIntent, "Spread the Word..."));
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                intent.putExtra("fromSetup", false);
            startActivity(intent);
        }else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to logout?")
                    .setIcon(R.drawable.ic_logout)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseAuth.getInstance().signOut(); //End user session
                            startActivity(new Intent(MainActivity.this, SplashActivity.class)); //Go back to home page
                            MainActivity.this.finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupHeader() {
        //sets profile picture in drawer header
        View headerView = vNavigation.getHeaderView(0);
        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });

        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);

    }

    public void onGlobalMenuHeaderClick(final View v) {
        //opens profile activity if header clicked
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startingLocation, MainActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
