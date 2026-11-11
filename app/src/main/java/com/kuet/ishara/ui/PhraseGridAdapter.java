package com.kuet.ishara.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kuet.ishara.databinding.ItemPhraseBinding;
import java.util.List;

public class PhraseGridAdapter extends RecyclerView.Adapter<PhraseGridAdapter.Holder> {
    public interface OnChoose { void onChoose(String phrase); }
    private final List<String> phrases;
    private final OnChoose listener;
    public PhraseGridAdapter(List<String> phrases, OnChoose listener) { this.phrases=phrases; this.listener=listener; }
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemPhraseBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int pos) {
        String phrase=phrases.get(pos); holder.binding.btnPhrase.setText(phrase);
        holder.binding.btnPhrase.setOnClickListener(v -> listener.onChoose(phrase));
    }
    @Override public int getItemCount() { return phrases.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final ItemPhraseBinding binding;
        Holder(ItemPhraseBinding b) { super(b.getRoot()); binding=b; }
    }
}
