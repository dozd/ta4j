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
package eu.verdelhan.ta4j.analysis.criteria;

import eu.verdelhan.ta4j.AnalysisCriterion;
import eu.verdelhan.ta4j.Order;
import eu.verdelhan.ta4j.TATestsUtils;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.Trade;
import eu.verdelhan.ta4j.TradingRecord;
import eu.verdelhan.ta4j.mocks.MockTimeSeries;
import static org.junit.Assert.*;
import org.junit.Test;
public class AverageProfitableTradesCriterionTest {

    @Test
    public void calculate() {
        TimeSeries series = new MockTimeSeries(100d, 95d, 102d, 105d, 97d, 113d);
        TradingRecord tradingRecord = new TradingRecord(
                Order.buyAt(0), Order.sellAt(1),
                Order.buyAt(2), Order.sellAt(3),
                Order.buyAt(4), Order.sellAt(5));
        
        AverageProfitableTradesCriterion average = new AverageProfitableTradesCriterion();
        
        assertEquals(2d/3, average.calculate(series, tradingRecord), TATestsUtils.TA_OFFSET);
    }

    @Test
    public void calculateWithOneTrade() {
        TimeSeries series = new MockTimeSeries(100d, 95d, 102d, 105d, 97d, 113d);
        Trade trade = new Trade(Order.buyAt(0), Order.sellAt(1));
            
        AverageProfitableTradesCriterion average = new AverageProfitableTradesCriterion();
        assertEquals(0d, average.calculate(series, trade), TATestsUtils.TA_OFFSET);
        
        trade = new Trade(Order.buyAt(1), Order.sellAt(2));
        assertEquals(1d, average.calculate(series, trade), TATestsUtils.TA_OFFSET);
    }

    @Test
    public void betterThan() {
        AnalysisCriterion criterion = new AverageProfitableTradesCriterion();
        assertTrue(criterion.betterThan(12, 8));
        assertFalse(criterion.betterThan(8, 12));
    }
}
