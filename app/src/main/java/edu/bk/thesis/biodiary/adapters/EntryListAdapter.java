package edu.bk.thesis.biodiary.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.bk.thesis.biodiary.R;
import edu.bk.thesis.biodiary.models.Diary;


public class EntryListAdapter
        extends RecyclerView.Adapter<EntryListAdapter.EntryListAdapterViewHolder>
{
    private final DiaryAdapterOnClickHandler mClickHandler;
    private       List<Diary.Entry>          mEntryList;

    public EntryListAdapter(DiaryAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    @Override
    public EntryListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context        context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View           view     = inflater.inflate(R.layout.entry_list_item, parent, false);
        return new EntryListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EntryListAdapterViewHolder holder, int position)
    {
        Diary.Entry entry = mEntryList.get(position);


        holder.mDot.setText(Html.fromHtml("&#8226;"));

        holder.mTimestamp.setText(entry.getDateInString());

        holder.mContent.setText(entry.getShortContent(200));
    }

    @Override
    public int getItemCount()
    {
        if (mEntryList == null) {
            return 0;
        }
        return mEntryList.size();
    }

    public void setEntryList(List<Diary.Entry> entryList)
    {
        mEntryList = entryList;
        notifyDataSetChanged();
    }

    public interface DiaryAdapterOnClickHandler
    {
        void onClick(Diary.Entry entry);
    }

    class EntryListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView mDot;
        final TextView mTimestamp;
        final TextView mContent;

        EntryListAdapterViewHolder(View view)
        {
            super(view);

            mDot = view.findViewById(R.id.tv_entry_dot);
            mTimestamp = view.findViewById(R.id.tv_entry_timestamp);
            mContent = view.findViewById(R.id.tv_entry_content);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int         adapterPosition = getAdapterPosition();
            Diary.Entry entry           = mEntryList.get(adapterPosition);
            mClickHandler.onClick(entry);
        }
    }
}
