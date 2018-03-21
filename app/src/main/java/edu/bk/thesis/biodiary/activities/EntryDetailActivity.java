package edu.bk.thesis.biodiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class EntryDetailActivity extends AppCompatActivity
{
    private Toolbar        mToolbar;
    private ActionMenuView mMenuView;

    private FloatingActionButton mEditEntryButton;

    private TextView mDetailTimestamp;
    private TextView mDetailContent;

    private Diary.Entry mEntry;

    private boolean mIsEdited = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_entry_detail, mMenuView.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_entry_detail_close:
                handleReturnResult();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);

        mToolbar = findViewById(R.id.entry_detail_toolbar);
        mMenuView = findViewById(R.id.entry_detail_toolbar_amv);
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

        mDetailTimestamp = findViewById(R.id.tv_entry_detail_timestamp);
        mDetailContent = findViewById(R.id.tv_entry_detail_content);

        mEditEntryButton = findViewById(R.id.fab_edit_entry);
        mEditEntryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForEditEntry();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                mEntry = (Diary.Entry) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mDetailTimestamp.setText(mEntry.getTimestampInString());
                mDetailContent.setText(mEntry.getContent());
            }
        }
    }

    private void startActivityForEditEntry()
    {
        mIsEdited = true;
        Intent intentToStartEntryEditorActivity = new Intent(this, EntryEditorActivity.class);
        intentToStartEntryEditorActivity.putExtra(Intent.EXTRA_TEXT, mEntry);
        startActivityForResult(intentToStartEntryEditorActivity,
                               BioDiaryMainActivity.ENTRY_EDIT_REQUEST);
    }

    private void handleReturnResult()
    {
        if (!mIsEdited) {
            setResult(Activity.RESULT_CANCELED);
        }
        else {
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_TEXT,
                            mEntry);
            setResult(Activity.RESULT_OK, result);
        }
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
