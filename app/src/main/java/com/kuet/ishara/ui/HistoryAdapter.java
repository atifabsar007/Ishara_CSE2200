package com.kuet.ishara.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kuet.ishara.databinding.ItemMessageBinding;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    public static class Item {
        public final long id, timestamp;
        public final String text, source;
        public Item(long id, String text, String source, long timestamp) {
            this.id=id; this.text=text; this.source=source; this.timestamp=timestamp;
        }
    }
    public interface OnAction { void onSpeak(Item item); void onSave(Item item); void onDelete(Item item); }
    private List<Item> items;
    private final OnAction action;
    public HistoryAdapter(List<Item> items, OnAction action) { this.items=items; this.action=action; }
    public void submit(List<Item> next) { items=next; notifyDataSetChanged(); }
    @Override @NonNull public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int pos) {
        Item item=items.get(pos);
        holder.b.txtMessage.setText(item.text);
        holder.b.txtMeta.setText(item.timestamp==0 ? item.source : item.source+"  •  "+DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(new Date(item.timestamp)));
        holder.b.btnSpeak.setOnClickListener(v -> action.onSpeak(item));
        holder.b.btnSave.setOnClickListener(v -> action.onSave(item));
        holder.b.btnDelete.setOnClickListener(v -> action.onDelete(item));
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        final ItemMessageBinding b;
        Holder(ItemMessageBinding b) { super(b.getRoot()); this.b=b; }
    }
}
