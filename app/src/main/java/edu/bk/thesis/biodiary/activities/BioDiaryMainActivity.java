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
import edu.bk.thesis.biodiary.models.Diary;


public class BioDiaryMainActivity extends AppCompatActivity
        implements EntryListAdapter.DiaryAdapterOnClickHandler
{
    static final int NEW_ENTRY_REQUEST = 1;

    private FloatingActionButton mNewEntryButton;
    private RecyclerView         mEntryList;
    private TextView             mEmptyDiaryNotify;
    private EntryListAdapter     mEntryListAdapter;
    private Diary                mDiary;
    private DatabaseHandler      mDatabaseHandler;

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
                startActivityForNewEntry();
            }
        });

        // diaryList
        mEntryList = findViewById(R.id.rv_diary);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                                                                    LinearLayoutManager.VERTICAL,
                                                                    false);
        mEntryList.setLayoutManager(layoutManager);
        mEntryList.setItemAnimator(new DefaultItemAnimator());
        // mEntryList.addItemDecoration(new DividerItemDecoration(this, 16));
        mEntryList.setHasFixedSize(true);
        mEntryListAdapter = new EntryListAdapter(this);
        mEntryList.setAdapter(mEntryListAdapter);

        // init
        mDatabaseHandler = new DatabaseHandler(this);
        mDiary = new Diary(mDatabaseHandler.getAllEntries());
        mEntryListAdapter.setDiary(mDiary);
        toggleEmptyDiary();
    }

    private void startActivityForNewEntry()
    {
        Intent intentToStartEntryEditorActivity = new Intent(BioDiaryMainActivity.this,
                                                             EntryEditorActivity.class);
        startActivityForResult(intentToStartEntryEditorActivity, NEW_ENTRY_REQUEST);
    }

    private void toggleEmptyDiary()
    {
        if (mDiary.getEntryList().size() > 0) {
            mEmptyDiaryNotify.setVisibility(View.GONE);
        }
        else {
            mEmptyDiaryNotify.setVisibility(View.VISIBLE);
        }
    }

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
                System.out.println("Action: Settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == NEW_ENTRY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Diary.Entry newEntry = (Diary.Entry) data.getSerializableExtra(Intent.EXTRA_TEXT);
                handleNewEntryCreated(newEntry);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleNewEntryCreated(Diary.Entry entry)
    {
        long        id                = mDatabaseHandler.insertEntry(entry);
        Diary.Entry entryFromDatabase = mDatabaseHandler.getEntry(id);

        if (entryFromDatabase != null) {
            mDiary.getEntryList().add(0, entryFromDatabase);
            mEntryListAdapter.notifyDataSetChanged();

            toggleEmptyDiary();
        }
    }

    @Override
    public void onClick(Diary.Entry entry)
    {
        startActivityForEntryDetail(entry);
    }

    private void startActivityForEntryDetail(Diary.Entry entry)
    {
        Intent intentToStartEntryDetailActivity = new Intent(this, EntryDetailActivity.class);
        intentToStartEntryDetailActivity.putExtra(Intent.EXTRA_TEXT, entry);
        startActivity(intentToStartEntryDetailActivity);
    }
}
