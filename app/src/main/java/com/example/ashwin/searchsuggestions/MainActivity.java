package com.example.ashwin.searchsuggestions;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ashwin.searchsuggestions.models.Suggestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean mVoiceSearch = false;
    private ArrayList<Suggestion> mSuggestions;
    private HashMap<String, String> mSuggestionsMap;
    private String[] mSuggestionsArray;
    private SimpleCursorAdapter mSuggestionsAdapter;
    private boolean mAreSuggestionsLoaded = false;

    private SearchView mSearchView;
    private TextView queryText, voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppThemeBlack);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        initToolbar();
    }

    private void initViews() {
        queryText = (TextView) findViewById(R.id.queryText);
        voice = (TextView) findViewById(R.id.voice_search);

        voice.setText("Voice input: " + String.valueOf(mVoiceSearch));
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Suggestions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo( getComponentName() )); //new ComponentName(this, MainActivity.class)
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryRefinementEnabled(true);

        initSuggestions();

        return true;
    }

    private void initSuggestions() {
        mSuggestions = new ArrayList<Suggestion>();

        Suggestion suggestion = new Suggestion("Clash of Titans", "Action");
        mSuggestions.add(suggestion);

        suggestion = new Suggestion("Escape Plan", "Action");
        mSuggestions.add(suggestion);

        suggestion = new Suggestion("Karate Kid", "Adventure");
        mSuggestions.add(suggestion);

        suggestion = new Suggestion("The Snowman", "Thriller");
        mSuggestions.add(suggestion);

        suggestion = new Suggestion("Sherlock Holmes", "Adventure");
        mSuggestions.add(suggestion);

        suggestion = new Suggestion("The Adventures of Tintin", "Animation");
        mSuggestions.add(suggestion);

        suggestion = new Suggestion("The Foreigner", "Action");
        mSuggestions.add(suggestion);

        mSuggestionsMap = new HashMap<>();
        for (Suggestion s : mSuggestions) {
            mSuggestionsMap.put(s.getTitle(), s.getSubtitle());
        }

        mSuggestionsArray = mSuggestionsMap.keySet().toArray(new String[mSuggestionsMap.keySet().size()]);

        final String[] from = new String[]{"suggestions"};
        final int[] to = new int[]{android.R.id.text1};
        mSuggestionsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mSearchView.setSuggestionsAdapter(mSuggestionsAdapter);
        mSearchView.setOnSuggestionListener(this);

        mAreSuggestionsLoaded = true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        queryText.setText(newText);

        voice.setText("Voice input: " + String.valueOf(mVoiceSearch));
        mVoiceSearch = false;

        populateAdapter(newText);

        return false;
    }

    private void populateAdapter(String query) {
        if (mAreSuggestionsLoaded) {
            final MatrixCursor c_m1 = new MatrixCursor(new String[]{BaseColumns._ID, "suggestions"});
            final MatrixCursor c_m2 = new MatrixCursor(new String[]{BaseColumns._ID, "suggestions"});
            final MatrixCursor c_m3 = new MatrixCursor(new String[]{BaseColumns._ID, "suggestions"});
            final MatrixCursor c_m4 = new MatrixCursor(new String[]{BaseColumns._ID, "suggestions"});
            for (int i = 0; i < mSuggestionsArray.length; i++) {
                switch (match_texts(query, mSuggestionsArray[i])) {
                    case 4: {
                        c_m4.addRow(new Object[]{i, mSuggestionsArray[i]});
                        break;
                    }
                    case 3: {
                        c_m3.addRow(new Object[]{i, mSuggestionsArray[i]});
                        break;
                    }
                    case 2: {
                        c_m2.addRow(new Object[]{i, mSuggestionsArray[i]});
                        break;
                    }
                    case 1: {
                        c_m1.addRow(new Object[]{i, mSuggestionsArray[i]});
                        break;
                    }
                    case 0: {
                        break;
                    }
                }
            }

            Cursor c = new MergeCursor( new Cursor[]{c_m4, c_m3, c_m2, c_m1} );
            mSuggestionsAdapter.changeCursor(c);
        }
    }

    private int match_texts(String query, String text){
        int matches = 0;
        ArrayList<String> q = new ArrayList<>(Arrays.asList(query.split(" ")));
        ArrayList<String> t = new ArrayList<>(Arrays.asList(text.split(" ")));
        for (int i = 0; i < q.size(); i++) {
            for (int j = 0; j < t.size(); j++) {
                if (t.get(j).toLowerCase().startsWith(q.get(i).toLowerCase())) {
                    matches += 1;
                    t.remove(j);
                    break;
                }
            }
        }
        return matches;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            //gets the search query from the voice recognizer intent
            String query = intent.getStringExtra(SearchManager.QUERY);

            //set voiceSearch = true
            mVoiceSearch = true;

            //set the search box text to the received query but does not submit the search
            mSearchView.setQuery(query, false);
        }
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor searchCursor = mSuggestionsAdapter.getCursor();
        if (searchCursor.moveToPosition(position)) {
            String selectedItem = searchCursor.getString(1);
            mSearchView.setQuery(selectedItem, false);
            Toast.makeText(this, selectedItem + " is selected", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}
