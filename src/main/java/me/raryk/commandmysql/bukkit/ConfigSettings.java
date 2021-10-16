package me.raryk.commandmysql.bukkit;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigSettings {
    private Configuration config;

    public Configuration getConfig() {
        return config;
    }

    public void loadConfig() {
        try {
            if(!CommandMySQL.plugin().getDataFolder().exists())
                CommandMySQL.plugin().getDataFolder().mkdir();

            if (!new File(CommandMySQL.plugin().getDataFolder(), "config.yml").exists()) {
                InputStream in = CommandMySQL.plugin().getResource("config.yml");
                Files.copy(in, new File(CommandMySQL.plugin().getDataFolder(), "config.yml").toPath());
            }

            config = YamlConfiguration.loadConfiguration(new File(CommandMySQL.plugin().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}