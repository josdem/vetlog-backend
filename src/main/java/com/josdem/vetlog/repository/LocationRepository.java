/*
  Copyright 2025 Jose Morales contact@josdem.io

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

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
