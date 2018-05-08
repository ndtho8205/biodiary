package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.adapters.EntryListAdapter;
import edu.bk.thesis.biodiary.handlers.DatabaseHandler;
import edu.bk.thesis.biodiary.handlers.PreferencesHandler;
import edu.bk.thesis.biodiary.models.Diary;


public class BioDiaryMainActivity extends AppCompatActivity
    implements EntryListAdapter.DiaryAdapterOnClickHandler
{

    static final int ENTRY_CREATE_REQUEST = 1;
    static final int ENTRY_DETAIL_REQUEST = 2;
    static final int ENTRY_EDIT_REQUEST   = 3;

    static final String EXTRA_DELETE_ENTRY = "DELETE_ENTRY";

    private FloatingActionButton mNewEntryButton;

    private TextView mEmptyDiaryNotify;

    private RecyclerView     mEntryList;
    private EntryListAdapter mEntryListAdapter;

    private DatabaseHandler    mDatabaseHandler;
    private PreferencesHandler mPreferencesHandler;

    private Diary mDiary;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_logout:
                mPreferencesHandler.logoutUser();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case R.id.action_reset:
                mPreferencesHandler.reset();

                startActivity(new Intent(this, SplashScreenActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Diary.Entry entry)
    {
        handleEntryPressed(entry);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodiary_main);

        // emptyDiaryNotify
        mEmptyDiaryNotify = findViewById(R.id.tv_empty_diary);

        // newEntryButton
        mNewEntryButton = findViewById(R.id.fab_new_entry);
        mNewEntryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleNewEntryButtonPressed();
            }
        });

        // diaryList
        mEntryList = findViewById(R.id.rv_diary);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                                                                    LinearLayoutManager.VERTICAL,
                                                                    false);
        mEntryList.setLayoutManager(layoutManager);
        mEntryList.setItemAnimator(new DefaultItemAnimator());
        mEntryList.setHasFixedSize(true);
        mEntryListAdapter = new EntryListAdapter(this);
        mEntryList.setAdapter(mEntryListAdapter);

        // handles
        mPreferencesHandler = new PreferencesHandler(getApplicationContext());
        mDatabaseHandler = new DatabaseHandler(this);

        // diary
        mDiary = new Diary(mDatabaseHandler.getAllEntries());
        mEntryListAdapter.setDiary(mDiary);

        toggleEmptyDiary();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            Diary.Entry entry
                = (Diary.Entry) data.getSerializableExtra(Intent.EXTRA_TEXT);

            switch (requestCode) {
                case ENTRY_CREATE_REQUEST:
                    handleEntryCreated(entry);
                    break;
                case ENTRY_DETAIL_REQUEST:
                    if (data.hasExtra(EXTRA_DELETE_ENTRY)) {
                        handleEntryDeleted(entry);
                    }
                    else {
                        handleEntryEdited(entry);
                    }
            }
        }
    }

    private void handleEntryPressed(Diary.Entry entry)
    {
        Intent intentToStartEntryDetailActivity = new Intent(this, EntryDetailActivity.class);
        intentToStartEntryDetailActivity.putExtra(Intent.EXTRA_TEXT, entry);
        startActivityForResult(intentToStartEntryDetailActivity, ENTRY_DETAIL_REQUEST);
    }

    private void handleNewEntryButtonPressed()
    {
        Intent intentToStartEntryEditorActivity = new Intent(BioDiaryMainActivity.this,
                                                             EntryEditorActivity.class);
        startActivityForResult(intentToStartEntryEditorActivity, ENTRY_CREATE_REQUEST);
    }

    private void toggleEmptyDiary()
    {
        if (mDiary.getEntryList().size() > 0) {
            mEmptyDiaryNotify.setVisibility(View.GONE);
        }
        else {
            mEmptyDiaryNotify.setVisibility(View.VISIBLE);
            mNewEntryButton.setVisibility(View.VISIBLE);
        }
    }

    private void handleEntryCreated(Diary.Entry entry)
    {
        long        id                = mDatabaseHandler.insertEntry(entry);
        Diary.Entry entryFromDatabase = mDatabaseHandler.getEntry(id);

        if (entryFromDatabase != null) {
            mDiary.getEntryList().add(0, entryFromDatabase);
            mEntryListAdapter.notifyDataSetChanged();

            toggleEmptyDiary();
        }
    }

    private void handleEntryDeleted(Diary.Entry entry)
    {
        mDatabaseHandler.deleteEntry(entry);

        for (int i = 0; i < mDiary.getEntryList().size(); ++i) {
            if (mDiary.getEntryList().get(i).getId() == entry.getId()) {
                mDiary.getEntryList().remove(i);
            }
        }
        mEntryListAdapter.notifyDataSetChanged();

        toggleEmptyDiary();
    }

    private void handleEntryEdited(Diary.Entry entry)
    {
        mDatabaseHandler.updateEntry(entry);

        for (Diary.Entry e : mDiary.getEntryList()) {
            if (e.getId() == entry.getId()) {
                e.setContent(entry.getContent());
                e.setDate(entry.getDateInMilisecond());
            }
        }
        mEntryListAdapter.notifyDataSetChanged();

        toggleEmptyDiary();
    }
}
