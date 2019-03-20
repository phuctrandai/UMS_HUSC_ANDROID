package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;

import java.util.List;

public class LinkListViewAdapter extends RecyclerView.Adapter<LinkListViewAdapter.DataViewHolder> {
    List<String> linkList;
    Context context;

    public LinkListViewAdapter(Context context, List<String> linkList) {
        this.context = context;
        this.linkList = linkList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.link_item, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int i) {
        final String link = linkList.get(i);

        dataViewHolder.tvLienKet.setText(link);
    }

    @Override
    public int getItemCount() {
        return linkList == null ? 0 : linkList.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLienKet;

        public DataViewHolder(View itemView) {
            super(itemView);

            tvLienKet = itemView.findViewById(R.id.tv_link);
        }
    }

}
