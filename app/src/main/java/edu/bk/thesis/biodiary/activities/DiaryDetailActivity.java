package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class DiaryDetailActivity extends AppCompatActivity
{
    private TextView mDetailTitle;
    private TextView mDetailDate;
    private TextView mDetailContent;

    private Diary mDiary;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        mDetailTitle = findViewById(R.id.tv_diary_detail_title);
        mDetailDate = findViewById(R.id.tv_diary_detail_date);
        mDetailContent = findViewById(R.id.tv_diary_detail_content);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                mDiary = (Diary) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mDetailTitle.setText(mDiary.getTitle());
                mDetailDate.setText(mDiary.getDateInString());
                mDetailContent.setText(mDiary.getContent());
            }
        }
    }
}
