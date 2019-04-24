package com.practice.phuc.ums_husc.MessageModule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.practice.phuc.ums_husc.Adapter.MessageSuggestionsAdapter;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchMessageActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static List<TINNHAN> mSuggestions = new ArrayList<>();
    private MessageSuggestionsAdapter mMessageSuggestionsAdapter;

    public static void setSuggestions(List<TINNHAN> list) {
        mSuggestions.clear();
        mSuggestions.addAll(list);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint("Tìm kiếm...");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mMessageSuggestionsAdapter.onFilter(s);
        return true;
    }

    private void setUpRecylerView() {
        mMessageSuggestionsAdapter = new MessageSuggestionsAdapter(this, mSuggestions);
        RecyclerView mRvSuggestion = findViewById(R.id.rv_message);
        mRvSuggestion.setLayoutManager(new LinearLayoutManager(this));
        mRvSuggestion.setHasFixedSize(true);
        mRvSuggestion.setAdapter(mMessageSuggestionsAdapter);
    }
}
