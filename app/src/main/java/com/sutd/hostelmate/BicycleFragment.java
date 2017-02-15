package com.sutd.hostelmate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import static android.graphics.Typeface.NORMAL;
import static android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.ui.IconGenerator;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BicycleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BicycleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BicycleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Map stuff
    MapView mMapView;
    private GoogleMap googleMap;
    IconGenerator iconFactory;
    HashMap<String,Marker> hashMapMarker = new HashMap<>();
    Integer markerCount = 0;
    LatLng sutd = new LatLng(1.342223, 103.964144);
    LatLng block59M = new LatLng(1.34194, 103.963644);
    LatLng block57M = new LatLng(1.342223, 103.964044);
    LatLng block59L = new LatLng(1.34214, 103.963644);
    LatLng block57L = new LatLng(1.342423, 103.964044);
    LatLng block55M = new LatLng(1.342223, 103.964544);
    LatLng block55L = new LatLng(1.342423, 103.964544);
    private static String initBlk59count = "10";
    private static String initBlk57count = "57";
    private static String initBlk55count = "55";

    private TextView bicycleText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private DatabaseReference bicycleRef;
    private ChildEventListener bChildListener;

    public BicycleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BicycleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BicycleFragment newInstance(String param1, String param2) {
        BicycleFragment fragment = new BicycleFragment();
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

        bicycleRef = FirebaseDatabase.getInstance().getReference().child("bicycles");

        iconFactory = new IconGenerator(getActivity());

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView;

        rootView = inflater.inflate(R.layout.fragment_bicycle, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.bicycleMap);
        mMapView.onCreate(savedInstanceState);

        mMapView.setClickable(false);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setAllGesturesEnabled(false);

                addMarker(iconFactory, block59M);
                addMarker(iconFactory, block57M);
                addMarker(iconFactory, block55M);
                addLabel(iconFactory, "59", makeLabel("BLK 59", initBlk59count),block59L);
                addLabel(iconFactory, "57", makeLabel("BLK 57", initBlk57count),block57L);
                addLabel(iconFactory, "55", makeLabel("BLK 55", initBlk55count),block55L);

//                 For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sutd).zoom(18).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });



        return rootView;
    }


    private void addLabel(IconGenerator iconFactory,String block, CharSequence text, LatLng position) {

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        Marker marker = googleMap.addMarker(markerOptions);
        hashMapMarker.put(block, marker);
        markerCount++;
    }

    private void addMarker(IconGenerator iconFactory, LatLng position) {

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.defaultMarker()).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        googleMap.addMarker(markerOptions);
    }

    private void removeMarker(int pos) {
        Marker marker = hashMapMarker.get(4);
        marker.remove();
        markerCount--;
    }

    private CharSequence makeLabel(String block, String count) {
        String sequence = block +"\n " + count;
        SpannableStringBuilder ssb = new SpannableStringBuilder(sequence);
        ssb.setSpan(new RelativeSizeSpan(1.2f), 0, block.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(NORMAL), 0, block.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

        ssb.setSpan(new RelativeSizeSpan(2f), block.length()+1, sequence.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(NORMAL), block.length()+1, sequence.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb;
    }

    private void updateLabel(String block, String count) {
        Marker marker = hashMapMarker.get(block);
        marker.remove();

        LatLng latLng = null;

        switch (block) {
            case "59":
                latLng = block59L;
                break;
            case "57":
                latLng = block57L;
                break;
            case "55":
                latLng = block55L;
                break;
        }

        addLabel(iconFactory, block, makeLabel("BLK " + block, count),latLng);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String block = dataSnapshot.getKey().toString();
                String count = dataSnapshot.child("count").getValue().toString();

                updateLabel(block, count);

                Log.i("onChildChanged", "BLK " + block + ": " + count);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        bChildListener = bicycleRef.addChildEventListener(childEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mListener != null) {
            bicycleRef.removeEventListener(bChildListener);
        }

    }
}
