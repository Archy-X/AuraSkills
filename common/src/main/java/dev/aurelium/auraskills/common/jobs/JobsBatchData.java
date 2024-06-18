package dev.aurelium.auraskills.common.jobs;

public class JobsBatchData {

    private long lastAddTime = System.currentTimeMillis();
    private double accumulatedIncome = 0.0;

    public long getLastAddTime() {
        return lastAddTime;
    }

    public void setLastAddTime(long lastAddTime) {
        this.lastAddTime = lastAddTime;
    }

    public double getAccumulatedIncome() {
        return accumulatedIncome;
    }

    public void setAccumulatedIncome(double accumulatedIncome) {
        this.accumulatedIncome = accumulatedIncome;
    }

    public void addAccumulatedIncome(double amount) {
        this.accumulatedIncome += amount;
    }
}
