package com.quantrian.easygoniometer.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.quantrian.easygoniometer.utilities.HourAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

        Reading[] dataObjects = buildTestData(5);

        List<Entry> entriesExt = new ArrayList<Entry>();
        List<Entry> entriesFlex = new ArrayList<Entry>();

        for (Reading data : dataObjects){
            entriesExt.add(new Entry(data.date,data.extension));
            entriesFlex.add(new Entry(data.date,data.flexion));
        }

        ArrayList<ILineDataSet> lines = new ArrayList<ILineDataSet>();

        //String[] xAxis = new String[]{"1","2","3","4","5","6"};

        LineDataSet dataSet1 = new LineDataSet(entriesExt, getActivity().getString(R.string.extension_label));
        dataSet1.setColor(Color.CYAN);
        dataSet1.setValueTextColor(Color.BLACK);
        lines.add(dataSet1);

        LineDataSet dataSet2 = new LineDataSet(entriesFlex, getActivity().getString(R.string.flexion_label));
        dataSet2.setColor(Color.RED);
        dataSet2.setValueTextColor(Color.BLACK);
        lines.add(dataSet2);

        IAxisValueFormatter xAxisValueFormatter = new HourAxisValueFormatter(referenceTimestamp);

        XAxis xAxis1 = chart.getXAxis();
        xAxis1.setValueFormatter(xAxisValueFormatter);


        chart.setData(new LineData(lines));
        chart.setScaleYEnabled(false);
        chart.invalidate();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mListener.onFragmentInteraction(Float.toString(e.getX()));
                //Toast.makeText(getContext(), "Value: " +e.getY()+ " at " + e.getX(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });


        // Inflate the layout for this fragment
        return rootView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

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
       // mListener = null;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String message);
    }

    private Reading[] buildTestData(int max){
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -5);

        Reading[] outArray = new Reading[5];
        outArray[0] =new Reading(85,10,dummyDate(cal,0));
        outArray[1] =new Reading(90,8,dummyDate(cal,1));
        outArray[2] =new Reading(92,7,dummyDate(cal,1));
        outArray[3] =new Reading(90,4,dummyDate(cal,1));
        outArray[4] =new Reading(98,3,dummyDate(cal,1));

        Log.d("WTFROFLBBQ", "buildTestData: size = " +outArray.length);

        referenceTimestamp =  outArray[0].date;

        outArray = zeroReferenceDates(outArray,referenceTimestamp);
        //referenceTimestamp = referenceTimestamp/1000;

        return outArray;
    }

    private long dummyDate(Calendar cal, int addor){

        cal.add(Calendar.DATE, +addor);
        Log.d("TIMEOUT", "dummyDate: "+ cal.getTime());
        long timeOut = cal.getTime().getTime()/1000;
        Log.d("TIMEOUT", "dummyDate: "+timeOut);
        return timeOut;
    }

    public class DummyData{
        private final long X;
        private final int Y;

        public DummyData(long Xer, int Yer){
            X = Xer;
            Y = Yer;
        }

        public long getX(){return X;}

        public int getY() {
            return Y;
        }
    }

    private Reading[] zeroReferenceDates(Reading[]oldSet, long reference){
        Reading[] convertedDates = new Reading[oldSet.length];


        for (int i=0;i<oldSet.length;i++){
            convertedDates[i]= new Reading(oldSet[i].flexion, oldSet[i].extension,(oldSet[i].date-reference));
            Log.d("TIME_CONTROL", "zeroReferenceDates: "+ oldSet[i].date+" - "+ reference +" = "+ convertedDates[i].date);
        }
        return convertedDates;
    }
}
