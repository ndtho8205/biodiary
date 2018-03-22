package edu.bk.thesis.biodiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class EntryEditorActivity extends AppCompatActivity
{
    private Toolbar   mToolbar;
    private ImageView mCloseButton;

    private EditText mEntryDate;
    private EditText mEntryContent;

    private Diary.Entry mEntry;

    private boolean isNewEntry;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_entry_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
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
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        mCloseButton = findViewById(R.id.action_editor_close);
        mCloseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleCloseButtonPressed();
            }
        });

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
    }

    private void handleCloseButtonPressed()
    {
        if (isNewEntry) {
            handleReturnNewEntry();
        }
        else {
            handleReturnEditedEntry();
        }
        finish();
    }

    private void handleReturnNewEntry()
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
    }

    private void handleReturnEditedEntry()
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
    }
}
