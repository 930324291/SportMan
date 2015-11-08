package com.agitation.sportman.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.View;

import com.agitation.sportman.BaseActivity;
import com.agitation.sportman.R;
import com.agitation.sportman.utils.DataHolder;
import com.agitation.sportman.utils.MapTransformer;
import com.agitation.sportman.utils.Mark;
import com.agitation.sportman.utils.SharePreferenceUtil;
import com.agitation.sportman.utils.ToastUtils;
import com.androidquery.AQuery;
import com.androidquery.auth.BasicHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by fanwl on 2015/9/18.
 */
public class Login extends BaseActivity {

    private TextInputLayout login_username,login_password;
    private AppCompatCheckBox re_password;
    private AQuery aq;
    private boolean isRemeber = false;
    public static final String IS_RM_PW="IS_RM_PW";
    public static final String LOGIN_UN_NAME="LOGIN_UN_NAME";
    public static final String LOGIN_PW_NAME="LOGIN_PW_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initToolbar();
        init();
        initVarible();
    }

    private void initToolbar() {
        if (toolbar!=null){
            title.setText("登录");
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initVarible() {
        aq = new AQuery(this);
    }

    private void init() {
        login_username = (TextInputLayout) findViewById(R.id.login_username);
        login_password = (TextInputLayout) findViewById(R.id.login_password);
        re_password = (AppCompatCheckBox) findViewById(R.id.re_password);

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToLogin();
//                startActivity(new Intent(Login.this,MainTabActivity.class));
            }
        });
        findViewById(R.id.login_to_registered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Registered.class));
            }
        });

        isRemeber = SharePreferenceUtil.getBoolean(this, "isRemeber", false);
        re_password.setChecked(isRemeber);

        if (isRemeber){
            String password = SharePreferenceUtil.getString(Login.this, "passWord", "");
            login_password.getEditText().setText(password);
        }
        String name = SharePreferenceUtil.getString(Login.this, "name", "");
        login_username.getEditText().setText(name);
    }

    private void getToLogin(){
        final String name = login_username.getEditText().getText().toString().trim();
        final String password = login_password.getEditText().getText().toString().trim();

        View focusView = null;
        boolean toLogin = true;
        if (TextUtils.isEmpty(name)){
            login_username.setError("用户名不能为空");
            focusView = login_username;
            toLogin = false;
        }else if (TextUtils.isEmpty(password)){
            login_password.setError("密码不能为空");
            focusView = login_password;
            toLogin = false;
        }
        if (!toLogin){
            focusView.requestFocus();
        }


        String url = Mark.getServerIp()+"/baseApi/login";
        Map<String,Object> param = new HashMap<>();
        param.put("userName",name);
        param.put("passWord", password);
        aq.transformer(new MapTransformer()).ajax(url,param,Map.class,new AjaxCallback<Map>(){
            @Override
            public void callback(String url, Map result, AjaxStatus status) {
                if (result!=null){
                    boolean isRegistered = Boolean.parseBoolean(result.get("result")+"");
                    if (isRegistered){
                        isRemeber = re_password.isChecked();
                        if (isRemeber){
                            SharePreferenceUtil.setValue(Login.this,"passWord",password);
                        }
                        SharePreferenceUtil.setValue(Login.this,"name",name);
                        SharePreferenceUtil.setValue(Login.this,"isRemeber",isRemeber);
                        DataHolder dataHolder = DataHolder.getInstance();
                        dataHolder.setBasicHandle(new BasicHandle(name,password));
                        startActivity(new Intent(Login.this, MainTabActivity.class));
                    }else {
                        ToastUtils.showToast(Login.this, "注册失败" + "," + result.get("error"));
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        UpdateManager updateManager = new UpdateManager(Login.this);
//        updateManager.checkUpdate();
    }
}
