package com.kghy1234gmail.messagesinabottle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LettersInfoActivity extends AppCompatActivity {

    TextView lettersinfo_tv_title, lettersinfo_tv_date_sent, lettersinfo_tv_note;

    int no;
    String recv_id, sent_id, title, note, date_sent, date_read, recv_read;

    ImageView img;

    String deleteLetterUrl = "http://kghy123.dothome.co.kr/MessageInABottle/deleteLetter.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letters_info);

        init();

    }

    public void init(){

        lettersinfo_tv_date_sent = (TextView)findViewById(R.id.lettersinfo_tv_date_sent);
        lettersinfo_tv_title = (TextView)findViewById(R.id.lettersinfo_tv_title);
        lettersinfo_tv_note = (TextView)findViewById(R.id.lettersinfo_tv_note);

        img = (ImageView)findViewById(R.id.lettersinfo_img);

        no = getIntent().getIntExtra("no", 0);
        recv_id = getIntent().getStringExtra("recv_id");
        sent_id = getIntent().getStringExtra("sent_id");
        title = getIntent().getStringExtra("title");
        note = getIntent().getStringExtra("note");
        date_sent = getIntent().getStringExtra("date_sent");
        date_read = getIntent().getStringExtra("date_read");
        recv_read = getIntent().getStringExtra("recv_read");
        String imgUrl = "http://kghy123.dothome.co.kr/MessageInABottle/"+getIntent().getStringExtra("img");

        Glide.with(this).load(imgUrl).into(img);

        lettersinfo_tv_date_sent.setText(date_sent);
        lettersinfo_tv_title.setText(title);
        lettersinfo_tv_note.setText(note);

    }

    public void clickDelete(View v){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("");
        dialog.setMessage("삭제하시겠습니까?");
        dialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                new Thread(){
                    @Override
                    public void run() {

                        try {
                            URL url = new URL(deleteLetterUrl);
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.setUseCaches(false);
                            conn.setRequestMethod("POST");

                            OutputStream os = conn.getOutputStream();
                            String data = "sent_id=" + sent_id + "&recv_id=" + recv_id + "&date_sent=" + date_sent;
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

                            Log.d("DELETELETTER", buffer.toString());

                            finish();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

            }
        });
        dialog.setNegativeButton("아니오",null);

        dialog.show();

    }

    public void clickReply(View v){

        Intent intent = new Intent(this, LetterReplyActivity.class);
        intent.putExtra("sent_id", sent_id);
        intent.putExtra("recv_id", recv_id);
        startActivity(intent);

        new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(deleteLetterUrl);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");

                    OutputStream os = conn.getOutputStream();
                    String data = "sent_id=" + sent_id + "&recv_id=" + recv_id + "&date_sent=" + date_sent;
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

                    Log.d("DELETELETTER", buffer.toString());

                    finish();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        finish();

    }


}
