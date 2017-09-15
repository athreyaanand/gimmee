package xyz.tracestudios.gimmee.photocarousel;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import xyz.tracestudios.gimmee.R;
import xyz.tracestudios.gimmee.SerializedContext;

public class PhotoCarouselActivity extends FragmentActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */

    int numPages;

    int currentPageSelected;

    ArrayList<String> photoURLs;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    TabLayout controlDots;
    FloatingActionButton controlLeft;
    FloatingActionButton controlRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_carousel);

        photoURLs = getIntent().getStringArrayListExtra("photoURLs");

        numPages = photoURLs.size();

        currentPageSelected = 0;

        // Instantiate a ViewPager and a PagerAdapter.

        mPager = (ViewPager) findViewById(R.id.photoCarouselPager);
        controlDots = (TabLayout) findViewById(R.id.photoCarouselControlDots);

        controlLeft = (FloatingActionButton) findViewById(R.id.photoCarouselControlLeft);
        controlRight = (FloatingActionButton) findViewById(R.id.photoCarouselControlRight);

        mPager.setPageTransformer(true, new PhotoCarouselTransformer());
        mPagerAdapter = new PhotoCarouselAdapter(new SerializedContext(getApplicationContext()), photoURLs, getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        controlDots.setupWithViewPager(mPager, true);

        //set def
        controlLeft.setImageResource(R.drawable.ic_menu_camera);
        controlRight.setImageResource(R.drawable.arrow_right_black);


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPageSelected = position;

                if(position == 0)
                    controlLeft.setImageResource(R.drawable.ic_menu_camera);
                else
                    controlLeft.setImageResource(R.drawable.arrow_left_black);


                if(position == numPages - 1)
                    controlRight.setImageResource(R.drawable.ic_menu_camera);
                else
                    controlRight.setImageResource(R.drawable.arrow_right_black);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        controlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(currentPageSelected == 0)
                    finish();
                else
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);

            }
        });

        controlRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentPageSelected == numPages - 1)
                    finish();
                else
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        });



    }

    @Override
    public void onBackPressed() {

            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.

            //finish frga to go to posts
            finish();
            super.onBackPressed();
    }

}
