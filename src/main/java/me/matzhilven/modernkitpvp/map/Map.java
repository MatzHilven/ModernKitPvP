package me.matzhilven.modernkitpvp.map;

import me.matzhilven.modernkitpvp.utils.Region;
import org.bukkit.Location;

public abstract class Map {

    private final String id;
    private String name;
    private Location spawnPoint;
    private Region region;

    public Map(String id) {
        this(id, null, null, null);
    }

    public Map(String id, String name, Location spawnPoint, Region region) {
        this.id = id;
        this.name = name;
        this.spawnPoint = spawnPoint;
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
