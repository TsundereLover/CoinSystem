package net.bausucht.tsunderelover.coinsystem;

import net.bausucht.tsunderelover.coinsystem.api.CoinAPI;
import net.bausucht.tsunderelover.coinsystem.commands.*;
import net.bausucht.tsunderelover.coinsystem.listeners.PlayerCoinsChangeListener;
import net.bausucht.tsunderelover.coinsystem.listeners.PlayerJoinListener;
import net.bausucht.tsunderelover.coinsystem.listeners.PlayerPayPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CoinSystem extends JavaPlugin {

    private static final String PREFIX = "§7[§cCoinSystem§7] §c";

    @Override
    public void onEnable() {
        CoinAPI.init();
        init();
    }

    @Override
    public void onDisable() {

    }

    private void init() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        getCommand("coins").setExecutor(new CommandCoins());
        getCommand("addcoins").setExecutor(new CommandAddCoins());
        getCommand("removecoins").setExecutor(new CommandRemoveCoins());
        getCommand("setcoins").setExecutor(new CommandSetCoins());
        getCommand("pay").setExecutor(new CommandPay());

        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerCoinsChangeListener(), this);
        pluginManager.registerEvents(new PlayerPayPlayerListener(), this);
    }

    public static String getPrefix() {
        return PREFIX;
    }
}
