package com.kghy1234gmail.messagesinabottle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LogoutService extends Service {

    String id;
    String updateLogoutStateUrl = "http://kghy123.dothome.co.kr/MessageInABottle/updateLogoutState.php";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(){
            @Override
            public void run() {

                try {
                    InputStream is = openFileInput("info.json");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line1 = reader.readLine();
                    StringBuffer stringBuffer = new StringBuffer();
                    while(line1 != null){
                        stringBuffer.append(line1);
                        line1 = reader.readLine();
                    }

                    JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                    id = jsonObject.getString("id");

                    Log.d("logoutID", id);


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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
