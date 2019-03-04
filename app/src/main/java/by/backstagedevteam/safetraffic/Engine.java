package by.backstagedevteam.safetraffic;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class implements the functions of working main engine
 * <p>
 * * @author Dmitry Kostyuchenko
 * * @see by.backstagedevteam.safetraffic
 * * @since 2019
 */
public class Engine {
    public static final double DEFAULT_AREA_RADIUS = 5;
    public static final double DEFAULT_AREA_ROUTE = 5;
    public MarkersQueue queue;
    private boolean isRun = false;

    private DBWorker dbWorker;

    public Engine(Context context) {
        dbWorker = new DBWorker(context);
        dbWorker.clear();
        dbWorker.initImportGPX();
    }

    /**
     * This method started routing mode
     *
     * @param routes of MapKit
     */
    public void start(List<DrivingRoute> routes) {
        queue = new MarkersQueue(getMarkerForDriving(routes));
        isRun = true;
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
                    if (Markers.getDistance(point.get(i), db.get(j).getPosition()) <= DEFAULT_AREA_ROUTE) {
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
        if (location.getLatitude() != 0 && location.getLongitude() != 0) {
            changeLocation(context, location);
        }
    }

    /**
     * This method handles location changes and triggers notifications
     *
     * @param context
     * @param location user
     */
    private void changeLocation(Context context, Location location) {
        String str;
        if (location != null) {
            str = String.format(
                    "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                    location.getLatitude(), location.getLongitude(), new Date(
                            location.getTime()));

        } else {
            str = "Location unknown!";
        }
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        if (isRun) {
            Point pLoc = new Point(location.getLatitude(), location.getLongitude());
            if (queue.getMarker().checkIntersection(pLoc)) {
                String text = "Distance: " +
                        Markers.getDistance(queue.getMarker().getPosition(), new Point(location.getLatitude(), location.getLongitude())) +
                        " Type ";
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                queue.next();
            }
        }
    }

    /**
     * This method return All markers of Data Base
     *
     * @return array markers
     */
    public ArrayList<Markers> getDB() {
        return dbWorker.getMarkers();
    }

    /**
     * This method return All markers of Queue
     *
     * @return array markers
     */
    public ArrayList<Markers> getQueue() {
        return queue.getAllMarkers();
    }
}
