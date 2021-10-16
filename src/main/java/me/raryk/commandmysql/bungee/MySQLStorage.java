package me.raryk.commandmysql.bungee;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MySQLStorage {
    Connection link;
    Statement statement;
    Boolean isConnect;

    public MySQLStorage() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            link = DriverManager.getConnection("jdbc:mysql://" + CommandMySQL.pathConfig("storage_mysql.hostname") + ":" + CommandMySQL.pathConfig("storage_mysql.port") + "/" + CommandMySQL.pathConfig("storage_mysql.database") + "?verifyServerCertificate=false&useSSL=false&useUnicode=true&characterEncoding=utf8", CommandMySQL.pathConfig("storage_mysql.username"), CommandMySQL.pathConfig("storage_mysql.password"));

            statement = link.createStatement();
            statement.execute("SET NAMES 'utf8'");
            statement.execute("SET CHARACTER SET utf8");
            statement.execute("SET CHARACTER_SET_CONNECTION=utf8");
            statement.execute("SET SQL_MODE = ''");

            isConnect = true;
        } catch (ClassNotFoundException | SQLException e) {
            isConnect = false;
        }
    }

    public Boolean isConnect() {
        return this.isConnect;
    }

    public HashMap<Object, Object> query(String sql) {
        try {
            ResultSet result = statement.executeQuery(sql);

            int i = 0;
            ArrayList<HashMap<Object, Object>> data = new ArrayList<>();

            while(result.next()) {
                HashMap<Object, Object> row = new HashMap<>();
                for (int index = 1; index <= result.getMetaData().getColumnCount(); index++) {
                    row.put(result.getMetaData().getColumnName(index), result.getObject(result.getMetaData().getColumnName(index)));
                }

                data.add(i, row);
                i++;
            }

            HashMap<Object, Object> query = new HashMap<>();
            query.put("row", (data.size() > 0 ? data.get(0) : new ArrayList<>()));
            query.put("rows", data);
            query.put("num_rows", i);

            return query;
        } catch (SQLException e) {
            return null;
        }
    }

    public void execute(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object escape(Object value) {
        String string = String.valueOf(value);

        if (string == null) {
            return null;
        }

        if (string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
            return string;
        }

        String clean_string = string;
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("\\n","\\\\n");
        clean_string = clean_string.replaceAll("\\r", "\\\\r");
        clean_string = clean_string.replaceAll("\\t", "\\\\t");
        clean_string = clean_string.replaceAll("\\00", "\\\\0");
        clean_string = clean_string.replaceAll("'", "\\\\'");
        clean_string = clean_string.replaceAll("\\\"", "\\\\\"");

        return clean_string;
    }
}