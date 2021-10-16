package me.raryk.commandmysql.bungee;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

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
                InputStream in = CommandMySQL.plugin().getResourceAsStream("config.yml");
                Files.copy(in, new File(CommandMySQL.plugin().getDataFolder(), "config.yml").toPath());
            }

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(CommandMySQL.plugin().getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}