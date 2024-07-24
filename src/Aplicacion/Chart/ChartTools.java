package Aplicacion.Chart;

import java.io.File;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class ChartTools extends ApplicationFrame{

    static String titleChart;
    public ChartTools(String titleChart){ 
        super(titleChart);
        //setContentPane(createChartPanel());
        this.titleChart = titleChart;
    }

    // Sobre carga de constructor para imprimir el chart 3D
    public ChartTools(String titleChart,String titleChart3D){ 
        super(titleChart3D);
        //setContentPane(createDemoPanel3D());
        this.titleChart = titleChart;
    }

    public static PieDataset createDataSet(DefaultPieDataset dataset) {
        createChartPanel(dataset);
        return dataset;
    }

    private static JFreeChart createChart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(
            titleChart, // title chart
            dataset, // data
            true, // include legend
            true, // tooltips
            false // URLs
        );
        return chart;
    }
    public static JPanel createChartPanel(PieDataset dataSet) {
        JFreeChart chart = createChart(dataSet);
        return new ChartPanel(chart);
    }

    public void PrintChart(String windowTitle){
        ChartTools demo = new ChartTools(titleChart);
        demo.setSize(400,300);
        UIUtils.centerFrameOnScreen(demo); // ? Anteriormente RefineryUtils.centerFrameOnScreen
        demo.setVisible(true);
    }

    // * ------------------- PIE CHART 3D ----------------------------------------
    private static JFreeChart createChart3D(PieDataset dataset) {
        try{
            JFreeChart chart = ChartFactory.createPieChart3D(
                titleChart, // title chart
                dataset, // data
                true, // include legend
                true, // tooltips
                false // URLs
            );
            final PiePlot3D plot = (PiePlot3D) chart.getPlot(); 
            plot.setStartAngle(270);
            plot.setForegroundAlpha(0.60f);
            plot.setInteriorGap(0.02);
            int width = 640;   /* Width of the image */             
            int height = 480;  /* Height of the image */                             
            File pieChart3D = new File( "pie_Chart3D.jpeg" );                           
            ChartUtils.saveChartAsJPEG( pieChart3D , chart , width , height );   
            return chart;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private static JPanel createDemoPanel3D(PieDataset dataSet) {
        JFreeChart chart = createChart3D(dataSet);
        return new ChartPanel(chart);
    }

    public static void PrintChart3D(String windowTitle){
        ChartTools demo = new ChartTools(titleChart,titleChart);
        demo.setSize(400,300);
        UIUtils.centerFrameOnScreen(demo); // ? Anteriormente RefineryUtils.centerFrameOnScreen
        demo.setVisible(true);
    }
    

    /* otro acercamiento a realizar graficos de pie  */
    
}
