package by.backstagedevteam.safetraffic;

import java.util.ArrayList;

/**
 * This class implements markers queue and base function
 *
 *  * @author Dmitry Kostyuchenko
 *  * @see by.backstagedevteam.safetraffic
 *  * @since 2019
 */
public class MarkersQueue {
    private ArrayList<Markers> queue;
    private int pointer = 0;

    /**
     * This constructor create empty queue and set pointer = 0;
     */
    public MarkersQueue(){
        queue = new ArrayList<>();
    }

    /**
     * This constructor create queue of array markers and set pointer = 0;
     */
    public MarkersQueue(ArrayList<Markers> markers){
        queue = markers;
    }

    /**
     * This constructor create queue of markers and set pointer = 0;
     */
    public MarkersQueue(Markers markers){
        queue = new ArrayList<>();
        queue.add(markers);
    }

    /**
     * This method add marker from the queue
     */
    public void addMarker(Markers markers){
        queue.add(markers);
    }

    /**
     * This method add array markers from the queue
     */
    public void addMarker(ArrayList<Markers> markers){
        for (Markers item:
             markers) {
            queue.add(item);
        }
    }

    /**
     * This method return the marker from the queue in the current position
     *
     * @return markers
     */
    public Markers getMarker(){
        return queue.get(pointer);
    }

    /**
     * This method return the marker from the queue at a given position
     *
     * @return markers
     */
    public Markers getMarker(int pos){
        return queue.get(pos);
    }

    /**
     * This method return all markers in queue
     *
     * @return array markers
     */
    public ArrayList<Markers> getAllMarkers(){
        return queue;
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
     * This method increment pointer value and return pointer
     *
     * @return pointer
     */
    public Markers nextMarker(){
        pointer++;
        return getMarker();
    }

    /**
     * This method increment pointer value and return pointer
     *
     * @return pointer
     */
    public int next(){
        pointer++;
        return pointer;
    }

    /**
     * This method decrement pointer value and return pointer
     *
     * @return pointer
     */
    public int early(){
        pointer--;
        return pointer;
    }
}
