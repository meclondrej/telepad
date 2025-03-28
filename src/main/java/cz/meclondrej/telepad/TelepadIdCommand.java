package cz.meclondrej.telepad;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TelepadIdCommand extends AbstractCommandHandler {

    public TelepadIdCommand() {
        super("id", "telepad.id");
    }

    @Override
    public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
        if (TelepadManager.telepads.size() == 0) {
            exec.sendMessage(Plugin.formatMessage("there are no telepads"));
            return true;
        }
        int halfSize = TelepadManager.horizontalSize / 2;
        if (args.length < 2) {
            if (!(exec instanceof Player)) {
                exec.sendMessage("local id only supports player executors");
                return true;
            }
            Player player = (Player) exec;
            if (!player.hasPermission("telepad.id.local")) {
                exec.sendMessage(Plugin.formatMessage("insufficient permissions"));
                return true;
            }
            Location location = player.getLocation();
            Telepad target = null;
            for (Telepad telepad : TelepadManager.telepads)
                if (telepad.inTelepad(location)) {
                    target = telepad;
                    break;
                }
            if (target == null) {
                player.sendMessage(Plugin.formatMessage("not standing on telepad"));
                return true;
            }
            player.sendMessage(
                    Plugin.formatMessage(
                            "%s: %d %d %d"
                                    .formatted(
                                            target.label(),
                                            target.location().getBlockX() + halfSize,
                                            target.location().getBlockY(),
                                            target.location().getBlockZ() + halfSize)));
            return true;
        }
        if ((exec instanceof Player) && !((Player) exec).hasPermission("telepad.id.remote")) {
            exec.sendMessage(Plugin.formatMessage("insufficient permissions"));
            return true;
        }
        for (Telepad telepad : TelepadManager.telepads)
            if (telepad.label().equals(args[1])) {
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
        exec.sendMessage(Plugin.formatMessage("cannot find telepad with given label"));
        return true;
    }

    public List<String> onTabComplete(
            CommandSender exec, Command cmd, String alias, String[] args) {
        return new ArrayList<String>();
    }
}
