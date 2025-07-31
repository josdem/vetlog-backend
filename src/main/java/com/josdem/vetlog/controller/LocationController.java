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

import com.josdem.vetlog.exception.InvalidTokenException;
import com.josdem.vetlog.model.Location;
import com.josdem.vetlog.repository.LocationRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.josdem.vetlog.command.LocationRequestCommand;
import com.josdem.vetlog.command.PetLocationCommand;

@Slf4j
@RestController
@RequestMapping("/geolocation")
public class LocationController {

    @Value("${app.domain}")
    private String domain;

    @Value("${geoToken}")
    private String geoToken;

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @PostMapping("/storeLocation")
    ResponseEntity<String> storeLocation(
                                           @RequestHeader("token") String token,
                                           @Valid @RequestBody LocationRequestCommand locationRequestCommand,
                                           HttpServletResponse response) {

        response.addHeader("Access-Control-Allow-Methods", "POST");
        response.addHeader("Access-Control-Allow-Origin", domain);

        if (!geoToken.equals(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        log.info("Storing geolocation for pets: {}", locationRequestCommand);

        Location location = new Location(locationRequestCommand.latitude(), locationRequestCommand.longitude());
        locationRequestCommand.petIds().forEach(petId -> locationRepository.save(petId, location));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Location stored successfully");
    }

     @PostMapping("/storePetLocation")
    public ResponseEntity<String> storePets(
                                            @Valid @RequestBody PetLocationCommand pets ,
                                            HttpServletResponse response){
                                              
      log.info("Storing pets: {}", pets.toString());
      response.addHeader("Access-Control-Allow-Methods", "POST");
      response.addHeader("Access-Control-Allow-Origin", domain);

      pets.petsIds().forEach(id -> {
          locationRepository.save(id, new Location(0.00 , 0.00)); 
      });

      return new ResponseEntity<>("Created new pets", HttpStatus.CREATED);
    } 
}
