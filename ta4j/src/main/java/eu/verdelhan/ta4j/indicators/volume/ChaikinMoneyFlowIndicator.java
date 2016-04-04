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
package eu.verdelhan.ta4j.indicators.volume;


import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;
import eu.verdelhan.ta4j.indicators.RollingSum;
import eu.verdelhan.ta4j.indicators.helpers.CloseLocationValueIndicator;
import eu.verdelhan.ta4j.indicators.simple.VolumeIndicator;

/**
 * Chaikin Money Flow (CMF) indicator.
 * <p>
 * @see http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:chaikin_money_flow_cmf
 * @see http://www.fmlabs.com/reference/default.htm?url=ChaikinMoneyFlow.htm
 */
public class ChaikinMoneyFlowIndicator extends CachedIndicator<Decimal> {

    private TimeSeries series;
    
    private CloseLocationValueIndicator clvIndicator;
    
    private VolumeIndicator volumeIndicator;
    
    private int timeFrame;

    private final RollingSum rollingSum;

    public ChaikinMoneyFlowIndicator(TimeSeries series, int timeFrame) {
        super(series);
        this.series = series;
        this.timeFrame = timeFrame;
        this.clvIndicator = new CloseLocationValueIndicator(series);
        this.volumeIndicator = new VolumeIndicator(series, timeFrame);
        this.rollingSum = new RollingSum() {
            @Override
            public Decimal getValueToSum(int i) {
                return getMoneyFlowVolume(i);
            }
        };
    }

    @Override
    protected Decimal calculate(int index) {
        int startIndex = Math.max(0, index - timeFrame + 1);
        Decimal sumOfMoneyFlowVolume = Decimal.ZERO;
        sumOfMoneyFlowVolume = rollingSum.computeSum(startIndex, index);
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
