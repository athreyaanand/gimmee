package xyz.tracestudios.gimmee;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import xyz.tracestudios.gimmee.firebasemodels.Post;

public class CreatePostActivity extends AppCompatActivity {

    FirebaseUser gimmeeUser;

    private FirebaseAuth gimmeeAuth;
    private FirebaseAuth.AuthStateListener gimmeeAuthListener;

    private StorageReference gimmeeStorageRef;

    FirebaseDatabase gimmeeDatabase;
    DatabaseReference gimmeeDatabaseRef;

    RadioGroup conditionRadioGroup;

    Button submitPostBttn;
    Button addImgButton;

    EditText editTitle;
    EditText editDescription;
    EditText editPrice;

    TextView titleTextCount;
    TextView descriptionTextCount;

    int uploadTries;

    final int MAX_TITLE_LENGTH = 90;
    final int MAX_DESC_LENGTH = 200;

    final int MAX_UPLOAD_TRIES = 3;

    AutofitRecyclerView recyclerView;

    private ArrayList<String> mArrayUri;  //uri of images selected

    ImageView imgPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        System.out.println("in cretae post");

        //
        gimmeeDatabase = FirebaseDatabase.getInstance();
        gimmeeDatabaseRef = gimmeeDatabase.getReference();
        gimmeeStorageRef = FirebaseStorage.getInstance().getReference();
        gimmeeUser = FirebaseAuth.getInstance().getCurrentUser();

        imgPreview = (ImageView) findViewById(R.id.placeholder);

        submitPostBttn = (Button) findViewById(R.id.submitPostBttn);

        addImgButton = (Button) findViewById(R.id.addPictureButton);

        editTitle = (EditText) findViewById(R.id.editTitle);
            editTitle.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editDescription = (EditText) findViewById(R.id.editDescription);
        editPrice = (EditText) findViewById(R.id.editPrice);

        mArrayUri = new ArrayList<>();
        recyclerView= (AutofitRecyclerView) findViewById(R.id.recyclerView_myc); //recyclerView_myc will be autofitrecyelrview
            recyclerView.setVisibility(View.INVISIBLE);

        submitPostBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("Listener Activated");
                preparePost();
            }
        });

        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        conditionRadioGroup = (RadioGroup) findViewById(R.id.conditionRadioGroup);

        titleTextCount = (TextView) findViewById(R.id.titleTextCount);
        descriptionTextCount = (TextView) findViewById(R.id.descriptionTextCount);

        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                titleTextCount.setText(editable.length() + "/" + MAX_TITLE_LENGTH);
            }
        });

        editDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                descriptionTextCount.setText(editable.length() + "/" + MAX_DESC_LENGTH);
            }
        });

        gimmeeAuth = FirebaseAuth.getInstance();

    }

    private void previewMediaInGrid(ArrayList<String> mArrayUri) {
        recyclerView.setVisibility(View.VISIBLE);
        if (mArrayUri.size()!=0) {
            imgPreview.setVisibility(View.GONE);
        }
        GridAdapterUpload mAdapter = new GridAdapterUpload(mArrayUri, getApplicationContext());
        recyclerView.setAdapter(mAdapter);

    }

    private void preparePost() {

        String title = editTitle.getText().toString().trim();

        if(title.length() < 5){
            warnUser("Your title is too short! It needs to be at least 5 characters long");
            return;
        }

        String description = editDescription.getText().toString().trim();

        if(description.length() < 10){
            warnUser("Your description is too short! It needs to be at least 10 characters long");
            return;
        }

        int radioBttnID = conditionRadioGroup.getCheckedRadioButtonId();

        int condition = conditionRadioGroup.indexOfChild(findViewById(radioBttnID));
        System.out.println("CONDITION: " + condition);

        double price = Double.parseDouble(editPrice.getText().toString());
        int priceInCents = (int)(price * 100);

        //TODO: GET RID OF TEMP VAR
        List<Double> location = new ArrayList<>();
        location.add(34.371052);
        location.add(-118.507805);
        List<String> photoPaths = new ArrayList<>();
            photoPaths.add("https://i.imgur.com/JJvueSi.png");
            photoPaths.add("https://i.imgur.com/8ZkdS9x.jpg");
            photoPaths.add("https://i.imgur.com/WR0BGqI.jpg");
            photoPaths.add("https://i.imgur.com/FII21oY.jpg");
            photoPaths.add("https://i.imgur.com/uqirrzL.jpg");

        String UID = gimmeeUser.getUid();

        String key = "";
        long timestamp = System.currentTimeMillis() / 1000;
        List<String> likedBy = new ArrayList<>();
        likedBy.add("");

        int comments = 0;
        boolean isSold = false;

        //get location

        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setPrice(priceInCents);
        post.setCondition(condition);
        post.setComments(comments);
        post.setLikedBy(likedBy);
        post.setSellerUID(UID);
        post.setTimestamp(timestamp);
        post.setKey(key);
        post.setPhotoURLs(photoPaths);
        post.setLocation(location);
        post.setSold(isSold);

        firebaseMakePost(post, photoPaths);
    }

    private void warnUser(String warning) {

        new AlertDialog.Builder(CreatePostActivity.this)
                .setTitle("Error")
                .setMessage(warning)
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void firebaseMakePost(Post post, List<String> photoPaths) {

        System.out.println("making post");

        String key = gimmeeDatabaseRef.child("posts").push().getKey();
        post.setKey(key);

        //get firebase urls
        //List<String> firebasePhotoURLs = firebaseUploadImages(key, photoPaths);
        //post.setPhotoURLs(firebasePhotoURLs);

        //TODO: remove
        post.setPhotoURLs(photoPaths);

        //update time again incase of long processing time
        post.setTimestamp(System.currentTimeMillis() / 1000);

        gimmeeDatabaseRef.child("posts").child(key).setValue(post);

        Toast.makeText(this, "POST CREATED", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "PRICE:" + post.getPrice(), Toast.LENGTH_SHORT).show();

        finish();
    }

    private List<String> firebaseUploadImages(String key, List<String> photoPaths) {

        List<String> firebasePhotoURLs = new ArrayList<>();

        ArrayList<Uri> uris = new ArrayList<>();
        for (String path : photoPaths) {
            Uri file = Uri.fromFile(new File(path));
            uris.add(file);
        }

        int fileNum = 0;
        for (Uri file : uris) {
            firebasePhotoURLs.add(firebaseTryUpload(key, file, fileNum));
        }

        return firebasePhotoURLs;
    }

    private String firebaseTryUpload(final String key, final Uri uri, final int fileNum) {

        uploadTries = 1;

        final String[] url = new String[1];

        StorageReference postIMGRef = gimmeeStorageRef.child("posts").child(key).child("images").child("img_" + fileNum + ".jpg");

        postIMGRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        System.out.println("FIREBASE: Image Upload -- SUCCESS");
                        System.out.println("URL: " + taskSnapshot.getDownloadUrl().toString());

                        // Get a URL to the uploaded content
                        url[0] =  taskSnapshot.getDownloadUrl().toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        System.out.println("FIREBASE: Image Upload -- FAIL");
                        System.out.println("ERROR: " + exception.toString());

                        url[0] = retryUpload(key, uri, fileNum);
                    }
                });

        return url[0];
    }

    private String retryUpload(String key, Uri uri, int fileNum) {
        uploadTries++;
        return uploadTries < MAX_UPLOAD_TRIES ? firebaseTryUpload(key, uri, fileNum) : "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            String path = (targetUri.toString());

            mArrayUri.add(path);
            previewMediaInGrid(mArrayUri);

        }
    }

}
