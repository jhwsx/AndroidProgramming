package com.example.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wzc on 2017/8/7.
 * 用于对声音文件进行定位,管理记录及播放
 */

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUND_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;
    private final SoundPool mSoundPool;
    private AssetManager mAssetManager;

    private List<Sound> mSounds = new ArrayList<>();

    public BeatBox(Context context) {
        mAssetManager = context.getAssets();
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        mSoundPool.release();
    }

    private void loadSounds() {
        String[] soundNames = new String[]{};
        try {
            soundNames = mAssetManager.list(SOUND_FOLDER);
            Log.i(TAG, "soundNames: " + Arrays.toString(soundNames));
            Log.i(TAG, "soundNames.length: " + soundNames.length);
        } catch (IOException e) {
            Log.e(TAG, "Could not list sample_sounds", e);
            e.printStackTrace();
        }

        for (String fileName : soundNames) {
            try {
                String assetPath = SOUND_FOLDER + "/" + fileName;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch (IOException e) {
                Log.e(TAG, "Could not load sound " + fileName, e);
                e.printStackTrace();
            }
        }

    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = mAssetManager.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }


}
