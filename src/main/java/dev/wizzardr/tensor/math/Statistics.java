package dev.wizzardr.tensor.math;

import lombok.experimental.UtilityClass;
import java.util.*;

@UtilityClass
public class Statistics {

    /**
     * Calculate the minimum value in the data.
     *
     * @param data The data to calculate the minimum value from.
     * @return The minimum value of the data.
     */
    public <T extends Number & Comparable<T>> T getMin(ArrayDeque<T> data) {
        return Collections.min(data);
    }

    /**
     * Calculate the maximum value in the data.
     *
     * @param data The data to calculate the maximum value from.
     * @return The maximum value of the data.
     */
    public <T extends Number & Comparable<T>> T getMax(ArrayDeque<T> data) {
        return Collections.max(data);
    }

    /**
     * Calculate the mean (average) of the data.
     *
     * @param data The data to calculate the mean from.
     * @return The mean of the data.
     */
    public <T extends Number> double getAverage(ArrayDeque<T> data) {
        double sum = 0.0;
        int size = data.size();
        if (size == 0) return 0.0; // Handle empty data
        for (T value : data) {
            sum += value.doubleValue();
        }
        return sum / size;
    }

    /**
     * Calculate the cps of the data. CPS is calculated as 20 / average.
     *
     * @param data The data to calculate the CPS from.
     * @return The CPS of the data.
     */
    public <T extends Number> double getCps(ArrayDeque<T> data) {
        return 20.0 / getAverage(data);
    }

    /**
     * Computes the distribution of a collection of numbers.
     * The distribution is computed by counting the number of occurrences of each number in the collection.
     * The distribution is returned as an array of integers, where the index of each element represents the
     * number of occurrences of that number in the collection.
     *
     * @param data The collection of numbers to compute the distribution of.
     * @return An array of integers representing the distribution of the numbers in the collection.
     */
    public <T extends Number> int[] getDistribution(ArrayDeque<T> data) {
        int size = 10;
        int[] counter = new int[size + 1];
        for (Number i : data) {
            if (i.intValue() <= size) {
                counter[i.intValue()]++;
            }
        }
        return counter;
    }

    /**
     * Finds outliers in the data. An outlier is defined as any number greater than 3.
     *
     * @param data The data to check for outliers.
     * @return A list of integers that are outliers in the data.
     */
    public <T extends Number> List<Integer> getOutliers(ArrayDeque<T> data) {
        return data.stream()
                .map(Number::intValue)
                .filter(i -> i > 3)
                .toList();
    }

    /**
     * Calculate the variance of the data.
     *
     * @param data The data to calculate the variance from.
     * @return The variance of the data.
     */
    public <T extends Number> double getVariance(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double sumSquaredDiffs = 0.0;
        for (T value : data) {
            double diff = value.doubleValue() - mean;
            sumSquaredDiffs += diff * diff;
        }
        return sumSquaredDiffs / data.size();
    }

    /**
     * Calculate the standard deviation of the data.
     *
     * @param data The data to calculate the standard deviation from.
     * @return The standard deviation of the data.
     */
    public <T extends Number> double getStDev(ArrayDeque<T> data) {
        return Math.sqrt(getVariance(data));
    }

    /**
     * Calculate the skewness of the data.
     *
     * @param data The data to calculate the skewness from.
     * @return The skewness of the data.
     */
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

    /**
     * Calculate the kurtosis of the data.
     *
     * @param data The data to calculate the kurtosis from.
     * @return The kurtosis of the data.
     */
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

    /**
     * Calculate the coefficient of variation of the data.
     *
     * @param data The data to calculate the coefficient of variation from.
     * @return The coefficient of variation of the data.
     */
    public <T extends Number> double getVariation(ArrayDeque<T> data) {
        double mean = getAverage(data);
        double stDev = getStDev(data);
        return stDev / mean;
    }

    /**
     * Calculate the Shannon entropy of the data based on the frequency of each value.
     *
     * @param data The data to calculate the entropy from.
     * @return The Shannon entropy of the data in bits.
     */
    public <T extends Number> double getEntropy(ArrayDeque<T> data) {
        int n = data.size();
        if (n <= 1) return 0;

        double sum = 0;
        for (T value : data) {
            double p = value.doubleValue() / n;
            if (p > 0) {
                sum -= p * Math.log(p);
            }
        }
        return sum / Math.log(2);
    }

    /**
     * Calculate the Gini coefficient of the data.
     *
     * @param data The data to calculate the Gini coefficient from.
     * @return The Gini coefficient of the data.
     */
    public <T extends Number> double getGini(ArrayDeque<T> data) {
        double[] sorted = data.stream().mapToDouble(Number::doubleValue).sorted().toArray();
        double sum = Arrays.stream(sorted).sum();
        double giniSum = 0.0;
        for (int i = 0; i < sorted.length; i++) {
            giniSum += sorted[i] * (sorted.length - i);
        }
        return 1 - (2 * giniSum) / (sorted.length * sum);
    }

    /**
     * Calculate the recurrence rate of the data.
     *
     * @param data The data to calculate the recurrence rate from.
     * @param epsilon The threshold for determining recurrence.
     * @return The recurrence rate of the data.
     */
    public <T extends Number> double getRecurrenceRate(ArrayDeque<T> data, double epsilon) {
        double[] values = data.stream().mapToDouble(Number::doubleValue).toArray();
        int size = values.length;
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (Math.abs(values[i] - values[j]) <= epsilon) {
                    count++;
                }
            }
        }
        return (double) count / (size * (size - 1.0) / 2.0);
    }

    /**
     * Calculate the number of unique values in the data.
     *
     * @param data The data to calculate the number of unique values from.
     * @return The number of unique values in the data.
     */
    public <T extends Number> int getModifCount(ArrayDeque<T> data) {
        return (int) data.stream().mapToDouble(Number::doubleValue).distinct().count();
    }

    /**
     * Calculate the BDS statistic of the data.
     *
     * @param data The data to calculate the BDS statistic from.
     * @return The BDS statistic of the data.
     */
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

    /**
     * Calculate the mean absolute difference between consecutive elements in the data.
     *
     * @param data The data to calculate the mean absolute difference from.
     * @return The mean absolute difference of the data.
     */
    public <T extends Number> double getMAD(ArrayDeque<T> data) {
        if (data.size() <= 1) return 0.0;
        Iterator<T> iterator = data.iterator();
        double prev = iterator.next().doubleValue();
        double sum = 0.0;
        int count = 0;
        while (iterator.hasNext()) {
            double curr = iterator.next().doubleValue();
            sum += Math.abs(curr - prev);
            prev = curr;
            count++;
        }
        return sum / count;
    }

    /**
     * Calculate the oscillation of the data, which is defined as the difference between the maximum and minimum values.
     *
     * @param data The data to calculate the oscillation from.
     * @return The oscillation of the data.
     */
    public <T extends Number & Comparable<T>> double getOscillation(ArrayDeque<T> data) {
        T min = getMin(data);
        T max = getMax(data);
        return max.doubleValue() - min.doubleValue();
    }

    /**
     * Count the number of elements in the data that have an integer value of 0.
     *
     * @param data The data to count double clicks from.
     * @return The count of such elements.
     */
    public <T extends Number> int getDoubleClicks(ArrayDeque<T> data) {
        return (int) data.stream().filter(n -> n.intValue() == 0).count();
    }
}