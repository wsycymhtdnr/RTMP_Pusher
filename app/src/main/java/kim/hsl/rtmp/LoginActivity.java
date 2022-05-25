package kim.hsl.rtmp;

import static kim.hsl.rtmp.publish.PublishActivity.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
//import com.tencent.mmkv.MMKV;

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
public class LoginActivity extends AppCompatActivity {
    private MaterialToolbar mToolbar;
    private TextView mTvRegisterLogin;
    private Button mBtnLogin;
    private EditText mEtPhone;
    private EditText mEtPassword;
    Api request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        request = retrofit.create(Api.class);
        mEtPhone = findViewById(R.id.et_phone);
        mBtnLogin = findViewById(R.id.btn_login);
        mEtPassword = findViewById(R.id.et_password);
        mToolbar = findViewById(R.id.mtoolbar_login);
        mTvRegisterLogin = findViewById(R.id.tv_register_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mEtPhone.getText().toString();
                String password = mEtPassword.getText().toString();
                if (phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "账号或密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Call<JsonResponse<String>> call = request.login(new User(phone, password));
                call.enqueue(new Callback<JsonResponse<String>>() {
                    @Override
                    public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {
                        if (response.body() != null && response.body().getCode().equals("0")) {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            SharedPreferences share = getSharedPreferences("myshare",MODE_PRIVATE);
                            SharedPreferences.Editor edr = share.edit();
                            edr.putString("phone",phone);
                            edr.commit();
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonResponse<String>> call, Throwable throwable) {

                    }
                });
            }
        });
        mTvRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
