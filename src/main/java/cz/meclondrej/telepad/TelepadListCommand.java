package cz.meclondrej.telepad;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TelepadListCommand extends AbstractCommandHandler {

    public TelepadListCommand() {
        super("list", "telepad.list");
    }

    @Override
    public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
        if (TelepadManager.telepads.size() == 0) {
            exec.sendMessage(Plugin.formatMessage("there are no telepads"));
            return true;
        }
        int halfSize = TelepadManager.horizontalSize / 2;
        for (Telepad telepad : TelepadManager.telepads)
            exec.sendMessage(
                    Plugin.formatMessage(
                            "%s: %d %d %d"
                                    .formatted(
                                            telepad.label(),
                                            telepad.location().getBlockX() + halfSize,
                                            telepad.location().getBlockY(),
                                            telepad.location().getBlockZ() + halfSize)));
        return true;
    }

    public List<String> onTabComplete(
            CommandSender exec, Command cmd, String alias, String[] args) {
        return new ArrayList<String>();
    }
}
