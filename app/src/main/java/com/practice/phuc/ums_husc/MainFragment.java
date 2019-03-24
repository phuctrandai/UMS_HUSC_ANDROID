package com.practice.phuc.ums_husc;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Adapter.NewsRecyclerDataAdapter;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Response;

public class MainFragment extends Fragment {
    private LoadThongBaoTask mLoadThongBaoTask;
    private Context context;
    private List<THONGBAO> thongBaoList;
    private Bundle mBundle;
    private String mErrorMessage;

    private final String STATUS_KEY = "statusKey";
    private final int STATUS_LOADING = 0;
    private final int STATUS_SHOW_ERROR = 1;
    private final int STATUS_SHOW_DATA = 2;
    private final int STATUS_NOT_NETWORK = 3;

    // Cast json to model
    Moshi moshi;
    Type usersType;
    JsonAdapter<List<THONGBAO>> jsonAdapter;

    // UI
    RecyclerView rvItems;
    LinearLayout mThongBaoLayout;
    RelativeLayout mProgressViewLayout;
    RelativeLayout mNetworkError;
    TextView mThongBaoLoi;
    Button mThuLai;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(Context context) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.context = context;
        return mainFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "On create MainFragment");
        mBundle = new Bundle();
        mBundle.putInt(STATUS_KEY, STATUS_LOADING);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DEBUG", "On create VIEW MainFragment");
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Bind UI
        rvItems = (RecyclerView) view.findViewById(R.id.rv_thongBao);
        mThongBaoLayout = view.findViewById(R.id.layout_thongBao);
        mProgressViewLayout = view.findViewById(R.id.loading_progress_layout);
        mNetworkError = view.findViewById(R.id.layout_thongBaoKhongCoMang);
        mThongBaoLoi = view.findViewById(R.id.tv_thongBaoLoi);
        mThuLai = view.findViewById(R.id.btn_thuLai);

        // Set up recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
        rvItems.setLayoutManager(layoutManager);
        rvItems.setHasFixedSize(true);

        // Set up button thu lai
        mThuLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempGetData();
            }
        });

        if (mBundle.getInt(STATUS_KEY) == STATUS_SHOW_DATA) {
            showProgress(false);
            showError(false, mErrorMessage);
            showData();
        } else if (mBundle.getInt(STATUS_KEY) == STATUS_SHOW_ERROR) {
            showError(true, mErrorMessage);
        } else {
            Log.d("DEBUG", "Get data");
            attempGetData();
        }
        return view;
    }

    @Override
    public void onPause() {
        Log.d("DEBUG", "On pause MainFragment");
        Log.d("DEBUG", mBundle.getInt(STATUS_KEY) + " - Fragment status");
        super.onPause();
    }

    private void attempGetData() {
        if (NetworkUtil.getConnectivityStatus(this.context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            mBundle.putInt(STATUS_KEY, STATUS_NOT_NETWORK);
            showError(true, getString(R.string.network_not_available));
        } else {
            mLoadThongBaoTask = new LoadThongBaoTask();
            mLoadThongBaoTask.execute((String) null);
        }
    }

    public class LoadThongBaoTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadThongBaoTask == null) return false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                mResponse = getData();
                if (mResponse == null) {

                    Log.d("DEBUG", "Response null");
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;

                } else {

                    Log.d("DEBUG", "Response code: " + mResponse.code());
                    if (mResponse.code() == NetworkUtil.OK) {

                        String json = mResponse.body().string();
                        setData(json);
                        return true;

                    } else if (mResponse.code() == NetworkUtil.NOT_FOUND) {
                        mErrorMessage = getString(R.string.error_time_out);
                    } else {
                        mErrorMessage = getString(R.string.error_time_out);
                    }
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d("DEBUG", "Get thong bao : " + success);
            if (mLoadThongBaoTask != null) {
                if (success) {
                    showData();
                    showProgress(false);
                } else {
                    showError(true, mErrorMessage);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mLoadThongBaoTask = null;
            super.onCancelled();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "DESTROY MAIN FRAGMENT");
        mLoadThongBaoTask = null;
        super.onDestroy();
    }

    // Save status
    private void saveStatus(int statusCode) {
        if (mBundle != null) {
            mBundle.putInt(STATUS_KEY, statusCode);
        }
    }

    // Lay danh sach thong bao tu may chu
    private Response getData(){
        String url = Reference.HOST + Reference.LOAD_THONG_BAO_API;

        return NetworkUtil.makeRequest(url, false, null);
    }

    // Doi chuoi JSON sang model
    private void setData(String json) {
        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(List.class, THONGBAO.class);
        jsonAdapter = moshi.adapter(usersType);

        try {
            thongBaoList = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hien thi danh sach thong bao len view
    private void showData() {
        mBundle.putInt(STATUS_KEY, STATUS_SHOW_DATA);
        rvItems.setAdapter(new NewsRecyclerDataAdapter(thongBaoList, MainFragment.this.context));
    }

    // Hien thi hinh anh dang tai du lieu
    private void showProgress(final boolean show) {
        mThongBaoLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressViewLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mNetworkError.setVisibility(View.GONE);
        if (show) {
            mBundle.putInt(STATUS_KEY, STATUS_LOADING);
        }
    }

    // Hien thi thong bao loi len man hinh
    private void showError(final boolean show, String errorMessage) {
        mThongBaoLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mProgressViewLayout.setVisibility(View.GONE);
        mNetworkError.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            mErrorMessage = errorMessage;
            mThongBaoLoi.setText(errorMessage);
            mBundle.putInt(STATUS_KEY, STATUS_SHOW_ERROR);
        }
    }
}
