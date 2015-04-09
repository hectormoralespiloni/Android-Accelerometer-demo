package com.AccelerometerDemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.hardware.SensorManager;
import android.graphics.Color;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;
import java.text.NumberFormat;
import java.util.LinkedList;
import com.androidplot.xy.*;

public class AccelerometerDemo extends Activity implements SensorEventListener
{
    private SensorManagerSimulator sensorManager;
    
    private static final double GRAV_1G = 9.80665;
    private static final int HISTORY_SIZE = 30;
    private XYPlot accHistoryPlot = null;
    private SimpleXYSeries XAxisSeries = null;
    private SimpleXYSeries YAxisSeries = null;
    private SimpleXYSeries ZAxisSeries = null;
    private LinkedList<Number> XAxisHistory;
    private LinkedList<Number> YAxisHistory;
    private LinkedList<Number> ZAxisHistory;
    
    private CheckBox cbXAxis;
    private CheckBox cbYAxis;
    private CheckBox cbZAxis;

    
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	XAxisHistory = new LinkedList<Number>();
    	YAxisHistory = new LinkedList<Number>();
    	ZAxisHistory = new LinkedList<Number>();    	
    	XAxisSeries = new SimpleXYSeries("X-Axis");
    	YAxisSeries = new SimpleXYSeries("Y-Axis");
    	ZAxisSeries = new SimpleXYSeries("Z-Axis");
        
    	//initialize plotter stuff
        accHistoryPlot = (XYPlot)findViewById(R.id.accHistoryPlot);
        accHistoryPlot.setRangeBoundaries(-2, 2, XYPlot.BoundaryMode.FIXED);
        accHistoryPlot.setDomainBoundaries(0, 30, XYPlot.BoundaryMode.FIXED);
        accHistoryPlot.addSeries(XAxisSeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK));
        accHistoryPlot.addSeries(YAxisSeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 200, 100), Color.BLACK));
        accHistoryPlot.addSeries(ZAxisSeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK));
        accHistoryPlot.setDomainStepValue(5);
        accHistoryPlot.setTicksPerRangeLabel(3);
        accHistoryPlot.setDomainLabel("Last 30 Samples");
        accHistoryPlot.getDomainLabelWidget().pack();
        accHistoryPlot.setRangeLabel("Acceleration G's");
        accHistoryPlot.getRangeLabelWidget().pack();
        accHistoryPlot.disableAllMarkup();          
        
        //initialize check boxes listeners
        cbXAxis = (CheckBox)findViewById(R.id.cbXAxis);        
        cbXAxis.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					accHistoryPlot.addSeries(XAxisSeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 100, 200), Color.BLACK));
				}
				else {
					accHistoryPlot.removeSeries(XAxisSeries);
				}
			}        	
        });
        cbYAxis = (CheckBox)findViewById(R.id.cbYAxis);        
        cbYAxis.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					accHistoryPlot.addSeries(YAxisSeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(100, 200, 100), Color.BLACK));
				}
				else {
					accHistoryPlot.removeSeries(YAxisSeries);
				}
			}        	
        });  
        cbZAxis = (CheckBox)findViewById(R.id.cbZAxis);        
        cbZAxis.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					accHistoryPlot.addSeries(ZAxisSeries, LineAndPointRenderer.class, new LineAndPointFormatter(Color.rgb(200, 100, 100), Color.BLACK));
				}
				else {
					accHistoryPlot.removeSeries(ZAxisSeries);
				}
			}        	
        });        
        
        //mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); 
        sensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
        sensorManager.connectSimulator();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }
    
    @Override 
    protected void onPause() {
    	super.onPause();
    	sensorManager.unregisterListener(this);
    }
           
    public synchronized void onSensorChanged(SensorEvent sensorEvent) 
    {  
        //get rid the oldest sample in history:
        if (XAxisHistory.size() > HISTORY_SIZE) {
            XAxisHistory.removeFirst();
            YAxisHistory.removeFirst();
            ZAxisHistory.removeFirst();               
        }
         
        Double x = sensorEvent.values[0]/GRAV_1G;
        Double y = sensorEvent.values[1]/GRAV_1G;
        Double z = sensorEvent.values[2]/GRAV_1G;
        
        //add the latest history sample               
        XAxisHistory.addLast(x);
        YAxisHistory.addLast(y);
        ZAxisHistory.addLast(z);
        
        //print current G values
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(0);
        
        TextView textXAxis = (TextView)findViewById(R.id.textXAxis);
        TextView textYAxis = (TextView)findViewById(R.id.textYAxis);
        TextView textZAxis = (TextView)findViewById(R.id.textZAxis);        
        textXAxis.setText(" " + nf.format(x).toString());
        textYAxis.setText(" " + nf.format(y).toString());
        textZAxis.setText(" " + nf.format(z).toString());
 
        //update the plot with the updated history Lists:
        XAxisSeries.setModel(XAxisHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        YAxisSeries.setModel(YAxisHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        ZAxisSeries.setModel(ZAxisHistory, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);      
 
        //redraw the Plots:
        accHistoryPlot.redraw();
    } 
    
    public void onAccuracyChanged(Sensor sensor, int i) {        
    } 
}
