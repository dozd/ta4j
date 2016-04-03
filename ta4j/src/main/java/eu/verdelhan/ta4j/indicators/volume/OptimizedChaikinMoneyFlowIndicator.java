/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014-2015 Marc de Verdelhan & respective authors (see AUTHORS)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package eu.verdelhan.ta4j.indicators.volume;


import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;
import eu.verdelhan.ta4j.indicators.helpers.CloseLocationValueIndicator;
import eu.verdelhan.ta4j.indicators.simple.VolumeIndicator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Chaikin Money Flow (CMF) indicator.
 * <p>
 * @see http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:chaikin_money_flow_cmf
 * @see http://www.fmlabs.com/reference/default.htm?url=ChaikinMoneyFlow.htm
 */
public class OptimizedChaikinMoneyFlowIndicator extends CachedIndicator<Decimal> {

    private TimeSeries series;

    private CloseLocationValueIndicator clvIndicator;

    private VolumeIndicator volumeIndicator;

    private final Map<Integer, Decimal> sums = new HashMap();
    private static final int CACHE_WINDOW = 100;

    private int timeFrame;

    public OptimizedChaikinMoneyFlowIndicator(TimeSeries series, int timeFrame) {
        super(series);
        this.series = series;
        this.timeFrame = timeFrame;
        this.clvIndicator = new CloseLocationValueIndicator(series);
        this.volumeIndicator = new VolumeIndicator(series, timeFrame);
    }

    @Override
    protected Decimal calculate(int index) {
        Set<Integer> toRemove = sums.keySet().stream().filter(i -> i < index - timeFrame).collect(Collectors.toSet());
        toRemove.forEach(sums::remove);
        int startIndex = Math.max(0, index - timeFrame + 1);
        Decimal sumOfMoneyFlowVolume = Decimal.ZERO;
        Decimal sum = null;
        for (int i = startIndex; i <= index; ) {
            if (sums.containsKey(i) && i + CACHE_WINDOW < index) {
                sumOfMoneyFlowVolume = sumOfMoneyFlowVolume.plus(sums.get(i));
                sum = null;
                i += CACHE_WINDOW;
            } else {
                if (i % CACHE_WINDOW == 0) {
                    if (sum != null) {
                        sums.put(i - CACHE_WINDOW, sum);
                    }
                    sum = Decimal.ZERO;
                }
                Decimal moneyFlowVolume = getMoneyFlowVolume(i);
                if (sum != null) {
                    sum = sum.plus(moneyFlowVolume);
                }
                sumOfMoneyFlowVolume = sumOfMoneyFlowVolume.plus(moneyFlowVolume);
                i++;
            }
        }
        Decimal sumOfVolume = volumeIndicator.getValue(index);

        return sumOfMoneyFlowVolume.dividedBy(sumOfVolume);
    }

    /**
     * @param index the tick index
     * @return the money flow volume for the i-th period/tick
     */
    private Decimal getMoneyFlowVolume(int index) {
        return clvIndicator.getValue(index).multipliedBy(series.getTick(index).getVolume());
    }
}
