package com.jstream.handler;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import com.jstream.services.MediaPlayer;
import com.jstream.core.ScraperProvider;

/** Handles all UI and logic for searching for a Movie. It implements the SearchHandler contract. */
public class MovieHandler implements SearchHandler {

    // These are the "tools" this handler needs to do its job.
    // They are passed in from the main app.
    private final Scanner in;
    private final List<ScraperProvider> providers;
    private final MediaPlayer player;

    /** Constructor to "inject" the shared tools. */
    public MovieHandler(Scanner scanner, List<ScraperProvider> providers, MediaPlayer player) {
        this.in = scanner;
        this.providers = providers;
        this.player = player;
    }

    @Override
    public void search() {
        System.out.print("Enter movie name: ");
        String query = in.nextLine().trim();

        if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            return;
        }

        // Prompt user for manual Release Year entry
        System.out.print("Enter Release Year [Optional][Press Enter to skip]: ");
        String yearInput = in.nextLine().trim();

        // Treat empty string as null so scrapers know to use OMDb
        String year = yearInput.isEmpty() ? null : yearInput;

        //System.out.println("Searching all providers for: " + query + (year != null ? " (" + year + ")" : ""));

        // Search all providers for the movie
        for (ScraperProvider provider : providers) {

            Optional<String> streamUrl = provider.searchMovie(query,year);

            if (streamUrl.isPresent()) {
                //System.out.println("Success! Found a result.");
                System.out.println("URL: " + streamUrl.get());
                player.play(streamUrl.get());
                return; // Stop searching and return to main menu
            }
        }

        System.out.println(">>> No results found for '" + query + "' on any provider.");
    }
}