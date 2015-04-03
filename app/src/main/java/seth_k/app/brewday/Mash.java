package seth_k.app.brewday;

public class Mash {
    // Thermo constants are the ratio of the specific heat of grain to the specific heat of water
    // divided by the density of water (because water is measured in volume).
    private static final double THERMO_CONSTANT_US = 0.2; // using quarts and pounds (units: qt/lb)
    private static final double THERMO_CONSTANT_SI = 0.41; //using liters and kilograms (units: l/kg)
    private double mGrainWeight = 0.0;
    private double mGrainTemp = 65d;
    private double mWaterRatio = 1.25;
    private double mStrikeTarget = 154d;

    public Mash() {
    }

    public double getGrainWeight() {
        return mGrainWeight;
    }

    public void setGrainWeight(double grainWeight) {
        mGrainWeight = grainWeight;
    }

    public double getWaterRatio() {
        return mWaterRatio;
    }

    public void setWaterRatio(double waterRatio) {
        mWaterRatio = waterRatio;
    }

    public double getGrainTemp() {
        return mGrainTemp;
    }

    public void setGrainTemp(double grainTemp) {
        mGrainTemp = grainTemp;
    }

    public double getStrikeTarget() {
        return mStrikeTarget;
    }

    public void setStrikeTarget(double strikeTarget) {
        mStrikeTarget = strikeTarget;
    }

    public double calculateStrikeVolume() {
        return mGrainWeight * mWaterRatio;
    }

    public double calculateStrikeTemp() {
        return (mStrikeTarget - mGrainTemp) * THERMO_CONSTANT_US / mWaterRatio + mStrikeTarget;
    }
}