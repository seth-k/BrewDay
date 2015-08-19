package seth_k.app.brewday;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;

/**
 * Class for modeling Hops.
 *
 */
public class Hops implements Parcelable {

    public static final DecimalFormat amountFormat = new DecimalFormat("#.00");
    public String mName;
    double mAmount = 0.0;
    long mBoilTime = 0;

    public Hops(String name) {
        mName = name;
    }

    public Hops(String name, double amount, int duration) {
        mName = name;
        mAmount = amount;
        mBoilTime = duration;
    }

    private Hops(Parcel in) {
        mName = in.readString();
        mAmount = in.readDouble();
        mBoilTime = in.readLong();
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public long getBoilTime() {
        return mBoilTime;
    }

    public void setBoilTime(long boilTime) {
        mBoilTime = boilTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return amountToString() + mName + " hops (" + mBoilTime +" min)";
    }

    public String amountToString() {
        return amountFormat.format(mAmount) + "oz ";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeDouble(mAmount);
        parcel.writeLong(mBoilTime);
    }

    public static final Parcelable.Creator<Hops> CREATOR = new Parcelable.Creator<Hops>() {

        @Override
        public Hops createFromParcel(Parcel parcel) {
            return new Hops(parcel);
        }

        @Override
        public Hops[] newArray(int i) {
            return new Hops[i];
        }
    };
}
