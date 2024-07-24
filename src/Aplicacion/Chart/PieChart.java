package Aplicacion.Chart;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.PieDataset;

public class PieChart extends JFrame{

    public PieChart(String applicationTitle,String chartTitle, PieDataset ChartDataset) {
        super(applicationTitle);
        PieDataset dataset = ChartDataset;
        JFreeChart chart = createChart(dataset,chartTitle);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);
    }

    private JFreeChart createChart(PieDataset dataset, String titleChart) {
        JFreeChart chart = ChartFactory.createPieChart3D(
            titleChart, // title chart
            dataset, // data
            true, // include legend
            true, // tooltips
            false // URLs
        );
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(270);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        return chart;
    }
    
}
