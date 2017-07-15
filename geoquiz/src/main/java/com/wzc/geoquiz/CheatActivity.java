package com.wzc.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    // 优质做法使用: 包名修饰常量信息,避免不同应用命名冲突
    private static final String EXTRA_QUESTION_ANSWER = "com.wzc.geoquiz.question_answer";
    private static final String EXTRA_ANSWER_SHOWN = "com.wzc.geoquiz.answer_shown";
    private boolean mQuestionAnswer;
    private TextView mTvAnswer;
    private Button mBtnShowAnswer;
    private TextView mTvAPILevel;

    public static Intent newIntent(Context context, boolean questionAnswer) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_QUESTION_ANSWER, questionAnswer);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result){
       return  result.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mQuestionAnswer = getIntent().getBooleanExtra(EXTRA_QUESTION_ANSWER, false);

        mTvAnswer = (TextView) findViewById(R.id.tv_answer);
        mBtnShowAnswer = (Button) findViewById(R.id.btn_show_answer);

        mBtnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionAnswer) {
                    mTvAnswer.setText(R.string.button_true_text);
                } else {
                    mTvAnswer.setText(R.string.button_false_text);
                }
                setAnswerShownResult(true);
                // 在隐藏按钮的同时,显示一段圆球特效动画,这是Lollipop(API21)的代码
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mBtnShowAnswer.getWidth() / 2;
                    int cy = mBtnShowAnswer.getHeight() / 2;
                    float radius = mBtnShowAnswer.getWidth();
                    Animator animator = ViewAnimationUtils.createCircularReveal(mBtnShowAnswer, cx, cy, radius, 0);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mBtnShowAnswer.setVisibility(View.INVISIBLE);
                        }
                    });
                    animator.start();
                } else {
                    mBtnShowAnswer.setVisibility(View.INVISIBLE);
                }

            }
        });
        // 向用户报告设备运行系统的API级别
        mTvAPILevel = (TextView) findViewById(R.id.tv_cheat_api_level);
        mTvAPILevel.setText(getAPILevel());

    }

    private String getAPILevel() {
        String result =  new StringBuffer().append("API level ").append(Build.VERSION.SDK_INT).toString();
        return result;
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }


}
