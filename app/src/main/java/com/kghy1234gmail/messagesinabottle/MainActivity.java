package com.kghy1234gmail.messagesinabottle;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String updateLoginStateUrl = "http://kghy123.dothome.co.kr/MessageInABottle/updateLoginState.php";
    String updateLogoutStateUrl = "http://kghy123.dothome.co.kr/MessageInABottle/updateLogoutState.php";
    String howmanypeopleUrl= "http://kghy123.dothome.co.kr/MessageInABottle/howmanypeople.php";

    TextView tvPeople;
    TabLayout tabLayout;
    ViewPager viewPager;

    MainFragmentPagerAdapter adapter;

    String id;
    String people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        new HowManyPeople().start();

    }

    public void init(){

        tvPeople = (TextView)findViewById(R.id.main_tv_people);

        tabLayout = (TabLayout)findViewById(R.id.main_tablayout);
        viewPager = (ViewPager)findViewById(R.id.main_viewpager);

        adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        id = getIntent().getStringExtra("id");

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 100:

                if(grantResults[0]!=PackageManager.PERMISSION_GRANTED) finish();
                break;
        }
    }

    public void refresh(){
        new Thread(){
            @Override
            public void run() {

                //현재 접속자수 띄우기
                new HowManyPeople().start();


            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:

                Fragment_send fragment = (Fragment_send) getSupportFragmentManager().getFragments().get(1);
                fragment.onActivityResult(requestCode, resultCode, data);

                break;

        }
    }

    public void updateLogoutState(){

        new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(updateLogoutStateUrl);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    String data = "id=" + id;
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = br.readLine();
                    StringBuffer buffer = new StringBuffer();
                    while(line!=null){
                        buffer.append(line);
                        line = br.readLine();
                    }

                    Log.d("UPDATE",  "LogOut" + buffer.toString());



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    public void updateLoginState(){

        new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(updateLoginStateUrl);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    String data = "id=" + id;
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = br.readLine();
                    StringBuffer buffer = new StringBuffer();
                    while(line!=null){
                        buffer.append(line);
                        line = br.readLine();
                    }

                    Log.d("UPDATE",  "LogIn" + buffer.toString());



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginState();
        new HowManyPeople().start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, LogoutService.class);
        intent.putExtra("url", updateLogoutStateUrl);
        intent.putExtra("id", id);
        startService(intent);
    }

    class HowManyPeople extends Thread{
        @Override
        public void run() {

            URL url = null;
            try {
                url = new URL(howmanypeopleUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = br.readLine();
                StringBuffer buffer = new StringBuffer();
                while(line!=null){
                    buffer.append(line);
                    line = br.readLine();
                }

                people = buffer.toString();

                Log.d("people",  "people" + buffer.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPeople.setText("현재 접속자 수 : " + people);
                    }
                });


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
