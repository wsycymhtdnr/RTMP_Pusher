package kim.hsl.rtmp.net;

import java.util.List;

import kim.hsl.rtmp.model.JsonResponse;
import kim.hsl.rtmp.model.User;
import kim.hsl.rtmp.model.Video;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * @Author: liyunfei
 * @Description: 网络接口
 * @Date: 2022-05-23 22:21
 */
public interface Api {
    @GET("/rsa-pks")
    Call<JsonResponse<String>> getRSA();

    @POST("/users")
    Call<JsonResponse<String>> register(@Body User user);

    @POST("/user-tokens")
    Call<JsonResponse<String>> login(@Body User user);

    @POST("/videos")
    Call<JsonResponse<String>> publish(@Body Video video);

    @Multipart
    @PUT("/file-slices")
    Call<JsonResponse<String>> upload(@Part MultipartBody.Part slice, @Part("fileMd5") String fileMd5, @Part("sliceNo") int sliceNo, @Part("totalSliceNo") int totalSliceNo);
}
