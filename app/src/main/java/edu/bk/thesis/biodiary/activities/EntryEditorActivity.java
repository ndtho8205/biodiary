package edu.bk.thesis.biodiary.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;
import edu.bk.thesis.biodiary.utils.DateConverter;


public class EntryEditorActivity extends AppCompatActivity
{
    private Toolbar   mToolbar;
    private ImageView mCloseButton;

    private TextView mEntryDate;
    private EditText mEntryContent;

    private Diary.Entry mEntry;

    private Calendar mCalendar;
    private boolean  isNewEntry;

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

        mCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener
                = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
            {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateEntryDate();
            }
        };

        mEntryDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new DatePickerDialog(EntryEditorActivity.this, dateSetListener,
                                     mCalendar.get(Calendar.YEAR),
                                     mCalendar.get(Calendar.MONTH),
                                     mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        isNewEntry = true;

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                isNewEntry = false;
                mEntry = (Diary.Entry) intent.getSerializableExtra(Intent.EXTRA_TEXT);
                mEntryDate.setText(mEntry.getDateInString(DateConverter.PATTERN_SHORT_DATE));
                mEntryContent.setText(mEntry.getContent());
            }
        }

        if (isNewEntry) {
            updateEntryDate();
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

    private void updateEntryDate()
    {
        mEntryDate.setText(DateConverter.date2String(mCalendar.getTime(),
                                                     DateConverter.PATTERN_SHORT_DATE));
    }

    private void handleReturnNewEntry()
    {
        if (mEntryContent.getText().toString().trim().isEmpty()) {
            setResult(Activity.RESULT_CANCELED);
        }
        else {
            Intent result = new Intent();
            try {
                result.putExtra(Intent.EXTRA_TEXT,
                                new Diary.Entry(mEntryContent.getText().toString(),
                                                mEntryDate.getText().toString(),
                                                DateConverter.PATTERN_SHORT_DATE));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
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
            try {
                result.putExtra(Intent.EXTRA_TEXT,
                                new Diary.Entry(
                                        mEntry.getId(),
                                        mEntry.getTimestamp(),
                                        mEntryContent.getText().toString(),
                                        mEntryDate.getText().toString(),
                                        DateConverter.PATTERN_SHORT_DATE));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            setResult(Activity.RESULT_OK, result);
        }
    }
}
