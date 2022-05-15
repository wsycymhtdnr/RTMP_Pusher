package kim.hsl.rtmp;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @Author: liyuncong
 * @Description:
 * @Date: 2022-05-15 03:00
 */
public class MineFragment extends BaseFragment {
    private Button mBtStartLive;
    private TextView mTvUserNameMime;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        mBtStartLive = mRootView.findViewById(R.id.bt_start_live);
        mTvUserNameMime = mRootView.findViewById(R.id.tv_user_name_mine);
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
