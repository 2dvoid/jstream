package com.jstream.scraper.samftp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;
import java.util.regex.Pattern;

class DocumentaryScraper extends SamBaseScraper {

    private static final String URL = "http://172.16.50.9/DHAKA-FLIX-9/Documentary";

    DocumentaryScraper() {
        super(URL);
    }

    @Override
    Optional<String> search(Pattern queryPattern, int targetYear) {
        try {
            Document doc = Jsoup.connect(baseUrl).timeout(3000).get();

            for (Element link : doc.select("a[href]")) {
                String text = link.text();
                if (!link.attr("href").endsWith("/")) continue;

                // STRICT: Regex Name + Year check
                if (queryPattern.matcher(text).matches() && text.contains(String.valueOf(targetYear))) {
                    return scanForVideoFile(resolveUrl(baseUrl, link.attr("href")));
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}