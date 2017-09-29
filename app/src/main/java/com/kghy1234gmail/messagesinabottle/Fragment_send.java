package com.kghy1234gmail.messagesinabottle;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment_send extends android.support.v4.app.Fragment {

    String saveImg;
    String imgPath;
    Uri uri;
    String imgUploadUri = "http://kghy123.dothome.co.kr/MessageInABottle/imageUpload.php";

    EditText etTitle;
    EditText etNote;

    ImageView img;

    String title;
    String note;

    Button sendBtn,selectBtn;
    String sendMessageUrl = "http://kghy123.dothome.co.kr/MessageInABottle/sendMessage.php";
    String sendMessageToUrl = "http://kghy123.dothome.co.kr/MessageInABottle/sendMessageTo.php";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_send, container, false);
        init(view);

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case 10:
                if(resultCode == getActivity().RESULT_CANCELED) return;

                uri = data.getData();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(uri);
                getContext().sendBroadcast(intent);

                imgPath = data.getData().toString();
                Log.d("imgPath", imgPath);

                if (imgPath.contains("content://")) {
                    ContentResolver resolver = getContext().getContentResolver();
                    Cursor cursor = resolver.query(uri, null, null, null, null);
                    if (cursor != null && cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.d("imgPath", imgPath);
                    }

                }

                Glide.with(getContext()).load(imgPath).into(img);

                break;


        }
    }

    public void init(View view){
        img = (ImageView)view.findViewById(R.id.fragment_send_img);

        selectBtn = (Button)view.findViewById(R.id.fragment_read_selet_btn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ImageSelectActivity.class);
                ((MainActivity)getActivity()).startActivityForResult(intent, 10);
            }
        });

        sendBtn = (Button)view.findViewById(R.id.fragment_read_send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectBtn.setEnabled(true);
                note = etNote.getText().toString();
                note = note.replace("\n", " ");
                title = etTitle.getText().toString();
                title = title.replace("\n", " ");

                etNote.setText("");
                etTitle.setText("");

                new Thread(){
                    @Override
                    public void run() {
                        try {

                            if(imgPath != null) {
                                RequestQueue queue = Volley.newRequestQueue(getContext());

                                SimpleMultiPartRequest request = new SimpleMultiPartRequest(Request.Method.POST, imgUploadUri, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        saveImg = response;

                                        Glide.with(getContext()).load("").into(img);

                                        new Thread(){
                                            @Override
                                            public void run() {
                                                URL url = null;
                                                try {
                                                    url = new URL(sendMessageUrl);

                                                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                                    conn.setDoOutput(true);
                                                    conn.setDoOutput(true);
                                                    conn.setRequestMethod("POST");
                                                    conn.setUseCaches(false);

                                                    String data = "id=" + ((MainActivity)getActivity()).id;

                                                    OutputStream os = conn.getOutputStream();
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

                                                    String recv_id = buffer.toString();
                                                    Log.d("recv_id", recv_id);

                                                    url = new URL(sendMessageToUrl);
                                                    conn = (HttpURLConnection)url.openConnection();
                                                    conn.setDoOutput(true);
                                                    conn.setDoInput(true);
                                                    conn.setRequestMethod("POST");
                                                    conn.setUseCaches(false);

                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    Date date = new Date();
                                                    String now = sdf.format(date);

                                                    os = conn.getOutputStream();
                                                    data = "sent_id="+((MainActivity)getActivity()).id+"&recv_id="+recv_id+"&title="+title+"&note="+note+"&date_sent="+now+"&date_read="+now+"&recv_read="+"0" + "&img=" + (saveImg==null?0:saveImg);
                                                    os.write(data.getBytes());
                                                    os.flush();
                                                    os.close();

                                                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                                    line  = br.readLine();
                                                    buffer = new StringBuffer();
                                                    while(line != null){
                                                        buffer.append(line);
                                                        line = br.readLine();
                                                    }

                                                    Log.d("messageTo", buffer.toString());

                                                    saveImg = null;
                                                    title = null;
                                                    note = null;
                                                    imgPath = null;

                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                } catch (ProtocolException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }.start();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("ERROR", error.toString());
                                    }
                                });

                                request.addFile("upload", imgPath);
                                request.addStringParam("title", "title");
                                queue.add(request);

                            }else{
                                URL url = new URL(sendMessageUrl);
                                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                conn.setDoOutput(true);
                                conn.setDoOutput(true);
                                conn.setRequestMethod("POST");
                                conn.setUseCaches(false);

                                String data = "id=" + ((MainActivity)getActivity()).id;

                                OutputStream os = conn.getOutputStream();
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

                                String recv_id = buffer.toString();
                                Log.d("recv_id", recv_id);

                                url = new URL(sendMessageToUrl);
                                conn = (HttpURLConnection)url.openConnection();
                                conn.setDoOutput(true);
                                conn.setDoInput(true);
                                conn.setRequestMethod("POST");
                                conn.setUseCaches(false);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                String now = sdf.format(date);

                                os = conn.getOutputStream();
                                data = "sent_id="+((MainActivity)getActivity()).id+"&recv_id="+recv_id+"&title="+title+"&note="+note+"&date_sent="+now+"&date_read="+now+"&recv_read="+"0" + "&img=" + (saveImg==null?0:saveImg);
                                os.write(data.getBytes());
                                os.flush();
                                os.close();

                                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                line  = br.readLine();
                                buffer = new StringBuffer();
                                while(line != null){
                                    buffer.append(line);
                                    line = br.readLine();
                                }

                                Log.d("messageTo", buffer.toString());

                                saveImg = null;
                                title = null;
                                note = null;
                                imgPath = null;
                            }


                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }


                }.start();

            }
        });

        etNote = (EditText)view.findViewById(R.id.fragment_send_et_note);
        etTitle = (EditText)view.findViewById(R.id.fragment_send_et_title);
    }

}
