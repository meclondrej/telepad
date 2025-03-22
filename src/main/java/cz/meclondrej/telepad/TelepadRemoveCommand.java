package cz.meclondrej.telepad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TelepadRemoveCommand extends AbstractCommandHandler {

	public TelepadRemoveCommand() {
		super("remove", "telepad.remove");
	}

	@Override
	public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
		if (args.length < 2) {
			exec.sendMessage(Plugin.formatMessage("telepad label required"));
			return true;
		}
		for (int i = 0; i < TelepadManager.telepads.size(); i++)
			if (TelepadManager.telepads.get(i).label().equals(args[1])) {
				TelepadManager.telepads.remove(i);
				exec.sendMessage(Plugin.formatMessage("telepad removed"));
				return true;
			}
		exec.sendMessage(Plugin.formatMessage("cannot find telepad with given label"));
		return true;
	}

	public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
		if (args.length == 2)
			return TelepadManager.telepads.stream().map(x -> x.label()).collect(Collectors.toList());
		return new ArrayList<String>();
	}

}