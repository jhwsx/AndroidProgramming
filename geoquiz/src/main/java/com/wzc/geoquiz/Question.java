package com.wzc.geoquiz;

/**
 * Created by wzc on 2017/7/7.
 * 问题类
 */

public class Question {
    private int mQuestionContentResId;
    private boolean mQuestionAnswer;

    public Question(int questionContentResId, boolean questionAnswer) {
        mQuestionContentResId = questionContentResId;
        mQuestionAnswer = questionAnswer;
    }

    public int getQuestionContentResId() {
        return mQuestionContentResId;
    }

    public void setQuestionContentResId(int questionContentResId) {
        mQuestionContentResId = questionContentResId;
    }

    public boolean isQuestionAnswer() {
        return mQuestionAnswer;
    }

    public void setQuestionAnswer(boolean questionAnswer) {
        mQuestionAnswer = questionAnswer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "mQuestionContentResId='" + mQuestionContentResId + '\'' +
                ", mQuestionAnswer=" + mQuestionAnswer +
                '}';
    }
}
