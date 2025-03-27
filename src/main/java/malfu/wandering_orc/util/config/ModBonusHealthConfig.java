package malfu.wandering_orc.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModBonusHealthConfig {
    private static final String CONFIG_FILE_NAME = "wandering_orc_bonus_health_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration properties with defaults
    public static int orcChampionBonusHealth = 10;
    public static int orcWarriorBonusHealth = 7;
    public static int orcArcherBonusHealth = 6;
    public static int minotaurBonusHealth = 20;
    public static int trollBonusHealth = 7;
    public static int trollDoctorBonusHealth = 7;
    public static int orcWarlockBonusHealth = 7;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                boolean updated = false;

                if (data.orcChampionBonusHealth == null || data.orcChampionBonusHealth > 100 || data.orcChampionBonusHealth < 0) {
                    data.orcChampionBonusHealth = 10;
                    updated = true;
                }
                if (data.orcWarriorBonusHealth == null || data.orcWarriorBonusHealth > 100 || data.orcWarriorBonusHealth < 0) {
                    data.orcWarriorBonusHealth = 7;
                    updated = true;
                }
                if (data.orcArcherBonusHealth == null || data.orcArcherBonusHealth > 100 || data.orcArcherBonusHealth < 0) {
                    data.orcArcherBonusHealth = 6;
                    updated = true;
                }
                if (data.minotaurBonusHealth == null || data.minotaurBonusHealth > 100 || data.minotaurBonusHealth < 0) {
                    data.minotaurBonusHealth = 25;
                    updated = true;
                }
                if (data.trollBonusHealth == null || data.trollBonusHealth > 100 || data.trollBonusHealth < 0) {
                    data.trollBonusHealth = 7;
                    updated = true;
                }
                if (data.trollDoctorBonusHealth == null || data.trollDoctorBonusHealth > 100 || data.trollDoctorBonusHealth < 0) {
                    data.trollDoctorBonusHealth = 7;
                    updated = true;
                }
                if (data.orcWarlockBonusHealth == null || data.orcWarlockBonusHealth > 100 || data.orcWarlockBonusHealth < 0) {
                    data.orcWarlockBonusHealth = 7;
                    updated = true;
                }

                orcChampionBonusHealth = data.orcChampionBonusHealth;
                orcWarriorBonusHealth = data.orcWarriorBonusHealth;
                orcArcherBonusHealth = data.orcArcherBonusHealth;
                minotaurBonusHealth = data.minotaurBonusHealth;
                trollBonusHealth = data.trollBonusHealth;
                trollDoctorBonusHealth = data.trollDoctorBonusHealth;
                orcWarlockBonusHealth = data.orcWarlockBonusHealth;

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
        ConfigData data = new ConfigData(orcChampionBonusHealth, orcWarriorBonusHealth, orcArcherBonusHealth, minotaurBonusHealth,
                trollBonusHealth, trollDoctorBonusHealth, orcWarlockBonusHealth);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Integer orcChampionBonusHealth;
        Integer orcWarriorBonusHealth;
        Integer orcArcherBonusHealth;
        Integer minotaurBonusHealth;
        Integer trollBonusHealth;
        Integer trollDoctorBonusHealth;
        Integer orcWarlockBonusHealth;

        ConfigData(int orcChampionBonusHealth, int orcWarriorBonusHealth, int orcArcherBonusHealth, int minotaurBonusHealth,
                   int trollBonusHealth, int trollDoctorBonusHealth, int orcWarlockBonusHealth) {

            this.orcChampionBonusHealth = orcChampionBonusHealth;
            this.orcWarriorBonusHealth = orcWarriorBonusHealth;
            this.orcArcherBonusHealth = orcArcherBonusHealth;
            this.minotaurBonusHealth = minotaurBonusHealth;
            this.trollBonusHealth = trollBonusHealth;
            this.trollDoctorBonusHealth = trollDoctorBonusHealth;
            this.orcWarlockBonusHealth = orcWarlockBonusHealth;
        }
    }
}
