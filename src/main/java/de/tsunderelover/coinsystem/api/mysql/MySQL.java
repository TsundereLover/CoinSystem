package de.tsunderelover.coinsystem.api.mysql;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.tsunderelover.coinsystem.api.events.PlayerCoinsChangeEvent;
import de.tsunderelover.coinsystem.api.events.PlayerPayPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.*;

public class MySQL {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final LoadingCache<UUID, Integer> coinCache = CacheBuilder
            .newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build(new CacheLoader<UUID, Integer>() {
                @Override
                public Integer load(UUID uuid) throws Exception {
                    try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT COINS FROM " + getDatabase() + " WHERE UUID = " + '"' + uuid.toString() + '"')) {
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            int result = -1;

                            if (resultSet.next()) {
                                result = resultSet.getInt("coins");
                            }

                            return result;
                        }
                    }
                }
            });

    private final HikariConfig hikariConfig = new HikariConfig();

    private HikariDataSource dataSource;

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    public void connect() {
        loadMySQLConfig();

        getHikariConfig().setJdbcUrl("jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase());
        getHikariConfig().setUsername(getUsername());
        getHikariConfig().setPassword(getPassword());
        getHikariConfig().setMinimumIdle(5);
        getHikariConfig().setMaximumPoolSize(50);
        getHikariConfig().setConnectionTimeout(10000);
        getHikariConfig().setIdleTimeout(600000);
        getHikariConfig().setMaxLifetime(1800000);
        getHikariConfig().addDataSourceProperty("cachePrepStmts" , "true");
        getHikariConfig().addDataSourceProperty("prepStmtCacheSize" , "100");
        getHikariConfig().addDataSourceProperty("prepStmtCacheSqlLimit" , "50");

        this.dataSource = new HikariDataSource(getHikariConfig());
    }

    public void update(final String update) {
        CompletableFuture.runAsync( () -> {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(update)) {
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, getExecutorService()).handle((aVoid, throwable) -> throwable);
    }

    public void registerPlayer(Player player, int coins) throws ExecutionException, InterruptedException {
        if (!isRegistered(player)) {
            update("INSERT INTO " + getDatabase() + " (PLAYERNAME, UUID, COINS) VALUES(" + '"' + player.getName() + '"' + ", " + '"' + player.getUniqueId().toString() + '"' + ", " + coins + ")");
        }
    }

    public synchronized int getCoins(Player player) throws ExecutionException, InterruptedException {
        return CompletableFuture.supplyAsync( () -> {
            try {
                return this.coinCache.get(player.getUniqueId());

            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return -1;
        }, getExecutorService()).get();
    }

    public synchronized void setCoins(Player player, int amount) {
        CompletableFuture.runAsync( () -> {
            try {
                if (isRegistered(player)) {
                    if (amount <= Integer.MAX_VALUE) {
                        update("UPDATE " + getDatabase() + " SET COINS = " + amount + " WHERE UUID = " + '"' + player.getUniqueId().toString() + '"');
                        Bukkit.getPluginManager().callEvent(new PlayerCoinsChangeEvent(player, amount));
                        this.coinCache.put(player.getUniqueId(), amount);

                    } else {
                        System.out.println("Der ausgerechnete neue Wert ist zu groß!");
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutorService());
    }

    public synchronized void addCoins(Player player, int amount) {
        CompletableFuture.runAsync( () -> {
            try {
                if (isRegistered(player)) {
                    int newAmount = getCoins(player) + amount;
                    if (newAmount <= Integer.MAX_VALUE) {
                        update("UPDATE " + getDatabase() + " SET COINS = " + newAmount + " WHERE UUID = " + '"' + player.getUniqueId().toString() + '"');
                        Bukkit.getPluginManager().callEvent(new PlayerCoinsChangeEvent(player, amount));
                        this.coinCache.put(player.getUniqueId(), newAmount);

                    } else {
                        System.out.println("Der ausgerechnete neue Wert ist zu groß!");
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutorService());
    }

    public synchronized void removeCoins(Player player, int amount) {
        CompletableFuture.runAsync( () -> {
            try {
                if (isRegistered(player)) {
                    int newAmount = getCoins(player) - amount;
                    if (newAmount >= 0) {
                        update("UPDATE " + getDatabase() + " SET COINS = " + newAmount + " WHERE UUID = " + '"' + player.getUniqueId().toString() + '"');
                        Bukkit.getPluginManager().callEvent(new PlayerCoinsChangeEvent(player, amount));
                        this.coinCache.put(player.getUniqueId(), newAmount);

                    } else {
                        System.out.println("Der ausgerechnete neue Wert ist zu klein!");
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutorService());
    }

    public synchronized void pay(Player payer, Player paid, int amount) {
        CompletableFuture.runAsync( () -> {
            try {
                if (isRegistered(payer) && isRegistered(paid)) {
                    removeCoins(payer, amount);
                    addCoins(paid, amount);
                    Bukkit.getPluginManager().callEvent(new PlayerPayPlayerEvent(payer, paid, amount));
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, getExecutorService());
    }

    public synchronized boolean isRegistered(Player player) throws ExecutionException, InterruptedException {
        return CompletableFuture.supplyAsync( () -> {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM " + getDatabase() + " WHERE UUID = " + '"' + player.getUniqueId().toString() + '"')) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }, getExecutorService()).get();
    }

    public boolean isConnected() throws SQLException {
        return getConnection() != null;
    }

    private void loadMySQLConfig() {
        File mySQLConfig = new File("plugins//CoinSystem//mysql.yml");

        FileConfiguration mySQLConfigYaml;

        if (!mySQLConfig.exists()) {
            mySQLConfig.getParentFile().mkdirs();
            mySQLConfigYaml = new YamlConfiguration();

            mySQLConfigYaml.set("host", "localhost");
            mySQLConfigYaml.set("port", "3306");
            mySQLConfigYaml.set("database", "coinsystem");
            mySQLConfigYaml.set("username", "root");
            mySQLConfigYaml.set("password", "");

            try {
                mySQLConfigYaml.save(mySQLConfig);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        mySQLConfigYaml = YamlConfiguration.loadConfiguration(mySQLConfig);

        setHost(mySQLConfigYaml.getString("host"));
        setPort(mySQLConfigYaml.getString("port"));
        setDatabase(mySQLConfigYaml.getString("database"));
        setUsername(mySQLConfigYaml.getString("username"));
        setPassword(mySQLConfigYaml.getString("password"));
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private HikariDataSource getDataSource() {
        return this.dataSource;
    }

    private HikariConfig getHikariConfig() {
        return this.hikariConfig;
    }

    private ExecutorService getExecutorService() {
        return executorService;
    }
}
