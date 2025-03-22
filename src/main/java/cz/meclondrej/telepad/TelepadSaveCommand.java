package cz.meclondrej.telepad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TelepadSaveCommand extends AbstractCommandHandler {

	public TelepadSaveCommand() {
		super("save", "telepad.save");
	}

	@Override
	public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
		try {
			TelepadManager.save();
		} catch (IOException ex) {
			exec.sendMessage(Plugin.formatMessage("an error occured while saving"));
			return true;
		}
		exec.sendMessage(Plugin.formatMessage("successfully saved into config file"));
		return true;
	}

	public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
		return new ArrayList<String>();
	}

}