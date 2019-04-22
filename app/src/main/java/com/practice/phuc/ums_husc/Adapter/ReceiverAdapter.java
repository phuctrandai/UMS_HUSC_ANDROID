package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.TaiKhoan;

import java.util.ArrayList;
import java.util.List;

public class ReceiverAdapter extends BaseAdapter {
    private Context mContext;
    private List<TaiKhoan> mReceiverList;

    public ReceiverAdapter(Context context) {
        mContext = context;
        mReceiverList = new ArrayList<>();
    }

    public List<TaiKhoan> getReceiverList() {
        return mReceiverList;
    }

    private void removeReceiver(TaiKhoan obj) {
        for (int i=0 ; i < mReceiverList.size(); i++) {
            if (mReceiverList.get(i).MaTaiKhoan.trim().equals(obj.MaTaiKhoan.trim())) {
                mReceiverList.remove(i);
                return;
            }
        }
    }

    public void insertReceiver(TaiKhoan obj) {
        mReceiverList.add(obj);
    }

    public boolean updateReceiverList(TaiKhoan obj) {
        boolean result;

        if (isInReceiverList(obj)) {
            removeReceiver(obj);
            result = false;
        }
        else {
            insertReceiver(obj);
            result = true;
        }
        notifyDataSetChanged();
        return result;
    }

    boolean isInReceiverList(TaiKhoan obj) {
        Log.d("DEBUG", "MTK: " + obj.MaTaiKhoan);

        for (TaiKhoan item : mReceiverList) {
            if (item.MaTaiKhoan.trim().equals(obj.MaTaiKhoan.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getCount() {
        return mReceiverList == null ? 0 : mReceiverList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReceiverList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mReceiverList.get(position).MaTaiKhoan);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_receiver, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvReceiverName = convertView.findViewById(R.id.tv_receiverName);
            viewHolder.btnRemove = convertView.findViewById(R.id.btn_removeReceiver);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvReceiverName.setText(mReceiverList.get(position).HoTen);
        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReceiverList(mReceiverList.get(position));
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private TextView tvReceiverName;
        private ImageButton btnRemove;
    }
}
