package com.jstream;

import java.util.Optional;

public interface ScraperProvider {

    Optional<String> searchMovie(String query);

}