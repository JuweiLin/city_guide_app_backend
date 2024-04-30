package com.yourcompany.app;

import org.springframework.stereotype.Component;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Component
public class RequestHandler {

    private final GeoApiContext context;

    public RequestHandler() {
        this.context = new GeoApiContext.Builder()
            .apiKey("AIzaSyC7SRSipgUCOxnWLsE6Qo0P6YbkB7JJpUM") // Ensure this API key is securely managed
            .build();
    }

    public PlacesSearchResponse getNearbyPlaces(double latitude, double longitude, int radius) throws ApiException, InterruptedException, java.io.IOException {
        LatLng location = new LatLng(latitude, longitude);
        return PlacesApi.nearbySearchQuery(context, location).radius(radius).await();
    }

    public String getSecureData(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return "Access granted for UID: " + decodedToken.getUid();
    }
}
