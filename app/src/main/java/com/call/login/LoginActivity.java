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

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        _loginButton.setOnClickListener(this);
        _signupLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:{
                login();
                break;
            }
            case R.id.link_signup:{
                // Start the Signup activity
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            }
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();


        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        String mapStr = SharedPreferencesUtil.getString(this,Constant.USER_POOL_FILE_NAME,Constant.USER_POOL_KEY);
        Log.e("test","mapStr:" + mapStr);
        Map<String,User> userPool = new Gson().fromJson(mapStr,new TypeToken<Map<String,User>>(){}.getType());
        Log.e("test","userPool:" + userPool);

        if (userPool == null || !userPool.containsKey(email)){
            Toast.makeText(LoginActivity.this,"该用户未注册",Toast.LENGTH_SHORT).show();
            return false;
        }

        User user = userPool.get(email);

        Log.e("test","json:" + mapStr);
        Log.e("test","userPool:" + userPool.toString());
        Log.e("test","user:" + userPool.get(email));
        Log.e("test","email:" + email);

        if (!user.getPassword().equals(password)){
            Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
            return false;
        }


        SharedPreferencesUtil.putObject(this,Constant.CURRENT_USER_FILE_NAME,Constant.CURRENT_USER_KEY,user);

        return true;
    }
}
