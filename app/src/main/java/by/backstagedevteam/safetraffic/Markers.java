package by.backstagedevteam.safetraffic;


import android.util.Log;

import com.yandex.mapkit.geometry.Point;

/**
 * This class implements base structure of Marker.
 *
 * @author Dmitry Kostyuchenko
 * @see by.backstagedevteam.safetraffic
 * @since 2019
 */
public class Markers {
    public static final double DEFAULT_AREA_RADIUS = 5;

    private double latitude;
    private double longitude;
    private MarkerType type;

    /**
     * This constructor uses default marker type "DangerousArea"
     *
     * @param latitude
     * @param longitude
     */
    public Markers(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        type = MarkerType.DangerousArea;
    }

    public Markers(double latitude, double longitude, MarkerType type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
    }

    public Markers(Point point, MarkerType type) {
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Point getPosition() {
        return new Point(latitude, longitude);
    }

    /**
     * This method checks the intersection of objects. Uses default area.
     *
     * @param pos position of the first object
     * @return {@code true} if objects intersect
     */
    public boolean checkIntersection(Point pos) {
        if (getDistance(new Point(latitude, longitude), pos) < DEFAULT_AREA_RADIUS) {
            return true;
        }
        return false;
    }

    /**
     * This method checks the intersection of objects. Uses default area.
     *
     * @param pos1 position of the first object
     * @param pos2 position of the second object
     * @return {@code true} if objects intersect
     */
    public boolean checkIntersection(Point pos1, Point pos2) {
        if (getDistance(pos1, pos2) < DEFAULT_AREA_RADIUS) {
            return true;
        }
        return false;
    }

    /**
     * This method checks the intersection of objects. Uses custom area.
     *
     * @param pos1   position of the first object
     * @param pos2   position of the second object
     * @param radius custom area
     * @return {@code true} if objects intersect
     */
    public boolean checkIntersection(Point pos1, Point pos2, double radius) {
        if (getDistance(pos1, pos2) < radius) {
            return true;
        }
        return false;
    }

    /**
     * This method return distance between objects.
     *
     * @param pos1 position of the first object
     * @param pos2 position of the second object
     * @return distance
     */
    public static double getDistance(Point pos1, Point pos2) {
        double R = 6378.137; // Radius of earth in KM
        double lat1 = pos1.getLatitude();
        double lon1 = pos1.getLongitude();
        double lat2 = pos2.getLatitude();
        double lon2 = pos2.getLongitude();
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; //meter
        //double dLat = pos2.getLatitude() - pos1.getLatitude();
        //double dLon = pos2.getLongitude() - pos1.getLongitude();
        //return Math.sqrt(dLon * dLon + dLat * dLat);
    }

    public MarkerType getType() {
        return type;
    }

    /**
     * This method print marker from log
     */
    public void printDBG() {
        final String TAG = "Markers print";
        Log.d(TAG, "lat=" + getLatitude() + ", lon=" + getLongitude() + ", type=" + getType());
    }
}
