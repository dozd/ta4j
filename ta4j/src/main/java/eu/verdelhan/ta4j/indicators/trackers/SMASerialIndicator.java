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
package eu.verdelhan.ta4j.indicators.trackers;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

import java.util.LinkedList;
import java.util.List;

/**
 * Simple moving average (SMA) indicator.
 * <p/>
 */
public class SMASerialIndicator extends CachedIndicator<Decimal> {

    private final Indicator<Decimal> indicator;

    private final int timeFrame;
    private int lastIndex = Integer.MAX_VALUE;
    private Decimal lastSum;
    private LinkedList<Decimal> sums = new LinkedList<Decimal>();

    public SMASerialIndicator(Indicator<Decimal> indicator, int timeFrame) {
        super(indicator);
        this.indicator = indicator;
        this.timeFrame = timeFrame;
    }

    @Override
    protected Decimal calculate(int index) {
        Decimal sum;
        if ((index - 1) == lastIndex) {
            Decimal v = indicator.getValue(index);
            sum = lastSum.plus(v);
            if (sums.size() >= timeFrame) {
                sum.min(sums.getFirst());
                sums.removeFirst();
            }
        } else {
            sum = Decimal.ZERO;
            for (int i = Math.max(0, index - timeFrame + 1); i <= index; i++) {
                sum = sum.plus(indicator.getValue(i));
            }
        }
        lastSum = sum;
        lastIndex = index;
        sums.add(indicator.getValue(index));

        final int realTimeFrame = Math.min(timeFrame, index + 1);
        return sum.dividedBy(Decimal.valueOf(realTimeFrame));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " timeFrame: " + timeFrame;
    }

}
