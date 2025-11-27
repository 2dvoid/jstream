package com.jstream.scraper.samftp.movie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SouthDubbed implements MovieScraper {

    private static final String URL = "http://172.16.50.14/DHAKA-FLIX-14/SOUTH%20INDIAN%20MOVIES/Hindi%20Dubbed";
    private static final Pattern BEFORE_PATTERN = Pattern.compile("(\\d{4})\\s*&\\s*Before", Pattern.CASE_INSENSITIVE);
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d{4})\\s*(?:-|to)\\s*(\\d{4})", Pattern.CASE_INSENSITIVE);

    @Override
    public Optional<String> search(Pattern queryPattern, int targetYear) {
        try {
            Document rootDoc = Jsoup.connect(URL).timeout(3000).get();
            String targetFolderUrl = null;

            for (Element link : rootDoc.select("a[href]")) {
                String text = link.text();
                if (!link.attr("href").endsWith("/")) continue;

                // 1. Standard Year Match "(2020)"
                if (text.contains("(" + targetYear + ")")) {
                    targetFolderUrl = Utils.resolveUrl(URL, link.attr("href"));
                    break;
                }
                // 2. "Before" Bucket Match
                Matcher before = BEFORE_PATTERN.matcher(text);
                if (before.find() && targetYear <= Integer.parseInt(before.group(1))) {
                    targetFolderUrl = Utils.resolveUrl(URL, link.attr("href"));
                    break;
                }
                // 3. "Range" Bucket Match
                Matcher range = RANGE_PATTERN.matcher(text);
                if (range.find()) {
                    int start = Integer.parseInt(range.group(1));
                    int end = Integer.parseInt(range.group(2));
                    if (targetYear >= start && targetYear <= end) {
                        targetFolderUrl = Utils.resolveUrl(URL, link.attr("href"));
                        break;
                    }
                }
            }

            if (targetFolderUrl == null) return Optional.empty();

            // Scan inside the found folder for the movie
            Document yearDoc = Jsoup.connect(targetFolderUrl).timeout(3000).get();
            for (Element link : yearDoc.select("a[href]")) {
                if (queryPattern.matcher(link.text()).matches() && link.attr("href").endsWith("/")) {
                    return Utils.scanForVideo(Utils.resolveUrl(targetFolderUrl, link.attr("href")));
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}