# What?
jStream is a CLI tool that lets you search and stream movies, TV series, and anime directly from popular BDIX FTP servers.

# Why?
Using raw FTP servers to watch media is a daunting task. First, you need to open a browser and navigate to a specific IP address. Since these servers rarely provide search functionality, you are forced to manually click through folder after folder to find what you need.

If you are lucky enough to find the movie, you then have to choose between downloading a large file or manually copying the link into a media player. If you don't find it, you have to start the whole process over on a different server.

**jStream simplifies this entire workflow.** It acts as a unified search engine for these fragmented servers. You don't need to know IPs, browse directories, or copy links. You just enter the movie media, and jStream finds the best source and starts playing it immediately.

# How to run?
1. Install and setup Java 21 (or newer)
2. Install a supported media player:
	1. MPV for Linux
	2. VLC for Windows
	3. VLC for MacOS
3. Ensure the player executable (vlc or mpv) is added to your system's PATH environment variable so jStream can launch it.
4. Download the the `.jar` file from the latest Release
5. Run the `.jar` file: `java -jar jStreamXXX.jar`
6. Enjoy!
