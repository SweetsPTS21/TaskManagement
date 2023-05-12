package com.example.demoktra2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoktra2.model.Item;
import com.example.demoktra2.R;
import com.example.demoktra2.model.UserTask;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter  extends RecyclerView.Adapter<RecycleViewAdapter.HomeViewHolder>{
    private List<UserTask> list;
    private ItemListener itemListener;
    public RecycleViewAdapter() {
        list = new ArrayList<>();
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setList(List<UserTask> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public UserTask getItem(int position){
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        UserTask userTask = list.get(position);
        holder.title.setText(userTask.getTitle());
        holder.title2.setText(userTask.getDescription());
        holder.date.setText(userTask.getDate());
        holder.time.setText(userTask.getTime());
        holder.category.setText(userTask.getStatus());
        holder.category2.setText(userTask.getCategory());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title,category,title2,date,time, category2;

        public HomeViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.tvTitle);
            title2 = view.findViewById(R.id.tvTitle2);
            date = view.findViewById(R.id.tvDate);
            time = view.findViewById(R.id.tvTime);
            category = view.findViewById(R.id.tvCategory);
            category2 = view.findViewById(R.id.tvCategory2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemListener!=null){
                itemListener.onItemClick(view,getAdapterPosition());
            }
        }
    }
    public interface ItemListener{
        void onItemClick(View view,int position);
    }
}
