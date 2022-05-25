package kim.hsl.rtmp.publish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wlf.mediapick.MediaPicker;
import com.wlf.mediapick.entity.MediaEntity;
import com.wlf.mediapick.entity.MediaPickConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import kim.hsl.rtmp.R;
import kim.hsl.rtmp.model.JsonResponse;
import kim.hsl.rtmp.model.Video;
import kim.hsl.rtmp.model.VideoTag;
import kim.hsl.rtmp.net.Api;
import kim.hsl.rtmp.util.Base64Utils;
import kim.hsl.rtmp.util.Bitmaptest;
import kim.hsl.rtmp.util.MD5Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PublishActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://df12-171-113-194-1.ngrok.io/";
    private ImageView mIvClose;
    private Button mBtPublish;
    private EditText mInput;
    private ImageView mIvAddFile;
    private int width, height;
    private String filePath, fileName, fileType, mRemoteFilePath;
    private boolean isVideo = true;
    private UUID coverUploadUUID, fileUploadUUID;
    private String coverUploadUrl, fileUploadUrl;
    private Api request;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.i("RetrofitLog","retrofitBack = "+message);
            }
        });

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(600000, TimeUnit.MILLISECONDS)
                .readTimeout(600000, TimeUnit.MILLISECONDS)
                .build();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(BASE_URL)
                .build();
        request = retrofit.create(Api.class);

        mIvClose = findViewById(R.id.action_close);
        mBtPublish = findViewById(R.id.action_publish);
        mInput = findViewById(R.id.input_view);
        mIvAddFile = findViewById(R.id.action_add_file);
        mBtPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });

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

    private void publish() {
        Video video = new Video();
        video.setArea("0");
        if (mInput.getText().toString().equals("")) {
            Toast.makeText(PublishActivity.this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBitmap == null) {
            Toast.makeText(PublishActivity.this, "请先选择要上传的视频", Toast.LENGTH_SHORT).show();
            return;
        }
        //Log.d("PublishActivity",Base64Utils.bitmapToBase64(Bitmaptest.scaleImage(mBitmap, 50, 50)).length() + "");
        video.setThumbnail(Base64Utils.bitmapToBase64(Bitmaptest.scaleImage(mBitmap, mBitmap.getWidth() / 10, mBitmap.getHeight()/10)));
        //video.setThumbnail("12345");
        video.setType("0");
        video.setDuration("1213");
        video.setDescription("description");
        video.setTitle(mInput.getText().toString());
        video.setUrl(mRemoteFilePath);
        VideoTag videoTag = new VideoTag(1L, 1L);
        List<VideoTag> list = new ArrayList<>();
        list.add(videoTag);
        video.setVideoTagList(list);
        Call<JsonResponse<String>> call = request.publish(video);
        call.enqueue(new Callback<JsonResponse<String>>() {
            @Override
            public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {
                if (response.body() != null && response.body().getCode().equals("0")) {
                    Toast.makeText(PublishActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<String>> call, Throwable throwable) {

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
                fileName = list.get(0).getName();
                fileType = list.get(0).getMimeType();
                Log.d("dasdad", list.get(0).getMimeType());
                upload();
            }
        }
    }

    private void upload() {
        File file = new File(filePath);

        MultipartBody.Part part = MultipartBody.Part.createFormData("slice",
                fileName, RequestBody.create(file, MediaType.parse(fileType)));
        Call<JsonResponse<String>> call = request.upload(part, MD5Utils.getMD5(fileName), 1, 1);
        call.enqueue(new Callback<JsonResponse<String>>() {
            @Override
            public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {
                if (response.body() != null && response.body().getCode().equals("0")) {
                    mBitmap = getLocalVideoBitmap(filePath);
                    mIvAddFile.setImageBitmap(mBitmap);
                    mRemoteFilePath = response.body().getData();
                    Toast.makeText(PublishActivity.this, "视频上传成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PublishActivity.this, "视频上传失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<String>> call, Throwable throwable) {
                Toast.makeText(PublishActivity.this, "视频上传失败", Toast.LENGTH_SHORT).show();
                Log.d("PublishActivity:", throwable.toString());
            }
        });
    }

    public Bitmap getLocalVideoBitmap(String localPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(localPath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }
}
