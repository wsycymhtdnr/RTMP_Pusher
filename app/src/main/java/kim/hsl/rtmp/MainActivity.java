package kim.hsl.rtmp;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private Fragment[] mFragments = {new HomeFragment(), new CourseFragment(), new MineFragment()};
    private Map<MenuItem, Integer> MenuItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.vp2_main);
        mBottomNavigationView = findViewById(R.id.bnv_main);
        // 给下方导航菜单设置index
        for (int i = 0; i < mBottomNavigationView.getMenu().size(); i++) {
            MenuItems.put(mBottomNavigationView.getMenu().getItem(i), i);
        }
        mViewPager.setAdapter(new MainViewPagerAdapter(this, Arrays.asList(mFragments)));

        // ViewPager 和 BottomNavigationView 相互绑定
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mBottomNavigationView.setSelectedItemId(mBottomNavigationView.getMenu().getItem(position).getItemId());
            }
        });
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (MenuItems.containsKey(menuItem) && MenuItems.get(menuItem) != null) {
                    mViewPager.setCurrentItem(MenuItems.get(menuItem));
                }
                return true;
            }
        });
    }


    // ViewPager 适配器
    static class MainViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> mFragments;

        public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            mFragments = fragments;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @NonNull
        @Override
        public Fragment createFragment(int i) {
            return Objects.requireNonNull(mFragments.get(i));
        }

        @Override
        public int getItemCount() {
            return mFragments.size();
        }
    }
}