package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.NewsModule.DetailNewsActivity;
import com.practice.phuc.ums_husc.R;

import java.util.List;

public class NewsRecyclerDataAdapter extends RecyclerView.Adapter<NewsRecyclerDataAdapter.DataViewHolder> {
    private List<THONGBAO> thongBaoList;
    private Context context;

    public NewsRecyclerDataAdapter(Context context, List<THONGBAO> thongbaos) {
        this.thongBaoList = thongbaos;
        this.context = context;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news, viewGroup, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsRecyclerDataAdapter.DataViewHolder viewHolder, int i) {
        final String tieuDe = thongBaoList.get(i).getTieuDe();
        viewHolder.tvTieuDe.setText(tieuDe);

        final String thoiGianDang = thongBaoList.get(i).getThoiGianDang();
        final String ngayDang = DateHelper.formatYMDToDMY(thoiGianDang.substring(0, 10));
        final String gioDang = thoiGianDang.substring(11, 16);
        final String thoiGianDangStr = ngayDang + " " + gioDang;
        viewHolder.tvThoiGianDang.setText(thoiGianDangStr);

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, DetailNewsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Reference.getInstance().BUNDLE_EXTRA_NEWS, THONGBAO.toJson(thongBaoList.get(position)));

                context.startActivity(intent);
            }
        });
        setFadeAnimation(viewHolder.mRootLayout, i);
    }

    @Override
    public int getItemCount() {
        return thongBaoList == null ? 0 : thongBaoList.size();
    }

    public int lastPosition = -1;

    private void setFadeAnimation(View view, int position) {
        if (context != null && (position > lastPosition)) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_translate_animation);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull final DataViewHolder holder) {
        holder.clearAnimation();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView mRootLayout;
        private TextView tvTieuDe;
        private TextView tvThoiGianDang;


        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootLayout = itemView.findViewById(R.id.news_item_layout);
            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
            tvThoiGianDang = itemView.findViewById(R.id.tv_thoiGianDang);

            itemView.setOnClickListener(this);
        }

        private ItemClickListener itemClickListener;

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        void clearAnimation() {
            mRootLayout.clearAnimation();
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    private interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }

    public void changeDataSet(List<THONGBAO> messageList) {
        thongBaoList.clear();
        thongBaoList.addAll(messageList);
        notifyDataSetChanged();
    }

    public void insertItem(THONGBAO item, int position) {
        for (int i = 0; i < thongBaoList.size(); i++) {
            if (thongBaoList.get(i).getMaThongBao() == item.getMaThongBao()) {
                return;
            }
        }
        thongBaoList.add(position, item);
        notifyItemInserted(position);
    }

    public void insertItemRange(List<THONGBAO> messageList, int positionStart, int itemCount) {
        thongBaoList.addAll(messageList);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void clearDataSet() {
        thongBaoList.clear();
    }

}
