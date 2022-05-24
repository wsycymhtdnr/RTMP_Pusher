package kim.hsl.rtmp;

import android.app.Application;

//import com.tencent.mmkv.MMKV;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //MMKV.initialize(this); //初始化mmkv
    }
}