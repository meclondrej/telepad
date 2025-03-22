package cz.meclondrej.telepad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.meclondrej.telepad.Telepad.RingException;

public class TelepadConnectCommand extends AbstractCommandHandler {

    public TelepadConnectCommand() {
        super("connect", "telepad.connect");
    }

    @Override
    public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
        if (!(exec instanceof Player)) {
            exec.sendMessage(Plugin.formatMessage("ring only supports player executors"));
            return true;
        }
        Player player = (Player) exec;
        if (args.length < 3) {
            player.sendMessage(Plugin.formatMessage("telepad labels required"));
            return true;
        }
        Telepad firstTelepad = null, secondTelepad = null;
        for (Telepad telepad : TelepadManager.telepads) {
            if (telepad.label().equals(args[1]))
                firstTelepad = telepad;
            if (telepad.label().equals(args[2]))
                secondTelepad = telepad;
            if (firstTelepad != null && secondTelepad != null)
                break;
        }
        if (firstTelepad == null) {
            player.sendMessage(Plugin.formatMessage("cannot find telepad with first given label"));
            return true;
        }
        if (secondTelepad == null) {
            player.sendMessage(Plugin.formatMessage("cannot find telepad with second given label"));
            return true;
        }
        if (firstTelepad == secondTelepad) {
            player.sendMessage(Plugin.formatMessage("telepad cannot ring itself"));
            return true;
        }
        try {
            firstTelepad.ring(secondTelepad);
        } catch (RingException ex) {
            switch (ex.getRingExceptionType()) {
                case LocalObstructed:
                    player.sendMessage(Plugin.formatMessage("first telepad obstructed"));
                    return true;
                case RemoteObstructed:
                    player.sendMessage(Plugin.formatMessage("second telepad obstructed"));
                    return true;
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
        if (args.length == 2)
            return TelepadManager.telepads.stream().map(x -> x.label()).collect(Collectors.toList());
        if (args.length == 3)
            return TelepadManager.telepads.stream().filter(x -> !x.label().equals(args[1])).map(x -> x.label()).collect(Collectors.toList());
        return new ArrayList<String>();
    }

}