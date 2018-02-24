package com.quantrian.easygoniometer.ui;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.quantrian.easygoniometer.R;

public class DashboardActivity extends AppCompatActivity implements ChartFragment.OnFragmentInteractionListener{

    private static final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    private ChartFragment mChartFragment;
    private DetailsFragment mDetailsFragment;
    private static final String CHART_FRAGMENT_TAG ="chart_fragment_tag";

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

        /*
        Load the Chart Fragment
         */

        ChartFragment fraggy = new ChartFragment();
        //fraggy.setArguments(bundle);
        FragmentManager fragmentManager1 = getSupportFragmentManager();

        mChartFragment = (ChartFragment) fragmentManager1
                .findFragmentByTag(CHART_FRAGMENT_TAG);

        if(mChartFragment==null) {
            fragmentManager1.beginTransaction()
                    .replace(R.id.chart_frame, fraggy, CHART_FRAGMENT_TAG)
                    .commit();
        }

        /*
        Load the Details Fragment
         */

        DetailsFragment frag2 = new DetailsFragment();
        //fraggy.setArguments(bundle);
        FragmentManager fragmentManager2 = getSupportFragmentManager();

        mDetailsFragment = (DetailsFragment) fragmentManager2
                .findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if(mDetailsFragment==null) {
            fragmentManager2.beginTransaction()
                    .replace(R.id.detail_frame, frag2, DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    /*
    For Future Vinnie
    This implements the OnFragmentInteractionListener from the ChartFragment so something that
    happens in the chart fragment comes back to the activity.  We'll use it here to send reading
    info to the details fragment.

     */

    @Override
    public void onFragmentInteraction(String message) {
        //Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
        loadDetails(message);
    }

    /*
    Passing the message is handled in a separate method to keep a separation of responsibilities.
     */

    public void loadDetails(String msg){
        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (detailsFragment!=null){
            detailsFragment.updateHeading(msg);
        }

    }
}
