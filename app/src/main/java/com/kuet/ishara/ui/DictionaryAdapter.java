package com.kuet.ishara.ui;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kuet.ishara.R;
import com.kuet.ishara.databinding.ItemDictionaryBinding;
import com.kuet.ishara.logic.GestureDictionary;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.function.Consumer;
public final class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.Holder>{
  private final List<String> filtered=new ArrayList<>();
  private final Consumer<String> selected;
  public DictionaryAdapter(Consumer<String> selected){this.selected=selected;}
  public void filter(String text){
    filtered.clear();String q=text.trim().toLowerCase(Locale.ROOT);
    for(Map.Entry<String,String> e:GestureDictionary.all().entrySet())
      if(e.getKey().toLowerCase(Locale.ROOT).contains(q)||e.getValue().contains(q)) filtered.add(e.getKey());
    notifyDataSetChanged();
  }
  @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent,int type){return new Holder(ItemDictionaryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));}
  @Override public void onBindViewHolder(@NonNull Holder h,int pos){
    String label=filtered.get(pos);
    h.b.txtEnglish.setText(label.replace('_',' '));h.b.txtBangla.setText(GestureDictionary.translate(label));
    h.b.handPicture.setImageResource(handImage(label));
    h.b.getRoot().setOnClickListener(v->selected.accept(label));
  }
  private int handImage(String label){switch(label){
    case "Thumb_Up":return R.drawable.hand_thumb_up;
    case "Thumb_Down":return R.drawable.hand_thumb_down;
    case "Open_Palm":return R.drawable.hand_open_palm;
    case "Closed_Fist":return R.drawable.hand_closed_fist;
    case "Victory":return R.drawable.hand_victory;
    case "Pointing_Up":return R.drawable.hand_pointing_up;
    default:return R.drawable.hand_iloveyou;}}
  @Override public int getItemCount(){return filtered.size();}
  static final class Holder extends RecyclerView.ViewHolder{final ItemDictionaryBinding b;
    Holder(ItemDictionaryBinding b){super(b.getRoot());this.b=b;}}
}
