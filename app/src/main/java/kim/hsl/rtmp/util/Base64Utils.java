package kim.hsl.rtmp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Base64Utils {
    public static void gcBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle(); // 回收图片所占的内存
            bitmap = null;
            System.gc(); // 提醒系统及时回收
        }
    }

    /**
     * @param @param  bitmap
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: bitmapToBase64
     * @Description: TODO(Bitmap 转换为字符串)
     */

    @SuppressLint("NewApi")
    public static String bitmapToBase64(Bitmap bitmap) {

        // 要返回的字符串
        String reslut = null;

        ByteArrayOutputStream baos = null;

        try {

            if (bitmap != null) {

                baos = new ByteArrayOutputStream();
                /**
                 * 压缩只对保存有效果bitmap还是原来的大小
                 */
                bitmap.compress(CompressFormat.JPEG, 30, baos);

                baos.flush();
                baos.close();
                // 转换为字节数组
                byte[] byteArray = baos.toByteArray();

                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return reslut;

    }

    /**
     * @param @param  base64String
     * @param @return 设定文件
     * @return Bitmap 返回类型
     * @throws
     * @Title: base64ToBitmap
     * @Description: TODO(base64l转换为Bitmap)
     */
    public static Bitmap base64ToBitmap(String base64String) {
        /*
         * byte[] decode = Base64.decode(base64String, Base64.DEFAULT); YuvImage
         * yuvimage = new YuvImage(decode, ImageFormat.NV21, 20, 20, null);//
         * 20、20分别是图的宽度与高度 ByteArrayOutputStream baos = new
         * ByteArrayOutputStream(); yuvimage.compressToJpeg(new Rect(0, 0, 20,
         * 20), 80, baos);// 80--JPG图片的质量[0-100],100最高 byte[] jdata =
         * baos.toByteArray(); Bitmap bitmap =
         * BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
         */

        byte[] decode = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);

        return bitmap;
    }
}