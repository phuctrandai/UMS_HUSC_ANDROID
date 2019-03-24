package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.DetailNewsActivity;
import com.practice.phuc.ums_husc.Helper.JustifyTextInTextView;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.R;

import java.util.List;

public class NewsRecyclerDataAdapter extends RecyclerView.Adapter<NewsRecyclerDataAdapter.DataViewHolder> {
    private List<THONGBAO> thongBaoList;
    private Context context;

    public NewsRecyclerDataAdapter(List<THONGBAO> thongbaos, Context context) {
        this.thongBaoList = thongbaos;
        this.context = context;
    }

    @NonNull
    @Override
    public  DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsRecyclerDataAdapter.DataViewHolder viewHolder, int i) {
        final String tieuDe = thongBaoList.get(i).getTieuDe();
        final String thoiGianDang = thongBaoList.get(i).getThoiGianDang();
        final String noiDung = thongBaoList.get(i).getNoiDung();

        viewHolder.tvTieuDe.setText(tieuDe);
        JustifyTextInTextView.justify(viewHolder.tvTieuDe);

        final String thoiGianDangStr = thoiGianDang.substring(0, 10) + " " + thoiGianDang.substring(11, 16);
        viewHolder.tvThoiGianDang.setText(thoiGianDangStr);

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Bundle bundle = new Bundle();
                bundle.putString("title", tieuDe);
                bundle.putString("postTime", thoiGianDangStr);
                bundle.putString("body", noiDung);
                Intent intent = new Intent(context, DetailNewsActivity.class);
                intent.putExtra("news", bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return thongBaoList == null ? 0 : thongBaoList.size();
    }

    /**
     * Data ViewHolder class.
     */
    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTieuDe;
        private TextView tvThoiGianDang;

        private ItemClickListener itemClickListener;

        public DataViewHolder(View itemView) {
            super(itemView);

            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
            tvThoiGianDang = itemView.findViewById(R.id.tv_thoiGianDang);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
}
