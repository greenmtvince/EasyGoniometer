package com.quantrian.easygoniometer.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.Share;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
    private Reading mReading;

    //Facebook fields
    CallbackManager callbackManager;
    ShareDialog shareDialog;

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

        heading = mBaseView.findViewById(R.id.detail_heading);
        subTitle = mBaseView.findViewById(R.id.detail_subtitle);

        Bundle bundle = this.getArguments();
        Reading r = bundle.getParcelable("READING");
        if (r!=null){
            updateHeading(r);
        } else {
            int rom = bundle.getInt("ROM");
            heading.setText(String.format(getString(R.string.rom_heading),rom));
        }






        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        return mBaseView;
    }

    public void updateHeading(Reading reading){
        mReading=reading;

        //Get my Views
        LinearLayout flex_row = mBaseView.findViewById(R.id.flex_row);
        LinearLayout ext_row = mBaseView.findViewById(R.id.ext_row);
        LinearLayout share_row = mBaseView.findViewById(R.id.share_row);

        ImageView fb_iv = mBaseView.findViewById(R.id.facebookImg);
        ImageView sms_iv = mBaseView.findViewById(R.id.smsImg);
        ImageView mail_iv = mBaseView.findViewById(R.id.mailImg);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.facebookImg:
                        shareContent();
                        break;
                    case R.id.smsImg:
                        shareSMS();
                        break;
                    case R.id.mailImg:
                        shareEmail();
                        break;
                    default:
                        break;
                }
            }
        };

        fb_iv.setOnClickListener(listener);
        sms_iv.setOnClickListener(listener);
        mail_iv.setOnClickListener(listener);

        TextView flex_value = mBaseView.findViewById(R.id.flex_value);
        TextView ext_value = mBaseView.findViewById(R.id.ext_value);

        //Adjust Visibilities of Views
        subTitle.setVisibility(View.GONE);
        ext_row.setVisibility(View.VISIBLE);
        flex_row.setVisibility(View.VISIBLE);
        share_row.setVisibility(View.VISIBLE);

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

    public void shareContent(){
        if (ShareDialog.canShow(ShareLinkContent.class)){
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://developers.facebook.com"))
                    .setQuote(String.format("Flexion: %d  Extension %d  on %s",mReading.flexion,mReading.extension,DateConverter.secToString(mReading.date)))
                    .build();
            shareDialog.show(linkContent);
        }
    }

    public void shareSMS(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body",String.format("Flexion: %d  Extension %d  on %s",mReading.flexion,mReading.extension,DateConverter.secToString(mReading.date)));
        startActivity(sendIntent);
    }

    public void shareEmail(){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("mailto:"));
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "My knee range of motion on " + DateConverter.secToString(mReading.date));
        sendIntent.putExtra(Intent.EXTRA_TEXT,String.format("Flexion: %d  Extension %d  on %s",mReading.flexion,mReading.extension,DateConverter.secToString(mReading.date)));
        startActivity(sendIntent);
    }
}
