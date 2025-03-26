package dev.wizzardr.Tensor.math;

import lombok.experimental.UtilityClass;
import java.util.ArrayDeque;
import java.util.HashSet;

@UtilityClass
public class Statistics {

    // Calculate the mean (average) of the data
    public <T extends Number> double getAverage(ArrayDeque<T> data) {
        double sum = 0.0;
        int size = data.size();
        for (T value : data) {
            sum += value.doubleValue();
        }
        return sum / size;
    }

    // Calculate the cps of the data
    public <T extends Number> double getCps(ArrayDeque<T> data) {
        return 20.0 / getAverage(data);
    }

    // Calculate the variance of the data
    public <T extends Number> double getVariance(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double sumSquaredDiffs = 0.0;
        for (T value : data) {
            double diff = value.doubleValue() - mean;
            sumSquaredDiffs += diff * diff;
        }
        return sumSquaredDiffs / data.size();
    }

    // Calculate the standard deviation of the data
    public <T extends Number> double getStDev(ArrayDeque<T> data) {
        return Math.sqrt(getVariance(data));
    }

    // Calculate the skewness of the data
    public <T extends Number> double getSkewness(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double stDev = getStDev(data);
        double sumCubedDiffs = 0.0;
        for (T value : data) {
            double diff = (value.doubleValue() - mean) / stDev;
            sumCubedDiffs += diff * diff * diff;
        }
        return sumCubedDiffs / data.size();
    }

    // Calculate the kurtosis of the data
    public <T extends Number> double getKurtosis(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double stDev = getStDev(data);
        double sumFourthDiffs = 0.0;
        for (T value : data) {
            double diff = (value.doubleValue() - mean) / stDev;
            sumFourthDiffs += diff * diff * diff * diff;
        }
        return (sumFourthDiffs / data.size()) - 3; // Excess kurtosis
    }

    // Calculate the coefficient of variation of the data
    public <T extends Number> double getVariation(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double stDev = getStDev(data);
        return stDev / mean;
    }

    // Calculate the entropy of the data
    public <T extends Number> double getEntropy(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double stDev = getStDev(data);
        double entropy = 0.0;
        for (T value : data) {
            double p = (value.doubleValue() - mean) / stDev;
            if (p > 0) {
                entropy -= p * Math.log(p);
            }
        }
        return entropy;
    }

    // Calculate the Gini coefficient of the data
    public <T extends Number> double getGini(ArrayDeque<T> data) {
        double[] sorted = new double[data.size()];
        int index = 0;
        double sum = 0.0;
        for (T value : data) {
            sorted[index++] = value.doubleValue();
            sum += sorted[index - 1];
        }
        java.util.Arrays.sort(sorted);
        double giniSum = 0.0;
        for (int i = 0; i < sorted.length; i++) {
            giniSum += sorted[i] * (sorted.length - i);
        }
        return 1 - (2 * giniSum) / (sorted.length * sum);
    }

    // Calculate the recurrence rate of the data
    public <T extends Number> double getRecurrenceRate(ArrayDeque<T> data) {
        double mean = getAverage(data);
        int count = 0;
        for (T value : data) {
            if (value.doubleValue() == mean) {
                count++;
            }
        }
        return (double) count / data.size();
    }

    // Calculate the number of unique values in the data
    public <T extends Number> int getModifCount(ArrayDeque<T> data) {
        HashSet<Double> uniqueValues = new java.util.HashSet<>();
        for (T value : data) {
            uniqueValues.add(value.doubleValue());
        }
        return uniqueValues.size();
    }

    // Calculate the BDS statistic of the data
    public <T extends Number> double getBDS(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double stDev = getStDev(data);
        double bdsSum = 0.0;
        for (T value : data) {
            double diff = value.doubleValue() - mean;
            bdsSum += (diff * diff) / stDev;
        }
        return bdsSum;
    }
}