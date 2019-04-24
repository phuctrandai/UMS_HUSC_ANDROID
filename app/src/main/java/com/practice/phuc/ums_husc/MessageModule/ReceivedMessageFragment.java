package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.practice.phuc.ums_husc.Adapter.MessageRecyclerDataAdapter;
import com.practice.phuc.ums_husc.Helper.CustomSnackbar;
import com.practice.phuc.ums_husc.Helper.DBHelper;
import com.practice.phuc.ums_husc.Helper.MessageItemTouchHelper;
import com.practice.phuc.ums_husc.Helper.MessageTaskHelper;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ReceivedMessageFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, MessageItemTouchHelper.MessageItemTouchHelperListener {

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
    private MessageRecyclerDataAdapter mAdapter;
    private String mErrorMessage;
    private int mStatus, mLastAction;
    private int mCurrentItems, mTotalItems, mScrollOutItems;
    private long mCurrentPage;
    private boolean mIsScrolling, mIsViewDestroyed;
    private Snackbar mNotNetworkSnackbar, mErrorSnackbar, mUndoDeleteSnakbar;
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

    public void onInsertMessage(TINNHAN tinNhan, int position) {
        mAdapter.insertItem(tinNhan, position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_searchMessage:
                SearchMessageActivity.setSuggestions(mAdapter.getDataSet());
                Intent intent = new Intent(mContext, SearchMessageActivity.class);
                startActivity(intent);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mLastAction = ACTION_INIT;
        mStatus = STATUS_INIT;
        mDBHelper = new DBHelper(mContext);
        mAdapter = new MessageRecyclerDataAdapter(mContext, new ArrayList<TINNHAN>());
        mIsScrolling = true;
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
        View view = inflater.inflate(R.layout.content_fragment_message, container, false);
        mRvMessage = view.findViewById(R.id.rv_message);
        mLoadMoreLayout = view.findViewById(R.id.load_more_layout);
        mIsViewDestroyed = false;
        setHasOptionsMenu(true);
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
        showUndoSnackbar(false, null);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Reference.mHasNewReceivedMessage) {
            List<TINNHAN> list = Reference.getListNewReceivedMessage();
            for (TINNHAN item : list)
                mAdapter.insertItem(item, 0);
            Reference.clearListNewReceivedMessage();
            Reference.mHasNewReceivedMessage = false;
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
        mLoadReceivedMessageTask = null;
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        mLastAction = ACTION_REFRESH;

        showNetworkErrorSnackbar(false);
        showErrorSnackbar(false, null);
        showUndoSnackbar(false, null);

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
            showErrorSnackbar(false, null);
            mIsScrolling = false;
            mLoadMoreLayout.setVisibility(View.VISIBLE);
            attempGetData();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (!(viewHolder instanceof MessageRecyclerDataAdapter.DataViewHolder)) return;

        if (direction == ItemTouchHelper.LEFT) {
            final int deletedIndex = viewHolder.getAdapterPosition();
            final TINNHAN deletedItem = mAdapter.getDataSet().get(deletedIndex);

            mAdapter.removeItem(deletedIndex);
            MessageTaskHelper.getInstance().insertAttempDeleteMessage(deletedItem);

            MessageFragment parentFrag = (MessageFragment) ReceivedMessageFragment.this.getParentFragment();
            final DeletedMessageFragment deletedMessageFragment = (DeletedMessageFragment) Objects.requireNonNull(parentFrag)
                    .getChildFragment(DeletedMessageFragment.class.getName());
            if (deletedMessageFragment != null) {
                deletedMessageFragment.onInsertMessage(deletedItem, 0);
            }

            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    MessageTaskHelper.getInstance().attempDelete(deletedItem.MaTinNhan,
                            Reference.getStudentId(mContext), Reference.getAccountPassword(mContext));
                }
            };
            handler.postDelayed(runnable, 3500);

            showUndoSnackbar(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUndoDeleteSnakbar.dismiss();
                    mAdapter.insertItem(deletedItem, deletedIndex);
                    handler.removeCallbacks(runnable);
                    MessageTaskHelper.getInstance().removeAttempDeleteMessage(deletedItem);
                    if (deletedMessageFragment != null) {
                        deletedMessageFragment.onRemoveMessage(deletedItem);
                    }
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadReceivedMessageTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mJson;

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadReceivedMessageTask == null) return false;

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
                        mErrorMessage = getString(R.string.error_time_out);
                    }
                    return false;
                }
            } catch (Exception e) {
                Log.e("DEBUG", mErrorMessage);
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
                mLoadMoreLayout.setVisibility(View.GONE);
                mCurrentPage += 1;
                mIsScrolling = false;
                refreshData(TINNHAN.fromJsonToList(mJson));

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
                refreshData(list);
            }
        } else {
            mLoadReceivedMessageTask = new LoadReceivedMessageTask();
            mLoadReceivedMessageTask.execute((String) null);
        }
    }

    private Response fetchData() {
        if (mLastAction == ACTION_INIT) mCurrentPage = 1;

        SharedPreferences sp = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String maSinhVien = sp.getString(getString(R.string.pre_key_student_id), null);
        String matKhau = sp.getString(getString(R.string.pre_key_password), null);
        String url = Reference.getLoadTinNhanDenApiUrl(maSinhVien, matKhau, mCurrentPage, ITEM_PER_PAGE);

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void refreshData(List<TINNHAN> list) {
        if (list != null) {

            if (mLastAction == ACTION_REFRESH || mLastAction == ACTION_INIT) {

                if (MessageTaskHelper.getInstance().getAttempRestoreReceivedMessage().size() > 0)
                    for (TINNHAN item : MessageTaskHelper.getInstance().getAttempRestoreReceivedMessage()) {
                        if (!list.contains(item)) list.add(0, item);
                    }
                mAdapter.changeDataSet(list);
            } else if (mLastAction == ACTION_LOAD_MORE && list.size() > 0)
                mAdapter.insertItemRange(list, mAdapter.getItemCount(), ITEM_PER_PAGE);
        }
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

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new MessageItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRvMessage);
    }

    private void setUpSwipeRefreshLayout(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.layout_swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void showErrorSnackbar(boolean show, String message) {
        if (mIsViewDestroyed) return;

        if (show) {
            mStatus = STATUS_SHOW_ERROR;
            mErrorSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout
                    , message, Snackbar.LENGTH_INDEFINITE
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
        if (mIsViewDestroyed) return;

        if (show) {
            mStatus = STATUS_NOT_NETWORK;
            mNotNetworkSnackbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout
                    , getString(R.string.error_network_disconected), Snackbar.LENGTH_INDEFINITE
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

    private void showUndoSnackbar(boolean show, View.OnClickListener action) {
        if (mIsViewDestroyed) return;

        if (show) {
            mUndoDeleteSnakbar = CustomSnackbar.createTwoButtonSnackbar(mContext, mSwipeRefreshLayout
                    , "Đã xóa 1 mục"
                    , Snackbar.LENGTH_LONG
                    , null
                    , action);
            mUndoDeleteSnakbar.show();

        } else if (mUndoDeleteSnakbar != null) {
            mUndoDeleteSnakbar.dismiss();
        }
    }

}
