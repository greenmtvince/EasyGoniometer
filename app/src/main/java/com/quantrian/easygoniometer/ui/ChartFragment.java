package com.quantrian.easygoniometer.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.quantrian.easygoniometer.R;
import com.quantrian.easygoniometer.models.Reading;
import com.quantrian.easygoniometer.utilities.XAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {
    public long referenceTimestamp;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DATA = "DATA";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<Reading> mReadings;
    private ArrayList<Reading> mAdjustedReadings;

    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        LineChart chart = rootView.findViewById(R.id.chart);

        Bundle bundle = this.getArguments();

        if (bundle!= null){
            mReadings = bundle.getParcelableArrayList(DATA);
        }

        mAdjustedReadings = new ArrayList<>();
        //Convert dates to a zero reference for performance described in the MPAndroid documentation
        zeroReferenceDates();

        List<Entry> entriesExt = new ArrayList<Entry>();
        List<Entry> entriesFlex = new ArrayList<Entry>();

        for (Reading data : mAdjustedReadings){
            entriesExt.add(new Entry(data.date,data.extension));
            entriesFlex.add(new Entry(data.date,data.flexion));
        }

        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet>();

        LineDataSet dataSet1 = new LineDataSet(entriesExt, getActivity().getString(R.string.extension_label));
        dataSet1.setColor(Color.CYAN);
        dataSet1.setValueTextColor(Color.BLACK);
        lines.add(dataSet1);

        LineDataSet dataSet2 = new LineDataSet(entriesFlex, getActivity().getString(R.string.flexion_label));
        dataSet2.setColor(Color.RED);
        dataSet2.setValueTextColor(Color.BLACK);
        lines.add(dataSet2);

        IAxisValueFormatter xAxisValueFormatter = new XAxisValueFormatter(referenceTimestamp);

        XAxis xAxis1 = chart.getXAxis();
        xAxis1.setValueFormatter(xAxisValueFormatter);

        chart.setData(new LineData(lines));
        chart.setScaleYEnabled(false);
        chart.invalidate();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                //These intermediate values are explicitly calculated int his order because the
                //longValue() method has some weird rounding results.
                Float f = e.getX();
                Long l = f.longValue();
                Long output = l+referenceTimestamp;

                Log.d("OUTTIE", "onValueSelected: "+ f + " as long: "+l+" + " + referenceTimestamp + " The Value is " + output);
                mListener.onFragmentInteraction(output);
            }

            @Override
            public void onNothingSelected() {
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Long point);
    }


    private void zeroReferenceDates(){
        referenceTimestamp = mReadings.get(0).date;

        for (Reading r : mReadings){

            Reading tempReading = new Reading(r.flexion,r.extension,r.date);
            tempReading.date = tempReading.date-referenceTimestamp;
            //long combined = tempReading.date+referenceTimestamp;
            //Log.d("CONTENTVAL", "zeroReferenceDates: "+r.date +" vs " +combined);
            mAdjustedReadings.add(tempReading);
            //r.date = r.date-referenceTimestamp;
        }
    }
}
