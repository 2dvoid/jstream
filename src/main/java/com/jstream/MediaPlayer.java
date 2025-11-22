package com.jstream;

import java.io.IOException;

/**
 * Handles launching specific media players based on the OS.
 * Linux -> mpv
 * Windows -> vlc
 * Mac -> vlc
 */

public class MediaPlayer {

    public void play(String streamUrl) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb = new ProcessBuilder();

        if (os.contains("win")) {
            // Windows: Use 'start vlc'.
            // This works if VLC is in the PATH or registered in the system.
            pb.command("cmd.exe", "/c", "start", "vlc", streamUrl);
        } else if (os.contains("mac")) {
            // macOS: Use 'open -a vlc'.
            // This tells macOS to find the VLC app in /Applications and open the URL.
            pb.command("open", "-a", "vlc", streamUrl);
        } else {
            // Linux: Use 'mpv' as requested.
            pb.command("mpv", streamUrl);
        }

        try {
            
            // Disconnect the I/O so the player runs even when jStream exits.

            // Disconnect input
            pb.redirectInput(ProcessBuilder.Redirect.PIPE);

            // Discard output/errors 
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);

            // Start
            pb.start();

            System.out.println("Player launched successfully!");
            System.out.println("Exiting jStream... ");

            // Kill jStream immediately.
            System.exit(0);

        } catch (IOException e) {
            System.err.println(">>> Error launching player!");
            if (os.contains("win")) {
                System.err.println(">>> Make sure VLC is installed. You might need to add it to your System PATH.");
            } else if (os.contains("mac")) {
                System.err.println(">>> Make sure VLC is installed in your /Applications folder.");
            } else {
                System.err.println(">>> Make sure 'mpv' is installed.");
            }
            System.err.println("Error details: " + e.getMessage());
        }
    }
}