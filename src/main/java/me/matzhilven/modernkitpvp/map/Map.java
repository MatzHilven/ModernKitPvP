package me.matzhilven.modernkitpvp.map;

import me.matzhilven.modernkitpvp.utils.Region;
import org.bukkit.Location;

public abstract class Map {

    private final String id;
    private String name;
    private Region region;

    public Map(String id) {
        this(id, null, null);
    }

    public Map(String id, String name, Region region) {
        this.id = id;
        this.name = name;
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
