package edu.bk.thesis.biodiary.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.bk.thesis.biodiary.R;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryAdapterViewHolder>
{
    private String[] mDiaryData;
    private final DiaryAdapterOnClickHandler mClickHandler;

    public interface DiaryAdapterOnClickHandler {
        void onClick(String diary);
    }

    public DiaryAdapter(DiaryAdapterOnClickHandler clickHandler) {
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
        String diaryTitle = mDiaryData[position];
        holder.mTitle.setText(diaryTitle);
    }

    @Override
    public int getItemCount()
    {
        if (mDiaryData == null) {
            return 0;
        }
        return mDiaryData.length;
    }

    public void setDiaryData(String[] diaryData)
    {
        mDiaryData = diaryData;
        notifyDataSetChanged();
    }

    class DiaryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView mTitle;

        DiaryAdapterViewHolder(View view)
        {
            super(view);
            mTitle = view.findViewById(R.id.tv_diary_title);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int adapterPosition = getAdapterPosition();
            String diary = mDiaryData[adapterPosition];
            mClickHandler.onClick(diary);
        }
    }
}
