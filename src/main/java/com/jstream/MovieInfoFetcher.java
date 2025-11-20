package com.jstream;

import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class to fetch movie metadata (like Release Year) from OMDb.
 */
public class MovieInfoFetcher {

    // REPLACE THIS WITH YOUR REAL KEY FROM EMAIL
    private static final String API_KEY = "bc97f96c";
    private static final String API_URL = "http://www.omdbapi.com/?apikey=" + API_KEY + "&t=";

    /**
     * Fetches the release year for a given movie name.
     * @return The year as a String (e.g., "2020"), or null if not found.
     */
    public String fetchYear(String movieName) {
        try {
            // 1. Fetch the raw JSON text from OMDb
            String jsonResponse = Jsoup.connect(API_URL + movieName)
                    .ignoreContentType(true) // Crucial: tells Jsoup to accept JSON
                    .execute()
                    .body();

            // 2. Use Regex to find "Year":"1999"
            Pattern pattern = Pattern.compile("\"Year\"\\s*:\\s*\"(\\d{4})\"");
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                return matcher.group(1); // Returns the 4-digit year
            }
        } catch (IOException e) {
            System.err.println("Error fetching data from OMDb: " + e.getMessage());
        }
        return null; // Return null if we fail
    }
}