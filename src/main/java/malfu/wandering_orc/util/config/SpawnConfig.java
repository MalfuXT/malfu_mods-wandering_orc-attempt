package malfu.wandering_orc.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SpawnConfig {
    private static final String CONFIG_FILE_NAME = "wandering_orc_spawn_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static boolean groupSpawning = true;
    public static int groupSpawnDelay = 20;
    public static float naturalSpawnRate = 0.05f;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);

                boolean updated = false;

                if (data.groupSpawning == null) {
                    data.groupSpawning = true;
                    updated = true;
                }
                if (data.groupSpawnDelay == null || data.groupSpawnDelay < 1 || data.groupSpawnDelay > 60) {
                    data.groupSpawnDelay = 15;
                    updated = true;
                }

                if (data.naturalSpawnRate == null || data.naturalSpawnRate < 0.01f || data.naturalSpawnRate > 1.0f) {
                    data.naturalSpawnRate = 0.05f;
                    updated = true;
                }

                groupSpawning = data.groupSpawning;
                groupSpawnDelay = data.groupSpawnDelay;
                naturalSpawnRate = data.naturalSpawnRate;

                if (updated) {
                    saveConfig(configDir);
                }
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + e.getMessage());
            }
        } else {
            saveConfig(configDir);
        }
    }

    public static void saveConfig(File configDir) {
        File configFile = new File(configDir, CONFIG_FILE_NAME);
        ConfigData data = new ConfigData(groupSpawning, groupSpawnDelay, naturalSpawnRate);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Boolean groupSpawning;
        Integer groupSpawnDelay;
        Float naturalSpawnRate;

        ConfigData(boolean groupSpawning, int groupSpawnDelay, float naturalSpawnRate) {
            this.groupSpawning = groupSpawning;
            this.groupSpawnDelay = groupSpawnDelay;
            this.naturalSpawnRate = naturalSpawnRate;
        }
    }
}