package eu.verdelhan.ta4j.indicators;

import eu.verdelhan.ta4j.Decimal;

/**
 * Created by Mišo on 4. 4. 2016.
 */
public abstract class RollingSum {

    private Decimal rollingSum = null;
    private Integer sumFromIncl = null;
    private Integer sumToExcl = null;

    public Decimal computeSum(int from, int to) {
        if (rollingSum == null) {
            sumFromIncl = from;
            sumToExcl = from;
            rollingSum = Decimal.ZERO;
        }
        for (int i = sumFromIncl; i < from; i++) {
            rollingSum = rollingSum.minus(getValueToSum(i));
            sumFromIncl = i + 1;
        }
        for (int i = sumToExcl; i <= to; i++) {
            rollingSum = rollingSum.plus(getValueToSum(i));
            sumToExcl = i + 1;
        }
        return rollingSum;
    }

    public abstract Decimal getValueToSum(int i);
}
