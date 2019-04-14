package com.practice.phuc.ums_husc.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.StringHelper;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.Model.NGUOINHAN;
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
        setFadeAnimation(viewHolder.mRootLayout, i);

        final TINNHAN tinnhan = mTinNhanList.get(i);

        String tieuDe = tinnhan.getTieuDe();
        String thoiDiemGui = tinnhan.getThoiDiemGui();
        String hoTenNguoiGui = tinnhan.getHoTenNguoiGui();
        final String ngayDang = DateHelper.formatYMDToDMY(thoiDiemGui.substring(0, 10));
        String gioDang = thoiDiemGui.substring(11, 16);
        String thoiGianDangStr = ngayDang + " " + gioDang;
        String tenNguoiNhanCollapse = tinnhan.getTenNguoiNhanCollapse();

        viewHolder.tvTieuDe.setText(tieuDe);
        viewHolder.tvNguoiGui.setText(hoTenNguoiGui);
        viewHolder.tvNguoiNhan.setText(tenNguoiNhanCollapse);
        viewHolder.tvThoiDiemGui.setText(thoiGianDangStr);
        viewHolder.tvNguoiGuiLabel.setText(StringHelper.getFirstCharToCap(hoTenNguoiGui));

        String maSinhVien = Reference.getAccountId(mContext);
        final NGUOINHAN nguoiNhan = tinnhan.getNguoiNhanTrongDanhSach(maSinhVien);
        if (nguoiNhan != null && nguoiNhan.getThoiDiemXem() == null) {
            viewHolder.tvTieuDe.setTypeface(null, Typeface.BOLD);
            viewHolder.tvNguoiGui.setTypeface(null, Typeface.BOLD);
            viewHolder.tvThoiDiemGui.setTypeface(null, Typeface.BOLD);
            viewHolder.tvThoiDiemGui.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (nguoiNhan != null) {
                    nguoiNhan.setThoiDiemXem(DateHelper.toShortDateString(DateHelper.getCalendar().getTime()));
                    viewHolder.tvTieuDe.setTypeface(null, Typeface.NORMAL);
                    viewHolder.tvNguoiGui.setTypeface(null, Typeface.NORMAL);
                    viewHolder.tvThoiDiemGui.setTypeface(null, Typeface.NORMAL);
                    viewHolder.tvThoiDiemGui.setTextColor(mContext.getResources().getColor(R.color.colorDarkerGrey));
                }
                Intent intent = new Intent(mContext, DetailMessageActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(tinnhan));
                mContext.startActivity(intent);
            }
        });
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

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private FrameLayout mRootLayout;
        RelativeLayout viewBackground;
        public RelativeLayout viewForeground;
        private TextView tvTieuDe;
        private TextView tvThoiDiemGui;
        private TextView tvNguoiGui;
        private TextView tvNguoiGuiLabel;
        private TextView tvNguoiNhan;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootLayout = itemView.findViewById(R.id.message_item_layout);
            viewBackground = itemView.findViewById(R.id.layout_background);
            viewForeground = itemView.findViewById(R.id.layout_foreground);
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

    /*##### Helper method #####*/

    public List<TINNHAN> getDataSet() {
        return mTinNhanList;
    }

    public void changeDataSet(List<TINNHAN> messageList) {
        mTinNhanList.clear();
        mTinNhanList.addAll(messageList);
        notifyDataSetChanged();
    }

    public void insertItem(TINNHAN item, int position) {
        mTinNhanList.add(position, item);
        notifyItemInserted(position);
    }

    public void insertItemRange(List<TINNHAN> messageList, int positionStart, int itemCount) {
        mTinNhanList.addAll(messageList);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void removeItem(int position) {
        mTinNhanList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(TINNHAN item) {
        int position = mTinNhanList.indexOf(item);
        mTinNhanList.remove(position);
        notifyItemRemoved(position);
    }

    public void clearDataSet() {
        mTinNhanList.clear();
//        notifyDataSetChanged();
    }
}
