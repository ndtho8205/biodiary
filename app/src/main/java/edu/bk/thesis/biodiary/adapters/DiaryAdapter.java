package edu.bk.thesis.biodiary.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryAdapterViewHolder>
{
    private final DiaryAdapterOnClickHandler mClickHandler;
    private       Diary[]                    mDiaryData;

    public DiaryAdapter(DiaryAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    @Override
    public DiaryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context        context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View           view     = inflater.inflate(R.layout.diary_list_item, parent, false);
        return new DiaryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiaryAdapterViewHolder holder, int position)
    {
        Diary diary = mDiaryData[position];


        holder.mDot.setText(Html.fromHtml("&#8226;"));

        holder.mTimestamp.setText(diary.getDateInString());

        holder.mContent.setText(diary.getShortContent(200));
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

    public interface DiaryAdapterOnClickHandler
    {
        void onClick(Diary diary);
    }

    class DiaryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView mDot;
        final TextView mTimestamp;
        final TextView mContent;

        DiaryAdapterViewHolder(View view)
        {
            super(view);

            mDot = view.findViewById(R.id.tv_diary_dot);
            mTimestamp = view.findViewById(R.id.tv_diary_timestamp);
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
