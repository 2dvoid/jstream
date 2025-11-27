package com.jstream.scraper.samftp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

abstract class SamBaseScraper {

    protected final String baseUrl;

    SamBaseScraper(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // Every source must implement this specific search logic
    abstract Optional<String> search(Pattern queryPattern, int year);

    // --- SHARED HELPER: Scan a folder for video files ---
    protected Optional<String> scanForVideoFile(String folderUrl) {
        try {
            Document doc = Jsoup.connect(folderUrl).timeout(3000).get();
            for (Element link : doc.select("a[href]")) {
                String href = link.attr("href");
                if (href.matches("(?i).*\\.(mp4|mkv|avi)$")) {
                    String fullUrl = resolveUrl(folderUrl, href);
                    System.out.println("-> [SamFTP] Found File: " + fullUrl);
                    return Optional.of(fullUrl);
                }
            }
        } catch (IOException ignored) {}
        return Optional.empty();
    }

    // --- SHARED HELPER: Handle the multiple IP addresses ---
    protected String resolveUrl(String base, String href) {
        if (href.startsWith("http")) return href;
        if (href.startsWith("/")) {
            // Extract host (e.g. http://172.16.50.14)
            String host = base.substring(0, base.indexOf("/", 7));
            return host + href;
        }
        return base + (base.endsWith("/") ? "" : "/") + href;
    }
}