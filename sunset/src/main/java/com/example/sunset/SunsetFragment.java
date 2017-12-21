package com.example.sunset;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by wzc on 2017/12/19.
 */

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private View mShadowView;

    public static SunsetFragment newInstance() {

        Bundle args = new Bundle();

        SunsetFragment fragment = new SunsetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mSceneView = inflater.inflate(R.layout.fragment_sunset, container, false);
        mSunView = mSceneView.findViewById(R.id.sun);
        mSkyView = mSceneView.findViewById(R.id.sky);
        mShadowView = mSceneView.findViewById(R.id.shadow);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
        return mSceneView;
    }

    private int count = 0;

    private void startAnimation() {
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();
        int remainder = count % 2;
        switch (remainder) {
            case 0:
                sunset(sunYStart, sunYEnd);
                break;
            case 1:
                sunrise(sunYStart, sunYEnd);
                break;
        }
        count++;

    }

    private void sunrise(float sunYStart, float sunYEnd) {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYEnd, sunYStart)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunriseSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                .setDuration(3000);
        sunriseSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator sunScaleXAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleX", 1, 0.9f, 1)
                .setDuration(3000);
        sunScaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator sunScaleYAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleY", 1, 0.9f, 1)
                .setDuration(3000);
        sunScaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator shadowScaleXAnimator = ObjectAnimator
                .ofFloat(mShadowView, "scaleX", 1, 0.8f)
                .setDuration(3000);

        ObjectAnimator shadowScaleYAnimator = ObjectAnimator
                .ofFloat(mShadowView, "scaleY", 1, 0.8f)
                .setDuration(3000);
        shadowScaleYAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mShadowView.setVisibility(View.VISIBLE);
            }

        });
        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(nightSkyAnimator)
                .before(sunriseSkyAnimator)
                .with(heightAnimator)
                .with(shadowScaleXAnimator)
                .with(shadowScaleYAnimator)
                .with(sunScaleXAnimator)
                .with(sunScaleYAnimator);
        animatorSet.start();
    }

    private void sunset(float sunYStart, float sunYEnd) {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());
        final ObjectAnimator sunScaleXAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleX", 1, 0.9f, 1)
                .setDuration(3000);
        sunScaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
        final ObjectAnimator sunScaleYAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleY", 1, 0.9f, 1)
                .setDuration(3000);
        sunScaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator shadowScaleXAnimator = ObjectAnimator
                .ofFloat(mShadowView, "scaleX", 0.8f, 1)
                .setDuration(3000);

        ObjectAnimator shadowScaleYAnimator = ObjectAnimator
                .ofFloat(mShadowView, "scaleY", 0.8f, 1)
                .setDuration(3000);
        shadowScaleYAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mShadowView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShadowView.setVisibility(View.INVISIBLE);
            }
        });
        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(heightAnimator)
                .with(sunScaleXAnimator)
                .with(shadowScaleXAnimator)
                .with(shadowScaleYAnimator)
                .with(sunScaleYAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();
    }
}
