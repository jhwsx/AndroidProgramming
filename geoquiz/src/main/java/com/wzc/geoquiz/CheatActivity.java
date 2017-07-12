package com.wzc.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    // 优质做法使用: 包名修饰常量信息,避免不同应用命名冲突
    private static final String EXTRA_QUESTION_ANSWER = "com.wzc.geoquiz.question_answer";
    private static final String EXTRA_ANSWER_SHOWN = "com.wzc.geoquiz.answer_shown";
    private boolean mQuestionAnswer;
    private TextView mTvAnswer;
    private Button mBtnShowAnswer;

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
            }
        });

    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }


}
