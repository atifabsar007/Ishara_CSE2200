package com.kuet.ishara.ui;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kuet.ishara.databinding.ItemLessonBinding;
import com.kuet.ishara.logic.GestureDictionary;
import java.util.List;
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.Holder>{
 public interface Listener{void onPractice(String label);}
 private final List<String> labels;private final Listener listener;
 public LessonAdapter(List<String> labels,Listener listener){this.labels=labels;this.listener=listener;}
 @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p,int type){return new Holder(ItemLessonBinding.inflate(LayoutInflater.from(p.getContext()),p,false));}
 @Override public void onBindViewHolder(@NonNull Holder h,int pos){String g=labels.get(pos);
    h.b.txtName.setText(g.replace('_',' '));h.b.txtMeaning.setText("Demo phrase: "+GestureDictionary.translate(g));
    h.b.btnPractice.setOnClickListener(v->listener.onPractice(g));
 }
 @Override public int getItemCount(){return labels.size();}
 static class Holder extends RecyclerView.ViewHolder{final ItemLessonBinding b;Holder(ItemLessonBinding b){super(b.getRoot());this.b=b;}}
}
