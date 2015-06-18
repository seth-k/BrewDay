package seth_k.app.brewday;

import java.text.DecimalFormat;

/**
 * Class for modeling Hops.
 *
 */
public class Hops {

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
}
