package kim.hsl.rtmp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @Author: liyuncong
 * @Description:
 * @Date: 2022-05-15 02:58
 */
public abstract class BaseFragment extends Fragment {
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutRes(), container, false);
        initView();
        return mRootView;
    }

    protected void initView() {
    }

    protected abstract int getLayoutRes();
}
