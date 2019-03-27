package com.practice.phuc.ums_husc;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.practice.phuc.ums_husc.Adapter.NewsRecyclerDataAdapter;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private LoadNewsTask mLoadNewsTask;
    private Context mContext;
    private List<THONGBAO> mThongBaoList;
    private Bundle mBundle;
    private String mErrorMessage;
    private NewsRecyclerDataAdapter mAdapter;
    private int currentItems, totalItems, scrollOutItems;
    private boolean mIsScrolling;

    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;

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
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView rvItems;
    LinearLayout mLoadMoreLayout;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(Context context) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.mContext = context;
        return mainFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "On create MainFragment");
        mBundle = new Bundle();
        mBundle.putInt(STATUS_KEY, STATUS_LOADING);
        mThongBaoList = new ArrayList<>();
        mAdapter = new NewsRecyclerDataAdapter(mThongBaoList, mContext);
        mIsScrolling = false;
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
        mLoadMoreLayout = view.findViewById(R.id.load_more_layout);

        setUpRecyclerView();

        initSwipeRefreshLayout(view);

        if (NetworkUtil.getConnectivityStatus(mContext) != NetworkUtil.TYPE_NOT_CONNECTED)
            mBundle.putInt(STATUS_KEY, STATUS_LOADING);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mBundle.getInt(STATUS_KEY) == STATUS_SHOW_DATA) {

                } else if (mBundle.getInt(STATUS_KEY) == STATUS_NOT_NETWORK) {
                    showNetworkErrorSnackbar(true);
                } else if (mBundle.getInt(STATUS_KEY) == STATUS_SHOW_ERROR) {
                    showErrorSnackbar(true, mErrorMessage);
                } else if (mBundle.getInt(STATUS_KEY) == STATUS_LOADING) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    attempGetData();
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        showNetworkErrorSnackbar(false);
        showErrorSnackbar(false, mErrorMessage);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "ON DESTROY MAIN FRAGMENT");
        mLoadNewsTask = null;
        mThongBaoList.clear();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mThongBaoList.clear();
        mAdapter.lastPosition = -1;
        if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
            mBundle.putInt(STATUS_KEY, STATUS_NOT_NETWORK);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    showNetworkErrorSnackbar(true);
                }
            }, 1000);
        } else {
            attempGetData();
        }
    }

    // Set up swipe refresh layout
    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.layout_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    // set up recyler view
    private void setUpRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(mContext);
        rvItems.setLayoutManager(manager);
        rvItems.setAdapter(mAdapter);
        rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (mIsScrolling && (currentItems + scrollOutItems == totalItems)) {
                    mIsScrolling = false;
                    mLoadMoreLayout.setVisibility(View.VISIBLE);
                    attempGetData();
                }
            }
        });
    }

    // Kiem tra truoc khi lay du lieu
    private void attempGetData() {
        if (NetworkUtil.getConnectivityStatus(this.mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
            mBundle.putInt(STATUS_KEY, STATUS_NOT_NETWORK);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNetworkErrorSnackbar(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mLoadMoreLayout.setVisibility(View.GONE);
                }
            }, 1500);
        } else {
            mLoadNewsTask = new LoadNewsTask();
            mLoadNewsTask.execute((String) null);
        }
    }

    private class LoadNewsTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadNewsTask == null) return false;

            try {
                mResponse = fetchData();
                if (mResponse == null) {
                    Log.d("DEBUG", "Get thong bao Response null");
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;

                } else {
                    Log.d("DEBUG", "Get thong bao Response code: " + mResponse.code());
                    if (mResponse.code() == NetworkUtil.OK) {
                        String json = mResponse.body().string();
                        setData(json);
                        return true;

                    } else if (mResponse.code() == NetworkUtil.NOT_FOUND) {
                        mErrorMessage = getString(R.string.error_server_not_response);
                    } else {
                        mErrorMessage = getString(R.string.error_server_not_response);
                    }
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d("DEBUG", "Get thong bao : " + success);
            mSwipeRefreshLayout.setRefreshing(false);
            if (mLoadNewsTask != null) {
                if (success) {
                    mBundle.putInt(STATUS_KEY, STATUS_SHOW_DATA);
                    mAdapter.notifyDataSetChanged();
                    mLoadMoreLayout.setVisibility(View.GONE);
                    mIsScrolling = false;
                } else {
                    showErrorSnackbar(true, mErrorMessage);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mLoadNewsTask = null;
            super.onCancelled();
        }
    }

    private void showNetworkErrorSnackbar(boolean show) {
        if (show) {
            mBundle.putInt(STATUS_KEY, STATUS_NOT_NETWORK);
            mNotNetworkSnackbar = Snackbar.make(mSwipeRefreshLayout, getString(R.string.network_not_available),
                    Snackbar.LENGTH_INDEFINITE);
            mNotNetworkSnackbar.setAction("Thử lại", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    attempGetData();
                }
            });
            mNotNetworkSnackbar.show();
        } else if (mNotNetworkSnackbar != null) {
            mNotNetworkSnackbar.dismiss();
        }
    }

    private void showErrorSnackbar(boolean show, String message) {
        if (show) {
            mBundle.putInt(STATUS_KEY, STATUS_SHOW_ERROR);
            mErrorSnackbar = Snackbar.make(mSwipeRefreshLayout, message,
                    Snackbar.LENGTH_INDEFINITE);
            mErrorSnackbar.setAction("Thử lại", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    attempGetData();
                }
            });
            mErrorSnackbar.show();
        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }

    // Lay danh sach thong bao tu may chu
    private Response fetchData() {
        String url = Reference.HOST + Reference.LOAD_THONG_BAO_API;

        return NetworkUtil.makeRequest(url, false, null);
    }

    // Doi chuoi JSON sang model
    private void setData(String json) {
        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(List.class, THONGBAO.class);
        jsonAdapter = moshi.adapter(usersType);

        try {
            List<THONGBAO> temp = jsonAdapter.fromJson(json);
            mThongBaoList.addAll(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
