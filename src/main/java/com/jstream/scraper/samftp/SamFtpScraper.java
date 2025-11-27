package com.jstream.scraper.samftp;

import com.jstream.core.ScraperProvider;
import com.jstream.services.MovieInfoFetcher;
import com.jstream.scraper.samftp.movie.*; // Import all movie scrapers and interfaces

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class SamFtpScraper implements ScraperProvider {

    private final MovieInfoFetcher infoFetcher;
    private final List<MovieScraper> scrapers; // List of the 'MovieScraper' interface

    public SamFtpScraper() {
        this.infoFetcher = new MovieInfoFetcher();
        this.scrapers = new ArrayList<>();
        initializeScrapers();
    }

    private void initializeScrapers() {
        // Priority 1: English 1080p
        scrapers.add(new English1080p());

        // Priority 2: English Normal
        scrapers.add(new EnglishNormal());

        // Priority 3: Animation 1080p (Flat)
        scrapers.add(new Animation1080p());

        // Priority 4: Animation Normal
        scrapers.add(new AnimationNormal());

        // Priority 5: Hindi
        scrapers.add(new Hindi());

        // Priority 6: South Dubbed (Bucket Logic)
        scrapers.add(new SouthDubbed());

        // Priority 7: Kolkata
        scrapers.add(new Kolkata());

        // Priority 8: Documentary (Flat)
        scrapers.add(new Documentary());
    }

    @Override
    public Optional<String> searchMovie(String query, String manualYear) {
        // 1. Resolve Year (Manual or OMDb)
        String yearStr = (manualYear != null && !manualYear.isEmpty())
                ? manualYear
                : infoFetcher.fetchYear(query);

        // Safety check: abort if year is missing
        if (yearStr == null) return Optional.empty();

        int year = Integer.parseInt(yearStr);
        System.out.println("-> [SamFTP] Target Year: " + year);

        // 2. Prepare Regex (Whole Word Match, Case Insensitive)
        // This fixes the "Dia" vs "Obsidian" bug
        Pattern queryPattern = Pattern.compile("(?i).*\\b" + Pattern.quote(query) + "\\b.*");

        // 3. Coordinator Loop
        for (MovieScraper scraper : scrapers) {
            System.out.println("-> [SamFTP] Checking: " + scraper.getClass().getSimpleName());

            // Delegate the actual work to the specific scraper class
            Optional<String> result = scraper.search(queryPattern, year);

            if (result.isPresent()) {
                return result; // Found it! Return immediately.
            }
        }

        return Optional.empty();
    }
}