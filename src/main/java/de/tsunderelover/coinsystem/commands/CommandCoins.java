package de.tsunderelover.coinsystem.commands;

import de.tsunderelover.coinsystem.CoinSystem;
import de.tsunderelover.coinsystem.api.CoinAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCoins implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(CoinSystem.getPrefix() + "Deine Coins: " + CoinAPI.getCoins(player));

            } else if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);

                if (target != null) {
                    player.sendMessage(CoinSystem.getPrefix() + "Die Coins von " + target.getName() + ": " + CoinAPI.getCoins(target));
                }
            }
        }

        return false;
    }
}
