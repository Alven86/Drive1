package enal1586.ju.drive;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HistorySingleActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private String mRideId, mCurrentUserId, mCustomerId, mDriverId, mUserDriverOrCustomer;
    private TextView mRideLocation,mRideDistance,mRideDate,mUserName,mUserPhone;
    private ImageView mUserImage;
    private RatingBar mRatingBar;
    private DatabaseReference mHistoryRideInfoDb;
    private LatLng mDestinationLatLng, mPickupLatLng;
    private String mDistance;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    //constants
    private final static String Name = "name";
    private final static String RiderId = "rideId";
    private final static String Phone = "phone";
    private final static String Profile_Image= "profileImageUrl";
    private final static String Time_Stamp = "timestamp";
    private final static String Rating= "rating";
    private final static String Distance = "distance";
    private final static String Destination= "destination";
    private final static String Location= "location";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);





        polylines = new ArrayList<>();
        mRideId = getIntent().getExtras().getString(RiderId);


        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        mRideLocation = (TextView) findViewById(R.id.rideLocation);
        mRideDistance = (TextView) findViewById(R.id.rideDistance);
        mRideDate = (TextView) findViewById(R.id.rideDate);
        mUserName = (TextView) findViewById(R.id.userName);
        mUserPhone = (TextView) findViewById(R.id.userPhone);

        mUserImage = (ImageView) findViewById(R.id.userImage);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);



        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mHistoryRideInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(mRideId);
        getRideInformation();

    }


    private void getRideInformation() {
        mHistoryRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        if (child.getKey().equals("customer")){
                            mCustomerId = child.getValue().toString();
                            if(!mCustomerId.equals(mCurrentUserId)){
                                mUserDriverOrCustomer = "Drivers";
                                getUserInformation("Customers", mCustomerId);
                            }
                        }
                        if (child.getKey().equals("driver")){
                            mDriverId = child.getValue().toString();
                            if(!mDriverId.equals(mCurrentUserId)){
                                mUserDriverOrCustomer = "Customers";
                                getUserInformation("Drivers", mDriverId);
                                displayCustomerRelatedObjects();
                            }
                        }
                        if (child.getKey().equals(Time_Stamp)){
                            mRideDate.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if (child.getKey().equals(Rating)){
                            mRatingBar.setRating(Integer.valueOf(child.getValue().toString()));

                        }

                        if (child.getKey().equals(Distance)){
                            mDistance = child.getValue().toString();
                            mRideDistance.setText(mDistance.substring(0, Math.min(mDistance.length(), 5)) + " km");


                        }
                        if (child.getKey().equals(Destination)){
                            mRideLocation.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals(Location)){
                            mPickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()), Double.valueOf(child.child("from").child("lng").getValue().toString()));
                            mDestinationLatLng = new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()), Double.valueOf(child.child("to").child("lng").getValue().toString()));
                            if(mDestinationLatLng != new LatLng(0,0)){
                                getRouteToMarker();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void displayCustomerRelatedObjects() {
        mRatingBar.setVisibility(View.VISIBLE);
        mRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            mHistoryRideInfoDb.child("rating").setValue(rating);
            DatabaseReference mDriverRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mDriverId).child("rating");
            mDriverRatingDb.child(mRideId).setValue(rating);
        });



    }


    private void getUserInformation(String otherUserDriverOrCustomer, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserDriverOrCustomer).child(otherUserId);
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get(Name) != null)
                        mUserName.setText(Objects.requireNonNull(map.get(Name)).toString());
                    if(map.get(Phone) != null){
                        mUserPhone.setText(Objects.requireNonNull(map.get(Phone)).toString());
                    }
                    if(map.get(Profile_Image) != null){
                        Glide.with(getApplication()).load(Objects.requireNonNull(map.get(Profile_Image)).toString()).into(mUserImage);
                    }
                }

            }




            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }
    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .key("AIzaSyDYuz1hAC-gdXw9pNYdq2DFh18nMXbLE-s")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(mPickupLatLng, mDestinationLatLng)
                .build();
        routing.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
    }


    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, R.string.Error + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, R.string.Something_went_wrong_Try_again, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mPickupLatLng);
        builder.include(mDestinationLatLng);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width*0.2);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cameraUpdate);

        mMap.addMarker(new MarkerOptions().position(mPickupLatLng).title(getString(R.string.pickup_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup)));
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title(getString(R.string.destination)));

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

}