package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
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

import com.practice.phuc.ums_husc.Adapter.MessageRecyclerDataAdapter;
import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class ReceivedMessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public ReceivedMessageFragment() {
        // Required empty public constructor
    }

    public static ReceivedMessageFragment newInstance(Context context) {
        ReceivedMessageFragment rmf = new ReceivedMessageFragment();
        rmf.mContext = context;
        return rmf;
    }

    private Context mContext;
    private LoadReceivedMessageTask mLoadReceivedMessageTask;
    private List<TINNHAN> mMessageList;
    private MessageRecyclerDataAdapter mAdapter;
    private String mErrorMessage;
    private int mStatus;
    private int mCurrentItems, mTotalItems, mScrollOutItems;
    private boolean mIsScrolling;
    private int mLastAction;
    private long mCurrentPage;
    private boolean mIsMessageListChanged;
    private boolean mIsDestroyed;
    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;
    private DBHelper mDBHelper;

    private final int ITEM_PER_PAGE = 8;
    private final int STATUS_INIT = 0;
    private final int STATUS_SHOW_ERROR = 1;
    private final int STATUS_SHOW_DATA = 2;
    private final int STATUS_NOT_NETWORK = 3;
    private final int STATUS_LOAD_MORE = 4;
    private final int ACTION_INIT = 0;
    private final int ACTION_REFRESH = 1;
    private final int ACTION_LOAD_MORE = 2;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRvMessage;
    private LinearLayout mLoadMoreLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d("DEBUG", "On create RECEIVED MESSAGE FRAGMENT");
        mLastAction = ACTION_INIT;
        mStatus = STATUS_INIT;
        mMessageList = new ArrayList<>();
        mIsMessageListChanged = false;
        mAdapter = new MessageRecyclerDataAdapter(mContext, mMessageList, true);
        mIsScrolling = true;
        mDBHelper = new DBHelper(mContext);
        mIsDestroyed = false;
        long countRow = mDBHelper.countRow(DBHelper.MESSAGE);
        if (countRow > 0) {
            mCurrentPage = countRow / ITEM_PER_PAGE + 1;
        } else {
            mCurrentPage = 1;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_received_message, container, false);
        mRvMessage = view.findViewById(R.id.rv_message);
        mLoadMoreLayout = view.findViewById(R.id.load_more_layout);
        mIsDestroyed = false;

        setUpRecyclerView();
        setUpSwipeRefreshLayout(view);

        if (mStatus == STATUS_NOT_NETWORK
                && NetworkUtil.getConnectivityStatus(mContext) != NetworkUtil.TYPE_NOT_CONNECTED) {
            if (mLastAction == ACTION_REFRESH || mLastAction == ACTION_INIT) {
                mStatus = STATUS_INIT;
            } else if (mLastAction == ACTION_LOAD_MORE) {
                mStatus = STATUS_LOAD_MORE;
            }
        }
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (mStatus == STATUS_NOT_NETWORK) {
                    showNetworkErrorSnackbar(true);
                } else if (mStatus == STATUS_SHOW_ERROR) {
                    showErrorSnackbar(true, mErrorMessage);
                } else if (mStatus == STATUS_INIT) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    attempGetData();
                } else if (mStatus == STATUS_LOAD_MORE) {
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
        mIsDestroyed = true;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mIsDestroyed = true;
        mMessageList.clear();
        if (mLoadReceivedMessageTask != null) {
            mLoadReceivedMessageTask.cancel(true);
            mLoadReceivedMessageTask = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mLastAction = ACTION_REFRESH;
        showNetworkErrorSnackbar(false);
        showErrorSnackbar(false, "");
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
//            mDBHelper.deleteAllRecord(DBHelper.MESSAGE);
            mMessageList.clear();
            mAdapter.mLastPosition = -1;
            mCurrentPage = 1;
            attempGetData();
        }
    }

    private void onLoadMore() {
        mLastAction = ACTION_LOAD_MORE;
        boolean breakInternet = NetworkUtil.getConnectivityStatus(mContext) == NetworkUtil.TYPE_NOT_CONNECTED;
        if (breakInternet) {
            mStatus = STATUS_NOT_NETWORK;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mLoadMoreLayout.setVisibility(View.GONE);
                    showNetworkErrorSnackbar(true);
                }
            }, 1000);
        } else {
            showNetworkErrorSnackbar(false);
            showErrorSnackbar(false, "");
            mIsScrolling = false;
            mLoadMoreLayout.setVisibility(View.VISIBLE);
            attempGetData();
        }
    }

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
            }, 1000);

            if (mDBHelper.countRow(DBHelper.MESSAGE) > 0) {
                List<TINNHAN> list = mDBHelper.getAllMessage();
                mMessageList.addAll(list);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mLoadReceivedMessageTask = new LoadReceivedMessageTask();
            mLoadReceivedMessageTask.execute((String) null);
        }
    }

    private void showErrorSnackbar(boolean show, String message) {
        if (mIsDestroyed) return;

        if (show) {
            mStatus = STATUS_SHOW_ERROR;
            mErrorSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout
                    , message
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorSnackbar.dismiss();
                        }
                    }
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorSnackbar.dismiss();
                            if (mLastAction == ACTION_REFRESH) {
                                mSwipeRefreshLayout.setRefreshing(true);
                                onRefresh();
                            } else if (mLastAction == ACTION_LOAD_MORE) {
                                mLoadMoreLayout.setVisibility(View.VISIBLE);
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

    private void showNetworkErrorSnackbar(boolean show) {
        if (mIsDestroyed) return;

        if (show) {
            mStatus = STATUS_NOT_NETWORK;
            mNotNetworkSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout
                    , getString(R.string.network_not_available)
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNotNetworkSnackbar.dismiss();
                        }
                    }
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNotNetworkSnackbar.dismiss();
                            if (mLastAction == ACTION_REFRESH) {
                                mSwipeRefreshLayout.setRefreshing(true);
                                onRefresh();
                            } else if (mLastAction == ACTION_LOAD_MORE) {
                                mLoadMoreLayout.setVisibility(View.VISIBLE);
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

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.layout_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager manager = new LinearLayoutManager(mContext);
        mRvMessage.setLayoutManager(manager);
        mRvMessage.setHasFixedSize(true);
        mRvMessage.setAdapter(mAdapter);
        mRvMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                mCurrentItems = manager.getChildCount();
                mTotalItems = manager.getItemCount();
                mScrollOutItems = manager.findFirstVisibleItemPosition();

                if (mIsScrolling && (mCurrentItems + mScrollOutItems == mTotalItems)) {
                    mIsScrolling = false;
                    mLoadMoreLayout.setVisibility(View.VISIBLE);
                    onLoadMore();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadReceivedMessageTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadReceivedMessageTask == null) return false;

            try {
                mResponse = fetchData();
                if (mResponse == null) {
                    mErrorMessage = getString(R.string.error_time_out);
                    return false;

                } else {
                    Log.d("DEBUG", "Get tin nhan Response code: " + mResponse.code());
                    if (mResponse.code() == NetworkUtil.OK) {
                        String json = mResponse.body() != null ? mResponse.body().string() : "";
                        setData(castData(json));
                        return true;

                    } else if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                        mErrorMessage = mResponse.body() != null ? mResponse.body().string() : "";
                    } else {
                        mErrorMessage = getString(R.string.error_time_out);
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
            if (mLoadReceivedMessageTask == null) return;
            mSwipeRefreshLayout.setRefreshing(false);
            if (success) {
                mStatus = STATUS_SHOW_DATA;
                mCurrentPage += 1;
                mIsScrolling = false;
                if (mIsMessageListChanged) {
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), ITEM_PER_PAGE);
                    mIsMessageListChanged = false;
                }
                mLoadMoreLayout.setVisibility(View.GONE);
            } else {
                showErrorSnackbar(true, mErrorMessage);
            }
        }

        @Override
        protected void onCancelled() {
            mLoadReceivedMessageTask = null;
            super.onCancelled();
        }
    }

    private List<TINNHAN> castData(String json) {
        Moshi mMoshi = new Moshi.Builder().build();
        Type mUsersType = Types.newParameterizedType(List.class, TINNHAN.class);
        JsonAdapter<List<TINNHAN>> mJsonAdapter = mMoshi.adapter(mUsersType);

        try {
            return mJsonAdapter.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setData(List<TINNHAN> list) {
        if (list != null && list.size() > 0) {
//            if (mLastAction == ACTION_INIT)
//                mDBHelper.deleteAllRecord(DBHelper.MESSAGE);
            for (TINNHAN tinnhan : list) {
                mMessageList.add(tinnhan);
//                mDBHelper.insertMessage(tinnhan);
            }
            mIsMessageListChanged = true;
        }
    }

    private Response fetchData() {
        if (mLastAction == ACTION_INIT) mCurrentPage = 1;

        String maSinhVien = mContext.getSharedPreferences("sinhVien", Context.MODE_PRIVATE)
                .getString("maSinhVien", null);
        String matKhau = mContext.getSharedPreferences("sinhVien", Context.MODE_PRIVATE)
                .getString("matKhau", null);
        String url = Reference.getLoadTinNhanDenApiUrl(maSinhVien, matKhau, mCurrentPage, ITEM_PER_PAGE);

        return NetworkUtil.makeRequest(url, false, null);
    }
}
