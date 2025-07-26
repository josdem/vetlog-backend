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

import com.josdem.vetlog.model.Location;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/geolocation")
public class LocationController {

    public static final String DOMAIN = "vetlog.org";

    @Value("${geoToken}")
    private String geoToken;

    @Getter
    private final ConcurrentHashMap<Long, Location> petLocations = new ConcurrentHashMap<>();

    @PostMapping("/storeLocation")
    public ResponseEntity<String> storeLocation(
            @RequestHeader("token") String token,
            @RequestBody LocationRequest locationRequest,
            HttpServletResponse response) {

        response.addHeader("Access-Control-Allow-Methods", "POST");
        response.addHeader("Access-Control-Allow-Origin", DOMAIN);

        if (!geoToken.equals(token)) {
            return new ResponseEntity<>("FORBIDDEN", HttpStatus.FORBIDDEN);
        }

        log.info("Storing geolocation for pets: {}", locationRequest);

        Location location = new Location(locationRequest.lat, locationRequest.lng);
        locationRequest.petIds.forEach(petId -> {
            petLocations.put(petId, location);
        });

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @Data
    public static class LocationRequest {
        private double lat;
        private double lng;
        private List<Long> petIds;
    }
}
