package me.raryk.commandmysql.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class CommandMySQL extends JavaPlugin {
    private static CommandMySQL main;
    private static ConfigSettings configSettings;
    private static MySQLStorage storage;
    HashMap<Object, Object> query;

    @Override
    public void onEnable() {
        main = this;

        configSettings = new ConfigSettings();
        configSettings.loadConfig();

        storage = new MySQLStorage();

        if(storage.isConnect()) {
            storage.execute("CREATE TABLE IF NOT EXISTS `" + this.pathConfig("storage_mysql.table_commands") + "` ( `" + this.pathConfig("storage_mysql.column_commands.id") + "` int(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, `" + this.pathConfig("storage_mysql.column_commands.status") + "` int(1) DEFAULT NULL, `" + this.pathConfig("storage_mysql.column_commands.server") + "` varchar(128) DEFAULT NULL, `" + this.pathConfig("storage_mysql.column_commands.player") + "` varchar(128) DEFAULT NULL, `" + this.pathConfig("storage_mysql.column_commands.command") + "` text ) ENGINE=MyISAM DEFAULT CHARSET=utf8");

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                query = storage.query("SELECT * FROM " + this.pathConfig("storage_mysql.table_commands") + " WHERE " + this.pathConfig("storage_mysql.column_commands.status") + " = '1' AND " + this.pathConfig("storage_mysql.column_commands.server") + " = '" + storage.escape(this.pathConfig("server")) + "' AND " + this.pathConfig("storage_mysql.column_commands.player") + " IS NULL");

                if ((int) query.get("num_rows") >= 1) {
                    storage.execute("UPDATE " + this.pathConfig("storage_mysql.table_commands") + " SET " + this.pathConfig("storage_mysql.column_commands.status") + " = 0 WHERE " + this.pathConfig("storage_mysql.column_commands.id") + " = '" + storage.escape(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.id"))) + "'");

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.command"))));
                } else {
                    query = storage.query("SELECT * FROM " + this.pathConfig("storage_mysql.table_commands") + " WHERE " + this.pathConfig("storage_mysql.column_commands.status") + " = '1' AND " + this.pathConfig("storage_mysql.column_commands.server") + " = '" + storage.escape(this.pathConfig("server")) + "' AND " + this.pathConfig("storage_mysql.column_commands.player") + " IS NOT NULL");

                    if ((int) query.get("num_rows") >= 1 && isOnline(String.valueOf(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.player"))))) {
                        storage.execute("UPDATE " + this.pathConfig("storage_mysql.table_commands") + " SET " + this.pathConfig("storage_mysql.column_commands.status") + " = 0 WHERE " + this.pathConfig("storage_mysql.column_commands.id") + " = '" + storage.escape(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.id"))) + "'");

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.command"))));
                    }
                }
            }, 0, 20L);
        } else {
            this.getLogger().info("Please connection to MySQL");
        }
    }

    public static CommandMySQL plugin() {
        return main;
    }

    public static String pathConfig(String path) {
        return configSettings.getConfig().getString(path);
    }

    public boolean isOnline(String player) {
        Player p = Bukkit.getPlayer(player);
        if (p != null && p.isOnline()) {
            return true;
        } else {
            return false;
        }
    }
}