/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014-2015 Marc de Verdelhan & respective authors (see AUTHORS)
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
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
import eu.verdelhan.ta4j.indicators.RollingSum;
import org.slf4j.LoggerFactory;

/**
 * Average loss indicator.
 * <p/>
 */
public class AverageLossIndicator extends CachedIndicator<Decimal> {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AverageLossIndicator.class);

    private final Indicator<Decimal> indicator;

    private final int timeFrame;

    private final RollingSum rollingSum;

    public AverageLossIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
        this.rollingSum = new RollingSum() {
            @Override
            public Decimal getValueToSum(int i) {
                if (indicator.getValue(i).isLessThan(indicator.getValue(i - 1))) {
                    return indicator.getValue(i - 1).minus(indicator.getValue(i));
                } else {
                    return Decimal.ZERO;
                }
            }
        };
    }

    @Override
    protected Decimal calculate(int index) {
        Decimal result = Decimal.ZERO;
        result = rollingSum.computeSum(Math.max(1, index - timeFrame + 1), index);
        final int realTimeFrame = Math.min(timeFrame, index + 1);
        return result.dividedBy(Decimal.valueOf(realTimeFrame));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " timeFrame: " + timeFrame;
    }
}
