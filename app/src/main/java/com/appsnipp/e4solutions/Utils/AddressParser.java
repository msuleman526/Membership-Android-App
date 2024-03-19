package com.appsnipp.e4solutions.Utils;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressParser {

    static Activity activity;

    public static void getAddresses(Activity activity) {
        AddressParser.activity = activity;
        String[] addresses = {
                "3 albatross Close, Craigieburn, Vic, 3064",
                "3 albatross Close, Craigieburn, Vic 3064",
                "3 albatross Close Craigieburn, Vic 3064",
                "3 albatross Close. Craigieburn, Vic 3064",
                "3 albatross Close. Craigieburn. Vic 3064",
                "3 albatross Close Craigieburn Vic 3064",
                "3064 3 albatross Close, Craigieburn, Vic",
                "3 albatross Close, Craigieburn hello, Vic 3064"
        };

        for (String address : addresses) {
            parseAddress(address);
        }
    }

    private static void parseAddress(String address) {
        Geocoder geocoder;
        geocoder = new Geocoder(activity);

        try {
            List<Address> results = geocoder.getFromLocationName(address, 1);
            if (results != null && results.size() > 0) {
                String addres= results.get(0).getAddressLine(0);
                String[] addressParts = addres.split(","); // Split the address by comma
                if (addressParts.length > 0) {
                    addres =  addressParts[0].trim(); // Get the first part, which should be the street address
                }
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = results.get(0).getLocality();
                String statee = results.get(0).getAdminArea();
                String zipCode = results.get(0).getPostalCode();
                String country = results.get(0).getCountryName();

                System.out.println("Street: " + addres);
                System.out.println("City: " + city);
                System.out.println("State: " + statee);
                System.out.println("Zip Code: " + zipCode);
                System.out.println("Country: " + country);
                System.out.println("Address not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
