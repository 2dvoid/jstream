package com.jstream.scraper.samftp.movie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;
import java.util.regex.Pattern;

public class Animation1080p implements MovieScraper {

    private static final String URL = "http://172.16.50.14/DHAKA-FLIX-14/Animation%20Movies%20(1080p)";

    @Override
    public Optional<String> search(Pattern queryPattern, int targetYear) {
        try {
            Document doc = Jsoup.connect(URL).timeout(3000).get();

            for (Element link : doc.select("a[href]")) {
                String text = link.text();
                if (!link.attr("href").endsWith("/")) continue;

                // STRICT CHECK: Name regex matches AND text contains Year
                if (queryPattern.matcher(text).matches() && text.contains(String.valueOf(targetYear))) {
                    return Utils.scanForVideo(Utils.resolveUrl(URL, link.attr("href")));
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}