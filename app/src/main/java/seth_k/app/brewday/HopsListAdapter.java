package seth_k.app.brewday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HopsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Hops> mHops;
    private HopTimer mHopTimer;
    private final String mDurationFormat;

    public HopsListAdapter(Context context, List<Hops> hops, HopTimer timer) {
        mContext = context;
        mHops = hops;
        mHopTimer = timer;
        mDurationFormat = mContext.getString(R.string.hop_timer_item_duration_format);
    }

    @Override
    public int getCount() {
        return mHops.size();
    }

    @Override
    public Object getItem(int i) {
        return mHops.get(i);
    }

    @Override
    public long getItemId(int i) { // Not used here
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_hops, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Hops hops = mHops.get(i);
        holder.mAmount.setText(hops.amountToString());
        holder.mName.setText(hops.getName());
        holder.mDuration.setText(String.format(mDurationFormat, hops.getBoilTime()));

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        // Sort the list in descending order.
        Collections.sort(mHops, new Comparator<Hops>() {
            @Override
            public int compare(Hops hops1, Hops hops2) {
                long time1 = hops1.getBoilTime();
                long time2 = hops2.getBoilTime();
                return (int) (time2 - time1);
            }
        });
        //Update the alarm notifications if the timer is running
        mHopTimer.resetAlarmNotifications();
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.hops_item_amount) TextView mAmount;
        @Bind(R.id.hops_item_name) TextView mName;
        @Bind(R.id.hops_item_duration) TextView mDuration;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
