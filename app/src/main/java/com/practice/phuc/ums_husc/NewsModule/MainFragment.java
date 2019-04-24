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

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public MainFragment() {
    }

    public static Fragment newInstance(Context context) {
        MainFragment mainFragment = new MainFragment();
        mainFragment.mContext = context;
        return mainFragment;
    }

    private Context mContext;
    private DBHelper mDBHelper;
    private LoadNewsTask mLoadNewsTask;
    private NewsRecyclerDataAdapter mAdapter;
    private String mErrorMessage;
    private int mStatus, mLastAction;
    private int currentItems, totalItems, scrollOutItems;
    private long mCurrentPage;
    private boolean mIsScrolling, mIsViewDestroyed;
    private Snackbar mNotNetworkSnackbar, mErrorSnackbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvItems;
    private LinearLayout mLoadMoreLayout;

    private final int ITEM_PER_PAGE = 15;
    private final int STATUS_INIT = 0;
    private final int STATUS_SHOW_ERROR = 1;
    private final int STATUS_SHOW_DATA = 2;
    private final int STATUS_NOT_NETWORK = 3;
    private final int STATUS_LOAD_MORE = 4;
    private final int ACTION_INIT = 0;
    private final int ACTION_REFRESH = 1;
    private final int ACTION_LOAD_MORE = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mLastAction = ACTION_INIT;
        mStatus = STATUS_INIT;
        mAdapter = new NewsRecyclerDataAdapter(mContext, new ArrayList<THONGBAO>());
        mDBHelper = new DBHelper(mContext);
        mIsScrolling = true;
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
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        rvItems = view.findViewById(R.id.rv_thongBao);
        mLoadMoreLayout = view.findViewById(R.id.load_more_layout);
        mIsViewDestroyed = false;

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
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        onHasNewNews();
    }

    public void onHasNewNews() {
        if (Reference.mHasNewNews) {
            Reference.mHasNewNews = false;
            List<THONGBAO> newItems = Reference.getmListNewThongBao();
            for (int i = 0; i < newItems.size(); i++) {
                mAdapter.insertItem(newItems.get(i), 0);
            }
            Reference.getmListNewThongBao().clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsViewDestroyed = true;
    }

    @Override
    public void onDestroy() {
        mAdapter.clearDataSet();
        mLoadNewsTask = null;
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mLastAction = ACTION_REFRESH;

        showNetworkErrorSnackbar(false);
        showErrorSnackbar(false, null);

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

    @SuppressLint("StaticFieldLeak")
    private class LoadNewsTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mJson;

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadNewsTask == null) return false;

            try {
                mResponse = fetchData();
                if (mResponse == null) {
                    mErrorMessage = getString(R.string.error_server_not_response);
                    return false;

                } else {
                    if (mResponse.code() == NetworkUtil.OK) {
                        mJson = mResponse.body() != null ? mResponse.body().string() : "";

                        return true;

                    } else if (mResponse.code() == NetworkUtil.BAD_REQUEST) {
                        mErrorMessage = mResponse.body() != null ? mResponse.body().string() : "";

                    } else {
                        mErrorMessage = getString(R.string.error_server_not_response);
                    }
                    return false;
                }
            } catch (Exception e) {
                Log.d("DEBUG", mErrorMessage);
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mLoadNewsTask == null) return;

            mSwipeRefreshLayout.setRefreshing(false);

            if (success) {
                mStatus = STATUS_SHOW_DATA;
                mLoadMoreLayout.setVisibility(View.GONE);
                mCurrentPage += 1;
                mIsScrolling = false;
                refreshData(THONGBAO.fromJsonToList(mJson), false);

            } else {
                showErrorSnackbar(true, mErrorMessage);
            }
        }

        @Override
        protected void onCancelled() {
            mLoadNewsTask = null;
            super.onCancelled();
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

            if (mDBHelper.countRow(DBHelper.NEWS) > 0) {
                List<THONGBAO> list = mDBHelper.getAllNews();
                refreshData(list, true);
            }
        } else {
            mLoadNewsTask = new LoadNewsTask();
            mLoadNewsTask.execute((String) null);
        }
    }

    private Response fetchData() {
        if (mLastAction == ACTION_INIT) mCurrentPage = 1;

        String maSinhVien = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), Context.MODE_PRIVATE)
                .getString(getString(R.string.pre_key_student_id), null);
        String matKhau = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), Context.MODE_PRIVATE)
                .getString(getString(R.string.pre_key_password), null);
        String url = Reference.getLoadThongBaoApiUrl(maSinhVien, matKhau, mCurrentPage, ITEM_PER_PAGE);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void refreshData(List<THONGBAO> list, boolean isLocalData) {
        if (list != null) {
            mStatus = STATUS_SHOW_DATA;
            if (mLastAction == ACTION_REFRESH || mLastAction == ACTION_INIT)
                mAdapter.changeDataSet(list);

            else if (mLastAction == ACTION_LOAD_MORE && list.size() > 0)
                mAdapter.insertItemRange(list, mAdapter.getItemCount(), ITEM_PER_PAGE);

            if (list.size() > 0 && (!isLocalData))
                for (THONGBAO item : list) {
                    mDBHelper.insertNews(item);
                }
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
        rvItems.setHasFixedSize(true);
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

    private void showErrorSnackbar(boolean show, String message) {
        if (mIsViewDestroyed) return;
        if (show) {
            mStatus = STATUS_SHOW_ERROR;
            mErrorSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout,
                    message, Snackbar.LENGTH_INDEFINITE,
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
            if (mErrorSnackbar != null)
                mErrorSnackbar.show();

        } else if (mErrorSnackbar != null) {
            mErrorSnackbar.dismiss();
        }
    }

    private void showNetworkErrorSnackbar(boolean show) {
        if (mIsViewDestroyed) return;
        if (show) {
            mStatus = STATUS_NOT_NETWORK;
            if (mNotNetworkSnackbar != null && mNotNetworkSnackbar.isShown()) return;

            mNotNetworkSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout,
                    getString(R.string.error_network_disconected), Snackbar.LENGTH_INDEFINITE,
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
            if (mNotNetworkSnackbar != null)
                mNotNetworkSnackbar.show();

        } else if (mNotNetworkSnackbar != null) {
            mNotNetworkSnackbar.dismiss();
        }
    }

    public void smoothScrollToTop() {
        rvItems.smoothScrollToPosition(0);
    }

    public int findFirstVisibleItemPosition() {
        LinearLayoutManager manager = (LinearLayoutManager) rvItems.getLayoutManager();
        return manager == null ? -1 : manager.findFirstVisibleItemPosition();
    }
}
