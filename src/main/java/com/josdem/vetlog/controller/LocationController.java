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

package com.josdem.vetlog.controller;

import com.josdem.vetlog.command.LocationRequestCommand;
import com.josdem.vetlog.command.PetLocationCommand;
import com.josdem.vetlog.exception.InvalidTokenException;
import com.josdem.vetlog.model.Location;
import com.josdem.vetlog.repository.LocationRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/geolocation")
@RequiredArgsConstructor
public class LocationController {

  @Value("${app.domain}")
  private String domain;

  @Value("${geoToken}")
  private String geoToken;

  private final LocationRepository locationRepository;

  private void validateToken(String token) {
    if (token == null) {
      throw new InvalidTokenException("Missing token");
    }
    if (!Objects.equals(geoToken, token)) {
      throw new InvalidTokenException("Invalid token");
    }
  }

  @PostMapping("/storeLocation")
  ResponseEntity<String> storeLocation(
      @RequestHeader(value = "token", required = false) String token,
      @Valid @RequestBody LocationRequestCommand locationRequestCommand,
      HttpServletResponse response) {

    response.addHeader("Access-Control-Allow-Methods", "POST");
    response.addHeader("Access-Control-Allow-Origin", domain);

    validateToken(token);
    log.info("Storing geolocation for pets: {}", locationRequestCommand);

    Location location =
        new Location(locationRequestCommand.latitude(), locationRequestCommand.longitude());
    locationRepository.saveMultiplePets(locationRequestCommand.petIds(), location);

    return ResponseEntity.status(HttpStatus.CREATED).body("Location stored successfully");
  }

  @PostMapping("/storePetLocation")
  public ResponseEntity<String> storePets(
      @Valid @RequestBody PetLocationCommand pets, HttpServletResponse response) {

    log.info("Storing pets: {}", pets);
    response.addHeader("Access-Control-Allow-Methods", "POST");
    response.addHeader("Access-Control-Allow-Origin", domain);

    pets.petsIds().forEach(petId -> locationRepository.save(petId, new Location(0.00, 0.00)));

    return new ResponseEntity<>("Created new pets", HttpStatus.CREATED);
  }

  @DeleteMapping("/removeAll")
  public ResponseEntity<String> deleteAllStoreLocations(
      HttpServletResponse response,
      @RequestHeader(value = "token", required = false) String token) {
    log.info("Deleting all locations");
    response.addHeader("Access-Control-Allow-Methods", "DELETE");
    response.addHeader("Access-Control-Allow-Origin", domain);

    validateToken(token);
    locationRepository.deleteAll();
    return new ResponseEntity<>("Deleted all pet's locations", HttpStatus.OK);
  }

  @DeleteMapping("/storeLocation")
  public ResponseEntity<String> deleteLocationsByPetIds(
      @RequestBody @Valid PetLocationCommand pets,
      HttpServletResponse response,
      @RequestHeader(value = "token", required = false) String token) {

    log.info("Deleting locations for pets: {}", pets);
    response.addHeader("Access-Control-Allow-Methods", "DELETE");
    response.addHeader("Access-Control-Allow-Origin", domain);

    validateToken(token);
    locationRepository.deletePets(pets.petsIds());
    return new ResponseEntity<>(
        "Deleted locations for pets: " + pets.petsIds(), HttpStatus.NO_CONTENT);
  }

  @GetMapping("/storeLocation/{petId}")
  public ResponseEntity<Location> getLocationsByPetId(
      @PathVariable("petId") Long petId,
      HttpServletResponse response,
      @RequestHeader(value = "token", required = false) String token) {

    log.info("Getting location for pet: {}", petId);
    response.addHeader("Access-Control-Allow-Methods", "GET");
    response.addHeader("Access-Control-Allow-Origin", domain);

    validateToken(token);
    var location = locationRepository.findByPetId(petId);
    return new ResponseEntity<>(location, HttpStatus.OK);
  }
}
