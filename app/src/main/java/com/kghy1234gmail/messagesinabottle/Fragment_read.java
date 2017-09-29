package com.kghy1234gmail.messagesinabottle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Fragment_read extends android.support.v4.app.Fragment {

    String checkLettersUrl="http://kghy123.dothome.co.kr/MessageInABottle/checkLetters.php";

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    FragmentReadAdapter adapter;

    ArrayList<Letter> letters = new ArrayList<>();

    LoadLettersTask task;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read, container, false);
        init(view);
        return view;
    }

    public void init(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.fragment_read_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.fragment_read_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity)getActivity()).refresh();
                task = new LoadLettersTask();
                task.execute();


            }
        });


        adapter = new FragmentReadAdapter(getContext(), letters);
        recyclerView.setAdapter(adapter);
        task = new LoadLettersTask();
        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        task = new LoadLettersTask();
        task.execute();
        adapter.notifyDataSetChanged();
    }

    class LoadLettersTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            letters.clear();

            try {
                URL url = new URL(checkLettersUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                OutputStream os = conn.getOutputStream();
                String data = "id="+((MainActivity)getActivity()).id;
                os.write(data.getBytes());
                os.flush();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = br.readLine();
                String[] splits;

                while(line!=null){
                    splits = line.split("&");

                    Log.d("splits", splits[0] + splits[1] + splits[2] + splits[3] + splits[4] + splits[5]+ splits[6] + splits[7] + splits[8]);
                    Letter letter = new Letter();
                    letter.no = Integer.parseInt(splits[0]);
                    letter.recv_id = splits[1];
                    letter.sent_id = splits[2];
                    letter.title = splits[3];
                    letter.note = splits[4];
                    letter.date_sent = splits[5];
                    letter.date_read = splits[6];
                    letter.recv_read = splits[7];
                    letter.img = splits[8];
                    letter.recv_del = 1;
                    letter.sent_del = 1;

                    letters.add(letter);



                    line= br.readLine();
                    publishProgress();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.scrollToPosition(letters.size()-1);
        }
    }

}
