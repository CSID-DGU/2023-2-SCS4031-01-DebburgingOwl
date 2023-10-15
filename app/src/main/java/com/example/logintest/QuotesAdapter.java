package com.example.logintest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {

    private List<String> quotes;

    public QuotesAdapter(List<String> quotes) {
        this.quotes = quotes;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quote_item, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        holder.tvQuote.setText(quotes.get(position));
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    class QuoteViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuote;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuote = itemView.findViewById(R.id.tvQuote);
        }
    }
}
