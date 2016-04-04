/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Marc de Verdelhan & respective authors (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.verdelhan.ta4j.indicators.helpers;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

/**
 * Average gain indicator.
 * <p>
 */
public class AverageGainIndicator extends CachedIndicator<Decimal> {

    private final Indicator<Decimal> indicator;

    private final int timeFrame;

    public AverageGainIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
    }

    private Decimal rollingSum = null;
    private Integer sumFromIncl = null;
    private Integer sumToExcl = null;

    @Override
    protected Decimal calculate(int index) {
        Decimal result = Decimal.ZERO;
        int from = Math.max(1, index - timeFrame + 1);
        int to = index;
        if (rollingSum == null) {
            sumFromIncl = from;
            sumToExcl = from;
            rollingSum = Decimal.ZERO;
        }
        for (int i = sumFromIncl; i < from; i++) {
            rollingSum = rollingSum.minus(getIndicatorValue(i));
            sumFromIncl = i + 1;
            }
        for (int i = sumToExcl; i <= to; i++) {
            rollingSum = rollingSum.plus(getIndicatorValue(i));
            sumToExcl = i + 1;
        }
        final int realTimeFrame = Math.min(timeFrame, index + 1);
        return rollingSum.dividedBy(Decimal.valueOf(realTimeFrame));
    }

    private Decimal getIndicatorValue(int i) {
        final Decimal value;
        if (indicator.getValue(i - 1).isLessThan(indicator.getValue(i))) {
            value = indicator.getValue(i).minus(indicator.getValue(i - 1));
        } else {
            value = Decimal.ZERO;
        }
        return value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " timeFrame: " + timeFrame;
    }

}
