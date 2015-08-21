package seth_k.app.brewday;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * EditHopsFragment.OnHopsEditListener interface
 * to handle interaction events.
 * Use the {@link EditHopsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditHopsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HOPS = "hopsToEdit";
    private static final String ARG_POSITION = "hopsListPos";
    private static final String ARG_MODE = "editMode";
    public static final int MODE_EDIT = 1;
    public static final int MODE_ADD = 2;

    private Hops mHops;
    private int mListPosition;
    private int mMode;

    private OnHopsEditListener mListener;

    @InjectView(R.id.amount_picker) NumberPicker mAmountPicker;
    @InjectView(R.id.duration_picker) NumberPicker mDurationPicker;
    @InjectView(R.id.hops_picker) Spinner mHopsPicker;
    @InjectView(R.id.cancelButton) ImageView mCancelButton;
    @InjectView(R.id.deleteButton) ImageView mDeleteButton;

    private NumberPickerInterval mAmountInterval;
    private NumberPickerInterval mDurationInteval;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param Hops list item to edit
     * @return A new instance of fragment EditHopsFragment.
     */
    public static EditHopsFragment newInstance(Hops param, int pos, int mode) {
        EditHopsFragment fragment = new EditHopsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_HOPS, param);
        args.putInt(ARG_POSITION, pos);
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }
    public static EditHopsFragment newInstance(Hops param, int pos) {
        return newInstance(param, pos, MODE_EDIT);
    }

    public EditHopsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHops = getArguments().getParcelable(ARG_HOPS);
            mListPosition = getArguments().getInt(ARG_POSITION);
            mMode = getArguments().getInt(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_edit_hops, container, false);
        ButterKnife.inject(this, layout);

        // if adding new item, remove delete and move cancel over
        if (mMode == MODE_ADD) {
            mDeleteButton.setVisibility(View.GONE);
            RelativeLayout.LayoutParams  layoutParams = (RelativeLayout.LayoutParams) mCancelButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        mAmountInterval = new NumberPickerInterval(0.0, 5.0, 0.25);
        mAmountInterval.applyToNumberPicker(mAmountPicker);
        mDurationInteval = new NumberPickerInterval(0.0, 60.0, 5.0);
        mDurationInteval.applyToNumberPicker(mDurationPicker);


        //Set list of hop varieties for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.select_hops_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHopsPicker.setAdapter(adapter);

        if (mHops != null) {
            mAmountInterval.setValue(mAmountPicker, mHops.getAmount());
            mDurationInteval.setValue(mDurationPicker, mHops.getBoilTime());
            int spinnerIndex = adapter.getPosition(mHops.getName());
            mHopsPicker.setSelection(spinnerIndex);
        }

        return layout;

    }

    @OnClick(R.id.cancelButton)
    public void cancelEdit() {
        mListener.onCancelEdit();
    }

    @OnClick(R.id.deleteButton)
    public void deleteHops() {
        mListener.onDeleteHops(mHops, mListPosition);
    }

    @OnClick(R.id.doneButton)
    public void editDone() {
        mHops.setAmount(mAmountInterval.getValue(mAmountPicker));
        mHops.setBoilTime((long) mDurationInteval.getValue(mDurationPicker));
        mHops.setName(mHopsPicker.getSelectedItem().toString());
        if(mMode == MODE_ADD) {
            mListener.onAddHops(mHops);
        } else {
            mListener.onEditHops(mHops, mListPosition);
        }
    }

    public class NumberPickerInterval {
        double start;
        double end;
        double step;

        public NumberPickerInterval(double rangeStart, double rangeEnd, double step) {
            if (Math.signum(rangeEnd - rangeStart) != Math.signum(step)) {
                throw new IllegalArgumentException("The step value must be in the same direction as start -> end");
            }
            start = rangeStart;
            end = rangeEnd;
            this.step = step;
        }

        public void applyToNumberPicker(NumberPicker numberPicker) {
            int rangeLength = ((Double)( (end - start) / step)).intValue() + 1;
            String[] pickerLabels = new String[rangeLength];
            for (Integer i = 0; i < rangeLength; i++) {
                pickerLabels[i] = Double.toString(start + step * i.doubleValue());
            }
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(rangeLength-1);
            numberPicker.setDisplayedValues(pickerLabels);
        }

        public double getValue(NumberPicker picker) {
            Integer value = picker.getValue();
            return start + step * value.doubleValue();
        }

        public void setValue(NumberPicker picker, double value) {
            Double pickerValue = (value - start) / step;
            picker.setValue(pickerValue.intValue());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnHopsEditListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHopsEditListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnHopsEditListener {

        void onAddHops(Hops hops);

        void onEditHops(Hops hops, int pos);

        void onDeleteHops(Hops hops, int pos);

        void onCancelEdit();
    }

}