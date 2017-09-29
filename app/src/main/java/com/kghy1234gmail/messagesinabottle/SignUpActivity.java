package com.kghy1234gmail.messagesinabottle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    ImageView signup_img_id, signup_img_pw, signup_img_pw_confirm;

    EditText signup_et_id, signup_et_pw, signup_et_pw_confirm;

    Button signup_btn_signup, signup_btn_checkid;

    boolean canMakeId = false;
    boolean canMakePw = false;
    String id;

    String checkListUrl = "http://kghy123.dothome.co.kr/MessageInABottle/checkID.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sign_in);

        init();

    }

    public void init(){
        signup_img_id = (ImageView)findViewById(R.id.signup_img_id);
        signup_img_pw = (ImageView)findViewById(R.id.signup_img_pw);
        signup_img_pw_confirm = (ImageView)findViewById(R.id.signup_img_pw_confirm);

        signup_et_id = (EditText)findViewById(R.id.signup_et_id);
        signup_et_pw = (EditText)findViewById(R.id.signup_et_pw);
        signup_et_pw_confirm = (EditText)findViewById(R.id.signup_et_pw_confirm);

        signup_btn_checkid = (Button)findViewById(R.id.signup_btn_checkid);
        signup_btn_signup = (Button)findViewById(R.id.signup_btn_signup);

        signup_et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(signup_et_id.getText().toString().equals(""))signup_btn_checkid.setEnabled(false);
                else {
                    signup_btn_checkid.setEnabled(true);
                    if(canMakePw)signup_btn_signup.setEnabled(true);
                }

                if(canMakeId && !signup_et_id.toString().equals(id)){
                canMakeId = false;
                Glide.with(SignUpActivity.this).load(R.drawable.x).into(signup_img_id);
            }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        signup_et_pw_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(signup_et_pw_confirm.getText().toString().equals(signup_et_pw.getText().toString())){
                    Glide.with(SignUpActivity.this).load(R.drawable.check).into(signup_img_pw);
                    Glide.with(SignUpActivity.this).load(R.drawable.check).into(signup_img_pw_confirm);
                    canMakePw = true;
                    if(canMakeId)signup_btn_signup.setEnabled(true);
                }else{
                    Glide.with(SignUpActivity.this).load(R.drawable.x).into(signup_img_pw);
                    Glide.with(SignUpActivity.this).load(R.drawable.x).into(signup_img_pw_confirm);
                    canMakePw = false;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    public void clickCheckId(View v){

        //데이터베이스를 읽어와서 ID 중복확인
        id = signup_et_id.getText().toString();

        new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(checkListUrl);
                    HttpURLConnection conn =(HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    String data = "id=" + id;
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = br.readLine();
                    StringBuffer buffer = new StringBuffer();

                    while(line != null){
                        buffer.append(line);
                        line = br.readLine();
                    }

                    Log.d("buffer", buffer.toString());

                    if(buffer.toString().equals("1")){
                        canMakeId = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {Glide.with(SignUpActivity.this).load(R.drawable.check).into(signup_img_id);}
                        });
                        Log.d("check", "가능");
                    }else if(buffer.toString().equals("0")){
                        Log.d("check", "중복");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    public void clickSignUp(View v){

        if(canMakeId && canMakePw){
            getIntent().putExtra("id", id);
            getIntent().putExtra("pw", signup_et_pw.getText().toString());
            setResult(RESULT_OK, getIntent());
            finish();
        }

    }


    public void clickBack(View v){
        finish();
    }


}
