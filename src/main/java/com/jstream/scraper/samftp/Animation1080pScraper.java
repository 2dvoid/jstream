package com.jstream.scraper.samftp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;
import java.util.regex.Pattern;

class Animation1080pScraper extends SamBaseScraper {

    Animation1080pScraper(String baseUrl) {
        super(baseUrl);
    }

    @Override
    Optional<String> search(Pattern queryPattern, int targetYear) {
        try {
            // 1. Connect to the Root URL (No year folders here)
            Document doc = Jsoup.connect(baseUrl).timeout(3000).get();

            // 2. Scan ALL folders in the list
            for (Element link : doc.select("a[href]")) {
                String text = link.text();

                // Must be a folder
                if (!link.attr("href").endsWith("/")) continue;

                // STRICT CHECK:
                // A. Name must match the Regex (Word Boundary)
                // B. Text must contain the Year string (to differentiate Aladdin 1992 vs 2019)
                if (queryPattern.matcher(text).matches() && text.contains(String.valueOf(targetYear))) {

                    // Found it! Go inside and find the video file.
                    return scanForVideoFile(resolveUrl(baseUrl, link.attr("href")));
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}