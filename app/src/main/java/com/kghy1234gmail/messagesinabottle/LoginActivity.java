package com.kghy1234gmail.messagesinabottle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    final int REQ_SIGNUP = 5;

    EditText etId;
    EditText etPw;

    String id;
    String pw;

    String signUpUrl = "http://kghy123.dothome.co.kr/MessageInABottle/SignUp.php";
    String makeTableUrl = "http://kghy123.dothome.co.kr/MessageInABottle/makeTable.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_login);

        init();

    }

    public void init(){

        etId = (EditText)findViewById(R.id.login_et_id);
        etPw = (EditText)findViewById(R.id.login_et_pw);

        login();

    }


    //회원가입
    public void clickSignUp(View v){

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivityForResult(intent, REQ_SIGNUP);

    }

    //로그인
    public void clickSignIn(View v){

        //로그인된 화면으로 이동
        login();

    }

    public void login(){
        try {

            InputStream is = openFileInput("info.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            StringBuffer buffer = new StringBuffer();
            while(line != null){
                buffer.append(line);
                line = br.readLine();
            }

            JSONObject object = new JSONObject(buffer.toString());

            id = object.getString("id");
            pw = object.getString("pw");

            //로그인된 화면으로 이동

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("pw", pw);

            startActivity(intent);
            finish();

        } catch (FileNotFoundException e) {

            Toast.makeText(this, "다시 한번 확인해주세요!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {

        } catch (JSONException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_SIGNUP:
                if(resultCode == RESULT_OK){


                    id = data.getStringExtra("id");
                    pw = data.getStringExtra("pw");

                    Log.d("id", id);
                    Log.d("pw", pw);

                    etId.setText(id);
                    etPw.setText(pw);

                    new Thread(){
                        @Override
                        public void run() {
                            try {

                                URL url = new URL(signUpUrl);
                                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setDoInput(true);
                                conn.setDoOutput(true);
                                conn.setDefaultUseCaches(false);


                                OutputStream os = conn.getOutputStream();
                                String data = "id=" + id + "&pw=" + pw;
                                os.write(data.getBytes());
                                os.flush();
                                os.close();

                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                String line = br.readLine();
                                StringBuffer buffer = new StringBuffer();
                                while (line!=null){
                                    buffer.append(line);
                                    line = br.readLine();
                                }

                                Log.d("insert", buffer.toString());

                                url = new URL(makeTableUrl);
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setDoOutput(true);
                                conn.setDoInput(true);
                                conn.setRequestMethod("POST");
                                conn.setUseCaches(false);

                                data = "id=" + id;
                                os = conn.getOutputStream();
                                os.write(data.getBytes());
                                os.flush();
                                os.close();

                                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                                line = br.readLine();
                                buffer = new StringBuffer();
                                while(line != null){
                                    buffer.append(line);
                                    line = br.readLine();
                                }

                                Log.d("table", buffer.toString());

                                os = openFileOutput("info.json", MODE_PRIVATE);
                                JSONObject object = new JSONObject();
                                object.put("id", id);
                                object.put("pw", pw);

                                Log.d("json", object.toString());

                                os.write(object.toString().getBytes());
                                os.flush();
                                os.close();


                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
                break;
        }
    }
}
