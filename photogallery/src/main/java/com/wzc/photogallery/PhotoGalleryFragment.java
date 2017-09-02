package com.wzc.photogallery;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Created by wzc on 2017/8/16.
 */

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    private static final String API_KEY = "5f73a03dbc8c61d06a199b1c901c3069";
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private FetchItemTask mFetchItemTask;
    private PhotoAdapter mAdapter;
    private int mPage = 1;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

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
        mFetchItemTask.execute(mPage);

        mThumbnailDownloader = new ThumbnailDownloader<>();
        // 问题:为什么先调用start()再调用getLooper(),自己看一下源码.
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    private int mGridColumnWidthDp = 120;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mPhotoRecyclerView.getWidth();
                int gridColumnWidthPx = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, mGridColumnWidthDp, getResources().getDisplayMetrics());
                int columns = width / gridColumnWidthPx;
                Log.d(TAG, "columns = " + columns);
                mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columns));
                mPhotoRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * @param recyclerView
             * @param newState
             * //正在滚动  SCROLL_STATE_IDLE = 0;
             * //正在被外部拖拽,一般为用户正在用手指滚动  SCROLL_STATE_DRAGGING = 1;
             * //自动滚动开始  SCROLL_STATE_SETTLING = 2;
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged() newState = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                Log.d(TAG, "onScrolled() lastCompletelyVisibleItemPosition = " + lastCompletelyVisibleItemPosition);
                if (mAdapter.getItemCount() - 1 == lastCompletelyVisibleItemPosition) {

                    mPage++;
                    if (mPage > 10) {
                        Toast.makeText(getActivity(), "No more data.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Log.d(TAG, "onScrolled() 请求第" + mPage + "页");
                    new FetchItemTask().execute(mPage);
                }
            }
        });
        // 这里调用的目的是每次因设备旋转重新生成RecyclerView时,可重新为其配置对应的adapter
        setupAdapter();
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mFetchItemTask.cancel(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if (isAdded()) { // 判断是为了检查确认fragment已与目标activity相关联,进而保证getActivity()返回的结果不为空
            if (mAdapter == null) {
                mAdapter = new PhotoAdapter(mItems);
                mPhotoRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        private PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            TextView textView = new TextView(getActivity());
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
//            holder.bindGalleryItem(galleryItem);
            Drawable drawable = getResources().getDrawable(R.drawable.bill_up_close);
            holder.bindDrawable(drawable);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        //        private TextView mTitleTextView;
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
//            mTitleTextView = (TextView) itemView;
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
//            mTitleTextView.setText(galleryItem.toString());
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    // Params, the type of the parameters sent to the task upon execution.
    // Progress, the type of the progress units published during the background computation.
    // Result, the type of the result of the background computation.
    private class FetchItemTask extends AsyncTask<Integer, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {

            if (isCancelled()) {
                return new ArrayList<>();
            }
            return new FlickrFetchr().fetchItems(params[0]);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);
            mItems.addAll(items);
            // 每次模型数据发生变化时,为recyclerview配置adapter
            setupAdapter();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}