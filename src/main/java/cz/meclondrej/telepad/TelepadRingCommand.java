package cz.meclondrej.telepad;

import cz.meclondrej.telepad.Telepad.RingException;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TelepadRingCommand extends AbstractCommandHandler {

    public TelepadRingCommand() {
        super("ring", "telepad.ring");
    }

    @Override
    public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
        if (!(exec instanceof Player)) {
            exec.sendMessage(Plugin.formatMessage("ring only supports player executors"));
            return true;
        }
        Player player = (Player) exec;
        Location location = player.getLocation();
        if (args.length < 2) {
            player.sendMessage(Plugin.formatMessage("telepad label required"));
            return true;
        }
        Telepad localTelepad = null, remoteTelepad = null;
        for (Telepad telepad : TelepadManager.telepads) {
            if (telepad.inTelepad(location)) localTelepad = telepad;
            if (telepad.label().equals(args[1])) remoteTelepad = telepad;
            if (localTelepad != null && remoteTelepad != null) break;
        }
        if (localTelepad == null) {
            player.sendMessage(Plugin.formatMessage("not standing on telepad"));
            return true;
        }
        if (remoteTelepad == null) {
            player.sendMessage(Plugin.formatMessage("cannot find telepad with given label"));
            return true;
        }
        if (localTelepad == remoteTelepad) {
            player.sendMessage(Plugin.formatMessage("telepad cannot ring itself"));
            return true;
        }
        try {
            localTelepad.ring(remoteTelepad);
        } catch (RingException ex) {
            switch (ex.getRingExceptionType()) {
                case LocalObstructed:
                    player.sendMessage(Plugin.formatMessage("local telepad obstructed"));
                    return true;
                case RemoteObstructed:
                    player.sendMessage(Plugin.formatMessage("remote telepad obstructed"));
                    return true;
            }
        }
        return true;
    }

    public List<String> onTabComplete(
            CommandSender exec, Command cmd, String alias, String[] args) {
        if (args.length == 2)
            return TelepadManager.telepads.stream()
                    .map(x -> x.label())
                    .collect(Collectors.toList());
        return new ArrayList<String>();
    }
}
