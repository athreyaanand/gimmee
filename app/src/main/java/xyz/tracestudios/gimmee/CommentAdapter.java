package xyz.tracestudios.gimmee;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.tracestudios.gimmee.firebasemodels.Comment;
import xyz.tracestudios.gimmee.firebasemodels.User;


public class CommentAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentAdapter";

    @BindView(R.id.postDetailsCommentProfileImage)
    CircleImageView commentNamecardProfileImage;

    @BindView(R.id.postDetailsCommentUsernameAndCommentText)
        TextView commentUsernameAndCommentText;


    /////////////////////////

    FirebaseUser gimmeeUser;

    //Database
    FirebaseDatabase gimmeeDatabase;
    DatabaseReference gimmeeDatabaseRef;

    private FirebaseAuth gimmeeAuth;

    Context context;

    String uid;

    ArrayList<User> commentAuthors;

    ArrayList<Comment> comments;

    String commentFormat = "{{author}}: {{comment}}";

    public CommentAdapter(Context context, ArrayList<Comment> comments, ArrayList<User> commentAuthors) {
        super(context, 0, comments);
        this.context = context;
        this.comments = comments;
        this.commentAuthors = commentAuthors;

        gimmeeDatabase = FirebaseDatabase.getInstance();
        gimmeeDatabaseRef = gimmeeDatabase.getReference();
        gimmeeAuth = FirebaseAuth.getInstance();
        gimmeeUser = gimmeeAuth.getCurrentUser();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);

        ButterKnife.bind(this, view);

        //Grab post

        uid = gimmeeUser.getUid();

        commentUsernameAndCommentText.setText(commentFormat);

        if(comments.size() != 0 && comments.get(position) != null)
            bindCommentData(comments.get(position));

        if(commentAuthors.size() != 0 && commentAuthors.get(position) != null) {
            bindAuthorData(commentAuthors.get(position));
            attachListeners(commentAuthors.get(position));
        }

        System.out.println("View Returned @posistion=" + position);

        // return completed view to render
        return view;
    }

    private String getComment(){
        return commentUsernameAndCommentText.getText().toString();
    }

    private void bindAuthorData(User author) {

        String c = getComment().replace("{{author}}", author.getUsername());

        Spanned bold = Html.fromHtml("<b>"+c.substring(0,c.indexOf(":")+1)+"</b>"+c.substring(c.indexOf(":")+1));

        commentUsernameAndCommentText.setText(bold);

        //update pci view
        //TODO: set profile image to dl one
        Picasso.with(context)
                .load(author.getProfileImageURL())
                .placeholder(R.drawable.arrow_left_black)
                .error(R.drawable.arrow_right_black)
                .into(commentNamecardProfileImage);
    }

    private void bindCommentData(Comment comment) {
        String c = getComment().replace("{{comment}}", comment.getText());
        commentUsernameAndCommentText.setText(c);
    }

    private void attachListeners(final User author) {

        // Create & Attach Listeners
        commentNamecardProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile(author.getKey());
            }
        });

    }

    private void openProfile(String key) {
        //open profile
        Toast.makeText(context, "Open profile: " + key, Toast.LENGTH_SHORT).show();
    }

}
