package de.tsunderelover.coinsystem.listeners;

import de.tsunderelover.coinsystem.api.CoinAPI;
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
