package com.jstream.scraper.samftp.movie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.Optional;

public class Utils {

    public static String resolveUrl(String base, String href) {
        if (href.startsWith("http")) return href;
        if (href.startsWith("/")) {
            // Extracts http://172.16.50.X from the base URL
            String host = base.substring(0, base.indexOf("/", 7));
            return host + href;
        }
        return base + (base.endsWith("/") ? "" : "/") + href;
    }

    public static Optional<String> scanForVideo(String folderUrl) {
        try {
            Document doc = Jsoup.connect(folderUrl).timeout(3000).get();
            for (Element link : doc.select("a[href]")) {
                String href = link.attr("href");
                // Regex checks for video extensions
                if (href.matches("(?i).*\\.(mp4|mkv|avi)$")) {
                    String fullUrl = resolveUrl(folderUrl, href);
                    System.out.println("-> [SamFTP] Found File: " + fullUrl);
                    return Optional.of(fullUrl);
                }
            }
        } catch (IOException ignored) {}
        return Optional.empty();
    }
}