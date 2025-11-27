package com.jstream.core;

import java.util.Optional;

public interface ScraperProvider {

    /**
     * Searches for a movie.
     * @param query The movie name.
     * @param year  The release year (can be null or empty if unknown).
     * @return Optional containing the stream URL.
     */
    Optional<String> searchMovie(String query, String year);
}