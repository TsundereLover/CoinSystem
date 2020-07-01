package net.bausucht.tsunderelover.coinsystem.commands;

import net.bausucht.tsunderelover.coinsystem.api.CoinAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRemoveCoins implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("coinsystem.addcoins")) {
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[0]);
                    int amount = Integer.parseInt(args[1]);

                    if (target != null) {
                        CoinAPI.removeCoins(target, amount);
                    }
                }
            }
        }

        return false;
    }
}
