package kim.hsl.rtmp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import kim.hsl.rtmp.model.CourseModel;

/**
 * @Author: liyuncong
 * @Description:
 * @Date: 2022-05-15 03:00
 */
public class HomeFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initView() {
        CourseModel course0 = new CourseModel("https://img.cniao5.com/FrYffHQKU6PGgjUgvmwUKMP25IgW", "这是一个测试课程", 100);
        CourseModel course1 = new CourseModel("https://img.cniao5.com/Fv5SR6xtqqqhKykUhSOhqqCiEB2u", "这是一个Python的测试课程-编辑", 101);
        CourseModel course2 = new CourseModel("https://img.cniao5.com/o_1ddq1fomphmm1b8l1qir1ejv1uaf7.png", "SpringCloud微服务实战", 108);
        CourseModel course3 = new CourseModel("https://img.cniao5.com/o_1dd53qgubgruu9lsuq16fmjfq7.png", "基础人工智能特训营", 150);
        CourseModel course4 = new CourseModel("https://img.cniao5.com/o_1dch1tl35nrhq079nbpoo1jlo7.png", "大数据舆情监控系统", 180);
        List<CourseModel> list = new ArrayList<>();
        list.add(course0);
        list.add(course1);
        list.add(course2);
        list.add(course3);
        list.add(course4);
        list.add(course0);
        list.add(course1);
        list.add(course2);
        list.add(course3);
        list.add(course4);

        mRecyclerView = mRootView.findViewById(R.id.rv_course);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(new CourseAdapter(this.getContext(), list));
    }

    static class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
        private List<CourseModel> mList;
        private Context mContext;

        public CourseAdapter(Context context, List<CourseModel> list) {
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
            Glide.with(mContext).load(mList.get(i).getImgString()).into(courseViewHolder.imageView);
            courseViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "点击了课程", Toast.LENGTH_SHORT).show();
                    //mContext.startActivity(new Intent(mContext, LiveActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            private TextView tvTitle;
            private ImageView imageView;
            private TextView num;

            public CourseViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title_item_study);
                imageView = itemView.findViewById(R.id.iv_badge_item_study);
                num = itemView.findViewById(R.id.tv_students_item_course);
            }
        }

    }
}
