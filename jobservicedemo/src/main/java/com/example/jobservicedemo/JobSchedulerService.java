package com.example.jobservicedemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by wzc on 2017/12/10.
 */

public class JobSchedulerService extends JobService {
    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 这里使用toast代表需要执行的代码了逻辑
            Toast.makeText(JobSchedulerService.this, "JobSchedulerService task running", Toast.LENGTH_SHORT).show();

            // 任务完成后,调用本方法通知结果给系统
            // 参数二的含义: 让系统知道这个任务是否应该在最初的条件下被重复执行 这个boolean值很有用，因为它指明了你如何处理由于其他原因导致任务执行失败的情况，例如一个失败的网络请求调用。
            // true 表示 事情这次做不完了,请计划在下次某个时候继续吧
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });
    private static final String TAG = JobSchedulerService.class.getSimpleName();
    // 在主线程上收到onStartJob(JobParameters)方法的调用
    // 在这个方法里写回调逻辑,但这些逻辑需要执行在新线程上
    // JobParameters参数用来描述job的信息的
    // 返回值的含义:
    // return true 表示 任务收到,正在做,还没有做完(或者说服务需要处理任务) 你会让系统知道你会手动地调用jobFinished(JobParameters params, boolean needsRescheduled)方法。
    // return false 表示 交代的任务,我已全力去做,现在做完了(或者说这个job没有更多的工作要做了),返回false时,那么onStopJob(JobParameters)方法就不会被调用了(这是因为没有正在执行的任务,何谈取消呢)
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob: " + params.getJobId());
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
        return true;
    }
    // 如果系统认为你必须停止执行job,甚至是在你调用jobFinished(JobParameters, boolean)方法之前,就会调用本方法
    // 在中断任务时调用,表示服务马上就要停掉了
    // 返回值的含义:
    // return true 表示 任务应该计划在下次继续
    // return false 表示 不管怎样,事情就到此结束吧,不要计划下次了
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: " + params.getJobId());
        mJobHandler.removeMessages(1);
        return false;
    }
}
