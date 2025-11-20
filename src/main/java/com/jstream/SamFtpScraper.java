package com.jstream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;

public class SamFtpScraper implements ScraperProvider {

    private static final String HOST = "http://172.16.50.14";
    private static final String BASE_PATH = "/DHAKA-FLIX-14";
    private static final String MOVIE_PATH = "/English%20Movies%20(1080p)";

    private final MovieInfoFetcher infoFetcher;

    public SamFtpScraper() {
        this.infoFetcher = new MovieInfoFetcher();
    }

    @Override
    public Optional<String> searchMovie(String query) {
        //System.out.println("-> [SamFTP] Resolving release year for: " + query);

        String year = infoFetcher.fetchYear(query);

        if (year == null) {
            System.out.println("-> [SamFTP] Could not determine release year. Skipping.");
            return Optional.empty();
        }

        // Step 1: Construct the Year Folder URL
        String yearFolderUrl = buildYearUrl(year);

        try {
            // --- HOP 1: Find the Movie Folder inside the Year Folder ---
            Document yearDoc = Jsoup.connect(yearFolderUrl).get();
            Elements yearLinks = yearDoc.select("a[href]");

            String movieFolderUrl = null;

            for (Element link : yearLinks) {
                String text = link.text().toLowerCase();
                String href = link.attr("href");

                // We look for a link that contains the query AND ends with '/' (indicating a folder)
                if (text.contains(query.toLowerCase()) && href.endsWith("/")) {
                    // Found the specific movie folder!
                    // href is usually relative like "/DHAKA-FLIX-14/..." so we prepend HOST
                    movieFolderUrl = HOST + href;
                    //System.out.println("-> [SamFTP] Found Movie Folder: " + text);
                    break;
                }
            }

            if (movieFolderUrl == null) {
                System.out.println("-> [SamFTP] Movie folder not found in year " + year);
                return Optional.empty();
            }

            // --- HOP 2: Find the Video File inside the Movie Folder ---
            //System.out.println("-> [SamFTP] Scanning inside movie folder...");
            Document movieDoc = Jsoup.connect(movieFolderUrl).get();
            Elements fileLinks = movieDoc.select("a[href]");

            for (Element link : fileLinks) {
                String href = link.attr("href");

                // Now we look for the actual video file
                if (isVideoFile(href)) {
                    String fullStreamUrl = HOST + href;
                    //System.out.println("-> [SamFTP] Found File: " + fullStreamUrl);
                    return Optional.of(fullStreamUrl);
                }
            }

        } catch (IOException e) {
            System.err.println("-> [SamFTP] Error scraping: " + e.getMessage());
        }

        return Optional.empty();
    }

    private String buildYearUrl(String yearStr) {
        int year = Integer.parseInt(yearStr);

        // Logic for "1995 & Before"
        if (year <= 1995) {
            return HOST + BASE_PATH + MOVIE_PATH + "/(1995)%201080p%20&%20Before/";
        }

        // Standard folders: "(2020) 1080p"
        return HOST + BASE_PATH + MOVIE_PATH + "/(" + year + ")%201080p/";
    }

    private boolean isVideoFile(String url) {
        String lower = url.toLowerCase();
        return lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".avi");
    }
}