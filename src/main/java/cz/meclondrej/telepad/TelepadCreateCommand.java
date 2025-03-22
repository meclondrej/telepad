package cz.meclondrej.telepad;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TelepadCreateCommand extends AbstractCommandHandler {

	public TelepadCreateCommand() {
		super("create", "telepad.create");
	}

	@Override
	public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
		if (!(exec instanceof Player)) {
			exec.sendMessage(Plugin.formatMessage("create only supports player executors"));
			return true;
		}
		if (args.length < 2) {
			exec.sendMessage(Plugin.formatMessage("telepad label required"));
			return true;
		}
		Player player = (Player) exec;
		for (Telepad telepad : TelepadManager.telepads)
			if (telepad.label().equals(args[1])) {
				player.sendMessage(Plugin.formatMessage("conflicting label found"));
				return true;
			}
		Location location = player.getLocation();
		int halfSize = TelepadManager.horizontalSize / 2;
		TelepadManager.telepads.add(new Telepad(new Location(player.getWorld(), location.getBlockX() - halfSize,
				location.getBlockY(), location.getBlockZ() - halfSize), args[1]));
		player.sendMessage(Plugin.formatMessage("telepad created"));
		return true;
	}

	public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
		return new ArrayList<String>();
	}

}