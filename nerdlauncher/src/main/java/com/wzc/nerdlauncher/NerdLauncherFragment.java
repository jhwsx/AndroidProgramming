package com.wzc.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wzc on 2017/8/11.
 */

public class NerdLauncherFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private static final String TAG = "NerdLauncherFragment";

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        Intent startupIntent = new Intent();
        startupIntent.setAction(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        o1.loadLabel(pm).toString(),
                        o2.loadLabel(pm).toString()
                );
            }
        });
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
        Log.i(TAG, "activities.size() = " + activities.size());
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_activity, parent,false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mNameTextView;
        private final ImageView mIconImageView;
        private  ResolveInfo mResolveInfo;

        public ActivityHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iv);
            mNameTextView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo info) {
            mResolveInfo = info;
            PackageManager packageManager = getActivity().getPackageManager();
            String appName = info.loadLabel(packageManager).toString();
            Drawable icon = info.loadIcon(packageManager);
            mNameTextView.setText(appName);
            mIconImageView.setImageDrawable(icon);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(
                    activityInfo.applicationInfo.packageName,
                    activityInfo.name
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
