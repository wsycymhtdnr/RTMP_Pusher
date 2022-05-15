package kim.hsl.rtmp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: liyuncong
 * @Description:
 * @Date: 2022-05-15 03:00
 */
public class CourseFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private List<String> mList;
    private String[] mCourses = {"课程0", "课程1", "课程2", "课程3", "课程4", "课程5"};

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_course;
    }

    @Override
    protected void initView() {
        mRecyclerView = mRootView.findViewById(R.id.rv_course);
        mList = Arrays.asList(mCourses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(new CourseAdapter(this.getContext(), mList));
    }

    static class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
        private List<String> mList;
        private Context mContext;

        public CourseAdapter(Context context, List<String> list) {
            mContext = context;
            mList = list;
        }

        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course, viewGroup, false);
            return new CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder courseViewHolder, int i) {
            courseViewHolder.tvTitle.setText(mList.get(i));
            courseViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(mContext, "点击了课程", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(new Intent(mContext, LiveActivity.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            private TextView tvTitle;

            public CourseViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
            }
        }

    }
}
