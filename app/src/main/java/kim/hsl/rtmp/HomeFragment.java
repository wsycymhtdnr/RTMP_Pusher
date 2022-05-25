package kim.hsl.rtmp;

import static kim.hsl.rtmp.publish.PublishActivity.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kim.hsl.rtmp.model.CourseModel;
import kim.hsl.rtmp.model.JsonResponse;
import kim.hsl.rtmp.net.Api;
import kim.hsl.rtmp.util.Base64Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author: liyuncong
 * @Description:
 * @Date: 2022-05-15 03:00
 */
public class HomeFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private Api request;
    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initView() {
        mRecyclerView = mRootView.findViewById(R.id.rv_course);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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
        Call<JsonResponse<CourseModel>> call = request.getVideos(100, 1, "0");
        call.enqueue(new Callback<JsonResponse<CourseModel>>() {
            @Override
            public void onResponse(Call<JsonResponse<CourseModel>> call, Response<JsonResponse<CourseModel>> response) {
                if (response.body() != null && response.body().getCode().equals("0")) {
                    List<CourseModel.ListDTO> list = response.body().getData().getList();
                    mRecyclerView.setAdapter(new CourseAdapter(HomeFragment.this.getContext(), list));
                }

            }

            @Override
            public void onFailure(Call<JsonResponse<CourseModel>> call, Throwable throwable) {

            }
        });

    }

    static class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
        private List<CourseModel.ListDTO> mList;
        private Context mContext;

        public CourseAdapter(Context context, List<CourseModel.ListDTO> list) {
            mContext = context;
            mList = list;
        }

        @NonNull
        @Override
        public CourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_recyc, viewGroup, false);
            return new CourseAdapter.CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseAdapter.CourseViewHolder courseViewHolder, int i) {
            courseViewHolder.tvTitle.setText(mList.get(i).getTitle());
            courseViewHolder.num.setText(mList.get(i).getNum() + "人学习了");
            Glide.with(mContext).load(Base64Utils.base64ToBitmap(mList.get(i).getThumbnail())).into(courseViewHolder.imageView);
            courseViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "点击了课程", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, LiveActivity.class);
                    intent.putExtra("title", mList.get(i).getTitle());
                    intent.putExtra("url", mList.get(i).getUrl());
                    mContext.startActivity(intent);
                    //mContext.startActivity(new Intent(mContext, LiveActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            private CardView root;
            private TextView tvTitle;
            private ImageView imageView;
            private TextView num;

            public CourseViewHolder(@NonNull View itemView) {
                super(itemView);
                root = itemView.findViewById(R.id.root);
                tvTitle = itemView.findViewById(R.id.tv_title_item_study);
                imageView = itemView.findViewById(R.id.iv_badge_item_study);
                num = itemView.findViewById(R.id.tv_students_item_course);
            }
        }

    }
}
