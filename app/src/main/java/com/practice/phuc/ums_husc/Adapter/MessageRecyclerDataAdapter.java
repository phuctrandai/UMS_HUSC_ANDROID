package com.practice.phuc.ums_husc.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.StringHelper;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.List;

public class MessageRecyclerDataAdapter extends RecyclerView.Adapter<MessageRecyclerDataAdapter.DataViewHolder> {

    private Context mContext;
    private List<TINNHAN> mTinNhanList;
    public int mLastPosition;

    public MessageRecyclerDataAdapter(Context context, List<TINNHAN> tinNhanList) {
        mContext = context;
        mTinNhanList = tinNhanList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        return new DataViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final DataViewHolder viewHolder, int i) {
        final TINNHAN tinnhan = mTinNhanList.get(i);

        final String tieuDe = tinnhan.getTieuDe();
        final String thoiDiemGui = tinnhan.getThoiDiemGui();
        final String hoTenNguoiGui = tinnhan.getHoTenNguoiGui();
        String ngayDang = DateHelper.formatYMDToDMY(thoiDiemGui.substring(0, 10));
        String gioDang = thoiDiemGui.substring(11, 16);
        final String thoiGianDangStr = ngayDang + " " + gioDang;
        final String nguoiNhan = tinnhan.getTenNguoiNhanCollapse();

        viewHolder.tvTieuDe.setText(tieuDe);
        viewHolder.tvNguoiGui.setText(hoTenNguoiGui);
        viewHolder.tvNguoiNhan.setText(nguoiNhan);
        viewHolder.tvThoiDiemGui.setText(thoiGianDangStr);
        viewHolder.tvNguoiGuiLabel.setText(StringHelper.getFirstCharToCap(hoTenNguoiGui));

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(mContext, DetailMessageActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(tinnhan));
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
        private RelativeLayout mRootLayout;
        private TextView tvTieuDe;
        private TextView tvThoiDiemGui;
        private TextView tvNguoiGui;
        private TextView tvNguoiGuiLabel;
        private TextView tvNguoiNhan;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootLayout = itemView.findViewById(R.id.message_item_layout);
            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
            tvNguoiGui = itemView.findViewById(R.id.tv_nguoiGui);
            tvThoiDiemGui = itemView.findViewById(R.id.tv_thoiDiemGui);
            tvNguoiGuiLabel = itemView.findViewById(R.id.tv_nguoiGuiLabel);
            tvNguoiNhan = itemView.findViewById(R.id.tv_nguoiNhan);

            itemView.setOnClickListener(this);
        }

        private ItemClickListener itemClickListener;
        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        private void clearAnimation() {
            mRootLayout.clearAnimation();
        }
    }

    private interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
}
