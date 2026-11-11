package com.kuet.ishara.ui;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kuet.ishara.databinding.ItemChatBinding;
import java.util.List;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {
    private final List<String> items;
    public ChatAdapter(List<String> items){this.items=items;}
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup group,int viewType){return new Holder(ItemChatBinding.inflate(LayoutInflater.from(group.getContext()),group,false));}
    @Override public void onBindViewHolder(@NonNull Holder h,int pos){h.b.txtBubble.setText(items.get(pos));}
    @Override public int getItemCount(){return items.size();}
    static class Holder extends RecyclerView.ViewHolder { final ItemChatBinding b; Holder(ItemChatBinding b){super(b.getRoot());this.b=b;} }
}
