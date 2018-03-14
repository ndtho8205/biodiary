package edu.bk.thesis.biodiary.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryAdapterViewHolder>
{
    private       Diary[]                    mDiaryData;
    private final DiaryAdapterOnClickHandler mClickHandler;

    public interface DiaryAdapterOnClickHandler
    {
        void onClick(Diary diary);
    }

    public DiaryAdapter(DiaryAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    @Override
    public DiaryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context        context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View           view     = inflater.inflate(R.layout.biodiary_list_item, parent, false);
        return new DiaryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiaryAdapterViewHolder holder, int position)
    {
        Diary diary = mDiaryData[position];

        holder.mTitle.setText(diary.getTitle());

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        holder.mDate.setText(df.format(diary.getDate()));

        holder.mContent.setText(diary.getContent());
    }

    @Override
    public int getItemCount()
    {
        if (mDiaryData == null) {
            return 0;
        }
        return mDiaryData.length;
    }

    public void setDiaryData(Diary[] diaryData)
    {
        mDiaryData = diaryData;
        notifyDataSetChanged();
    }

    class DiaryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView mTitle;
        final TextView mDate;
        final TextView mContent;

        DiaryAdapterViewHolder(View view)
        {
            super(view);
            mTitle = view.findViewById(R.id.tv_diary_title);
            mDate = view.findViewById(R.id.tv_diary_date);
            mContent = view.findViewById(R.id.tv_diary_content);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int   adapterPosition = getAdapterPosition();
            Diary diary           = mDiaryData[adapterPosition];
            mClickHandler.onClick(diary);
        }
    }
}
