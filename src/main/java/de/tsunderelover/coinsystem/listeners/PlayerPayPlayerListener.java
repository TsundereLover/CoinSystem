package de.tsunderelover.coinsystem.listeners;

import de.tsunderelover.coinsystem.api.events.PlayerPayPlayerEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerPayPlayerListener implements Listener {

    @EventHandler
    public void onPay(PlayerPayPlayerEvent event) {
        event.getPayer().sendMessage("Test1");
        event.getPaid().sendMessage("Test2");
    }

}
