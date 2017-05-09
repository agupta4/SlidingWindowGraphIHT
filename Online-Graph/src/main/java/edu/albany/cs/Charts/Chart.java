package edu.albany.cs.Charts;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.util.ArrayList;

import com.objectplanet.chart.ext.PlotterChart;

import edu.albany.cs.base.PreRec; 
 
public class Chart { 
	private	ArrayList<PreRec> PR; 
	private int Timestamp;
	private ArrayList<Double> funcValue;
    public Chart(ArrayList<PreRec> pR, int timestamp, ArrayList<Double> funcValue) {
		super();
		PR = pR;
		Timestamp = timestamp;
		this.funcValue = funcValue;
	}
    
	public void PlotChart(){ 
        Color[] seriesColors = new Color[] {new Color(0x01b501)}; 
        String[] legendLabels = new String[] {"function_Value"}; 
        int[] length = new int[] {this.Timestamp}; 
        PlotterChart chart = new PlotterChart(); 
        chart.setTitleOn(true); 
        chart.setTitle("Basic Plotter Chart"); 
        chart.setFont("titleFont", new Font("Courier", Font.PLAIN, 16)); 
        System.out.println(this.funcValue);
        for (int serie = 0; serie < 1; serie++) { 
            double[][] plots = new double[length[serie]][2]; 
            for (int i = 0; i < plots.length; i++) { 
                plots[i][0] = i;  
                plots[i][1] = this.funcValue.get(i); 
            } 
            chart.setPlots(serie, plots); 
        } 

        chart.setPlotSize(0, 1); 
        chart.setPlotSize(0, 1); 
        chart.setConnectedLinesOn(0, true); 
        chart.setSeriesColors(seriesColors); 
        chart.setLegendOn(true); 
        chart.setLegendLabels(legendLabels); 
        chart.setXValueLinesOn(false); 
        chart.setXValueLabelsOn(true); 
        chart.setYValueLabelsOn(true); 
        chart.setValueLabelStyle(PlotterChart.FLOATING); 
        chart.setFloatingOnLegendOn(false); 
        chart.setBackground(Color.white); 
        
        com.objectplanet.chart.NonFlickerPanel p = new com.objectplanet.chart.NonFlickerPanel(new BorderLayout()); 
        p.add("Center", chart); 
        Frame f = new Frame(); 
        f.add("Center", p); 
        f.setSize(450,400); 
        f.show(); 
    } 
} 