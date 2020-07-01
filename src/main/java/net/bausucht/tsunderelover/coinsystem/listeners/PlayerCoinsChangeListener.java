package net.bausucht.tsunderelover.coinsystem.listeners;

import net.bausucht.tsunderelover.coinsystem.api.events.PlayerCoinsChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerCoinsChangeListener implements Listener {

    @EventHandler
    public void onChange(PlayerCoinsChangeEvent event) {
        event.getPlayer().sendMessage("Test3");
    }

}
