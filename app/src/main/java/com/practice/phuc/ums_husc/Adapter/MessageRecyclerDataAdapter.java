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

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.List;

public class MessageRecyclerDataAdapter extends RecyclerView.Adapter<MessageRecyclerDataAdapter.DataViewHolder> {

    private Context mContext;
    private List<TINNHAN> mTinNhanList;
    public int mLastPosition;
    private boolean mIsLoadReceivedMessage;

    public MessageRecyclerDataAdapter(Context context, List<TINNHAN> tinNhanList, boolean isLoadReceivedMessage) {
        mContext = context;
        mTinNhanList = tinNhanList;
        mIsLoadReceivedMessage = isLoadReceivedMessage;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_item, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder viewHolder, int i) {
        TINNHAN tinnhan = mTinNhanList.get(i);
        int soNguoiNhan = tinnhan.getNGUOINHANs().length;

        final String tieuDe = tinnhan.getTieuDe();
        final String thoiDiemGui = tinnhan.getThoiDiemGui();
        final String noiDung = tinnhan.getNoiDung();
        final String nguoiGui = tinnhan.getHoTenNguoiGui();

        String temp = "";
        if (soNguoiNhan > 0)
            temp = tinnhan.getNGUOINHANs()[(0)].getHoTenNguoiNhan();
        if (soNguoiNhan > 1)
            temp += " và " + (soNguoiNhan - 1) + " người khác";
        final String nguoiNhan = temp;

        final String[] dsTenNguoiNhan = new String[soNguoiNhan];
        for (int j = 0; j < soNguoiNhan; j++) {
            dsTenNguoiNhan[j] = tinnhan.getNGUOINHANs()[j].getHoTenNguoiNhan();
        }

        if (mIsLoadReceivedMessage) {
            viewHolder.tvNguoiGui.setText(nguoiGui);
        } else {
            viewHolder.tvNguoiGui.setText(nguoiNhan);
            viewHolder.tvNguoiGuiLabel.setVisibility(View.GONE);
            viewHolder.tvNguoiNhanLabel.setVisibility(View.VISIBLE);
        }

        String ngayDang = DateHelper.formatYMDToDMY(thoiDiemGui.substring(0, 10));
        String gioDang = thoiDiemGui.substring(11, 16);
        final String thoiGianDangStr = ngayDang + " " + gioDang;
        viewHolder.tvThoiDiemGui.setText(thoiGianDangStr);
        viewHolder.tvTieuDe.setText(tieuDe);

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Bundle bundle = new Bundle();
                bundle.putString("title", tieuDe);
                bundle.putString("postTime", thoiGianDangStr);
                bundle.putString("body", noiDung);
                bundle.putString("sender", nguoiGui);
                bundle.putString("receiver", nguoiNhan);
                bundle.putStringArray("receiverNameList", dsTenNguoiNhan);
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
        viewHolder.clearAnimation();
    }

    private void setFadeAnimation(View view, int position) {
        if (mContext != null && position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_translate_animation);
            view.startAnimation(animation);
            mLastPosition = position;
        }
    }

    protected static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView mRootLayout;
        private TextView tvTieuDe;
        private TextView tvThoiDiemGui;
        private TextView tvNguoiGui;
        private TextView tvNguoiGuiLabel;
        private TextView tvNguoiNhanLabel;

        private ItemClickListener itemClickListener;
        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootLayout = itemView.findViewById(R.id.message_item_layout);
            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
            tvNguoiGui = itemView.findViewById(R.id.tv_nguoiGui);
            tvThoiDiemGui = itemView.findViewById(R.id.tv_thoiDiemGui);
            tvNguoiGuiLabel = itemView.findViewById(R.id.tv_nguoiGuiLabel);
            tvNguoiNhanLabel = itemView.findViewById(R.id.tv_nguoiNhanLabel);

            itemView.setOnClickListener(this);
        }
        private void clearAnimation() {
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
