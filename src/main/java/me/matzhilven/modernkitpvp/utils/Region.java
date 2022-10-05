package me.matzhilven.modernkitpvp.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class Region {

    private Location corner1;
    private Location corner2;

    public Region(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public static Region of(Location corner1) {
        Validate.notNull(corner1, "Corner1 is null!");
        return new Region(corner1, null);
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public boolean contains(Vector position) {
        return contains(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    public boolean contains(Location position) {
        return contains(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    public int getMinX() {
        return Math.min(corner1.getBlockX(), corner2.getBlockX());
    }

    public int getMinY() {
        return Math.min(corner1.getBlockY(), corner2.getBlockY());
    }

    public int getMinZ() {
        return Math.min(corner1.getBlockZ(), corner2.getBlockZ());
    }

    public int getMaxX() {
        return Math.max(corner1.getBlockX(), corner2.getBlockX());
    }

    public int getMaxY() {
        return Math.max(corner1.getBlockY(), corner2.getBlockY());
    }

    public int getMaxZ() {
        return Math.max(corner1.getBlockZ(), corner2.getBlockZ());
    }

    public boolean contains(int x, int y, int z) {
        return x >= getMinX() && x < getMaxX() && y >= getMinY() && y < getMaxY() && z >= getMinZ() && z < getMaxZ();
    }

    public void setCorner(int corner, Location location) {
        if (corner == 1) {
            corner1 = location;
        } else {
            corner2 = location;
        }
    }

    public Location getCenter() {
        return new Location(corner1.getWorld(), getMaxX() - getMinX(), getMaxY() - getMinY(), getMaxZ() - getMinZ());
    }
}
