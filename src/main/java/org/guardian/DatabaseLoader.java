package org.guardian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.guardian.util.BukkitUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DatabaseLoader {

    public static DatabaseBridge loadBridge(File file) {
        DatabaseBridge theBridge = null;
        PluginDescriptionFile bridgeDescription = null;
        try {
            // Load the jar
            JarFile jar = new JarFile(file);
            // Load the info about the bridge
            JarEntry entry = jar.getJarEntry("bridge.yml");
            InputStream stream = jar.getInputStream(entry);
            bridgeDescription = new PluginDescriptionFile(stream);
            // Get the main class
            String main = bridgeDescription.getMain();
            try {
                URL test = new URL("http://guardian.nekotech.tk:8080/job/Guardian-MySQL/Guardian-MySQL-RB/api/json");
                HttpURLConnection connection = (HttpURLConnection) test.openConnection();
                connection.connect();
                JSONObject object = (JSONObject) new JSONParser().parse(new InputStreamReader(connection.getInputStream()));
                String version = bridgeDescription.getVersion();
                if (!version.equals("Unknown") && Integer.parseInt(version) < Integer.parseInt(object.get("number").toString())) {
                    BukkitUtils.info("Guardian-Bridge is out of date, please download the latest");
                }
            } catch (Exception ex) {
                BukkitUtils.severe("Error occurred while checking if Guardian is up to date");
            }
            // Clean it all up
            stream.close();
            jar.close();
            // Get a new classloader
            URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, DatabaseBridge.class.getClassLoader());
            // Load the class
            Class<?> clazz = classLoader.loadClass(main);
            // Construct it
            Object object = clazz.newInstance();
            // Verify it
            if (!(object instanceof DatabaseBridge)) {
                return null;
            }
            // Its all good
            theBridge = (DatabaseBridge) object;
        } catch (FileNotFoundException ex) {
            BukkitUtils.severe("Database bridge does not contain a valid bridge.yml");
        } catch (ZipException ex) {
            BukkitUtils.severe("The database bridge appears to be an invalid jar file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Guardian.getInstance().getConf().bridgeDescription = bridgeDescription;
        BukkitUtils.info("Loading " + bridgeDescription.getName() + " v" + bridgeDescription.getVersion());
        // Lets return it
        return theBridge;
    }
}
