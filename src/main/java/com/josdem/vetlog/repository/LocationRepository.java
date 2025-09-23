package com.josdem.vetlog.repository;

import com.hazelcast.core.HazelcastInstance;
import com.josdem.vetlog.model.Location;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationRepository {

  private final HazelcastInstance hazelcastInstance;

  private static final String MAP_NAME = "memory";

  public void save(Long petId, Location location) {
    hazelcastInstance.getMap(MAP_NAME).put(petId, location);
  }

  public Location findByPetId(Long petId) {
    return (Location) hazelcastInstance.getMap(MAP_NAME).get(petId);
  }

  public void saveMultiplePets(List<Long> petIds, Location location) {
    var petsMap = hazelcastInstance.getMap(MAP_NAME);
    petIds.forEach(petId -> petsMap.put(petId, location));
  }

  public Map<Long, Location> findAll() {
    return hazelcastInstance.getMap(MAP_NAME);
  }

  public void deleteAll() {
    hazelcastInstance.getMap(MAP_NAME).clear();
  }

  public void deletePets(List<Long> petIds) {
    var petsMap = hazelcastInstance.getMap(MAP_NAME);
    petIds.forEach(petsMap::remove);
  }
}
