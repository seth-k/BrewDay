package seth_k.app.brewday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Seth on 6/17/2015.
 */
public class HopsListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Hops> mHops;

    public HopsListAdapter(Context context, List<Hops> hops) {
        mContext = context;
        mHops = hops;
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
        holder.mDuration.setText(hops.getBoilTime() + " min");

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.hops_item_amount) TextView mAmount;
        @InjectView(R.id.hops_item_name) TextView mName;
        @InjectView(R.id.hops_item_duration) TextView mDuration;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
