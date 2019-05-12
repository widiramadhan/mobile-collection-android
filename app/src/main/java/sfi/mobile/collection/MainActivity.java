package sfi.mobile.collection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import sfi.mobile.collection.fragment.ChangePasswordFragment;
import sfi.mobile.collection.fragment.ProgressFragment;
import sfi.mobile.collection.fragment.TabHistory;
import sfi.mobile.collection.fragment.TaskFragment;
import sfi.mobile.collection.fragment.HomeFragment;
import sfi.mobile.collection.fragment.ProfileFragment;
import sfi.mobile.collection.listener.MyPhoneStateListener;
import sfi.mobile.collection.services.MobileCollectionService;

public class MainActivity extends AppCompatActivity implements LocationListener{

    TextView txtNavHeaderFullName, txtNavHeaderUserName;

    private FragmentManager fragmentManager;
    private Fragment fragment = null;

    Intent serviceIntent;
    LocationManager locationManager;


    TelephonyManager telephonyManager;
    MyPhoneStateListener psListener;
    TextView txtSignalStr;

    /*** memanggil session yang terdaftar ***/
    SharedPreferences sharedpreferences;
    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String TAG_USER_ID = "USERID";
    public final static String TAG_USERNAME = "USERNAME";
    public final static String TAG_FULL_NAME = "FULLNAME";
    public final static String TAG_BRANCH_ID = "BRANCH_ID";
    public final static String TAG_EMP_ID = "EMP_ID";
    public final static String TAG_EMP_JOB_ID= "EMP_JOB_ID";
    public final static String TAG_BRANCH_NAME = "BRANCH_NAME";

    public static final String my_shared_preferences = "my_shared_preferences";
    String fullName, branchName;
    /*** end memanggil session yang terdaftar ***/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //memanggil service
        Intent i = new Intent(this, MobileCollectionService.class);
        this.startService(i);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new HomeFragment();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        txtNavHeaderFullName = (TextView)header.findViewById(R.id.nav_header_fullname);
        txtNavHeaderUserName = (TextView)header.findViewById(R.id.nav_header_username);

        /*** set session to variable ***/
        sharedpreferences = this.getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        fullName = sharedpreferences.getString(TAG_FULL_NAME, null);
        branchName = sharedpreferences.getString(TAG_BRANCH_NAME, null);

        /*** end set session to variable ***/

        txtNavHeaderFullName.setText(fullName);
        txtNavHeaderUserName.setText(branchName);

        //-----------------------------------------------------------------------//

        /*** ACTION MENU SAAT DIPILIH ***/
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_dashboard) {
                    fragment = new HomeFragment();
                } else if (id == R.id.nav_task) {
                    fragment = new TaskFragment();
                } else if (id == R.id.nav_progress) {
                    fragment = new ProgressFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString( "flag" , "0");
                    fragment.setArguments(arguments);
                } else if (id == R.id.nav_history) {
                    //fragment = new TabDraftWithDelete();
                    fragment = new TabHistory();
                } else if (id == R.id.nav_logout){
                    SharedPreferences preferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();

                    Intent iIntent = new Intent(MainActivity.this, Login.class);
                    startActivity(iIntent);
                    Toast.makeText(getApplicationContext(), "Anda berhasil Logout", Toast.LENGTH_LONG).show();
                    finish();
                   // super.onDestroy();
                    //stopService(serviceIntent);
                    stopService(new Intent(MainActivity.this, MobileCollectionService.class));
                   return true;
                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container_wrapper, fragment);
                fragmentTransaction.addToBackStack("A_B_TAG");
                transaction.commit();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        //stopService(serviceIntent);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("FragmentC") != null) {
            // I'm viewing Fragment C
            getSupportFragmentManager().popBackStack("A_B_TAG",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences preferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            Intent iIntent = new Intent(MainActivity.this, Login.class);
            startActivity(iIntent);
            Toast.makeText(getApplicationContext(), "Anda berhasil Logout", Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        if (id == R.id.action_ganti_password) {
            fragment = new ChangePasswordFragment();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container_wrapper, fragment);
            transaction.commit();

            return true;
        }
        if (id == R.id.action_profile) {
            fragment = new ProfileFragment();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container_wrapper, fragment);
            transaction.commit();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            assert drawer != null;
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        if (id == R.id.action_signal) {
            Toast.makeText(getApplicationContext(), "Connection is The Best", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_good_signal) {
            Toast.makeText(getApplicationContext(), "Connection is Good", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_warning_signal) {
            Toast.makeText(getApplicationContext(), "Connection is Very Weak", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_no_signal) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(final Menu menu) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //get location
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            //Log.e(TAG,"latitude in background -> "+lat);
                            //Log.e(TAG,"longitude in background -> "+lng);
                        }


                        //cek koneksi
                        MenuItem signal = menu.findItem(R.id.action_signal);
                        MenuItem good = menu.findItem(R.id.action_good_signal);
                        MenuItem warning = menu.findItem(R.id.action_warning_signal);
                        MenuItem no_signal = menu.findItem(R.id.action_no_signal);
                        if(getNetworkClass(getApplicationContext()) == "2G" || getNetworkClass(getApplicationContext()) == "Low" || getNetworkClass(getApplicationContext()) == "Weak"){
                            signal.setVisible(false);
                            good.setVisible(false);
                            warning.setVisible(true);
                            no_signal.setVisible(false);
                        }else if(getNetworkClass(getApplicationContext()) == "3G" || getNetworkClass(getApplicationContext()) == "Good"){
                            signal.setVisible(false);
                            good.setVisible(true);
                            warning.setVisible(false);
                            no_signal.setVisible(false);
                        }else if(getNetworkClass(getApplicationContext()) == "4G" || getNetworkClass(getApplicationContext()) == "Best") {
                            signal.setVisible(true);
                            good.setVisible(false);
                            warning.setVisible(false);
                            no_signal.setVisible(false);
                        }else{
                            signal.setVisible(false);
                            good.setVisible(false);
                            warning.setVisible(false);
                            no_signal.setVisible(true);
                        }
                    }
                });
            }
        }, 0, 3000);

        return true;
    }

    public String getNetworkClass(Context context) {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                int numberOfLevels = 5;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                if (level == 4 || level == 5) {
                    return "Best";
                } else if (level == 3) {
                    return "Good";
                } else if (level == 2) {
                    return "Low";
                } else if (level == 1) {
                    return "Weak";
                } else {
                    return "No Signal";
                }
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                int networkType = mTelephonyManager.getNetworkType();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return "2G";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return "3G";
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return "4G";
                    default:
                        return "Unknown";
                }
            } else {
                return "Other";
            }
        }else{
            return "Not Connection";
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
