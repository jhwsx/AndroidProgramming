package com.wzc.geoquiz;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mBtnAnswerTrue;
    private Button mBtnAnswerFalse;
    private TextView mTvQuestion;
    private ImageButton mBtnNext;
    private Context mContext;
    private Question[] mQuestionArray = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int mCurrentIndex;
    private ImageButton mBtnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt("current_index");
        }
        mContext = QuizActivity.this;
        mBtnAnswerTrue = (Button) findViewById(R.id.btn_answer_true);
        mBtnAnswerFalse = (Button) findViewById(R.id.btn_answer_false);
        mTvQuestion = (TextView) findViewById(R.id.tv_question);
        mBtnNext = (ImageButton) findViewById(R.id.btn_next);
        mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);

        updateQuestion();

        mBtnAnswerTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers(true);
            }
        });

        mBtnAnswerFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers(false);
            }
        });

        mBtnNext.setOnClickListener(new View.OnClickListener() {
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

        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) >= 0 ? mCurrentIndex - 1 : mQuestionArray.length-1;
                Log.d("QuizActivity", "mBtnPrev mCurrentIndex=" + mCurrentIndex);
                updateQuestion();
            }
        });
    }

    private void updateQuestion() {
        mTvQuestion.setText(mQuestionArray[mCurrentIndex].getQuestionContentResId());
    }

    private void checkAnswers(boolean isUserPressedTrue) {
        int resId;
        if (isUserPressedTrue == mQuestionArray[mCurrentIndex].isQuestionAnswer()) {
            resId = R.string.toast_correct;
        } else {
            resId = R.string.toast_incorrect;
        }
        Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("current_index", mCurrentIndex);
        super.onSaveInstanceState(outState);

    }
}
