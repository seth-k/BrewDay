package seth_k.app.brewday;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mash {
    public static final double DEFAULT_SACC_REST_TEMP = 154d;
    public static final int DEFAULT_SACC_REST_LENGTH = 60; // (min)
    // Thermo constants are the ratio of the specific heat of grain to the specific heat of water
    // divided by the density of water (because water is measured in volume).
    private static final double THERMO_CONSTANT_US = 0.2; // using quarts and pounds (units: qt/lb)
    private static final double THERMO_CONSTANT_SI = 0.41; //using liters and kilograms (units: l/kg)
    public static final double BOILING_POINT_WATER = 212.0;
    private double mGrainWeight = 0.0;
    private double mGrainTemp = 65d;
    private double mWaterRatio = 1.25;

    private List<MashStep> mSteps;
    private double mInfusionTemp = BOILING_POINT_WATER; // defaults to boiling point.
    private double mMashTunCorrection = 0.0; // adds a flat correction to the strike temp to
    // compensate for heat loss to the mashing vessel.

    public Mash() {
        mSteps = new ArrayList<>();
        mSteps.add(new MashStep(DEFAULT_SACC_REST_TEMP, DEFAULT_SACC_REST_LENGTH));
        updateMashSteps();
    }

    public double getGrainWeight() {
        return mGrainWeight;
    }

    public void setGrainWeight(double grainWeight) {
        mGrainWeight = grainWeight;
        updateMashSteps();
    }

    public double getGrainTemp() {
        return mGrainTemp;
    }

    public void setGrainTemp(double grainTemp) {
        mGrainTemp = grainTemp;
        updateMashSteps();
    }

    public double getWaterRatio() {
        return mWaterRatio; // return the field instead of getWaterRatio(0); grain weight can be zero initially
    }

    public double getWaterRatio(int step) {
        return mSteps.get(step).mTotalWaterVolume / mGrainWeight;
    }

    public void setInitialWaterRatio(double waterRatio) {
        mWaterRatio = waterRatio;
        updateMashSteps();
    }

    public double getStrikeTarget() {
        return mSteps.get(0).mTemperature;
    }

    public void setStrikeTarget(double strikeTarget) {
        mSteps.get(0).mTemperature = strikeTarget;
        updateMashSteps();
    }


    public double getInfusionTemp() {
        return mInfusionTemp;
    }

    public double getMashTunCorrection() {
        return mMashTunCorrection;
    }

    public void setMashTunCorrection(double mashTunCorrection) {
        mMashTunCorrection = mashTunCorrection;
        updateMashSteps();

    }

    public void setInfusionTemp(double infusionTemp) {
        if (infusionTemp > BOILING_POINT_WATER || infusionTemp <= mSteps.get(mSteps.size() - 1).mTemperature)
            throw new IllegalArgumentException("Infusion Water cannot be higher than boiling or lower that the top mash temp.");
        mInfusionTemp = infusionTemp;
        updateMashSteps();
    }

    public boolean addStep(double temp, long duration) {
        boolean success = mSteps.add(new MashStep(temp, duration));
        updateMashSteps();
        return success;
    }

    public double getTemperature(int step) {
        return mSteps.get(step).mTemperature;
    }

    public void setTemperature(int step, double temperature) {
        mSteps.get(step).mTemperature = temperature;
        updateMashSteps();
    }

    public long getDuration(int step) {
        return mSteps.get(step).mDuration;
    }

    public void setDuration(int step, long duration) {
        mSteps.get(step).mDuration = duration;
    }

    public double getWaterVolumeForStep(int step) {
        return mSteps.get(step).mWaterVolumeForStep;
    }

    public double getTotalWaterVolume(int step) {
        return mSteps.get(step).mTotalWaterVolume;
    }

    public double calculateStrikeVolume() {
        return mGrainWeight * mWaterRatio;
    }

    public double getStrikeVolume() {
        return mSteps.get(0).mWaterVolumeForStep;
    }

    public double getStrikeTemp() {
        return (mSteps.get(0).mTemperature - mGrainTemp) * THERMO_CONSTANT_US / mWaterRatio
                + mSteps.get(0).mTemperature + mMashTunCorrection;
    }

    public double calculateStepVolume(int step) {
        if (step < 1 || step <= mSteps.size())
            throw new ArrayIndexOutOfBoundsException();

        MashStep prev = mSteps.get(step - 1);
        MashStep next = mSteps.get(step);
        return calculateStepVolume(prev, next);
    }

    private double calculateStepVolume(MashStep prev, MashStep step) {
        return (step.mTemperature - prev.mTemperature)
                * (THERMO_CONSTANT_US * mGrainWeight + prev.mTotalWaterVolume)
                / (mInfusionTemp - step.mTemperature);
    }

    private void updateMashSteps() {
        MashStep prev = null;
        Collections.sort(mSteps);
        for (MashStep step : mSteps) {
            if (prev == null) {
                step.mWaterVolumeForStep = calculateStrikeVolume();
                step.mTotalWaterVolume = step.mWaterVolumeForStep;
            } else {
                step.mWaterVolumeForStep = calculateStepVolume(prev, step);
                step.mTotalWaterVolume = prev.mTotalWaterVolume + step.mWaterVolumeForStep;
            }
            prev = step;
        }
    }

    private class MashStep implements Comparable<MashStep> {
        double mTemperature;
        long mDuration;
        double mWaterVolumeForStep;
        double mTotalWaterVolume;

        public MashStep(double temp, long durationInMinutes) {
            mTemperature = temp;
            mDuration = durationInMinutes;
        }

        @Override
        public int compareTo(MashStep b) {
            if (mTemperature < b.mTemperature)
                return -1;
            if (mTemperature > b.mTemperature)
                return 1;
            return 0;
        }
    }


}