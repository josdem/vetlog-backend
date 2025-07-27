package com.josdem.vetlog.repository;

import com.josdem.vetlog.model.Location;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LocationRepository {
    private final Map<Long, Location> petLocations = new ConcurrentHashMap<>();

    public void save(Long petId, Location location) {
        petLocations.put(petId, location);
    }
    public Location findByPetId(Long petId) {
        return petLocations.get(petId);
    }
    public Map<Long, Location> findAll() {
        return Collections.unmodifiableMap(petLocations);
    }
}
