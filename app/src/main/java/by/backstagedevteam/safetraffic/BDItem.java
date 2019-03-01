package by.backstagedevteam.safetraffic;

import com.yandex.mapkit.geometry.Point;
/**
 *
 * OLD CLASS
 */
public class BDItem {
    public static int counter=0;

    private static final int DEFAULT_RADIUS = 2;

    private int id;
    private Point location;
    private double radius;
    private String hint;
    

    public BDItem(Point location){
        this.id = counter++;
        this.location = location;
        this.radius = DEFAULT_RADIUS;
        this.hint = "Marker " + this.id;
    }

    public int getId(){
        return id;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
