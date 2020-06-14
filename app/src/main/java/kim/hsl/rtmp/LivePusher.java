package kim.hsl.rtmp;

import android.app.Activity;
import android.view.SurfaceHolder;

public class LivePusher {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 处理音频的通道
     */
    private AudioChannel mAudioChannel;

    /**
     * 处理视频的通道
     */
    private VideoChannel mVideoChannel;

    /**
     * 创建直播推流器
     * @param activity
     *          初始化推流功能的界面
     * @param width
     *          图像宽度
     * @param height
     *          图像高度
     * @param bitrate
     *          视频码率
     * @param fps
     *          视频帧率
     * @param cameraId
     *          指定摄像头
     */
    public LivePusher(Activity activity, int width, int height, int bitrate,
                      int fps, int cameraId) {
        // 初始化 native 层的环境
        //native_init();
        // 初始化视频处理通道
        mVideoChannel = new VideoChannel(this, activity, width, height, bitrate, fps, cameraId);
        // 初始化音频处理通道
        mAudioChannel = new AudioChannel();
    }

    /**
     * 设置图像显示组件
     * @param surfaceHolder
     */
    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        mVideoChannel.setPreviewDisplay(surfaceHolder);
    }

    public void switchCamera() {
        mVideoChannel.switchCamera();
    }

    public void startLive(String path) {
        native_startRtmpPush(path);
        mVideoChannel.startLive();
        mAudioChannel.startLive();
    }

    public void stopLive(){
        mVideoChannel.stopLive();
        mAudioChannel.stopLive();
        native_stopPush();
    }


    /**
     * 初始化 NDK 环境
     */
    public native void native_init();

    /**
     * 开始向 RTMP 服务器推送数据
     * 连接远程 RTMP 服务器, 向该服务器推送数据
     * @param path
     */
    public native void native_startRtmpPush(String path);

    /**
     * 设置视频编码参数
     * @param width
     *          宽度
     * @param height
     *          高度
     * @param fps
     *          帧率
     * @param bitrate
     *          码率
     */
    public native void native_setVideoEncoderParameters(int width, int height, int fps, int bitrate);

    /**
     * 执行数据编码操作
     * @param data
     */
    public native void native_encodeCameraData(byte[] data);

    public native void native_stopPush();

    public native void native_release();
}
