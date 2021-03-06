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
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.ACCOUNT_ID;
import static com.practice.phuc.ums_husc.Helper.SharedPreferenceHelper.ACCOUNT_SP;

public class DeletedMessageFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, MessageItemTouchHelper.MessageItemTouchHelperListener {

    public DeletedMessageFragment() {
    }

    public static DeletedMessageFragment newInstance(Context context) {
        DeletedMessageFragment rmf = new DeletedMessageFragment();
        rmf.mContext = context;
        return rmf;
    }

    private Context mContext;
    private LoadDeletedMessageTask mLoadDeletedMessageTask;
    private MessageRecyclerDataAdapter mAdapter;
    private String mErrorMessage;
    private int mCurrentItems, mTotalItems, mScrollOutItems;
    private int mStatus, mLastAction;
    private long mCurrentPage;
    private boolean mIsScrolling, mIsViewDestroyed;
    private Snackbar mNotNetworkSnackbar, mErrorSnackbar, mUndoDeleteSnakbar;
    private DBHelper mDBHelper;
    private List<TINNHAN> mRestoreList, mDeleteList;

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

    public void onRemoveMessage(TINNHAN tinNhan) {
        Log.e("DEBUG", "REMOVE TIN NHAN");
        mAdapter.removeItem(tinNhan);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_searchMessage:
                SearchMessageActivity.setSuggestions(mAdapter.getDataSet(), SearchMessageActivity.SEARCH_DELETED);
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
        mAdapter = new MessageRecyclerDataAdapter(mContext, new ArrayList<TINNHAN>());
        mIsScrolling = true;
        mDBHelper = new DBHelper(mContext);
        mRestoreList = new ArrayList<>();
        mDeleteList = new ArrayList<>();
        long countRow = mDBHelper.countRow(DBHelper.MESSAGE);
        if (countRow > 0) {
            mCurrentPage = countRow / ITEM_PER_PAGE + 1;
        } else {
            mCurrentPage = 1;
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    public void onDestroyView() {
        super.onDestroyView();
        mIsViewDestroyed = true;
    }

    @Override
    public void onDestroy() {
        mAdapter.clearDataSet();
        mLoadDeletedMessageTask = null;
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
            showErrorSnackbar(false, "");
            mIsScrolling = false;
            mLoadMoreLayout.setVisibility(View.VISIBLE);
            attempGetData();
        }
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (!(viewHolder instanceof MessageRecyclerDataAdapter.DataViewHolder))
            return;

        final int swipedIndex = viewHolder.getAdapterPosition();
        final TINNHAN swipedItem = mAdapter.getDataSet().get(swipedIndex);
        final Handler handler = new Handler();
        mAdapter.removeItem(swipedIndex);

        if (direction == ItemTouchHelper.LEFT) {
            mDeleteList.add(swipedItem);
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    mDeleteList.remove(swipedItem);
                    MessageTaskHelper.getInstance()
                            .foreverDelete(mContext, swipedItem.MaTinNhan,
                                    Reference.getInstance().getStudentId(mContext),
                                    Reference.getInstance().getAccountPassword(mContext));
                    Log.e("DEBUG", "DO DELETE");
                }
            };
            handler.postDelayed(runnable, 3500);
            showUndoSnackbar(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUndoDeleteSnakbar.dismiss();
                    mAdapter.insertItem(swipedItem, swipedIndex);
                    mDeleteList.remove(swipedItem);
                    handler.removeCallbacks(runnable);
                }
            });
        } else if (direction == ItemTouchHelper.RIGHT) {
            mRestoreList.add(swipedItem);
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("DEBUG", "Restore message");
                    mRestoreList.remove(swipedItem);
                    MessageTaskHelper.getInstance().restore(mContext, swipedItem.MaTinNhan,
                            Reference.getInstance().getStudentId(mContext),
                            Reference.getInstance().getAccountPassword(mContext));
                }
            };
            handler.postDelayed(runnable, 3500);
            attempRestoreMessage(swipedItem);
        }
    }

    private void attempRestoreMessage(TINNHAN tinNhan) {
        MessageFragment parentFrag = (MessageFragment) getParentFragment();
        String accountId = SharedPreferenceHelper.getInstance()
                .getSharedPrefStr(mContext, ACCOUNT_SP, ACCOUNT_ID, "");

        if (tinNhan.MaNguoiGui.equals(accountId)) { // Restore sent message
            SentMessageFragment smf = (SentMessageFragment) Objects.requireNonNull(parentFrag)
                    .getChildFragment(SentMessageFragment.class.getName());
            if (smf != null) {
                smf.onInsertMessage(tinNhan, 0);
                MessageTaskHelper.getInstance().insertAttempRestoreSentMessage(tinNhan);
            }
        } else { // Restore received message
            ReceivedMessageFragment rmf = (ReceivedMessageFragment) Objects.requireNonNull(parentFrag)
                    .getChildFragment(ReceivedMessageFragment.class.getName());
            if (rmf != null) {
                rmf.onInsertMessage(tinNhan, 0);
                MessageTaskHelper.getInstance().insertAttempRestoreReceivedMessage(tinNhan);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDeletedMessageTask extends AsyncTask<String, Void, Boolean> {
        private Response mResponse;
        private String mJson;

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadDeletedMessageTask == null) return false;

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
            if (mLoadDeletedMessageTask == null) return;

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
            mLoadDeletedMessageTask = null;
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
            mLoadDeletedMessageTask = new LoadDeletedMessageTask();
            mLoadDeletedMessageTask.execute((String) null);
        }
    }

    private Response fetchData() {
        if (mLastAction == ACTION_INIT) mCurrentPage = 1;

        SharedPreferences sp = mContext.getSharedPreferences(getString(R.string.share_pre_key_account_info), MODE_PRIVATE);
        String maSinhVien = sp.getString(getString(R.string.pre_key_student_id), null);
        String matKhau = sp.getString(getString(R.string.pre_key_password), null);
        String url = Reference.getInstance().
                getHost(mContext) + "api/SinhVien/TinNhan/DaXoa/" +
                "?masinhvien=" + maSinhVien +
                "&matkhau=" + matKhau +
                "&sotrang=" + mCurrentPage +
                "&sodong=" + ITEM_PER_PAGE;

        return NetworkUtil.makeRequest(url, false, null);
    }

    private void refreshData(List<TINNHAN> list) {
        if (list != null) {

            if (mLastAction == ACTION_REFRESH || mLastAction == ACTION_INIT) {
                List<TINNHAN> attempDeletedList = MessageTaskHelper.getInstance().getAttempDeletemessage();
                List<TINNHAN> data = new ArrayList<>();

                for (int i = 0; i < attempDeletedList.size(); i++) {
                    boolean exist = false;
                    for (int j = 0; j < list.size(); j++) {
                        if (attempDeletedList.get(i).MaTinNhan.equals(list.get(j).MaTinNhan)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) list.add(attempDeletedList.get(i));
                }

                for (int i = 0; i < list.size(); i++) {
                    boolean allowAdd = true;

                    for (int j = 0; j < mRestoreList.size(); j++) {
                        if (list.get(i).MaTinNhan.equals(mRestoreList.get(j).MaTinNhan)) {
                            allowAdd = false;
                            break;
                        }
                    }

                    for (int j = 0; j < mDeleteList.size(); j++) {
                        if (list.get(i).MaTinNhan.equals(mDeleteList.get(j).MaTinNhan)) {
                            allowAdd = false;
                            break;
                        }
                    }

                    if (allowAdd) data.add(list.get(i));
                }
                mAdapter.changeDataSet(data);

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
                new MessageItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
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

    private void showUndoSnackbar(Boolean show, View.OnClickListener action) {
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
