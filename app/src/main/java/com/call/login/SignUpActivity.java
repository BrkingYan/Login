package com.call.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("创建账户中...");
        progressDialog.show();

        //保存账户信息
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        User user = new User();
        user.setUserName(name);
        user.setEmail(email);
        user.setPassword(password);
        //从xml获取map
        String mapStr = SharedPreferencesUtil.getString(SignUpActivity.this,Constant.USER_POOL_FILE_NAME,Constant.USER_POOL_KEY);
        Map<String,User> userPool = new Gson().fromJson(mapStr,new TypeToken<Map<String,User>>(){}.getType());
        if (userPool == null){
            userPool = new HashMap<>();
            Log.e("test",userPool.toString());
        }
        userPool.put(email,user);
        Log.e("test",userPool.toString());
        //保存到xml
        //SharedPreferencesUtil.clear(SignUpActivity.this,Constant.USER_POOL_FILE_NAME);//删除之前的map
        SharedPreferencesUtil.saveMap(SignUpActivity.this,Constant.USER_POOL_FILE_NAME,Constant.USER_POOL_KEY,userPool);
        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    //验证密码
    public boolean validate() {

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("请输入至少3个字符");
            return false;
        } else {
            _nameText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("请输入正确的邮箱名");
            return false;
        } else {
            _emailText.setError(null);
        }


        String mapStr = SharedPreferencesUtil.getString(SignUpActivity.this,Constant.USER_POOL_FILE_NAME,Constant.USER_POOL_KEY);
        Map<String,User> userPool = new Gson().fromJson(mapStr,new TypeToken<Map<String,User>>(){}.getType());

        if (userPool != null && userPool.containsKey(email)){
            _emailText.setError("该用户已注册");
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("请输入4-10位字母");
            return false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("密码不符");
            return false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return true;
    }
}
