package com.wzc.photogallery;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzc on 2017/8/16.
 */

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "5f73a03dbc8c61d06a199b1c901c3069";

    /**
     * 从url获取原始数据并返回字节数组
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 调用getInputStream()时,才真正连接到指定的URL地址;如果是post请求,需要调用getOutputStream().
        InputStream in = connection.getInputStream();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with" + urlSpec);
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 从url获取原始数据并返回字符串
     *
     * @param urlSpec
     * @return
     * @throws IOException
     */
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<>();
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extra", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.d(TAG, "Received json: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
           Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException {
        if (!jsonBody.has("photos")) {
            return;
        }
        JSONObject photosJSONObject = jsonBody.getJSONObject("photos");
        if (!photosJSONObject.has("photo")) {
            return;
        }
        JSONArray photoJSONArray = photosJSONObject.getJSONArray("photo");
        for (int i = 0; i < photoJSONArray.length(); i++) {
            JSONObject itemJSONObject = photoJSONArray.getJSONObject(i);

            GalleryItem galleryItem = new GalleryItem();
            if (!itemJSONObject.has("id")) {
                continue;
            }
            String id = itemJSONObject.getString("id");
            galleryItem.setId(id);
            if (!itemJSONObject.has("title")) {
                continue;
            }
            String caption = itemJSONObject.getString("title");
            galleryItem.setCaption(caption);
//            if (!itemJSONObject.has("url_s")) { // 这个参数值告诉Flickr：如有小尺寸图片，也一并返回其URL
//                continue;
//            }
//            String url = itemJSONObject.getString("url_s");
//            galleryItem.setUrl(url);
            items.add(galleryItem);
        }

    }
}
