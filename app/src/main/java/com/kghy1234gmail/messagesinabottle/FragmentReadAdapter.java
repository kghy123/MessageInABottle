package com.kghy1234gmail.messagesinabottle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentReadAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Letter> letters;

    public FragmentReadAdapter(Context context, ArrayList<Letter> letters) {
        this.context = context;
        this.letters = letters;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.fragment_read_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolder)holder).tv_title.setText(letters.get(position).title);
        ((ViewHolder)holder).tv_from.setText(letters.get(position).sent_id);
        ((ViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LettersInfoActivity.class);

                intent.putExtra("no", letters.get(position).no);
                intent.putExtra("recv_id", letters.get(position).recv_id);
                intent.putExtra("sent_id", letters.get(position).sent_id);
                intent.putExtra("title", letters.get(position).title);
                intent.putExtra("note", letters.get(position).note);
                intent.putExtra("date_sent", letters.get(position).date_sent);
                intent.putExtra("date_read", letters.get(position).date_read);
                intent.putExtra("recv_read", letters.get(position).recv_read);
                intent.putExtra("recv_del", letters.get(position).recv_del);
                intent.putExtra("sent_del", letters.get(position).sent_del);
                intent.putExtra("img", letters.get(position).img);

                context.startActivity(intent);
            }
        });

        int year=0, month=0, day=0;
        int h=0,m=0,s=0;
        String sentTime = letters.get(position).date_sent;
        Log.d("senttime", sentTime);
        String[] times = sentTime.split(" ");
        year = Integer.parseInt(times[0].substring(0,4));
        month = Integer.parseInt(times[0].substring(5,7));
        day = Integer.parseInt(times[0].substring(8,10));

        times = times[1].split(":");
        h = Integer.parseInt(times[0]);
        m = Integer.parseInt(times[1]);
        s = Integer.parseInt(times[2]);

        int nYear=0, nMonth=0, nDay=0;
        int nH=0,nM=0,nS=0;
        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Log.d("nowDate", nowDate);
        String[] nowTimes = nowDate.split(" ");
        nYear = Integer.parseInt(nowTimes[0].substring(0,4));
        nMonth = Integer.parseInt(nowTimes[0].substring(5,7));
        nDay = Integer.parseInt(nowTimes[0].substring(8,10));

        nowTimes = nowTimes[1].split(":");
        nH = Integer.parseInt(nowTimes[0]);
        nM = Integer.parseInt(nowTimes[1]);
        nS = Integer.parseInt(nowTimes[2]);

        if(nYear - year > 0){
            ((ViewHolder)holder).tv_time.setText(nYear-year + "년 전");
        }else if(nMonth - month > 0){
            ((ViewHolder)holder).tv_time.setText(nMonth - month + "달 전");
        }else if(nDay - day > 0){
            ((ViewHolder)holder).tv_time.setText(nDay - day + "일 전");
        }else if(nH - h > 0){
            ((ViewHolder)holder).tv_time.setText(nH - h + "시간 전");
        }else if(nM - m > 0){
            ((ViewHolder)holder).tv_time.setText(nM - m + "분 전");
        }else if(nS - s > 0){
            ((ViewHolder)holder).tv_time.setText(nS - s + "초 전");
        }


    }



    @Override
    public int getItemCount() {
        return letters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        TextView tv_from;
        TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_title = (TextView)itemView.findViewById(R.id.tv_title);
            tv_from = (TextView)itemView.findViewById(R.id.tv_from);
            tv_time = (TextView)itemView.findViewById(R.id.tv_time);
        }


    }
}
