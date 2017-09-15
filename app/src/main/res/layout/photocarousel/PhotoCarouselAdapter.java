package xyz.tracestudios.gimmee.photocarousel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import xyz.tracestudios.gimmee.SerializedContext;


public class PhotoCarouselAdapter extends FragmentStatePagerAdapter {


    SerializedContext serializedContext;

    List<String> photoURLs;


    public PhotoCarouselAdapter(SerializedContext serializedContext, List<String> photoURLs, FragmentManager fm) {
        super(fm);
        this.serializedContext = serializedContext;
        this.photoURLs = photoURLs;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
            bundle.putString("photoURL", photoURLs.get(position));
            bundle.putSerializable("serializedContext", serializedContext);

        PhotoCarouselFragment photoCarouselFragment = new PhotoCarouselFragment();
            photoCarouselFragment.setArguments(bundle);

        return photoCarouselFragment;
    }

    @Override
    public int getCount() {
        return photoURLs.size();
    }
}
