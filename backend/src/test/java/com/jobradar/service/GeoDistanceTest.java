package com.jobradar.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class GeoDistanceTest {
    @Test void sameCoordinateHasZeroDistance() {
        assertEquals(0.0, GeoDistance.betweenKm(18.5204, 73.8567, 18.5204, 73.8567), 0.0001);
    }
    @Test void distanceIsSymmetric() {
        double puneToMumbai = GeoDistance.betweenKm(18.5204, 73.8567, 19.0760, 72.8777);
        double mumbaiToPune = GeoDistance.betweenKm(19.0760, 72.8777, 18.5204, 73.8567);
        assertEquals(puneToMumbai, mumbaiToPune, 0.0001);
        assertTrue(puneToMumbai > 100 && puneToMumbai < 200);
    }
}