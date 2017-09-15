package xyz.tracestudios.gimmee;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

import butterknife.BindView;

public class ProfileSetupActivity extends AppCompatActivity {

    ImageView profilePicture;

    EditText profileNameEdTxt;
    EditText usernameEdTxt;
    EditText bioEdTxt;

    int avatarSize;

    String path;

    Bitmap bitmap;

    boolean fromRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        fromRegister = getIntent().getBooleanExtra("fromRegister", true);

        if (!fromRegister){
            //TODO: fill in by filling imageviews and textviews with current user details
        }

        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        profileNameEdTxt = (EditText) findViewById(R.id.profileNameEditText);
        usernameEdTxt = (EditText) findViewById(R.id.usernameEditText);
        bioEdTxt = (EditText) findViewById(R.id.bioEditText);

        Picasso.with(this)
                .load(R.drawable.ic_add_picture)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileNameEdTxt.getText().length()<3){
                    profileNameEdTxt.setError("Profile name must be longer than 3 characters.");
                }

                if (usernameEdTxt.getText().length()<3){
                    usernameEdTxt.setError("Username must be longer than 3 characters.");
                } else {

                    CircleTransformation ct = new CircleTransformation();
                    BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

                    new AlertDialog.Builder(ProfileSetupActivity.this)
                            .setIcon(drawable)
                            .setTitle("Double Check")
                            .setMessage("Profile Name: "+profileNameEdTxt.getText()+"\n\nUsername: @"+usernameEdTxt.getText()+"\n\nBio: "+bioEdTxt.getText())
                            .setPositiveButton("Yep, Looks Good", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO: send shtuff to firebase

                                    if (fromRegister){
                                        Intent introActivity = new Intent(ProfileSetupActivity.this, IntroActivity.class);
                                        introActivity.putExtra("fromSetup", true);
                                        startActivity(introActivity);
                                    } else {
                                        Intent mainActivity = new Intent(ProfileSetupActivity.this, MainActivity.class);
                                        startActivity(mainActivity);
                                    }

                                    finish();
                                }

                            })
                            .setNegativeButton("No, Make a Change", null)
                            .show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            path = (targetUri.toString());
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                Picasso.with(this)
                        .load(path)
                        .placeholder(R.drawable.img_circle_placeholder)
                        .resize(avatarSize, avatarSize)
                        .centerCrop()
                        .transform(new CircleTransformation())
                        .into(profilePicture);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
