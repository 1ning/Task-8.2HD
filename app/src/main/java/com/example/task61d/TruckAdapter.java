package com.example.task61d;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.MyViewHolder>{
    private List<Truck>list;
    private View inflater;
    private Context mContext;



    public TruckAdapter(Context context,List<Truck> list) {
        this.list = list;
        mContext = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(mContext).inflate(R.layout.add_contacts,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(inflater);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(list.get(position).type);
        switch (list.get(position).type)
        {
            case "Truck":
                holder.photo.setImageResource(R.drawable.truck_1);
                break;
            case "Van":
                holder.photo.setImageResource(R.drawable.truck_2);
                break;
            case "Refrigeratedtruck":
                holder.photo.setImageResource(R.drawable.truck_3);
                break;
            case "Minitruck":
                holder.photo.setImageResource(R.drawable.truck_4);
                break;
            case "Other":
                holder.photo.setImageResource(R.drawable.truck_5);
                break;
        }
        holder.context.setText(list.get(position).context);

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView photo;
        TextView context;
        ImageButton share;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            photo = (ImageView) itemView.findViewById(R.id.photo);
            context = (TextView) itemView.findViewById(R.id.context);
            share = (ImageButton) itemView.findViewById(R.id.share);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title1= (String) title.getText();
                    String context1= (String) context.getText();
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    String extraText="This car type is"+title1+", besides its Model is"+context1;
                    share.putExtra(Intent.EXTRA_TEXT, extraText);
                    mContext.startActivity(Intent.createChooser(share,"SHARE"));
                }
            });
        }
    }
}

