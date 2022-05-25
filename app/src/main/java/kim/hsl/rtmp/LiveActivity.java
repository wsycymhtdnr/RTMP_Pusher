package kim.hsl.rtmp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.hengyi.fastvideoplayer.library.FastVideoPlayer;

import java.util.ArrayList;
import java.util.List;

public class LiveActivity extends AppCompatActivity {
    private FastVideoPlayer videoPlayer;
    private static final String BASE_VIDEO_URL = "http://112.124.14.174:8888/group1/";
    private String userPhone = "游客";
    private RecyclerView mRecyclerView;
    private List<String> mList = new ArrayList<>();
    private MaterialButton comment_send;
    private EditText input;
    private DanmuAdapter adapter;
    //private Button play;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        comment_send = findViewById(R.id.comment_send);
        input = findViewById(R.id.input_view);
        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(input.getText().toString())) {
                    Toast.makeText(LiveActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    mList.add(userPhone + ":" + input.getText().toString());
                    adapter.notifyItemInserted(mList.size());
                }
            }
        });
        SharedPreferences share = getSharedPreferences("myshare",MODE_PRIVATE);
        String phone = share.getString("phone","");
        if (!TextUtils.isEmpty(phone)) {
            userPhone =phone;
        }
        mRecyclerView = findViewById(R.id.rv_danmu);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DanmuAdapter(this, mList);
        mRecyclerView.setAdapter(adapter);
        videoPlayer = findViewById(R.id.fastvideo_player);
        //play = findViewById(R.id.play);
        videoPlayer.setLive(true);//是直播还是点播  false为点播
        videoPlayer.setScaleType(FastVideoPlayer.SCALETYPE_FITXY);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String title = bundle.getString("title");
            String url = bundle.getString("url");
            if (title != null && url != null) {
                videoPlayer.setTitle(title);//设置标题
                videoPlayer.setUrl(BASE_VIDEO_URL + url);
            } else {
                videoPlayer.setTitle("课程*");//设置标题
                videoPlayer.setUrl("rtmp://112.124.14.174/live/0");
            }
        } else {
            videoPlayer.setTitle("课程*");//设置标题
            videoPlayer.setUrl("rtmp://112.124.14.174/live/0");
        }
        //videoPlayer.setUrl("http://dlhls.cdn.zhanqi.tv/zqlive/49427_jmACJ.m3u8");
        //封面图加载
        //Glide.with(this).load("https://bot.tmall.com/guide/img/guide1-bg760.png").into(videoPlayer.getCoverImage());

//        play.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                videoPlayer.play();
//            }
//        });

//        videoPlayer.setScreenListener(new FastVideoPlayerScreenListener() {
//            @Override
//            public void onFullScreen() {
//                Toast.makeText(MainActivity.this,"进入全屏",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onSmallScreen() {
//                Toast.makeText(MainActivity.this,"进入小屏",Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (videoPlayer != null) {
            videoPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayer != null) {
            videoPlayer.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null) {
            videoPlayer.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoPlayer != null) {
            videoPlayer.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (videoPlayer != null && videoPlayer.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    static class DanmuAdapter extends RecyclerView.Adapter<DanmuAdapter.DanmuViewHolder> {
        private List<String> mList;
        private Context mContext;

        public DanmuAdapter(Context context, List<String> list) {
            mContext = context;
            mList = list;
        }

        @NonNull
        @Override
        public DanmuAdapter.DanmuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_danmu, viewGroup, false);
            return new DanmuAdapter.DanmuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DanmuAdapter.DanmuViewHolder danmuViewHolder, int i) {
            danmuViewHolder.tvTitle.setText(mList.get(i));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class DanmuViewHolder extends RecyclerView.ViewHolder {
            private TextView tvTitle;

            public DanmuViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_danmu);
            }
        }

    }

}
