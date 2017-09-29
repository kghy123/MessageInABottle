package com.kghy1234gmail.messagesinabottle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LetterReplyActivity extends AppCompatActivity {

    EditText letterreply_tv_note, letterreply_tv_title;

    String sendLetterUrl = "http://kghy123.dothome.co.kr/MessageInABottle/sendMessageTo.php";

    String sent_id, recv_id;

    String title, note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter_reply);
        init();

    }

    public void init(){
        letterreply_tv_note = (EditText)findViewById(R.id.letterreply_tv_note);
        letterreply_tv_title = (EditText)findViewById(R.id.letterreply_tv_title);

        sent_id = getIntent().getStringExtra("recv_id");
        recv_id = getIntent().getStringExtra("sent_id");


    }

    public void clickBack(View v){
        finish();
    }

    public void clickSend(View v){

        title = letterreply_tv_title.getText().toString();
        title = title.replace("\n", " ");
        note = letterreply_tv_note.getText().toString();
        note = note.replace("\n", " ");

        new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(sendLetterUrl);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String now = sdf.format(date);

                    OutputStream os = conn.getOutputStream();
                    String data = "sent_id="+sent_id+"&recv_id="+recv_id+"&title="+title+"&note="+note+"&date_sent="+now+"&date_read="+now+"&recv_read="+"0";

                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line  = br.readLine();
                    StringBuffer buffer = new StringBuffer();
                    while(line != null){
                        buffer.append(line);
                        line = br.readLine();
                    }

                    Log.d("messageTo", buffer.toString());

                    finish();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();


    }

}
