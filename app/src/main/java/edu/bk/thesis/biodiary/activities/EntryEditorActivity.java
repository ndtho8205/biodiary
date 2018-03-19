package edu.bk.thesis.biodiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class EntryEditorActivity extends AppCompatActivity
{
    private EditText    mEntryDate;
    private EditText    mEntryContent;
    private Diary.Entry mEntry;
    private boolean     isNewEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        mEntryDate = findViewById(R.id.et_entry_date);
        mEntryContent = findViewById(R.id.et_entry_content);

        isNewEntry = true;

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                isNewEntry = false;
                mEntry = (Diary.Entry) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mEntryDate.setText(mEntry.getDateInString());
                mEntryContent.setText(mEntry.getContent());
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void returnNewEntry()
    {
        Intent result = new Intent();
        getIntent().putExtra(Intent.EXTRA_TEXT, mEntry);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isNewEntry) {
                    returnNewEntry();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
