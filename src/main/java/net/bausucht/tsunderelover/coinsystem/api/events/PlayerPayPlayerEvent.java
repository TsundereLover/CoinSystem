package net.bausucht.tsunderelover.coinsystem.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPayPlayerEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player payer;

    private final Player paid;

    private final int amount;

    public PlayerPayPlayerEvent(boolean isAsync, Player payer, Player paid, int amount) {
        this.payer = payer;
        this.paid = paid;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Player getPayer() {
        return payer;
    }

    public Player getPaid() {
        return paid;
    }

    public int getAmount() {
        return amount;
    }
}
