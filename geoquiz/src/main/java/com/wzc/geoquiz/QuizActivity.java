package com.wzc.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private TextView mTvQuestion;
    private Context mContext;
    private final Question[] mQuestionArray = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex;
    private static final String KEY_CURRENT_INDEX = "current_index";
    private Button mBtnCheat;
    private static final int REQUESTCODE_CHEAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX, 0);
        }
        mContext = QuizActivity.this;
        Button btnAnswerTrue = (Button) findViewById(R.id.btn_answer_true);
        Button btnAnswerFalse = (Button) findViewById(R.id.btn_answer_false);
        mTvQuestion = (TextView) findViewById(R.id.tv_question);
        ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
        ImageButton btnPrev = (ImageButton) findViewById(R.id.btn_prev);
        mBtnCheat = (Button) findViewById(R.id.btn_cheat);

        updateQuestion();

        btnAnswerTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers(true);
            }
        });

        btnAnswerFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers(false);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionArray.length;
                updateQuestion();
            }
        });

        mTvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionArray.length;
                updateQuestion();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) >= 0 ? mCurrentIndex - 1 : mQuestionArray.length - 1;
                Log.d("QuizActivity", "mBtnPrev mCurrentIndex=" + mCurrentIndex);
                updateQuestion();
            }
        });

        mBtnCheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent
                        = CheatActivity.newIntent(mContext, mQuestionArray[mCurrentIndex].isQuestionAnswer());
                startActivityForResult(intent, REQUESTCODE_CHEAT);
            }
        });
    }

    private void updateQuestion() {
//        Log.d(TAG, "Updating question text for question #" + mCurrentIndex, new Exception());
        mTvQuestion.setText(mQuestionArray[mCurrentIndex].getQuestionContentResId());
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_INDEX, mCurrentIndex);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUESTCODE_CHEAT) {
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                if (data == null) {
                    return;
                }
                boolean isCheat = CheatActivity.wasAnswerShown(data);
                checkCheat(isCheat);
                break;
            case RESULT_CANCELED:
                checkCheat(false);
                break;
            case RESULT_FIRST_USER:
                break;
        }
    }

    private void checkAnswers(boolean isUserPressedTrue) {
        int resId;
        if (isUserPressedTrue == mQuestionArray[mCurrentIndex].isQuestionAnswer()) {
            resId = R.string.toast_correct;
        } else {
            resId = R.string.toast_incorrect;
        }
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }

    private void checkCheat(boolean isCheat) {
        int resId;
        if (isCheat) {
            resId = R.string.judgment_toast;
        } else {
            resId = R.string.judgment1_toast;
        }
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }
}
