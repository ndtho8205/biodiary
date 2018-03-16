package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class DiaryDetailActivity extends AppCompatActivity
{
    private TextView mDetailTimestamp;
    private TextView mDetailContent;

    private Diary mDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        mDetailTimestamp = findViewById(R.id.tv_diary_detail_timestamp);
        mDetailContent = findViewById(R.id.tv_diary_detail_content);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                mDiary = (Diary) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mDetailTimestamp.setText(mDiary.getDateInString());
                mDetailContent.setText(mDiary.getContent());
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
