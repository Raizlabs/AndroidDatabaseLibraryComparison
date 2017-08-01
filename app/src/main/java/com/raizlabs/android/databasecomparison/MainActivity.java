package com.raizlabs.android.databasecomparison;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.raizlabs.android.databasecomparison.activeandroid.AATester;
import com.raizlabs.android.databasecomparison.aorm.AormTester;
import com.raizlabs.android.databasecomparison.dbflow.DBFlowTester;
import com.raizlabs.android.databasecomparison.events.LogTestDataEvent;
import com.raizlabs.android.databasecomparison.events.TrialCompletedEvent;
import com.raizlabs.android.databasecomparison.greendao.GreenDaoTester;
import com.raizlabs.android.databasecomparison.ollie.OllieTester;
import com.raizlabs.android.databasecomparison.ormlite.OrmLiteTester;
import com.raizlabs.android.databasecomparison.realm.RealmTester;
import com.raizlabs.android.databasecomparison.sprinkles.SprinklesTester;
import com.raizlabs.android.databasecomparison.sugar.SugarTester;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity {
    public static final String LOAD_TIME = "Load";
    public static final String SAVE_TIME = "Save";

    public static final int LOOP_COUNT = 25000;

    public static final int ADDRESS_BOOK_COUNT = 50;

    private static final String STATE_MAPDATA = "mapData";
    private static final String STATE_RUNNING_TESTS = "runningTests";
    private static final String STATE_TEST_NAME = "testName";

    private Button simpleTrialButton;
    private Button complexTrialButton;
    private TextView resultsLabel;
    private ScrollView resultsContainer;
    private TextView resultsTextView;
    private static StringBuilder resultsStringBuilder = new StringBuilder();
    private static ProgressBar progressBar;

    private BarChart chartView;
    private LinkedHashMap<String, ArrayList<BarEntry>> chartEntrySets = new LinkedHashMap<>();
    private boolean runningTests = false;
    private String runningTestName;
    private Thread runTestThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleTrialButton = (Button) findViewById(R.id.simple);
        complexTrialButton = (Button) findViewById(R.id.complex);
        resultsLabel = (TextView) findViewById(R.id.resultsLabel);
        resultsContainer = (ScrollView) findViewById(R.id.resultsContainer);
        resultsTextView = (TextView) findViewById(R.id.results);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setIndeterminate(true);
        chartView = (BarChart) findViewById(R.id.chart);

        if (savedInstanceState != null) {
            runningTests = savedInstanceState.getBoolean(STATE_RUNNING_TESTS);
            runningTestName = savedInstanceState.getString(STATE_TEST_NAME);
            chartEntrySets = (LinkedHashMap<String, ArrayList<BarEntry>>) savedInstanceState.getSerializable(STATE_MAPDATA);

            setBusyUI(runningTests, runningTestName);
            if (!runningTests && (chartEntrySets.size() > 0)) {
                // graph existing data
                initChart();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_RUNNING_TESTS, runningTests);
        savedInstanceState.putString(STATE_TEST_NAME, runningTestName);
        savedInstanceState.putSerializable(STATE_MAPDATA, chartEntrySets);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // handle data collection event
    public void onEvent(LogTestDataEvent event) {
        logTime(event.getStartTime(), event.getFramework(), event.getEventName());
    }

    // handle graphing event
    public void onEventMainThread(TrialCompletedEvent event){
        initChart();
        runningTests = false;
        setBusyUI(false, event.getTrialName());
    }

    /**
     * Logs msec between start time and now
     * @param startTime relative to start time in msec; use -1 to set elapsed time to zero
     * @param framework framework logging event
     * @param name string to log for event
     */
    public void logTime(long startTime, String framework, String name) {
        Log.e(MainActivity.class.getSimpleName(), name + " took: " + (System.currentTimeMillis() - startTime));
        long elapsedMsec = (startTime == -1) ? 0 : System.currentTimeMillis() - startTime;
        resultsStringBuilder.append(framework).append(' ').append(name)
                .append(" took: ")
                .append(elapsedMsec)
                .append(" msec\n");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultsTextView.setText(resultsStringBuilder.toString());
            }
        });
        // update chart data
        addChartData(framework, name, elapsedMsec);
    }

    private void setBusyUI(boolean enabled, String testName) {
        runningTestName = testName;
        if (enabled) {
            runningTests = true;
            resultsStringBuilder.setLength(0);
            resultsContainer.setVisibility(View.VISIBLE);
            chartView.setVisibility(View.GONE);
            enableButtons(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            runningTests = false;
            resultsContainer.setVisibility(View.GONE);
            if (runningTestName != null) {
                chartView.setVisibility(View.VISIBLE);
            }
            enableButtons(true);
            progressBar.setVisibility(View.GONE);
        }
        if (runningTestName != null) {
            resultsLabel.setText(getResources().getString(R.string.results, testName));
            resultsLabel.setVisibility(View.VISIBLE);
        }
    }

    private void initChart() {
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        // note that we show save first because that's how we initialize the DB
        for (String frameworkName : chartEntrySets.keySet()) {
            ArrayList<BarEntry> entrySet = chartEntrySets.get(frameworkName);
            BarDataSet dataSet = new BarDataSet(entrySet, frameworkName);
            dataSet.setColor(getFrameworkColor(frameworkName));
            dataSets.add(dataSet);
        }
        // load data and animate it
        ArrayList<String> xAxisLabels = new ArrayList<>();
        xAxisLabels.add("Save (msec)");
        xAxisLabels.add("Load (msec)");
        BarData data = new BarData(xAxisLabels, dataSets);
        chartView.setData(data);
        chartView.setDescription(null); // this takes up too much space, so clear it
        chartView.animateXY(2000, 2000);
        chartView.invalidate();
    }
    private void resetChart() {
        chartEntrySets.clear();
        // the order you add these in is the order they're displayed in
        chartEntrySets.put(DBFlowTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(GreenDaoTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(OrmLiteTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(OllieTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(RealmTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        chartEntrySets.put(AormTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        //chartEntrySets.put(SugarTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        //chartEntrySets.put(AATester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
        //chartEntrySets.put(SprinklesTester.FRAMEWORK_NAME, new ArrayList<BarEntry>());
    }
    private int getFrameworkColor(String framework) {
        // using the 300 line colors from http://www.google.com/design/spec/style/color.html#color-color-palette
        switch (framework) {
            case DBFlowTester.FRAMEWORK_NAME:
                return Color.rgb(0xE5,0x73,0x73); // red
            case AATester.FRAMEWORK_NAME:
                return Color.rgb(0xF0, 0x62, 0x92); // pink
            case OllieTester.FRAMEWORK_NAME:
                return Color.rgb(0xFF, 0xA5, 0x00); // orange
            case GreenDaoTester.FRAMEWORK_NAME:
                return Color.rgb(0xBA, 0x68, 0xC8); // purple
            case OrmLiteTester.FRAMEWORK_NAME:
                return Color.rgb(0x4D, 0xB6, 0xAC); // teal
            case SprinklesTester.FRAMEWORK_NAME:
                return Color.rgb(0x79, 0x86, 0xCB); // indigo
            case SugarTester.FRAMEWORK_NAME:
                return Color.rgb(0x64, 0xB5, 0XF6); // blue
            case RealmTester.FRAMEWORK_NAME:
                return Color.rgb(0xAE, 0xD5, 0X81); // light green
            case AormTester.FRAMEWORK_NAME:
                return Color.rgb(0xCC, 0xCC, 0xCC); //gray
            default:
                return Color.WHITE;
        }
    }
    private void addChartData(String framework, String category, long value) {
        BarEntry entry = new BarEntry(value, category.equals(SAVE_TIME) ? 0 : 1);
        chartEntrySets.get(framework).add(entry);
    }

    private void enableButtons(boolean enabled) {
        simpleTrialButton.setEnabled(enabled);
        complexTrialButton.setEnabled(enabled);
    }

    /**
     * runs simple benchmarks (onClick from R.id.simple)
     * @param v button view
     */
    public void runSimpleTrial(View v) {
        setBusyUI(true, getResources().getString(R.string.simple));
        resetChart();
        runTestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runningTests = true;
                Context applicationContext = MainActivity.this.getApplicationContext();
                OrmLiteTester.testAddressItems(applicationContext);
                GreenDaoTester.testAddressItems(applicationContext);
                DBFlowTester.testAddressItems(applicationContext);
                OllieTester.testAddressItems(applicationContext);
                RealmTester.testAddressItems(applicationContext);
                AormTester.testAddressItems(applicationContext);
                //SprinklesTester.testAddressItems(applicationContext);
                //AATester.testAddressItems(applicationContext);
                //SugarTester.testAddressItems(applicationContext);
                EventBus.getDefault().post(new TrialCompletedEvent(getResources().getString(R.string.simple)));
            }
        });
        runTestThread.start();
    }

    /**
     * runs complex benchmarks (onClick from R.id.complex)
     * @param v button view
     */
    public void runComplexTrial(View v) {
        setBusyUI(true, getResources().getString(R.string.complex));
        resetChart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runningTests = true;
                Context applicationContext = MainActivity.this.getApplicationContext();
                OrmLiteTester.testAddressBooks(applicationContext);
                GreenDaoTester.testAddressBooks(applicationContext);
                DBFlowTester.testAddressBooks(applicationContext);
                OllieTester.testAddressBooks(applicationContext);
                RealmTester.testAddressBooks(applicationContext);
                AormTester.testAddressBooks(applicationContext);
                //SprinklesTester.testAddressBooks(applicationContext);
                //AATester.testAddressBooks(applicationContext);
                //SugarTester.testAddressBooks(applicationContext);
                EventBus.getDefault().post(new TrialCompletedEvent(getResources().getString(R.string.complex)));
            }
        }).start();
    }
}
