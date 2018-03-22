package edu.bk.thesis.biodiary.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class EntryDetailActivity extends AppCompatActivity
{
    private Toolbar              mToolbar;
    private ImageView            mCloseButton;
    private FloatingActionButton mEditEntryButton;

    private TextView mDetailTimestamp;
    private TextView mDetailContent;

    private Diary.Entry mEntry;

    private boolean mIsEdited = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_entry_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_entry_detail_trash:
                handleDeleteEntryButtonPressed();
                return true;
            case R.id.action_entry_detail_copy:
                handleCopyEntryButtonPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleDeleteEntryButtonPressed()
    {
        Intent result = new Intent();
        result.putExtra(BioDiaryMainActivity.EXTRA_DELETE_ENTRY, true);
        result.putExtra(Intent.EXTRA_TEXT, mEntry);
        setResult(Activity.RESULT_OK, result);

        finish();
    }

    private void handleCopyEntryButtonPressed()
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied entry's content.",
                                              mEntry.getContent());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);

        mToolbar = findViewById(R.id.entry_detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        mCloseButton = findViewById(R.id.action_entry_detail_close);
        mCloseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleCloseButtonPressed();
            }
        });

        mEditEntryButton = findViewById(R.id.fab_edit_entry);
        mEditEntryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                handleEditEntryButtonPressed();
            }
        });

        mDetailTimestamp = findViewById(R.id.tv_entry_detail_timestamp);
        mDetailContent = findViewById(R.id.tv_entry_detail_content);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                mEntry = (Diary.Entry) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mDetailTimestamp.setText(mEntry.getTimestampInString());
                mDetailContent.setText(mEntry.getContent());
            }
        }
    }

    private void handleCloseButtonPressed()
    {
        if (!mIsEdited) {
            setResult(Activity.RESULT_CANCELED);
        }
        else {
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT, mEntry);
            setResult(Activity.RESULT_OK, result);
        }
        finish();
    }

    private void handleEditEntryButtonPressed()
    {
        mIsEdited = true;
        Intent intentToStartEntryEditorActivity = new Intent(this, EntryEditorActivity.class);
        intentToStartEntryEditorActivity.putExtra(Intent.EXTRA_TEXT, mEntry);
        startActivityForResult(intentToStartEntryEditorActivity,
                               BioDiaryMainActivity.ENTRY_EDIT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            if (requestCode == BioDiaryMainActivity.ENTRY_EDIT_REQUEST) {
                mEntry = (Diary.Entry) data.getSerializableExtra(Intent.EXTRA_TEXT);
                mDetailContent.setText(mEntry.getContent());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
