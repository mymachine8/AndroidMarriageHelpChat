package com.happyhome.kkommanapall;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.happyhome.kkommanapall.model.CareService;

import java.util.ArrayList;
import java.util.List;

public class CareRecyclerViewAdapter extends RecyclerView
        .Adapter<CareRecyclerViewAdapter
        .DataObjectHolder> {
    private ArrayList<CareService> mDataset;
    private static CareItemClickListener mCareClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView txtServiceName;
        TextView txtChatExec;
        TextView txtCallExec;
        public DataObjectHolder(View itemView) {
            super(itemView);
            txtServiceName = (TextView) itemView.findViewById(R.id.careservice_text);
            txtChatExec = (TextView) itemView.findViewById(R.id.chatExecutive);
            txtCallExec = (TextView) itemView.findViewById(R.id.callExecutive);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCareClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(CareItemClickListener careClickListener) {
        this.mCareClickListener = careClickListener;
    }

    public CareRecyclerViewAdapter(List<CareService> myDataset) {
        mDataset = (ArrayList)myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.careservice_list_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.txtServiceName.setText(mDataset.get(position).getServiceName());
    }

    public void addItem(CareService dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public CareService getItem(int index){
       return mDataset.get(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface CareItemClickListener {
        public void onItemClick(int position, View v);
    }
}