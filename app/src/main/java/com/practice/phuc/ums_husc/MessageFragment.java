package com.practice.phuc.ums_husc;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.practice.phuc.ums_husc.Adapter.MessageRecyclerDataAdapter;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private LoadMessageTask mLoadMessageTask;
    private Context mContext;
    private List<TINNHAN> mTinNhanList;
    private int mStatus;
    private String mErrorMessage;
    private MessageRecyclerDataAdapter mAdapter;
    private int currentItems, totalItems, scrollOutItems;
    private boolean mIsScrolling;
    private int mLastAction;
    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;

    private final int STATUS_INIT = 0;
    private final int STATUS_SHOW_ERROR = 1;
    private final int STATUS_SHOW_DATA = 2;
    private final int STATUS_NOT_NETWORK = 3;
    private final int STATUS_LOAD_MORE = 4;
    private final int ACTION_INIT = 0;
    private final int ACTION_REFRESH = 1;
    private final int ACTION_LOAD_MORE = 2;

    // Cast json to model
    Moshi moshi;
    Type usersType;
    JsonAdapter<List<TINNHAN>> jsonAdapter;

    // UI
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView rvItems;
    LinearLayout mLoadMoreLayout;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(Context context) {
        MessageFragment fragment = new MessageFragment();
        fragment.mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DEBUG", "On create MessageFragment");
        mStatus = STATUS_INIT;
        mTinNhanList = new ArrayList<>();
        mAdapter = new MessageRecyclerDataAdapter(mContext, mTinNhanList);
        mIsScrolling = true;
        mLastAction = ACTION_INIT;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DEBUG", "On create VIEW Message Fragment");
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Bind UI
        rvItems = (RecyclerView) view.findViewById(R.id.rv_tinNhan);
        mLoadMoreLayout = view.findViewById(R.id.load_more_layout);

        setUpRecyclerView();

        // set up swipe refresh layout
        initSwipeRefreshLayout(view);

        if (mStatus == STATUS_NOT_NETWORK
                && NetworkUtil.getConnectivityStatus(mContext) != NetworkUtil.TYPE_NOT_CONNECTED) {
            if (mLastAction == ACTION_REFRESH) {
                mStatus = STATUS_INIT;
            } else if (mLastAction == ACTION_LOAD_MORE) {
                mStatus = STATUS_LOAD_MORE;
            }
        }

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mStatus == STATUS_SHOW_DATA) {
                    Log.d("DEBUG", "Status: SHOW_DATA");
                } else if (mStatus == STATUS_NOT_NETWORK) {
                    Log.d("DEBUG", "Status: SHOW_NOT_NETWORK");
                    showNetworkErrorSnackbar(true);
                } else if (mStatus == STATUS_SHOW_ERROR) {
                    Log.d("DEBUG", "Status: SHOW_ERROR");
                    showErrorSnackbar(true, mErrorMessage);
                } else if (mStatus == STATUS_INIT) {
                    Log.d("DEBUG", "Status: LOADING");
                    mSwipeRefreshLayout.setRefreshing(true);
                    attempGetData();
                } else if (mStatus == STATUS_LOAD_MORE) {
                    Log.d("DEBUG", "Status: LOAD_MORE");
                    onLoadMore();
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
        Log.d("DEBUG", "DESTROY MESSAGE FRAGMENT");
        mLoadMessageTask = null;
        mTinNhanList.clear();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mLastAction = ACTION_REFRESH;

        if (NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED) {
            mStatus = STATUS_NOT_NETWORK;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    showNetworkErrorSnackbar(true);
                }
            }, 1000);
        } else {
            mTinNhanList.clear();
            mAdapter.lastPosition = -1;
            attempGetData();
        }
    }

    private void onLoadMore() {
        mIsScrolling = false;
        mLoadMoreLayout.setVisibility(View.VISIBLE);
        mLastAction = ACTION_LOAD_MORE;
        attempGetData();
    }

    private void initSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.layout_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
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
            mStatus = STATUS_NOT_NETWORK;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNetworkErrorSnackbar(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mLoadMoreLayout.setVisibility(View.GONE);
                }
            }, 1500);
        } else {
            mLoadMessageTask = new MessageFragment.LoadMessageTask();
            mLoadMessageTask.execute((String) null);
        }
    }

    private class LoadMessageTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadMessageTask == null) return false;

            try {
                mResponse = fetchData();
                if (mResponse == null) {
                    Log.d("DEBUG", "Get tin nhan Response null");
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;

                } else {
                    Log.d("DEBUG", "Get tin nhan Response code: " + mResponse.code());
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
            Log.d("DEBUG", "Get tin nhan : " + success);
            mSwipeRefreshLayout.setRefreshing(false);
            if (mLoadMessageTask != null) {
                if (success) {
                    mStatus = STATUS_SHOW_DATA;
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
            mLoadMessageTask = null;
            super.onCancelled();
        }
    }

    private void showNetworkErrorSnackbar(boolean show) {
        if (show) {
            mStatus = STATUS_NOT_NETWORK;
            mNotNetworkSnackbar = Snackbar.make(mSwipeRefreshLayout, getString(R.string.network_not_available),
                    Snackbar.LENGTH_INDEFINITE);
            mNotNetworkSnackbar.setAction("Thử lại", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLastAction == ACTION_REFRESH) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        onRefresh();
                    } else if (mLastAction == ACTION_LOAD_MORE) {
                        onLoadMore();
                    } else if (mLastAction == ACTION_INIT) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        attempGetData();
                    }
                }
            });
            mNotNetworkSnackbar.show();
        } else if (mNotNetworkSnackbar != null) {
            mNotNetworkSnackbar.dismiss();
        }
    }

    private void showErrorSnackbar(boolean show, String message) {
        if (show) {
            mStatus = STATUS_SHOW_ERROR;
            mErrorSnackbar = Snackbar.make(mSwipeRefreshLayout, message,
                    Snackbar.LENGTH_INDEFINITE);
            mErrorSnackbar.setAction("Thử lại", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLastAction == ACTION_REFRESH) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        onRefresh();
                    } else if (mLastAction == ACTION_LOAD_MORE) {
                        onLoadMore();
                    } else if (mLastAction == ACTION_INIT) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        attempGetData();
                    }
                }
            });
            mErrorSnackbar.show();
        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }

    private Response fetchData() {
        String url = Reference.HOST + Reference.LOAD_THONG_BAO_API;

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void setData(String json) {
        moshi = new Moshi.Builder().build();
        usersType = Types.newParameterizedType(List.class, TINNHAN.class);
        jsonAdapter = moshi.adapter(usersType);

        try {
            List<TINNHAN> temp = jsonAdapter.fromJson(json);
            mTinNhanList.addAll(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
