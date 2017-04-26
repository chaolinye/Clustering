package org.freedom.display;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;


/**
 * Created by chaolin on 2017/4/24.
 */
public class ScatterFrame extends JFrame {
    public ScatterFrame(String s , double[][][] data){
        super(s);
        JPanel jpanel = createDemoPanel(data);
        jpanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(new JScrollPane(jpanel));
    }

    private static JFreeChart createChart(XYDataset xydataset)
    {
        JFreeChart jfreechart = ChartFactory.createScatterPlot("Scatter Plot Demo 2", "X", "Y", xydataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        XYDotRenderer xydotrenderer = new XYDotRenderer();
        xydotrenderer.setDotWidth(2);
        xydotrenderer.setDotHeight(2);
        xyplot.setRenderer(xydotrenderer);
        NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        return jfreechart;
    }

    public static JPanel createDemoPanel(double[][][] data)
    {
        JFreeChart jfreechart = createChart(new SampleXYDataset2(data));
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setVerticalAxisTrace(true);
        chartpanel.setHorizontalAxisTrace(true);
        chartpanel.setPopupMenu(null);
        chartpanel.setDomainZoomable(true);
        chartpanel.setRangeZoomable(true);
        return chartpanel;
    }

    public static void main(String args[])
    {

    }
}
