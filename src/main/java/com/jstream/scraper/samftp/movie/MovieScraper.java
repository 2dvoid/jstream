package com.jstream.scraper.samftp.movie;

import java.util.Optional;
import java.util.regex.Pattern;

public interface MovieScraper { // Must be public now
    Optional<String> search(Pattern queryPattern, int year);
}
