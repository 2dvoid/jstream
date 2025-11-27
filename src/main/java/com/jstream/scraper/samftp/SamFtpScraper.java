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
        // 1. English 1080p
        scrapers.add(new English1080pScraper("http://172.16.50.14/DHAKA-FLIX-14/English%20Movies%20(1080p)"));

        // 2. English Normal
        scrapers.add(new EnglishNormalScraper("http://172.16.50.7/DHAKA-FLIX-7/English%20Movies"));

        // 3. Animation 1080p
        scrapers.add(new Animation1080pScraper("http://172.16.50.14/DHAKA-FLIX-14/Animation%20Movies%20(1080p)"));

        // 4. Animation Normal
        scrapers.add(new AnimationNormalScraper("http://172.16.50.14/DHAKA-FLIX-14/Animation%20Movies"));

        // 5. Hindi
        scrapers.add(new HindiScraper("http://172.16.50.14/DHAKA-FLIX-14/Hindi%20Movies"));

        // 6. South Dubbed
        scrapers.add(new SouthMoviesScraper("http://172.16.50.14/DHAKA-FLIX-14/SOUTH%20INDIAN%20MOVIES/Hindi%20Dubbed"));

        // 7. Kolkata
        scrapers.add(new KolkataScraper("http://172.16.50.7/DHAKA-FLIX-7/Kolkata%20Bangla%20Movies"));

        // 8. Documentary
        scrapers.add(new DocumentaryScraper("http://172.16.50.9/DHAKA-FLIX-9/Documentary"));
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