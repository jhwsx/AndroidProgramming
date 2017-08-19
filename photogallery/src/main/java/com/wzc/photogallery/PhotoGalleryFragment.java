package com.wzc.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzc on 2017/8/16.
 */

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private static final String API_KEY = "5f73a03dbc8c61d06a199b1c901c3069";
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private FetchItemTask mFetchItemTask;

    public static PhotoGalleryFragment newInstance() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Control whether a fragment instance is retained across Activity
        // re-creation (such as from a configuration change).
        setRetainInstance(true);
        mFetchItemTask = new FetchItemTask();
        mFetchItemTask.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        // 这里调用的目的是每次因设备旋转重新生成RecyclerView时,可重新为其配置对应的adapter
        setupAdapter();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mFetchItemTask.cancel(false);
    }

    private void setupAdapter() {
        if (isAdded()) { // 判断是为了检查确认fragment已与目标activity相关联,进而保证getActivity()返回的结果不为空
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        private PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            holder.bindGalleryItem(mGalleryItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            mTitleTextView.setText(galleryItem.toString());
        }
    }

    // Params, the type of the parameters sent to the task upon execution.
    // Progress, the type of the progress units published during the background computation.
    // Result, the type of the result of the background computation.
    private class FetchItemTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            if (isCancelled()) {
                return new ArrayList<>();
            }
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);
            mItems = items;
            // 每次模型数据发生变化时,为recyclerview配置adapter
            setupAdapter();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
