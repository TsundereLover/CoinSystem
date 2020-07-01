package net.bausucht.tsunderelover.coinsystem.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCoinsChangeEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;

    private final int amount;

    public PlayerCoinsChangeEvent(boolean isAsync, Player player, int amount) {
        this.player = player;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }
}
