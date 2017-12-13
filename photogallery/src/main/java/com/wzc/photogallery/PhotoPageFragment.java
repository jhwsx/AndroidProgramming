package com.wzc.photogallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by wzc on 2017/12/13.
 */

public class PhotoPageFragment extends VisibleFragment {
    private static final String ARG_URI = "photo_page_uri";
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static PhotoPageFragment newInstance(Uri uri) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Uri mUri;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUri = getArguments().getParcelable(ARG_URI);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mWebView = (WebView) view.findViewById(R.id.fragment_photo_page_web_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setProgress(100);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                appCompatActivity.getSupportActionBar().setSubtitle(title);
            }
        });
//        mWebView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
//                        mWebView.goBack();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
        mWebView.loadUrl(mUri.toString());
        return view;
    }

    public WebView getWebView() {
        return mWebView;
    }
}
