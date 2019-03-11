package sfi.mobile.collection.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import sfi.mobile.collection.R;


public class QuestionFragment extends Fragment implements LocationListener {

    private static final String TAG = QuestionFragment.class.getSimpleName();
    LocationManager locationManager;

    public QuestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        return view;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.rl);

        int loopView = 3;
        for (int i = 1; i <= loopView; i++) {
            TextView view = new TextView(getActivity());
            view.setId(i);
            view.setText("Textview Looping " + i);
            view.setTextColor(Color.parseColor("#ff0000"));

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (i > 1) {
                lp.addRule(RelativeLayout.BELOW, i - 1);
            }
            rl.addView(view, lp);
        }

        //Textview
        TextView view = new TextView(getActivity());
        view.setId(4);
        view.setText("Testing");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, 3);
        rl.addView(view, lp);

        //EditText
        EditText editText = new EditText(getActivity());
        editText.setId(5);
        editText.setText("Testing");
        lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, 4);
        rl.addView(editText, lp);

        //Dropdown
        Spinner spinner = new Spinner(getActivity());
        spinner.setId(6);
        lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, 5);
        String[] items = new String[]{"one", "two", "three"};
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        spinner.setAdapter(adapter);
        rl.addView(spinner, lp);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        //Button get Location
        Button button = new Button(getActivity());
        button.setId(7);
        button.setText("Get Location");
        lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, 6);
        rl.addView(button, lp);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //getLocation();
                getLocation2();
            }
        });
    }

    public void getLocation() {
        final LocationManager manager = (LocationManager)getContext().getSystemService(getContext().LOCATION_SERVICE );
        //manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, getActivity());
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
            Toast.makeText(getActivity(),"Silahkan aktifkan GPS terlebih dahulu",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }else{
            FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.e(TAG,"Location -> "+location);
                    if (location != null) {
                        Toast.makeText(getActivity(),
                                "Lat : " + location.getLatitude() + " Long : " + location.getLongitude(),
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(),"GPS signal not found",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /*public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.e("latitude", "latitude--" + latitude);
        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                //ro_gps_location.setText(state + " , " + city + " , " + country);
                //ro_address.setText(address + " , " + knownName + " , " + postalCode);
                Toast.makeText(getActivity(),
                        state + " , " + city + " , " + country + " , " + address + " , " + knownName + " , " + postalCode,
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/

    void getLocation2() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, QuestionFragment.this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getActivity(),"Current Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getActivity(), "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
