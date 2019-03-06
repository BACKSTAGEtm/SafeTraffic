package by.backstagedevteam.safetraffic;

import android.location.Location;
import android.util.Log;

import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;

/**
 * This class implements markers queue and base function
 * <p>
 * * @author Dmitry Kostyuchenko
 * * @see by.backstagedevteam.safetraffic
 * * @since 2019
 */
public class MarkersQueue {
    private ArrayList<Markers> queue;
    private int pointer = 0;
    //temp, user location, refresh in Engine for correct work
    Location location;
    Point currentLocation = new Point(0, 0);

    /**
     * Temp method. Update location for task "checkPointer"
     *
     * @param location is new location
     */
    public void updateLocation(Point location) {
        currentLocation = location;
        //this.location = new Location(location);
    }

    /**
     * This constructor create empty queue and set pointer = 0;
     */
    public MarkersQueue() {
        queue = new ArrayList<>();
    }

    /**
     * This constructor create queue of array markers and set pointer = 0;
     */
    public MarkersQueue(ArrayList<Markers> markers) {
        queue = markers;
    }

    /**
     * This constructor create queue of markers and set pointer = 0;
     */
    public MarkersQueue(Markers markers) {
        queue = new ArrayList<>();
        queue.add(markers);
    }

    /**
     * This method add marker from the queue
     */
    public void addMarker(Markers markers) {
        queue.add(markers);
    }

    /**
     * This method add array markers from the queue
     */
    public void addMarker(ArrayList<Markers> markers) {
        for (Markers item :
                markers) {
            queue.add(item);
        }
    }

    /**
     * This method return the marker from the queue in the current position
     *
     * @return markers
     */
    public Markers getMarker() {
        return queue.get(pointer);
    }

    /**
     * This method return the marker from the queue at a given position
     *
     * @return markers
     */
    public Markers getMarker(int pos) {
        return queue.get(pos);
    }

    /**
     * This method return all markers in queue
     *
     * @return array markers
     */
    public ArrayList<Markers> getAllMarkers() {
        return queue;
    }


    /**
     * This method set pointer
     * @param value
     */
    public void setPointer(int value) {
        if (value >= 0 && value < queue.size()) {
            pointer = value;
        }
    }

    /**
     * This method return pointer
     *
     * @return pointer
     */
    public int getPointer() {
        return pointer;
    }

    /**
     * TESTED METHOD!!
     * This method check current pointer and set correct value
     */
    public void checkPointer() {
        Log.d("Queue check", "Old pointer=" + pointer);
        //Point pLoc = new Point(location.getLatitude(), location.getLongitude());
        double dist = Markers.getDistance(queue.get(0).getPosition(), currentLocation);
        //double dist = Markers.getDistance(queue.get(0).getPosition(), pLoc);
        int tPointer = 0;
        for (int i = 1; i < queue.size(); i++) {
            double tDist = Markers.getDistance(queue.get(i).getPosition(), currentLocation);
//            double tDist = Markers.getDistance(queue.get(i).getPosition(), pLoc);
            if (tDist < dist) {
                dist = tDist;
                tPointer = i;
            }
        }
        if (tPointer + 1 != pointer) {
            pointer = tPointer;
        }
        Log.d("Queue check", "New pointer=" + pointer);
    }

    public void checkQueue() {
        Log.d("Queue check", "Old pointer=" + pointer);
        //Point pLoc = new Point(location.getLatitude(), location.getLongitude());
        if (pointer > 0) {
            /**
             * Calculate distance between current and previous marker, to current marker and previous marker
             */
            double btwMarkDist = Markers.getDistance(queue.get(pointer - 1).getPosition(), queue.get(pointer).getPosition());
            double toCurMarkDist = Markers.getDistance(currentLocation, queue.get(pointer).getPosition());
            double toPrevMarkDist = Markers.getDistance(currentLocation, queue.get(pointer - 1).getPosition());
//            TODO: WRITE CODE!:)
            if ((btwMarkDist + Markers.DEFAULT_AREA_RADIUS) > (toCurMarkDist)) {

            }
        }
    /*
        //double dist = Markers.getDistance(queue.get(0).getPosition(), pLoc);
        int tPointer = 0;
        for (int i = 1; i < queue.size(); i++) {
            double tDist = Markers.getDistance(queue.get(i).getPosition(), currentLocation);
//            double tDist = Markers.getDistance(queue.get(i).getPosition(), pLoc);
            if (tDist < dist) {
                dist = tDist;
                tPointer = i;
            }
        }
        if (tPointer + 1 != pointer) {
            pointer = tPointer;
        }
        Log.d("Queue check", "New pointer=" + pointer);*/
    }

    /**
     * This method increment pointer value and return pointer
     *
     * @return pointer
     */
    public Markers nextMarker() {
        pointer++;
        return getMarker();
    }

    /**
     * This method increment pointer value and return pointer
     *
     * @return pointer
     */
    public int next() {
        if (pointer++ >= queue.size()) { //temp code
            pointer = 0;
        }
        return pointer;
    }

    /**
     * This method decrement pointer value and return pointer
     *
     * @return pointer
     */
    public int early() {
        pointer--;
        return pointer;
    }
}
