package xyz.tracestudios.gimmee.photocarousel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import xyz.tracestudios.gimmee.R;
import xyz.tracestudios.gimmee.SerializedContext;

/**
 * Created by Joseph on 2/4/2017.
 */

public class PhotoCarouselFragment extends Fragment {

    SerializedContext serializedContext;

    ImageView photoCarouselImage;

    String photoURL;

    @Override
    public void setArguments(Bundle bundle){
        serializedContext = (SerializedContext) bundle.getSerializable("serializedContext");
        photoURL = bundle.getString("photoURL");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.photo_carousel_element, container, false);

        photoCarouselImage = (ImageView) view.findViewById(R.id.photoCarouselImage);

        Picasso.with(serializedContext.getContext())
                .load(photoURL)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(photoCarouselImage);


        return view;
    }

}
