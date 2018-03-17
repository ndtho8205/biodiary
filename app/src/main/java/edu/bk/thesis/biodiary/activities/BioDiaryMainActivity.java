package edu.bk.thesis.biodiary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.adapters.EntryListAdapter;
import edu.bk.thesis.biodiary.models.Diary;


public class BioDiaryMainActivity extends AppCompatActivity
        implements EntryListAdapter.DiaryAdapterOnClickHandler
{
    private FloatingActionButton mNewEntryButton;
    private RecyclerView         mEntryList;
    private EntryListAdapter     mEntryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodiary_main);

        // newEntryButton
        mNewEntryButton = findViewById(R.id.fab_new_entry);
        mNewEntryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // diaryList
        mEntryList = findViewById(R.id.rv_diary);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                                                                    LinearLayoutManager.VERTICAL,
                                                                    false);
        mEntryList.setLayoutManager(layoutManager);
        // mDiary.addItemDecoration(new DividerItemDecoration(this, 16));
        mEntryList.setHasFixedSize(true);
        mEntryListAdapter = new EntryListAdapter(this);
        mEntryList.setAdapter(mEntryListAdapter);

        // initialize diary data
        List<Diary.Entry> entryListData = new ArrayList<>();

        entryListData.add(new Diary.Entry(1,
                                          new Date().getTime(),
                                          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi lobortis risus in rutrum scelerisque. Sed bibendum lacus et quam blandit, nec feugiat neque porta. In lacinia viverra efficitur. Cras aliquet nulla non lacus hendrerit, id mattis enim ultrices. Cras fermentum vitae sapien ut imperdiet. Nulla eu imperdiet massa. Aenean in velit vel urna vulputate porta ornare eu lorem. Morbi eu maximus odio. Ut quis tortor quis ex dictum viverra nec nec nisi. Phasellus ac placerat mauris, at facilisis risus. Sed luctus volutpat felis. Donec dignissim luctus auctor. Nulla fringilla vulputate erat, pulvinar bibendum lectus ultrices eget."));
        entryListData.add(new Diary.Entry(2,
                                          new Date().getTime(),
                                          "Phasellus nec nisl vitae tellus sollicitudin facilisis eu sit amet velit. Curabitur ultricies viverra nunc, sit amet vestibulum metus tristique id. Sed sed lectus malesuada, venenatis sapien ac, fringilla lorem. Praesent efficitur erat sed lacus tempor, eget commodo felis euismod. Vestibulum dapibus ante sed lorem convallis iaculis. Nulla aliquam ligula elementum velit scelerisque scelerisque. Suspendisse ac velit nunc. Aliquam erat volutpat. Fusce dignissim quam at tortor malesuada sollicitudin. Etiam sollicitudin laoreet dignissim. Cras mollis aliquet ante, semper commodo eros dictum vel. Quisque id posuere odio, vel fringilla magna."));
        entryListData.add(new Diary.Entry(3,
                                          new Date().getTime(),
                                          "Nullam eget luctus erat, id elementum urna. Praesent tristique feugiat nibh eget molestie. Suspendisse potenti. Duis tincidunt id tortor quis hendrerit. Nullam molestie et velit a pellentesque. In hac habitasse platea dictumst. Praesent in mauris quis massa fringilla suscipit. Nunc a dignissim nunc. Duis eleifend ipsum quam, ac interdum sapien consequat sed. Praesent cursus consectetur risus ac blandit. Morbi vestibulum dapibus dui, nec dignissim orci dignissim eget. Suspendisse potenti."));
        entryListData.add(new Diary.Entry(4,
                                          new Date().getTime(),
                                          "Nullam eget luctus erat, id elementum urna. Praesent tristique feugiat nibh eget molestie. Suspendisse potenti. Duis tincidunt id tortor quis hendrerit. Nullam molestie et velit a pellentesque. In hac habitasse platea dictumst. Praesent in mauris quis massa fringilla suscipit. Nunc a dignissim nunc. Duis eleifend ipsum quam, ac interdum sapien consequat sed. Praesent cursus consectetur risus ac blandit. Morbi vestibulum dapibus dui, nec dignissim orci dignissim eget. Suspendisse potenti."));
        entryListData.add(new Diary.Entry(5,
                                          new Date().getTime(),
                                          "Nullam eget luctus erat, id elementum urna. Praesent tristique feugiat nibh eget molestie. Suspendisse potenti. Duis tincidunt id tortor quis hendrerit. Nullam molestie et velit a pellentesque. In hac habitasse platea dictumst. Praesent in mauris quis massa fringilla suscipit. Nunc a dignissim nunc. Duis eleifend ipsum quam, ac interdum sapien consequat sed. Praesent cursus consectetur risus ac blandit. Morbi vestibulum dapibus dui, nec dignissim orci dignissim eget. Suspendisse potenti."));
        entryListData.add(new Diary.Entry(6,
                                          new Date().getTime(),
                                          "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi lobortis risus in rutrum scelerisque. Sed bibendum lacus et quam blandit, nec feugiat neque porta. In lacinia viverra efficitur. Cras aliquet nulla non lacus hendrerit, id mattis enim ultrices. Cras fermentum vitae sapien ut imperdiet. Nulla eu imperdiet massa. Aenean in velit vel urna vulputate porta ornare eu lorem. Morbi eu maximus odio. Ut quis tortor quis ex dictum viverra nec nec nisi. Phasellus ac placerat mauris, at facilisis risus. Sed luctus volutpat felis. Donec dignissim luctus auctor. Nulla fringilla vulputate erat, pulvinar bibendum lectus ultrices eget."));
        entryListData.add(new Diary.Entry(7,
                                          new Date().getTime(),
                                          "Phasellus nec nisl vitae tellus sollicitudin facilisis eu sit amet velit. Curabitur ultricies viverra nunc, sit amet vestibulum metus tristique id. Sed sed lectus malesuada, venenatis sapien ac, fringilla lorem. Praesent efficitur erat sed lacus tempor, eget commodo felis euismod. Vestibulum dapibus ante sed lorem convallis iaculis. Nulla aliquam ligula elementum velit scelerisque scelerisque. Suspendisse ac velit nunc. Aliquam erat volutpat. Fusce dignissim quam at tortor malesuada sollicitudin. Etiam sollicitudin laoreet dignissim. Cras mollis aliquet ante, semper commodo eros dictum vel. Quisque id posuere odio, vel fringilla magna."));
        entryListData.add(new Diary.Entry(8,
                                          new Date().getTime(),
                                          "Nullam eget luctus erat, id elementum urna. Praesent tristique feugiat nibh eget molestie. Suspendisse potenti. Duis tincidunt id tortor quis hendrerit. Nullam molestie et velit a pellentesque. In hac habitasse platea dictumst. Praesent in mauris quis massa fringilla suscipit. Nunc a dignissim nunc. Duis eleifend ipsum quam, ac interdum sapien consequat sed. Praesent cursus consectetur risus ac blandit. Morbi vestibulum dapibus dui, nec dignissim orci dignissim eget. Suspendisse potenti."));
        entryListData.add(new Diary.Entry(9,
                                          new Date().getTime(),
                                          "Nullam eget luctus erat, id elementum urna. Praesent tristique feugiat nibh eget molestie. Suspendisse potenti. Duis tincidunt id tortor quis hendrerit. Nullam molestie et velit a pellentesque. In hac habitasse platea dictumst. Praesent in mauris quis massa fringilla suscipit. Nunc a dignissim nunc. Duis eleifend ipsum quam, ac interdum sapien consequat sed. Praesent cursus consectetur risus ac blandit. Morbi vestibulum dapibus dui, nec dignissim orci dignissim eget. Suspendisse potenti."));
        entryListData.add(new Diary.Entry(10,
                                          new Date().getTime(),
                                          "Nullam eget luctus erat, id elementum urna. Praesent tristique feugiat nibh eget molestie. Suspendisse potenti. Duis tincidunt id tortor quis hendrerit. Nullam molestie et velit a pellentesque. In hac habitasse platea dictumst. Praesent in mauris quis massa fringilla suscipit. Nunc a dignissim nunc. Duis eleifend ipsum quam, ac interdum sapien consequat sed. Praesent cursus consectetur risus ac blandit. Morbi vestibulum dapibus dui, nec dignissim orci dignissim eget. Suspendisse potenti."));

        mEntryListAdapter.setEntryList(entryListData);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.biodiary_main_activity_actions, menu);
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
    public void onClick(Diary.Entry entry)
    {
        Intent intentToStartEntryDetailActivity = new Intent(this, EntryDetailActivity.class);
        intentToStartEntryDetailActivity.putExtra(Intent.EXTRA_TEXT, entry);
        startActivity(intentToStartEntryDetailActivity);
    }
}
