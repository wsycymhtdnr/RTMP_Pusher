package kim.hsl.rtmp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import com.tencent.mmkv.MMKV;

import kim.hsl.rtmp.publish.PublishActivity;

/**
 * @Author: liyuncong
 * @Description:
 * @Date: 2022-05-15 03:00
 */
public class MineFragment extends BaseFragment {
    private Button mBtStartLive;
    private TextView mTvUserNameMime;
    private Button mBtPublish;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences share = getContext().getSharedPreferences("myshare",MODE_PRIVATE);
        String phone = share.getString("phone","");
        if (!TextUtils.isEmpty(phone)) {
            mTvUserNameMime.setText(phone);
        }
    }

    @Override
    protected void initView() {
        mBtStartLive = mRootView.findViewById(R.id.bt_start_live);
        mTvUserNameMime = mRootView.findViewById(R.id.tv_user_name_mine);
        mBtPublish = mRootView.findViewById(R.id.bt_post_file);
        mBtPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MineFragment.this.getContext().startActivity(new Intent(MineFragment.this.getContext(), PublishActivity.class));
            }
        });
        mTvUserNameMime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MineFragment.this.getContext().startActivity(new Intent(MineFragment.this.getContext(), LoginActivity.class));
            }
        });
        mBtStartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MineFragment.this.getContext().startActivity(new Intent(MineFragment.this.getContext(), PushActivity.class));
            }
        });
    }
}
