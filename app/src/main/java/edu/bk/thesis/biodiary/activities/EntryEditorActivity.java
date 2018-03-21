package edu.bk.thesis.biodiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class EntryEditorActivity extends AppCompatActivity
{
    private Toolbar        mToolbar;
    private ActionMenuView mMenuView;
    private EditText       mEntryDate;
    private EditText       mEntryContent;
    private Diary.Entry    mEntry;
    private boolean        isNewEntry;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_entry_editor, mMenuView.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_editor_close:
                if (isNewEntry) {
                    returnNewEntry();
                }
                else {
                    returnEditedEntry();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_editor);

        mToolbar = findViewById(R.id.editor_toolbar);
        mMenuView = findViewById(R.id.editor_toolbar_amv);
        mMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                return onOptionsItemSelected(item);
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        mEntryDate = findViewById(R.id.et_entry_date);
        mEntryContent = findViewById(R.id.et_entry_content);

        isNewEntry = true;

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                isNewEntry = false;
                mEntry = (Diary.Entry) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mEntryDate.setText(mEntry.getTimestampInString());
                mEntryContent.setText(mEntry.getContent());
            }
        }

        // ActionBar actionBar = getSupportActionBar();
        // if (actionBar != null) {
        //     actionBar.setDisplayHomeAsUpEnabled(true);
        // }
    }

    private void returnNewEntry()
    {
        if (mEntryContent.getText().toString().trim().isEmpty()) {
            setResult(Activity.RESULT_CANCELED);
        }
        else {
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT,
                            new Diary.Entry(mEntryDate.getText().toString(),
                                            mEntryContent.getText().toString()));
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }

    private void returnEditedEntry()
    {
        if (mEntryContent.getText().toString().trim().isEmpty()) {
            setResult(Activity.RESULT_CANCELED);
        }
        else {
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT,
                            new Diary.Entry(
                                    mEntry.getId(),
                                    mEntry.getTimestamp(),
                                    mEntryContent.getText().toString()));
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }
}
