package enal1586.ju.drive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;



public class HistoryView extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextView mRideId,mTime;
    //constants
    private final static String RiderId = "rideId";


    public HistoryView(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mRideId = (TextView) itemView.findViewById(R.id.rideId);
        mTime = (TextView) itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), HistorySingleActivity.class);
        Bundle b = new Bundle();
        b.putString(RiderId, mRideId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}