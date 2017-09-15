package xyz.tracestudios.gimmee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import es.dmoral.prefs.Prefs;


public class IntroActivity extends MaterialIntroActivity
{

    boolean fromSetup;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fromSetup = getIntent().getBooleanExtra("fromSetup", true);

        enableLastSlideAlphaExitTransition(true);

        getNextButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.avatar)
                .title("Welcome to Gimmee!")
                .description("The Ultimate Platform for the Ultimate Shopper")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.ic_global_reach)
                .title("Simple Posting, Global Reach")
                .description("Utilize the ability to sell whatever you desire to anywhere in the world with a simple click of a button!")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.ic_money)
                .title("Anyone Can Make Money!")
                .description("No limit to how much you can sell or who you are! Just Gimmee away!")
                .build());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.ic_customer_service)
                .title("24/7 Service for Questions")
                .description("If you have any questions feel free to contact the developers at tracedevs@gmail.com")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.ic_place_white)
                .title("Optimized to Your Location")
                .description("Always get to see the items near you first! The closest distances and the best prices!")
                .build());


        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.white)
                .buttonsColor(R.color.colorPrimary)
                .image(R.drawable.avatar)
                .title("That's It!")
                .description("Let's Gimmee!")
                .build());

    }


    @Override
    public void onFinish() {
        if (fromSetup){
            Intent intent  = new Intent (this, MainActivity.class);
            startActivity(intent);
        }
        super.onFinish();
    }
}
