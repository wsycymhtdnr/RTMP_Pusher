package kim.hsl.rtmp;

import static kim.hsl.rtmp.publish.PublishActivity.BASE_URL;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import kim.hsl.rtmp.model.JsonResponse;
import kim.hsl.rtmp.model.User;
import kim.hsl.rtmp.net.Api;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author: liyuncong
 * @Description:
 * @Date: 2022-05-15 15:43
 */
public class RegisterActivity extends AppCompatActivity {
    private MaterialToolbar mToolbar;
    private Button mBtnRegister;
    private EditText mEtPhone;
    private EditText mEtPassword;
    Api request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEtPhone = findViewById(R.id.et_phone);
        mEtPassword = findViewById(R.id.et_password);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        request = retrofit.create(Api.class);
//region 加密公钥，暂时不用
//        Call<JsonResponse<String>> call = request.getRSA();
//        call.enqueue(new Callback<JsonResponse<String>>() {
//            @Override
//            public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {
//                if (response.body() != null) {
//                    Log.e("NET", "RSA: " + response.body().getData());
//                } else {
//                    Log.e("NET", "RSA: " + "response.body() is null");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonResponse<String>> call, Throwable throwable) {
//                Log.e("NET", "RSA onFailure: " + throwable);
//            }
//        });
//endregion


        mToolbar = findViewById(R.id.mtoolbar_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String phone = mEtPhone.getText().toString();
                String password = mEtPassword.getText().toString();
                if (phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "账号或密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Call<JsonResponse<String>> call = request.register(new User(phone, password));
                call.enqueue(new Callback<JsonResponse<String>>() {
                    @Override
                    public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {
                        if (response.body() != null && response.body().getCode().equals("0")) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonResponse<String>> call, Throwable throwable) {
                        Log.e("NET", "RSA onFailure: " + throwable);
                    }
                });
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
