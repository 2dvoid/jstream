package com.jstream.scraper.samftp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SouthMoviesScraper extends SamBaseScraper {

    // Regex specific to South Movies folder structure
    private static final Pattern BEFORE_PATTERN = Pattern.compile("(\\d{4})\\s*&\\s*Before", Pattern.CASE_INSENSITIVE);
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d{4})\\s*(?:-|to)\\s*(\\d{4})", Pattern.CASE_INSENSITIVE);

    SouthMoviesScraper(String baseUrl) {
        super(baseUrl);
    }

    @Override
    Optional<String> search(Pattern queryPattern, int targetYear) {
        try {
            Document rootDoc = Jsoup.connect(baseUrl).timeout(3000).get();
            String targetFolderUrl = null;

            for (Element link : rootDoc.select("a[href]")) {
                String text = link.text();
                if (!link.attr("href").endsWith("/")) continue;

                // 1. Check Standard Year "(2020)"
                if (text.contains("(" + targetYear + ")")) {
                    targetFolderUrl = resolveUrl(baseUrl, link.attr("href"));
                    break;
                }

                // 2. Check "Before" Bucket Logic (e.g. "2009 & Before")
                Matcher before = BEFORE_PATTERN.matcher(text);
                if (before.find() && targetYear <= Integer.parseInt(before.group(1))) {
                    targetFolderUrl = resolveUrl(baseUrl, link.attr("href"));
                    break;
                }

                // 3. Check "Range" Bucket Logic (e.g. "2001-2009")
                Matcher range = RANGE_PATTERN.matcher(text);
                if (range.find()) {
                    int start = Integer.parseInt(range.group(1));
                    int end = Integer.parseInt(range.group(2));
                    if (targetYear >= start && targetYear <= end) {
                        targetFolderUrl = resolveUrl(baseUrl, link.attr("href"));
                        break;
                    }
                }
            }

            if (targetFolderUrl == null) return Optional.empty();

            // Scan inside the found folder
            Document yearDoc = Jsoup.connect(targetFolderUrl).timeout(3000).get();
            for (Element link : yearDoc.select("a[href]")) {
                if (queryPattern.matcher(link.text()).matches() && link.attr("href").endsWith("/")) {
                    return scanForVideoFile(resolveUrl(targetFolderUrl, link.attr("href")));
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}