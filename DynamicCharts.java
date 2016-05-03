/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoring.suhu;

/**
 *
 * @author je
 */

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public class DynamicCharts extends ApplicationFrame {
    private final TimeSeries series;
    private double lastValue = 100.0;
    JFreeChart chart;

    public DynamicCharts(final String title) {
        super(title);
        this.series = new TimeSeries("Temperature", Millisecond.class);
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        final JPanel content = new JPanel(new BorderLayout());
        content.add(chartPanel);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 500));
        setContentPane(content);
    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Temperature Graph",
                "Time",
                "Celcius",
                dataset,
                true,
                true,
                false);
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 50.0);
        return result;
    }

    public void setData(double x) {
        this.lastValue = x;
        this.series.add(new Millisecond(), this.lastValue);
    }
}
