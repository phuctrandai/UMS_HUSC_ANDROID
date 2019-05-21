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
import android.widget.FrameLayout;
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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final DataViewHolder viewHolder, int i) {
        setFadeAnimation(viewHolder.mRootLayout, i);

        final TINNHAN tinnhan = mTinNhanList.get(i);
        String tieuDe = tinnhan.TieuDe;
        String thoiDiemGui = tinnhan.ThoiDiemGui;
        String hoTenNguoiGui = tinnhan.HoTenNguoiGui;
        String tenNguoiNhanCollapse = tinnhan.getTenNguoiNhanCollapse();
        String thoiGianDangStr = thoiDiemGui;
        if (thoiDiemGui.length() > 16) {
            String ngayDang = DateHelper.formatYMDToDMY(thoiDiemGui.substring(0, 10));
            String gioDang = thoiDiemGui.substring(11, 16);
            thoiGianDangStr = ngayDang + " " + gioDang;
        }
        viewHolder.tvTieuDe.setText(tieuDe);
        viewHolder.tvNguoiGui.setText(hoTenNguoiGui);
        viewHolder.tvNguoiNhan.setText(tenNguoiNhanCollapse);
        viewHolder.tvThoiDiemGui.setText(thoiGianDangStr);
        viewHolder.tvNguoiGuiLabel.setText(StringHelper.getFirstCharToCap(hoTenNguoiGui));

//        String maTaiKhoan = Reference.getAccountId(mContext);
//        final NGUOINHAN nguoiNhan = tinnhan.getNguoiNhanTrongDanhSach(maTaiKhoan);

//        if (nguoiNhan != null && isNullOrEmpty(nguoiNhan.ThoiDiemXem)) {
//            viewHolder.tvTieuDe.setTypeface(null, Typeface.BOLD);
//            viewHolder.tvNguoiGui.setTypeface(null, Typeface.BOLD);
//            viewHolder.tvThoiDiemGui.setTypeface(null, Typeface.BOLD);
//            viewHolder.tvThoiDiemGui.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
//        }

        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(mContext, DetailMessageActivity.class);
                intent.putExtra(Reference.getInstance().BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(tinnhan));
                mContext.startActivity(intent);

//                if (nguoiNhan != null && isNullOrEmpty(nguoiNhan.ThoiDiemXem)) {
//                    MessageTaskHelper.getInstance().updateSeenTime(Integer.parseInt(tinnhan.MaTinNhan),
//                            Reference.getStudentId(mContext), Reference.getAccountPassword(mContext));
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            nguoiNhan.ThoiDiemXem = DateHelper.toDateTimeString(DateHelper.getCalendar().getTime());
//                            viewHolder.tvTieuDe.setTypeface(null, Typeface.NORMAL);
//                            viewHolder.tvNguoiGui.setTypeface(null, Typeface.NORMAL);
//                            viewHolder.tvThoiDiemGui.setTypeface(null, Typeface.NORMAL);
//                            viewHolder.tvThoiDiemGui.setTextColor(mContext.getResources().getColor(R.color.colorDarkerGrey));
//                        }
//                    }, 1000);
//                }
            }
        });
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout viewBackground;
        public RelativeLayout viewBackground2;
        public RelativeLayout viewForeground;
        private FrameLayout mRootLayout;
        private TextView tvTieuDe;
        private TextView tvThoiDiemGui;
        private TextView tvNguoiGui;
        private TextView tvNguoiGuiLabel;
        private TextView tvNguoiNhan;
        private ItemClickListener itemClickListener;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootLayout = itemView.findViewById(R.id.message_item_layout);
            viewBackground = itemView.findViewById(R.id.layout_background);
            viewBackground2 = itemView.findViewById(R.id.layout_background_2);
            viewForeground = itemView.findViewById(R.id.layout_foreground);
            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
            tvNguoiGui = itemView.findViewById(R.id.tv_nguoiGui);
            tvThoiDiemGui = itemView.findViewById(R.id.tv_thoiDiemGui);
            tvNguoiGuiLabel = itemView.findViewById(R.id.tv_nguoiGuiLabel);
            tvNguoiNhan = itemView.findViewById(R.id.tv_nguoiNhan);

            itemView.setOnClickListener(this);
        }

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

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mTinNhanList == null ? 0 : mTinNhanList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        for(int i=0; i < mTinNhanList.size(); i++) {
            if (mTinNhanList.get(i).MaTinNhan.equals(item.MaTinNhan))
                return;
        }
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
        for (int i=0 ; i<mTinNhanList.size(); i++) {
            if (mTinNhanList.get(i).MaTinNhan.equals(item.MaTinNhan)) {
                mTinNhanList.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void clearDataSet() {
        mTinNhanList.clear();
    }
}
