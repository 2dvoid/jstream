package com.jstream;

import com.jstream.core.ScraperProvider;
import com.jstream.scraper.samftp.SamFtpScraper; // Import from scraper package
import com.jstream.services.MediaPlayer;
import com.jstream.handler.MovieHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class JStreamApp {

    private final Scanner in;
    private final List<ScraperProvider> providers;
    private final MediaPlayer player;

    JStreamApp() {
        this.in = new Scanner(System.in);
        this.providers = new ArrayList<>();
        this.player = new MediaPlayer();

        // Add providers according to Priority
        this.providers.add(new SamFtpScraper()); // Priority 1
        //this.providers.add(new IccFtpScrapper()); // Priority 2
    }

    public static void main(String[] args) {
        new JStreamApp().run();
    }

    public void run() {
        //while (true) {
            printMenu();
            int choice = in.nextInt();
            in.nextLine(); // Consumes the newline character left by nextInt()

            switch (choice) {
                case 1:
                    // !!Delegate to Movie Handler;
                    //System.out.println("Delegating to Movie Handler...");
                    new MovieHandler(in, providers, player).search();
                    break;
                case 2:
                    System.out.println("This Feature is not implemented yet.");
                    break;
                case 3:
                    System.out.println("Exiting jStream. Goodbye!");
                    return;
                default:
                    System.out.println(">>> Invalid selection. Please try again.");
            }
        //}
    }

    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("        Welcome to JStream");
        System.out.println("========================================");
        System.out.println("1. Search Movie");
        System.out.println("2. Search TV Series (coming soon)");
        System.out.println("3. Exit");
        System.out.print("\nSelect an option: ");
    }
}