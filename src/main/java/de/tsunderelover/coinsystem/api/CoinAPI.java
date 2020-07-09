package de.tsunderelover.coinsystem.api;

import de.tsunderelover.coinsystem.api.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class CoinAPI {

    private static final MySQL MY_SQL = new MySQL();

    public static void init() {
        MY_SQL.connect();
        MY_SQL.update("CREATE TABLE IF NOT EXISTS " + MY_SQL.getDatabase() + " (PLAYERNAME VARCHAR(16),UUID VARCHAR(36),COINS INTEGER)");
        Bukkit.broadcastMessage("§a[MySQL] Connected!");
    }

    public static void registerPlayer(Player player, int amount) {
        try {
            MY_SQL.registerPlayer(player, amount);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getCoins(Player player) {
        try {
            return MY_SQL.getCoins(player);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void setCoins(Player player, int amount) {
        MY_SQL.setCoins(player, amount);
    }

    public static void addCoins(Player player, int amount) {
        MY_SQL.addCoins(player, amount);
    }

    public static void removeCoins(Player player, int amount) {
        MY_SQL.removeCoins(player, amount);
    }

    public static void pay(Player payer, Player paid, int amount) {
        MY_SQL.pay(payer, paid, amount);
    }

    private static MySQL getMySQL() {
        return MY_SQL;
    }
}
