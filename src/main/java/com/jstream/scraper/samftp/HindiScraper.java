package com.jstream.scraper.samftp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;
import java.util.regex.Pattern;

class HindiScraper extends SamBaseScraper {

    private static final String URL = "http://172.16.50.14/DHAKA-FLIX-14/Hindi%20Movies";

    HindiScraper() {
        super(URL);
    }

    @Override
    Optional<String> search(Pattern queryPattern, int targetYear) {
        try {
            Document rootDoc = Jsoup.connect(baseUrl).timeout(3000).get();
            String yearFolderUrl = null;

            for (Element link : rootDoc.select("a[href]")) {
                if (link.attr("href").endsWith("/") && link.text().contains("(" + targetYear + ")")) {
                    yearFolderUrl = resolveUrl(baseUrl, link.attr("href"));
                    break;
                }
            }

            if (yearFolderUrl == null) return Optional.empty();

            Document yearDoc = Jsoup.connect(yearFolderUrl).timeout(3000).get();
            for (Element link : yearDoc.select("a[href]")) {
                if (queryPattern.matcher(link.text()).matches() && link.attr("href").endsWith("/")) {
                    return scanForVideoFile(resolveUrl(yearFolderUrl, link.attr("href")));
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}