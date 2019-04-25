package com.practice.phuc.ums_husc.MessageModule;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.practice.phuc.ums_husc.Adapter.MessageSuggestionsAdapter;
import com.practice.phuc.ums_husc.Helper.NetworkUtil;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

public class SearchMessageActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final int ITEM_PER_PAGE = 50;
    public static final int SEARCH_RECEIVED = 1;
    public static final int SEARCH_SENT = 2;
    public static final int SEARCH_DELETED = 3;
    public static int mMessageType;

    private static List<TINNHAN> mSuggestions = new ArrayList<>();
    private MessageSuggestionsAdapter mMessageSuggestionsAdapter;
    private boolean mIsSearchExpand;
    private boolean mIsTextChanged;
    private int mCurrentPage;

    public static void setSuggestions(List<TINNHAN> list, int messageType) {
        mSuggestions.clear();
        mSuggestions.addAll(list);
        mMessageType = messageType;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mCurrentPage = 1;
        setUpRecylerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuggestions.clear();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_message_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        mSearch.expandActionView();
        mSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mIsSearchExpand = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mIsSearchExpand = false;
                return true;
            }
        });
        mIsSearchExpand = true;

        final SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint("Tìm kiếm...");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if (mIsTextChanged) {
            mCurrentPage = 1;
            new SearchMessageTask(mMessageType).execute(s);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (mIsSearchExpand) {
            mIsTextChanged = true;
            mMessageSuggestionsAdapter.onFilter(s);
        }
        return true;
    }

    private void setUpRecylerView() {
        mMessageSuggestionsAdapter = new MessageSuggestionsAdapter(this, mSuggestions);
        RecyclerView mRvSuggestion = findViewById(R.id.rv_message);
        mRvSuggestion.setLayoutManager(new LinearLayoutManager(this));
        mRvSuggestion.setHasFixedSize(true);
        mRvSuggestion.setAdapter(mMessageSuggestionsAdapter);
    }

    @SuppressLint("StaticFieldLeak")
    private class SearchMessageTask extends AsyncTask<String, Void, Boolean> {
        private int messageType;
        private Response mResponse;
        private String mJson;

        SearchMessageTask(int messageType) {
            this.messageType = messageType;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String url;
            switch (messageType) {
                case SEARCH_RECEIVED:
                    url = Reference.HOST + "api/sinhvien/tinnhan/timkiemdanhan/" +
                            "?tukhoa=" + strings[0] +
                            "&masinhvien=" + Reference.getStudentId(SearchMessageActivity.this) +
                            "&matkhau=" + Reference.getAccountPassword(SearchMessageActivity.this) +
                            "&sotrang=" + mCurrentPage +
                            "&sodong=" + ITEM_PER_PAGE;
                    break;

                case SEARCH_SENT:
                    url = Reference.HOST + "api/sinhvien/tinnhan/timkiemdagui/" +
                            "?tukhoa=" + strings[0] +
                            "&masinhvien=" + Reference.getStudentId(SearchMessageActivity.this) +
                            "&matkhau=" + Reference.getAccountPassword(SearchMessageActivity.this) +
                            "&sotrang=" + mCurrentPage +
                            "&sodong=" + ITEM_PER_PAGE;
                    break;

                default:
                    url = Reference.HOST + "api/sinhvien/tinnhan/timkiemdaxoa/" +
                            "?tukhoa=" + strings[0] +
                            "&masinhvien=" + Reference.getStudentId(SearchMessageActivity.this) +
                            "&matkhau=" + Reference.getAccountPassword(SearchMessageActivity.this) +
                            "&sotrang=" + mCurrentPage +
                            "&sodong=" + ITEM_PER_PAGE;
                    break;
            }
            mResponse = NetworkUtil.makeRequest(url, false, null);

            if (mResponse == null) return false;

            if (mResponse.code() == NetworkUtil.OK) {
                try {
                    mJson = mResponse.body() != null ? mResponse.body().string() : "";
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

            } else {
                try {
                    Log.e("DEBUG", mResponse.body() != null ? mResponse.body().string() : null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                List<TINNHAN> result = TINNHAN.fromJsonToList(mJson);
                if (result != null) {
                    mIsTextChanged = false;
                    mMessageSuggestionsAdapter.onNotifySearchResultChanged(result);
                }
            }
        }
    }
}
