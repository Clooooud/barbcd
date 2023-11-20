package io.github.clooooud.barbcd;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MainEntry {

    public static void main(String[] args) throws IOException {
        System.out.println("Version: " + MainEntry.class.getPackage().getImplementationVersion());

        if (hasInternet()) {
            InputStream in = new URL("https://github.com/Clooooud/barbcd/releases/latest/download/VERSION").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String latestVersion = reader.readLine();
            reader.close();

            System.out.println("Target Version: " + latestVersion);
            if (!new File("Updater.jar").exists()) {
                Files.copy(new URL("https://github.com/Clooooud/barbcd/releases/latest/download/Updater.jar").openStream(), Paths.get("Updater.jar"), StandardCopyOption.REPLACE_EXISTING);
            }

            if (!latestVersion.equals(MainEntry.class.getPackage().getImplementationVersion())) {
                System.out.println("An update has been found, please wait for the update to finish. The app will restart.");
                ProcessBuilder processBuilder = new ProcessBuilder("./jre/bin/java.exe", "-jar", "-Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1", "Updater.jar", "https://github.com/Clooooud/barbcd/releases/latest/download/barbcd.jar:end:barbcd.jar");
                processBuilder.inheritIO();
                processBuilder.start();
                System.exit(0);
            }
        }

        BarBCD.main(args);
    }

    public static boolean hasInternet() {
        return new InetSocketAddress("google.com", 80).getAddress() != null;
    }
}
