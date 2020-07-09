package de.tsunderelover.coinsystem.listeners;

import de.tsunderelover.coinsystem.api.events.PlayerCoinsChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerCoinsChangeListener implements Listener {

    @EventHandler
    public void onChange(PlayerCoinsChangeEvent event) {
        event.getPlayer().sendMessage("Test3");
    }

}
