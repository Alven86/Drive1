package enal1586.ju.drive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryView> {

    private List<HistoryInfo> mItemList;
    private Context mContext;

    public HistoryAdapter(List<HistoryInfo> itemList, Context context) {
        this.mItemList = itemList;
        this.mContext = context;
    }

    @Override
    public HistoryView onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        HistoryView rcv = new HistoryView(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(HistoryView holder, final int position) {
        holder.mRideId.setText(mItemList.get(position).getmRideId());
        if(mItemList.get(position).getmTime()!=null){
            holder.mTime.setText(mItemList.get(position).getmTime());
        }
    }
    @Override
    public int getItemCount() {
        return this.mItemList.size();
    }

}
