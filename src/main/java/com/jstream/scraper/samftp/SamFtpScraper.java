package com.jstream.scraper.samftp;

import com.jstream.core.ScraperProvider;
import com.jstream.services.MovieInfoFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class SamFtpScraper implements ScraperProvider {

    private final MovieInfoFetcher infoFetcher;
    private final List<SamBaseScraper> scrapers;

    public SamFtpScraper() {
        this.infoFetcher = new MovieInfoFetcher();
        this.scrapers = new ArrayList<>();
        initializeScrapers();
    }

    private void initializeScrapers() {
        // The URLs are now hidden inside these classes
        scrapers.add(new English1080pScraper());
        scrapers.add(new EnglishNormalScraper());
        scrapers.add(new Animation1080pScraper());
        scrapers.add(new AnimationNormalScraper());
        scrapers.add(new HindiScraper());
        scrapers.add(new SouthMoviesScraper());
        scrapers.add(new KolkataScraper());
        scrapers.add(new DocumentaryScraper());
    }

    @Override
    public Optional<String> searchMovie(String query, String manualYear) {
        String yearStr = (manualYear != null && !manualYear.isEmpty())
                ? manualYear
                : infoFetcher.fetchYear(query);

        if (yearStr == null) return Optional.empty();

        int year = Integer.parseInt(yearStr);
        System.out.println("-> [SamFTP] Target Year: " + year);

        Pattern queryPattern = Pattern.compile("(?i).*\\b" + Pattern.quote(query) + "\\b.*");

        for (SamBaseScraper scraper : scrapers) {
            Optional<String> result = scraper.search(queryPattern, year);
            if (result.isPresent()) return result;
        }

        return Optional.empty();
    }
}