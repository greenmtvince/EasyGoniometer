package com.quantrian.easygoniometer.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quantrian.easygoniometer.R;
import com.quantrian.easygoniometer.models.Reading;
import com.quantrian.easygoniometer.utilities.DateConverter;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 *
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView heading;
    private TextView subTitle;
    private View mBaseView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        // Inflate the layout for this fragment
        mBaseView = inflater.inflate(R.layout.fragment_details, container, false);

        Bundle bundle = this.getArguments();
        int rom = bundle.getInt("ROM");

        heading = mBaseView.findViewById(R.id.detail_heading);
        subTitle = mBaseView.findViewById(R.id.detail_subtitle);

        heading.setText(String.format(getString(R.string.rom_heading),rom));


        return mBaseView;
    }

    public void updateHeading(Reading reading){
        //Get my Views
        LinearLayout flex_row = mBaseView.findViewById(R.id.flex_row);
        LinearLayout ext_row = mBaseView.findViewById(R.id.ext_row);
        Button share_btn = mBaseView.findViewById(R.id.share_btn);
        TextView flex_value = mBaseView.findViewById(R.id.flex_value);
        TextView ext_value = mBaseView.findViewById(R.id.ext_value);

        //Adjust Visibilities of Views
        subTitle.setVisibility(View.GONE);
        ext_row.setVisibility(View.VISIBLE);
        flex_row.setVisibility(View.VISIBLE);
        share_btn.setVisibility(View.VISIBLE);

        //Display Values
        heading.setText(DateConverter.secToString(reading.date));
        flex_value.setText(String.format(getString(R.string.flexion_value),reading.flexion));
        ext_value.setText(String.format(getString(R.string.extension_value),reading.extension));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
