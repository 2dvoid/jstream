package com.jstream.scraper.samftp.movie;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.Optional;
import java.util.regex.Pattern;

public class Kolkata implements MovieScraper {

    private static final String URL = "http://172.16.50.7/DHAKA-FLIX-7/Kolkata%20Bangla%20Movies";

    @Override
    public Optional<String> search(Pattern queryPattern, int targetYear) {
        try {
            Document rootDoc = Jsoup.connect(URL).timeout(3000).get();
            String yearFolderUrl = null;
            for (Element link : rootDoc.select("a[href]")) {
                if (link.attr("href").endsWith("/") && link.text().contains("(" + targetYear + ")")) {
                    yearFolderUrl = Utils.resolveUrl(URL, link.attr("href"));
                    break;
                }
            }
            if (yearFolderUrl == null) return Optional.empty();
            Document yearDoc = Jsoup.connect(yearFolderUrl).timeout(3000).get();
            for (Element link : yearDoc.select("a[href]")) {
                if (queryPattern.matcher(link.text()).matches() && link.attr("href").endsWith("/")) {
                    return Utils.scanForVideo(Utils.resolveUrl(yearFolderUrl, link.attr("href")));
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}