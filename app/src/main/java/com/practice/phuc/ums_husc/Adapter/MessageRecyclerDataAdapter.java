package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.practice.phuc.ums_husc.DetailMessageActivity;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.List;

public class MessageRecyclerDataAdapter extends RecyclerView.Adapter<MessageRecyclerDataAdapter.DataViewHolder> {

    private Context mContext;
    private List<TINNHAN> mTinNhanList;
    public int lastPosition;

    public MessageRecyclerDataAdapter(Context context, List<TINNHAN> tinNhanList) {
        mContext = context;
        mTinNhanList = tinNhanList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {

        final String tieuDe = mTinNhanList.get(i).getTieuDe();
        final String thoiDiemGui = mTinNhanList.get(i).getThoiDiemGui();
        final String noiDung = mTinNhanList.get(i).getNoiDung();
        final String nguoiGui = mTinNhanList.get(i).getNguoiGui();

//        viewHolder.tvTieuDe.setText(tieuDe);
//        viewHolder.tvNguoiGui.setText(nguoiGui);

        final String thoiGianDangStr =
                thoiDiemGui != null ? thoiDiemGui.substring(0, 10) + " " + thoiDiemGui.substring(11, 16)
                        : "";
//        viewHolder.tvThoiDiemGui.setText(thoiGianDangStr);

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Bundle bundle = new Bundle();
                bundle.putString("title", tieuDe);
                bundle.putString("postTime", thoiGianDangStr);
                bundle.putString("body", noiDung);
                bundle.putString("sender", nguoiGui);
                Intent intent = new Intent(mContext, DetailMessageActivity.class);
                intent.putExtra("news", bundle);
                mContext.startActivity(intent);
            }
        });
        setFadeAnimation(viewHolder.mRootLayout, i);
    }

    @Override
    public int getItemCount() {
        return mTinNhanList == null ? 0 : mTinNhanList.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull final DataViewHolder viewHolder) {
        ((DataViewHolder) viewHolder).clearAnimation();
    }

    private void setFadeAnimation(View view, int position) {
        if (mContext != null && position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView mRootLayout;
        private TextView tvTieuDe;
        private TextView tvThoiDiemGui;
        private TextView tvNguoiGui;

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootLayout = itemView.findViewById(R.id.message_item_layout);
            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
            tvNguoiGui = itemView.findViewById(R.id.tv_nguoiGui);
            tvThoiDiemGui = itemView.findViewById(R.id.tv_thoiDiemGui);

            itemView.setOnClickListener(this);
        }

        public void clearAnimation() {
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
}
