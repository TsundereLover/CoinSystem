package net.bausucht.tsunderelover.coinsystem.listeners;

import net.bausucht.tsunderelover.coinsystem.api.CoinAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CoinAPI.registerPlayer(player, 1000);
    }

}
