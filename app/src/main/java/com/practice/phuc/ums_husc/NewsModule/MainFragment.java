package com.practice.phuc.ums_husc.NewsModule;

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

import com.practice.phuc.ums_husc.Adapter.NewsRecyclerDataAdapter;
import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.THONGBAO;
import com.practice.phuc.ums_husc.R;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private boolean mIsDestroyed;
    private DBHelper mDBHelper;
    private LoadNewsTask mLoadNewsTask;
    private Context mContext;
    private List<THONGBAO> mThongBaoList;
    private boolean mIsThongBaoListChange;
    private int mStatus;
    private String mErrorMessage;
    private NewsRecyclerDataAdapter mAdapter;
    private int currentItems, totalItems, scrollOutItems;
    private boolean mIsScrolling;
    private boolean mIsRefreshOnBackPressed;
    private int mLastAction;
    private Snackbar mNotNetworkSnackbar;
    private Snackbar mErrorSnackbar;
    private long mCurrentPage;
    private final int ITEM_PER_PAGE = 15;

    private final int STATUS_INIT = 0;
    private final int STATUS_SHOW_ERROR = 1;
    private final int STATUS_SHOW_DATA = 2;
    private final int STATUS_NOT_NETWORK = 3;
    private final int STATUS_LOAD_MORE = 4;
    private final int ACTION_INIT = 0;
    private final int ACTION_REFRESH = 1;
    private final int ACTION_LOAD_MORE = 2;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvItems;
    private LinearLayout mLoadMoreLayout;

    public MainFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(Context context) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.mContext = context;
        return mainFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        Log.d("DEBUG", "On create MainFragment");
        mStatus = STATUS_INIT;
        mThongBaoList = new ArrayList<>();
        mIsThongBaoListChange = false;
        mAdapter = new NewsRecyclerDataAdapter(mThongBaoList, mContext);
        mIsScrolling = false;
        mIsRefreshOnBackPressed = false;
        mLastAction = ACTION_INIT;
        mDBHelper = new DBHelper(mContext);
        mIsDestroyed = false;
        long countRow = mDBHelper.countRow(DBHelper.NEWS);
        if (countRow > 0) {
            mCurrentPage = countRow / ITEM_PER_PAGE + 1;
        } else {
            mCurrentPage = 1;
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.d("DEBUG", "On create VIEW MainFragment");
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        rvItems = view.findViewById(R.id.rv_thongBao);
        mLoadMoreLayout = view.findViewById(R.id.load_more_layout);

        setUpRecyclerView();
        setUpSwipeRefreshLayout(view);

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
//                    Log.d("DEBUG", "Status: SHOW_NOT_NETWORK");
                    showNetworkErrorSnackbar(true);
                } else if (mStatus == STATUS_SHOW_ERROR) {
//                    Log.d("DEBUG", "Status: SHOW_ERROR");
                    showErrorSnackbar(true, mErrorMessage);
                } else if (mStatus == STATUS_INIT) {
//                    Log.d("DEBUG", "Status: LOADING");
                    mSwipeRefreshLayout.setRefreshing(true);
                    attempGetData();
                } else if (mStatus == STATUS_LOAD_MORE) {
//                    Log.d("DEBUG", "Status: LOAD_MORE");
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
        Log.d("DEBUG", "on PAUSE Main fragment - STATUS: " + mStatus);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("DEBUG", "On RESUME Main fragment");
        super.onResume();
        onHasNewNews();
    }

    public void onHasNewNews() {
        if (Reference.mHasNewNews) {
            Reference.mHasNewNews = false;
            if (Reference.getmListNewThongBao().size() > 0) {
                for (THONGBAO t : Reference.getmListNewThongBao()) {
                    mThongBaoList.add(0, t);
                    mAdapter.notifyItemInserted(0);
                }
                Reference.getmListNewThongBao().clear();
            }
        }
    }

    @Override
    public void onDestroy() {
//        Log.d("DEBUG", "ON DESTROY MAIN FRAGMENT");
        mIsDestroyed = true;
        mLoadNewsTask = null;
        mThongBaoList.clear();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
//        Log.d("DEBUG", "on REFRESH Main fragment");
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
            mDBHelper.deleteAllRecord(DBHelper.NEWS);
            mThongBaoList.clear();
            mAdapter.lastPosition = -1;
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

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.layout_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

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

//                if (mIsRefreshOnBackPressed && findFirstVisibleItemPosition() == 0) {
////                    Log.d("DEBUG", "Refresh on press back");
//                    mIsRefreshOnBackPressed = false;
//                    mSwipeRefreshLayout.setRefreshing(true);
//                    showNetworkErrorSnackbar(false);
//
//                    long delayMillis = 1000;
//                    if (3 < mCurrentPage && mCurrentPage < 6) delayMillis = 2000;
//                    else if (mCurrentPage >= 6) delayMillis = 3000;
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onRefresh();
//                        }
//                    }, delayMillis);
//
//                    return;
//                }

                if (mIsScrolling && (currentItems + scrollOutItems == totalItems)) {
                    mLoadMoreLayout.setVisibility(View.VISIBLE);
                    onLoadMore();
                }
            }
        });
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

            if (mDBHelper.countRow(DBHelper.NEWS) > 0) {
                List<THONGBAO> list = mDBHelper.getAllNews();
                mThongBaoList.addAll(list);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mLoadNewsTask = new LoadNewsTask();
            mLoadNewsTask.execute((String) null);
        }
    }

    @SuppressLint("StaticFieldLeak")
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
                        String json = mResponse.body() != null ? mResponse.body().string() : "";
                        setData(castData(json));
                        return true;

                    } else if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                        mErrorMessage = mResponse.body() != null ? mResponse.body().string() : "";
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
                    mStatus = STATUS_SHOW_DATA;
                    mCurrentPage += 1;
                    mIsScrolling = false;
                    if (mIsThongBaoListChange) {
                        mAdapter.notifyDataSetChanged();
                        mIsThongBaoListChange = false;
                    }
                    mLoadMoreLayout.setVisibility(View.GONE);
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

    private Response fetchData() {
        if (mLastAction == ACTION_INIT) mCurrentPage = 1;

        String maSinhVien = mContext.getSharedPreferences("sinhVien", Context.MODE_PRIVATE)
                .getString("maSinhVien", null);
        String matKhau = mContext.getSharedPreferences("sinhVien", Context.MODE_PRIVATE)
                .getString("matKhau", null);
        String url = Reference.getLoadThongBaoApiUrl(maSinhVien, matKhau, mCurrentPage, ITEM_PER_PAGE);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void setData(List<THONGBAO> list) {
        if (list != null && list.size() > 0) {
            if (mLastAction == ACTION_INIT)
                mDBHelper.deleteAllRecord(DBHelper.NEWS);
            for (THONGBAO thongbao : list) {
                mThongBaoList.add(thongbao);
                mDBHelper.insertNews(thongbao);
            }
            mIsThongBaoListChange = true;
        }
    }

    private List<THONGBAO> castData(String json) {
        Moshi moshi = new Moshi.Builder().build();
        Type usersType = Types.newParameterizedType(List.class, THONGBAO.class);
        JsonAdapter<List<THONGBAO>> jsonAdapter = moshi.adapter(usersType);
        try {
            return jsonAdapter.fromJson(json);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showNetworkErrorSnackbar(boolean show) {
        if (mIsDestroyed) return;
        if (show) {
            mStatus = STATUS_NOT_NETWORK;
            if (mNotNetworkSnackbar != null && mNotNetworkSnackbar.isShown()) return;

            mNotNetworkSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout,
                    getString(R.string.network_not_available),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNotNetworkSnackbar.dismiss();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                            mNotNetworkSnackbar.dismiss();
                        }
                    });
            mNotNetworkSnackbar.show();
        } else if (mNotNetworkSnackbar != null) {
            mNotNetworkSnackbar.dismiss();
        }
    }

    private void showErrorSnackbar(boolean show, String message) {
        if (mIsDestroyed) return;

        if (show) {
            mStatus = STATUS_SHOW_ERROR;
            mErrorSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout,
                    message,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mErrorSnackbar.dismiss();
                        }
                    },
                    new View.OnClickListener() {
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

    public int findFirstVisibleItemPosition() {
        LinearLayoutManager manager = (LinearLayoutManager) rvItems.getLayoutManager();
        return manager == null ? -1 : manager.findFirstVisibleItemPosition();
    }

    public void smoothScrollToTop() {
        rvItems.smoothScrollToPosition(0);
        mIsRefreshOnBackPressed = true;
    }
}
