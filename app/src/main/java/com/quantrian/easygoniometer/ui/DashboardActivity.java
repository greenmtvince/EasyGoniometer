package com.quantrian.easygoniometer.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.quantrian.easygoniometer.R;
import com.quantrian.easygoniometer.models.Reading;
import com.quantrian.easygoniometer.utilities.DummyData;
import com.quantrian.easygoniometer.utilities.FetchReadings;
import com.quantrian.easygoniometer.utilities.TaskCompleteListener;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity implements ChartFragment.OnFragmentInteractionListener{

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    private ChartFragment mChartFragment;
    private DetailsFragment mDetailsFragment;
    private static final String CHART_FRAGMENT_TAG ="chart_fragment_tag";
    private ArrayList<Reading> mReadings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FloatingActionButton newFab = findViewById(R.id.new_fab);
        newFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GoniometerActivity.class);
                startActivity(intent);
            }
        });

        //initialize test data
        DummyData dummyData=new DummyData(this);
        //Toast.makeText(this, "DB Size is : "+ dummyData.getTableSize(),Toast.LENGTH_SHORT).show();


        new FetchReadings(this,new FetchReadingsCompleteListener()).execute();




    }

    /*
    For Future Vinnie
    This implements the OnFragmentInteractionListener from the ChartFragment so something that
    happens in the chart fragment comes back to the activity.  We'll use it here to send reading
    info to the details fragment.

     */

    @Override
    public void onFragmentInteraction(Long point) {
        //Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
        Reading reading = findReading(point);
        if(reading ==null){
            point = (long) 0;
        }

        loadDetails(reading);
    }

    /*
    Passing the message is handled in a separate method to keep a separation of responsibilities.
     */

    public void loadDetails(Reading reading){
        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (detailsFragment!=null){
            detailsFragment.updateHeading(reading);
        }
    }

    public void loadChartFragment(){
        /*
        Load the Chart Fragment
         */
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("DATA",mReadings);

        ChartFragment fraggy = new ChartFragment();
        fraggy.setArguments(bundle);
        FragmentManager fragmentManager1 = getSupportFragmentManager();

        mChartFragment = (ChartFragment) fragmentManager1
                .findFragmentByTag(CHART_FRAGMENT_TAG);

        if(mChartFragment==null) {
            fragmentManager1.beginTransaction()
                    .replace(R.id.chart_frame, fraggy, CHART_FRAGMENT_TAG)
                    .commit();
        }


    }

    public class FetchReadingsCompleteListener implements TaskCompleteListener<ArrayList<Reading>>{

        @Override
        public void onTaskComplete(ArrayList<Reading> result) {
            mReadings = result;
            //I don't want the fragments to load until the data is in memory
            loadChartFragment();
            initializeDetailsFragment();
        }
    }

    public void initializeDetailsFragment(){
        /*
        Load the Details Fragment
         */
        int rom = -999;
        try{
            Reading lastElement = mReadings.get(mReadings.size()-1);
            rom = lastElement.flexion - lastElement.extension;
        }
        catch (IndexOutOfBoundsException ex){
            Log.d("Get Elements", "onCreate: " + ex);
        }


        Bundle bundle = new Bundle();
        bundle.putInt("ROM",rom);

        DetailsFragment frag2 = new DetailsFragment();
        frag2.setArguments(bundle);
        FragmentManager fragmentManager2 = getSupportFragmentManager();

        mDetailsFragment = (DetailsFragment) fragmentManager2
                .findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if(mDetailsFragment==null) {
            fragmentManager2.beginTransaction()
                    .replace(R.id.detail_frame, frag2, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    public Reading findReading(long timeIndex){
        //I don't particularly like a search time of n on a ui operation, but don't have a compelling
        //reason to implement something faster yet.

        for (Reading r : mReadings){
            //Log.d("COMPARE", "findReading: timeindex: " + timeIndex + " reading: " + r.date);
            if (r.date==timeIndex){

                return r;
            }
        }
        return null;
    }
}
