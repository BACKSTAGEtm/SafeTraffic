package by.backstagedevteam.safetraffic;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the functions of working main engine
 * <p>
 *
 * @author Dmitry Kostyuchenko
 * @see by.backstagedevteam.safetraffic
 * @since 2019
 */
public class Engine {
    public static final double DEFAULT_AREA_RADIUS = 30;
    //reset marker notification area
    public static final double SAFE_SIZE_AREA = 1.5 * DEFAULT_AREA_RADIUS;
    public static final double BUFFER_AREA = 1; //Lan and lon

    private boolean isRun = false;
    private DBWorker dbWorker;
    private ArrayList<Markers> markersBuffer;
    //id with marker triggered notification
    private int idCurrentMarker = -1;
    private boolean isNeedBufferUpdate = false;

    private Location currentLocation;

    /**
     * This method safe update current location
     *
     * @param location - new location
     */
    public void updateCurrentLocation(Location location) {
        if (location.getLatitude() != 0 && location.getLongitude() != 0) {
            currentLocation = location;
        }
    }

    /**
     * This method return current location is location valid or return {@code null}
     *
     * @return {@code Location} is location valid
     * {@code null} is bad location
     */
    public Location getCurrentLocation() {
        if (currentLocation.getLatitude() != 0 && currentLocation.getLongitude() != 0) {
            return currentLocation;
        } else {
            return null;
        }
    }

    /**
     * This method return current location is location valid or return {@code null}
     *
     * @return {@code Point} is location valid
     * {@code null} is bad location
     */
    public Point getCurrentLocationPoint() {
        if (currentLocation.getLatitude() != 0 && currentLocation.getLongitude() != 0) {
            return new Point(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            return null;
        }
    }

    private final Handler handler = new Handler();

    public Engine(Context context) {
        dbWorker = new DBWorker(context);
        dbWorker.clear();
        dbWorker.initImportGPX(context);
        currentLocation = new Location(LocationManager.GPS_PROVIDER);
    }

    /**
     * This method started routing mode
     */
    public void start() {
        try {
            Log.d("StartEngine", "try");
            if (getCurrentLocation() != null) {
                isRun = true;
                /**
                 * Start background task for checked current pointer
                 */
                updateBuffer();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { //Check pointer code from valid notification
                        updateBuffer();
                    }
                }, 10000);
                Log.d("StartedEngine", "STARTED");
            }
        } catch (Exception e) {
            Log.d("StartEngine", e.getMessage());
        }
    }

    /**
     * This method stop routing mode
     */
    public void stop() {
        try {
            if (isRun) {
                isRun = false;
                /**
                 * Start background task for checked current pointer
                 */
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { //Check pointer code from valid notification
                        //updateBuffer();
                    }
                }, 10000);
            }
        } catch (Exception e) {
            Log.d("StopEngine", e.getMessage());
        }
    }

    /**
     * This method update marker buffer
     */
    public void updateBuffer() {
        if (idCurrentMarker == -1) {
            double pLat = currentLocation.getLatitude();
            double pLon = currentLocation.getLongitude();
            Log.d("CurrentLocation", "Lat=" + pLat + ", Lon=" + pLon);
            Point p1 = new Point(pLat + BUFFER_AREA, pLon - BUFFER_AREA);
            Point p2 = new Point(pLat + BUFFER_AREA, pLon - BUFFER_AREA);
            Log.d("BufferArea", String.valueOf(Markers.getDistance(p1, p2)) + "m");
//        markersBuffer = dbWorker.getMarkersOfArea(p1, p2);
            markersBuffer = dbWorker.getMarkers();
            Log.d("BufferSize", String.valueOf(markersBuffer.size()));
        } else {
            isNeedBufferUpdate = true;
        }
    }

    /**
     * This method closed way and clear memory
     */
    public void closeWay() {
        isRun = false;
        //handler.
    }

    /**
     * This method optimization array point. Checked area radius
     *
     * @param input array
     * @return output array
     */
    public static ArrayList<Point> optimGeometry(List<Point> input) {
        Log.d("Optimization", "poly size = " + input.size());
        ArrayList<Point> output = new ArrayList<>();
        if (input.size() > 1) {
            output.add(input.get(0));
            int pointer = 0;
            for (int i = 1; i < input.size(); i++) {
                if (Markers.getDistance(input.get(pointer), input.get(i)) > DEFAULT_AREA_RADIUS) {
                    output.add(input.get(i));
                    pointer = i;
                }
            }
            Log.d("Optimization", "After optimization poly size = " + output.size());
            return output;
        } else {
            output.add(input.get(0));
            return output;
        }
    }

    /**
     * This method return array markers from Route
     * Old method?
     *
     * @param routes of MapKit
     * @return array markers
     */
    public ArrayList<Markers> getMarkerForDriving(List<DrivingRoute> routes) {
        ArrayList<Markers> output = new ArrayList<>();
        for (DrivingRoute route : routes) {
            Polyline polyline = route.getGeometry();
            List<Point> point = polyline.getPoints();
            //point = Engine.optimGeometry(point);
            ArrayList<Markers> db = dbWorker.getMarkers();
            for (int i = 0; i < point.size(); i++) {
                for (int j = 0; j < db.size(); j++) {
                    if (Markers.getDistance(point.get(i), db.get(j).getPosition()) <= DEFAULT_AREA_RADIUS) {
                        output.add(db.get(j));
                    }
                }
            }
        }
        return output;
    }

    /**
     * This method is main
     *
     * @param context  of MainActivity
     * @param location user
     */
    public void handler(Context context, Location location) {
        if (isRun) {
            try {
                if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                    updateCurrentLocation(location);

                    handleLocation(location);
                }
            } catch (Exception e) {
                Log.d("handler", e.getMessage());
            }
        } else {

        }
    }

    /**
     * This method checked intersection location and markers
     *
     * @param location
     */
    private void handleLocation(Location location) {
        Point pLoc = getCurrentLocationPoint();
        Log.d("handleLocation", "id = " + idCurrentMarker);
        if (idCurrentMarker != -1) {
            if (markersBuffer.get(idCurrentMarker).checkIntersection(pLoc, SAFE_SIZE_AREA) == false) {
                idCurrentMarker = -1;
                if (isNeedBufferUpdate) {
                    updateBuffer();
                }
            } else {
                //Break check intersection
                return;
            }
        }
        for (int i = 0; i < markersBuffer.size(); i++) {
            if (markersBuffer.get(i).checkIntersection(pLoc)) {
//                Sending notification
//                sendNotification(markersBuffer.getType);
                idCurrentMarker = i;
                Log.d("handleLocation", "NOTIFICATION, Dist: " + Markers.getDistance(markersBuffer.get(i).getPosition(), pLoc));
                break;
            }
        }
    }


    /**
     * This method return all markers of Data Base
     *
     * @return array markers
     */
    public ArrayList<Markers> getDB() {
        return dbWorker.getMarkers();
    }

    /**
     * This method return markers of buffer
     *
     * @return array markers
     */
    public ArrayList<Markers> getBuffer() {
        return markersBuffer;
    }
}
