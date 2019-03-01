package by.backstagedevteam.safetraffic;

import android.graphics.PointF;
import android.location.Location;
import android.widget.Toast;

import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;
/**
 *
 * OLD CLASS
 */
public class BDProcessing {
    /*********location**********/
    private final Point ROUTE_START_LOCATION = new Point(59.959194, 30.407094);
    private final Point ROUTE_END_LOCATION = new Point(55.733330, 37.587649);
    //private static final Point m1 = new Point(59.959194, 30.407094);
    private static final int NUM_MARKER = 50;

    private static final Point m2 = new Point (2,1);
    private static final Point m3 = new Point (1,2);
    private static final Point m4 = new Point (3,1);
    private static final Point m5 = new Point (1,3);

    /***************************/
    ArrayList<BDItem> bd;
    public static int size = 0;

    public BDProcessing(){
        bd = new ArrayList<>();
        double hLat = (ROUTE_START_LOCATION.getLatitude() - ROUTE_END_LOCATION.getLatitude())/(NUM_MARKER+1);
        double hLon = (ROUTE_START_LOCATION.getLongitude() - ROUTE_END_LOCATION.getLongitude())/(NUM_MARKER+1);
        double lat = ROUTE_START_LOCATION.getLatitude();
        double lon = ROUTE_START_LOCATION.getLongitude();
        for (int i = 0; i < NUM_MARKER; i++) {
            lat += hLat;
            lon += hLon;
            bd.add(new BDItem(new Point(lat,lon)));
        }
        size = NUM_MARKER;
    }

    public BDProcessing(Location location, Point destination){
        bd = new ArrayList<>();
        Point currentLocation = new Point(location.getLatitude(),location.getLongitude());
        double hLat = (currentLocation.getLatitude() - destination.getLatitude())/(NUM_MARKER+1);
        double hLon = (currentLocation.getLongitude() - destination.getLongitude())/(NUM_MARKER+1);
        double lat = currentLocation.getLatitude();
        double lon = currentLocation.getLongitude();

        for (int i = 0; i < NUM_MARKER; i++) {
            lat += hLat;
            lon += hLon;
            bd.add(new BDItem(new Point(lat,lon)));
        }
        size = NUM_MARKER;
    }

    public BDProcessing(Point currentLocation, Point destination){
        bd = new ArrayList<>();
        double hLat = (currentLocation.getLatitude() - destination.getLatitude())/(NUM_MARKER+1);
        double hLon = (currentLocation.getLongitude() - destination.getLongitude())/(NUM_MARKER+1);
        double lat = currentLocation.getLatitude();
        double lon = currentLocation.getLongitude();
        for (int i = 0; i < NUM_MARKER; i++) {
            lat += hLat;
            lon += hLon;
            bd.add(new BDItem(new Point(lat,lon)));
        }
        size = NUM_MARKER;
    }

    public BDItem checkIntersection(Point location) {
        for (int i = 0; i < bd.size(); i++) {
            Point locMarker = bd.get(i).getLocation();
            double dLat = locMarker.getLatitude() - location.getLatitude();
            double dLon = locMarker.getLongitude() - location.getLongitude();
            if (Math.sqrt(dLat*dLat+dLon*dLon)<bd.get(i).getRadius()){
                return bd.get(i);
            }

        }
        return null;
    }

    public ArrayList<Markers> getBDbyMarkers(){
        ArrayList<Markers> markers = new ArrayList<>();
        for (int i = 0; i < bd.size(); i++) {
            Point location = bd.get(i).getLocation();
            markers.add(new Markers(location.getLatitude(),location.getLongitude(),MarkerType.Crosswalk));
        }
        return markers;
    }
}
