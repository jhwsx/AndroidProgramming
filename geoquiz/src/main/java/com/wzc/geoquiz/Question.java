package com.wzc.geoquiz;

/**
 * Created by wzc on 2017/7/7.
 * 问题类
 */

class Question {
    private final int mQuestionContentResId;
    private final boolean mQuestionAnswer;

    public Question(int questionContentResId, boolean questionAnswer) {
        mQuestionContentResId = questionContentResId;
        mQuestionAnswer = questionAnswer;
    }

    public int getQuestionContentResId() {
        return mQuestionContentResId;
    }

    public boolean isQuestionAnswer() {
        return mQuestionAnswer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "mQuestionContentResId='" + mQuestionContentResId + '\'' +
                ", mQuestionAnswer=" + mQuestionAnswer +
                '}';
    }
}
