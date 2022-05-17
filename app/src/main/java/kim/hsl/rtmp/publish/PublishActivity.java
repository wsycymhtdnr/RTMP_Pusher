package kim.hsl.rtmp.publish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.mooc.libcommon.dialog.LoadingDialog;
import com.mooc.libcommon.utils.FileUtils;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.wlf.mediapick.MediaPicker;
import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConstants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kim.hsl.rtmp.R;

public class PublishActivity extends AppCompatActivity {
    private ImageView mIvClose;
    private Button mBtPublish;
    private EditText mInput;
    private ImageView mIvAddFile;
    private int width, height;
    private String filePath, coverFilePath;
    private boolean isVideo = true;
    private UUID coverUploadUUID, fileUploadUUID;
    private String coverUploadUrl, fileUploadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        mIvClose = findViewById(R.id.action_close);
        mBtPublish = findViewById(R.id.action_publish);
        mInput = findViewById(R.id.input_view);
        mIvAddFile = findViewById(R.id.action_add_file);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPicker.create(PublishActivity.this)
                        .setMediaType(MediaPickConstants.MEDIA_TYPE_VIDEO)
                        .setMaxPickNum(1) // 设置最大选择个数
                        .forResult(1);
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            List<MediaEntity> list = MediaPicker.obtainMediaResults(data);
            if (list != null) {
                Log.e("PublishActivity", list.get(0).getPath());
                filePath = list.get(0).getPath();
                publish();
            }
        }
    }

    private void publish() {
        showLoading();
        final List<OneTimeWorkRequest> workRequests = new ArrayList<>();
        if (!TextUtils.isEmpty(filePath)) {
            if (isVideo) {
                //生成视频封面文件
                FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                    @SuppressLint({"RestrictedApi", "CheckResult"})
                    @Override
                    public void onChanged(String coverPath) {
                        coverFilePath = coverPath;
                        Glide.with(mIvAddFile).load(coverFilePath);
                        OneTimeWorkRequest request = getOneTimeWorkRequest(coverPath);
                        coverUploadUUID = request.getId();
                        workRequests.add(request);

                        enqueue(workRequests);
                    }
                });
            }
            OneTimeWorkRequest request = getOneTimeWorkRequest(filePath);
            fileUploadUUID = request.getId();
            workRequests.add(request);
            //如果是视频文件则需要等待封面文件生成完毕后再一同提交到任务队列
            //否则 可以直接提交了
            if (!isVideo) {
                enqueue(workRequests);
            }
        } else {
            publishFeed();
        }
    }

    private void enqueue(List<OneTimeWorkRequest> workRequests) {
        WorkContinuation workContinuation = WorkManager.getInstance(PublishActivity.this).beginWith(workRequests);
        workContinuation.enqueue();

        workContinuation.getWorkInfosLiveData().observe(PublishActivity.this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                //block runing enuqued failed susscess finish
                int completedCount = 0;
                int failedCount = 0;
                for (WorkInfo workInfo : workInfos) {
                    WorkInfo.State state = workInfo.getState();
                    Data outputData = workInfo.getOutputData();
                    UUID uuid = workInfo.getId();
                    if (state == WorkInfo.State.FAILED) {
                        // if (uuid==coverUploadUUID)是错的
                        if (uuid.equals(coverUploadUUID)) {
                            showToast("封面图上传失败");
                        } else if (uuid.equals(fileUploadUUID)) {
                            showToast("视频文件上传失败");
                        }
                        failedCount++;
                    } else if (state == WorkInfo.State.SUCCEEDED) {
                        String fileUrl = outputData.getString("fileUrl");
                        if (uuid.equals(coverUploadUUID)) {
                            coverUploadUrl = fileUrl;
                        } else if (uuid.equals(fileUploadUUID)) {
                            fileUploadUrl = fileUrl;
                        }
                        completedCount++;
                    }
                }

                if (completedCount >= workInfos.size()) {
                    publishFeed();
                } else if (failedCount > 0) {
                    dismissLoading();
                }
            }
        });
    }

    private void publishFeed() {
        ApiService.post("/feeds/publish")
                .addParam("coverUrl", coverUploadUrl)
                .addParam("fileUrl", fileUploadUrl)
                .addParam("fileWidth", width)
                .addParam("fileHeight", height)
                .addParam("userId", 0)
                .addParam("tagId", 0)
                .addParam("tagTitle", "")
                .addParam("feedText", mInput.getText().toString())
                .addParam("feedType", 2)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        showToast("发布成功");
                        PublishActivity.this.finish();
                        dismissLoading();
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                        dismissLoading();
                    }
                });
    }

    private LoadingDialog mLoadingDialog = null;

    private void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setLoadingText("正在发布");
        }
        mLoadingDialog.show();
    }

    private void dismissLoading() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        } else {
            runOnUiThread(() -> {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
            });
        }
    }

    private void showToast(String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PublishActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @SuppressLint("RestrictedApi")
    @NotNull
    private OneTimeWorkRequest getOneTimeWorkRequest(String filePath) {
        Data inputData = new Data.Builder()
                .putString("file", filePath)
                .build();

//        @SuppressLint("RestrictedApi") Constraints constraints = new Constraints();
//        //设备存储空间充足的时候 才能执行 ,>15%
//        constraints.setRequiresStorageNotLow(true);
//        //必须在执行的网络条件下才能好执行,不计流量 ,wifi
//        constraints.setRequiredNetworkType(NetworkType.UNMETERED);
//        //设备的充电量充足的才能执行 >15%
//        constraints.setRequiresBatteryNotLow(true);
//        //只有设备在充电的情况下 才能允许执行
//        constraints.setRequiresCharging(true);
//        //只有设备在空闲的情况下才能被执行 比如息屏，cpu利用率不高
//        constraints.setRequiresDeviceIdle(true);
//        //workmanager利用contentObserver监控传递进来的这个uri对应的内容是否发生变化,当且仅当它发生变化了
//        //我们的任务才会被触发执行，以下三个api是关联的
//        constraints.setContentUriTriggers(null);
//        //设置从content变化到被执行中间的延迟时间，如果在这期间。content发生了变化，延迟时间会被重新计算
        //这个content就是指 我们设置的setContentUriTriggers uri对应的内容
//        constraints.setTriggerContentUpdateDelay(0);
//        //设置从content变化到被执行中间的最大延迟时间
        //这个content就是指 我们设置的setContentUriTriggers uri对应的内容
//        constraints.setTriggerMaxContentDelay(0);
        OneTimeWorkRequest request = new OneTimeWorkRequest
                .Builder(UploadFileWorker.class)
                .setInputData(inputData)
//                .setConstraints(constraints)
//                //设置一个拦截器，在任务执行之前 可以做一次拦截，去修改入参的数据然后返回新的数据交由worker使用
//                .setInputMerger(null)
//                //当一个任务被调度失败后，所要采取的重试策略，可以通过BackoffPolicy来执行具体的策略
//                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
//                //任务被调度执行的延迟时间
//                .setInitialDelay(10, TimeUnit.SECONDS)
//                //设置该任务尝试执行的最大次数
//                .setInitialRunAttemptCount(2)
//                //设置这个任务开始执行的时间
//                //System.currentTimeMillis()
//                .setPeriodStartTime(0, TimeUnit.SECONDS)
//                //指定该任务被调度的时间
//                .setScheduleRequestedAt(0, TimeUnit.SECONDS)
//                //当一个任务执行状态编程finish时，又没有后续的观察者来消费这个结果，难么workamnager会在
//                //内存中保留一段时间的该任务的结果。超过这个时间，这个结果就会被存储到数据库中
//                //下次想要查询该任务的结果时，会触发workmanager的数据库查询操作，可以通过uuid来查询任务的状态
//                .keepResultsForAtLeast(10, TimeUnit.SECONDS)
                .build();
        return request;
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == CaptureActivity.REQ_CAPTURE && data != null) {
//            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
//            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
//            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
//            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);
//
//            showFileThumbnail();
//        }
//    }
}
