package by.backstagedevteam.safetraffic;

import android.Manifest;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;

import com.yandex.mapkit.map.CircleMapObject;

import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingArrivalPoint;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.RequestPoint;
import com.yandex.mapkit.directions.driving.RequestPointType;
import com.yandex.mapkit.map.MapObjectCollection;

import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements UserLocationObjectListener, DrivingSession.DrivingRouteListener {
    private final String MAPKIT_API_KEY = "a574df9b-3431-4ff7-a6a9-2532869cfc80";

    //private final Point ROUTE_START_LOCATION = new Point(59.959194, 30.407094);
    //private final Point ROUTE_END_LOCATION = new Point(55.733330, 37.587649);
    private final Point ROUTE_START_LOCATION = new Point(52.44251724316334, 31.001705053000766);
    private final Point ROUTE_END_LOCATION = new Point(52.44580572179339, 30.99427892590486);

    private final Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
            (ROUTE_START_LOCATION.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2);

    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private LocationManager locationManager;
    private Location deviceLocation;

    TextView textCoorVal;

    private Engine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);

        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        /*mapView.getMap().move(
                new CameraPosition(new Point(55.751574,37.573856), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH,0),
                null);*/
        userLocationLayer = mapView.getMap().getUserLocationLayer();
        userLocationLayer.setEnabled(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
        textCoorVal = (TextView) findViewById(R.id.textUserLocationVal);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        engine = new Engine(this);

        //mapObjects = mapView.getMap().getMapObjects().addCollection();
        //createMapObjects(engine.getDB(), Color.RED);
        //TEMP
    }

    /**
     * This method add new visible markers to map
     * @param markers
     * @param colorfill new markers
     */
    private void createMapObjects(ArrayList<Markers> markers, int colorfill) {
        for (Markers item :
                markers) {
            //TODO Change Icon!
            CircleMapObject circle = mapObjects.addCircle(
                    new Circle(item.getPosition(), (float) Markers.DEFAULT_AREA_RADIUS), Color.GREEN, 2, colorfill);
            circle.setZIndex(100.0f);

            PlacemarkMapObject mark = mapObjects.addPlacemark(item.getPosition());
            mark.setOpacity(0.5f);
            mark.setIcon(ImageProvider.fromResource(this, R.drawable.pin));
            mark.setDraggable(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83)));

        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, R.drawable.user_arrow));

        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        /*
        pinIcon.setIcon(
                "icon",
                ImageProvider.fromResource(this, R.drawable.icon),
                new IconStyle().setAnchor(new PointF(0f, 0f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(0f)
                        .setScale(1f)
        );*/

        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, R.drawable.search_result),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE);
    }

    @Override
    public void onObjectRemoved(UserLocationView view) {
    }

    @Override
    public void onObjectUpdated(UserLocationView view, ObjectEvent event) {
    }

    public void onDrivingRoutes(List<DrivingRoute> routes) {
        engine.start(routes);
        for (DrivingRoute route : routes) {
            mapObjects.addPolyline(route.getGeometry());
        }
        /****Markers***/
        createMapObjects(engine.getQueue(), Color.BLUE);
    }

    @Override
    public void onDrivingRoutesError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void submitRequest() {
        DrivingOptions options = new DrivingOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        Point startLocation = new Point(deviceLocation.getLatitude(), deviceLocation.getLongitude());
        requestPoints.add(new RequestPoint(
//                ROUTE_START_LOCATION,
                startLocation,
                new ArrayList<Point>(),
                new ArrayList<DrivingArrivalPoint>(),
                RequestPointType.WAYPOINT));
        requestPoints.add(new RequestPoint(
                ROUTE_END_LOCATION,
                new ArrayList<Point>(),
                new ArrayList<DrivingArrivalPoint>(),
                RequestPointType.WAYPOINT));
        drivingSession = drivingRouter.requestRoutes(requestPoints, options, this);
    }

    public void CreateRouting(View view) {
        //Point startRout = userLocationLayer.

//        Point centerScreen = new Point(
//                (ROUTE_START_LOCATION.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
//                (ROUTE_START_LOCATION.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2);
        //TODO: check valid location
        try {
            if (deviceLocation != null) {
                Point centerScreen = new Point(
                        (deviceLocation.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
                        (deviceLocation.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2);
                mapView.getMap().move(new CameraPosition(
                        //        SCREEN_CENTER, 2, 0, 0));
                        centerScreen, 8, 0, 0));
                drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
                mapObjects = mapView.getMap().getMapObjects().addCollection();
                submitRequest();
            } else {
                Toast.makeText(this, "Current location unknown!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Log.d("routing", e.getMessage());
        }

    }

    /**
     * Location zone
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 1, 1, locationListener);
        /*locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("LocChange",location.getLatitude()+", " + location.getLongitude());
            deviceLocation = location;
            if (location != null) {
                engine.handler(MainActivity.this, location);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            changeProvider(false);
        }

        @Override
        public void onProviderEnabled(String provider) {
            changeProvider(true);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location lastLocation = locationManager.getLastKnownLocation(provider);
            Log.d("LocLast",lastLocation.getLatitude()+", " + lastLocation.getLongitude());
            if (lastLocation != null) {
                deviceLocation = lastLocation;
                engine.handler(MainActivity.this, lastLocation);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                changeStatus("GPS PROVIDER");
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                changeStatus("NETWORK PROVIDER");
            }
        }
    };

    private void changeProvider(boolean status) {
        if (status) {
            Toast.makeText(this, "Provider is Disable!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Provider is Enable!", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStatus(String status) {
        Toast.makeText(this, "Status" + status, Toast.LENGTH_SHORT).show();
    }
}
