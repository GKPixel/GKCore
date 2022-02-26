package com.gkpixel.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class GKPhysics {

    public GKPhysics() {


    }

    /**
     * Gets a location with a specified distance away from the left side of a
     * location.
     *
     * @param location The origin location
     * @param distance The distance to the left
     * @return the location of the distance to the left
     */
    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    /**
     * Returns a location with a specified distance away from the right side of
     * a location.
     *
     * @param location The origin location
     * @param distance The distance to the right
     * @return the location of the distance to the right
     */
    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getFront(Location location, double distance) {
        float angle = (location.getYaw() / 60) - 1.5f;
        float pitch = location.getPitch() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), Math.tan(pitch), Math.sin(angle)).normalize().multiply(distance));
    }

    public static Vector rotateZ(Vector vector, double angle) {

        //normalize(vector); // No  need to normalize, vector is already ok...

        double oldX = vector.getX();

        vector.setX((vector.getX() * Math.cos(angle) - vector.getY() * Math.sin(angle)));

        vector.setY((float) (oldX * Math.sin(angle) + vector.getY() * Math.cos(angle)));
        return vector;

    }

    public static Vector vector3_difference(Vector v1, Vector v2) {
        return new Vector(
                v1.getX() - v2.getX(),
                v1.getY() - v2.getY(),
                v1.getZ() - v2.getZ());
		/*if(v1!=null && v2!=null) {
			return  new Vector(
					  v1.getX() - v2.getX(),
					  v1.getY() - v2.getY(),
					  v1.getZ() - v2.getZ());
		}else {
			return new Vector(0,0,0);
		}*/
    }

    public static Vector vector3_abs(Vector v) {
        return new Vector(Math.abs(v.getX()), Math.abs(v.getY()), Math.abs(v.getZ()));
    }

    public static double vector3_distance(Vector v1, Vector v2) {
        if (v1 != null && v2 != null) {
            Vector difference = vector3_difference(v1, v2);
            double distance = Math.sqrt(
                    Math.pow(difference.getX(), 2f) +
                            Math.pow(difference.getY(), 2f) +
                            Math.pow(difference.getZ(), 2f));
            return distance;
        }
        return 0;
    }

    public static Vector vector_face(Location from, Location to) {
        return vector_face(from.toVector(), to.toVector());
    }

    public static Vector vector_face(Vector from, Vector to) {
        Vector distance = new Vector(from.getX(), from.getY(), from.getZ()).subtract(new Vector(to.getX(), to.getY(), to.getZ()));


        //Don't actually need to call normalize for directionA - just doing it to indicate
        //that this vector must be normalized.
        final Vector directionA = new Vector(0, 1, 0).normalize();
        final Vector directionB = distance.clone().normalize();

        float rotationAngle = (float) Math.acos(directionA.dot(directionB));

        Vector rotationAxis = directionA.clone().getCrossProduct(directionB).normalize();
        rotationAxis = new Vector(rotationAxis.getX(), rotationAxis.getY(), rotationAxis.getZ());
        return rotationAxis;
        //rotate object about rotationAxis by rotationAngle
    }

    public static Vector LocationVectorFromString(String s) {
        final String[] parts = s.split("=");
        final double x = Double.parseDouble(parts[2].replace(",y", ""));
        final double y = Double.parseDouble(parts[3].replace(",z", ""));
        final double z = Double.parseDouble(parts[4].replace(",pitch", ""));
        return new Vector(x, y, z);
    }

    public static World WorldFromString(String s) {
        final String[] parts = s.split("=");
        final String worldString = parts[2].replace("},x", "");
        World world = Bukkit.getWorld(worldString);
        return world;
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        angle = Math.toRadians(angle);
        double y, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Location rotateAroundAxisX(Location loc, double angle) {
        Vector newVector = rotateAroundAxisX(loc.toVector(), angle);
        loc.setX(newVector.getX());
        loc.setY(newVector.getY());
        loc.setZ(newVector.getZ());
        return loc;
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        double x, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Location rotateAroundAxisY(Location loc, double angle) {
        Vector newVector = rotateAroundAxisY(loc.toVector(), angle);
        loc.setX(newVector.getX());
        loc.setY(newVector.getY());
        loc.setZ(newVector.getZ());
        return loc;
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        angle = Math.toRadians(angle);
        double x, y, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public static Location rotateAroundAxisZ(Location loc, double angle) {
        Vector newVector = rotateAroundAxisZ(loc.toVector(), angle);
        loc.setX(newVector.getX());
        loc.setY(newVector.getY());
        loc.setZ(newVector.getZ());
        return loc;
    }

}