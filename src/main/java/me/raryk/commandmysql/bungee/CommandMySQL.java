package me.raryk.commandmysql.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public final class CommandMySQL extends Plugin {
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

            ProxyServer.getInstance().getScheduler().schedule(this, () -> {
                query = storage.query("SELECT * FROM " + this.pathConfig("storage_mysql.table_commands") + " WHERE " + this.pathConfig("storage_mysql.column_commands.status") + " = '1' AND " + this.pathConfig("storage_mysql.column_commands.server") + " = '" + storage.escape(this.pathConfig("server")) + "' AND " + this.pathConfig("storage_mysql.column_commands.player") + " IS NULL");

                if ((int) query.get("num_rows") >= 1) {
                    storage.execute("UPDATE " + this.pathConfig("storage_mysql.table_commands") + " SET " + this.pathConfig("storage_mysql.column_commands.status") + " = 0 WHERE " + this.pathConfig("storage_mysql.column_commands.id") + " = '" + storage.escape(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.id"))) + "'");

                    ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), String.valueOf(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.command"))));
                } else {
                    query = storage.query("SELECT * FROM " + this.pathConfig("storage_mysql.table_commands") + " WHERE " + this.pathConfig("storage_mysql.column_commands.status") + " = '1' AND " + this.pathConfig("storage_mysql.column_commands.server") + " = '" + storage.escape(this.pathConfig("server")) + "' AND " + this.pathConfig("storage_mysql.column_commands.player") + " IS NOT NULL");

                    if ((int) query.get("num_rows") >= 1 && isOnline(String.valueOf(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.player"))))) {
                        storage.execute("UPDATE " + this.pathConfig("storage_mysql.table_commands") + " SET " + this.pathConfig("storage_mysql.column_commands.status") + " = 0 WHERE " + this.pathConfig("storage_mysql.column_commands.id") + " = '" + storage.escape(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.id"))) + "'");

                        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), String.valueOf(((HashMap) query.get("row")).get(this.pathConfig("storage_mysql.column_commands.command"))));
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
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
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
        if (p != null && p.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}