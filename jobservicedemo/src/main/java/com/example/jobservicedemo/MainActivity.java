package com.example.jobservicedemo;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * 参考文章:
 * http://wiki.jikexueyuan.com/project/android-weekly/issue-146/using-jobscheduler.html
 */
public class MainActivity extends AppCompatActivity {

    private JobScheduler mJobScheduler;
    private Button mBtnScheduleJob;
    private Button mBtnCancelJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化JobScheduler对象
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        mBtnScheduleJob = (Button) findViewById(R.id.btn_schedule_job);
        mBtnCancelJob = (Button) findViewById(R.id.btn_cancel_job);
        mBtnScheduleJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int JOB_ID = 1;
                // 初始化JobInfo对象
                JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
                builder.setPeriodic(3000);
                JobInfo jobInfo = builder.build();
                // 编排任务
                mJobScheduler.schedule(jobInfo);
          }
        });
        mBtnCancelJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJobScheduler.cancelAll();
            }
        });
    }
}
