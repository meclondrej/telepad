package cz.meclondrej.telepad;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TelepadCommand extends AbstractCommandHandler {

    ArrayList<AbstractCommandHandler> subcommands = new ArrayList<AbstractCommandHandler>();

    public TelepadCommand() {
        super("telepad", null);
        this.subcommands.add(new TelepadCreateCommand());
        this.subcommands.add(new TelepadRemoveCommand());
        this.subcommands.add(new TelepadListCommand());
        this.subcommands.add(new TelepadRingCommand());
        this.subcommands.add(new TelepadSaveCommand());
        this.subcommands.add(new TelepadConnectCommand());
        this.subcommands.add(new TelepadIdCommand());
    }

    @Override
    public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
        if (args.length < 1) {
            exec.sendMessage(Plugin.formatMessage("must specify a subcommand"));
            return true;
        }

        for (AbstractCommandHandler subcommand : this.subcommands)
            if (args[0].equals(subcommand.getName())) {
                if ((exec instanceof Player) && subcommand.getPermission() != null && !((Player)exec).hasPermission(subcommand.getPermission())) {
                    exec.sendMessage(Plugin.formatMessage("insufficient permissions"));
                    return true;
                }
                return subcommand.handle(exec, cmd, alias, args);
            }

        exec.sendMessage(Plugin.formatMessage("invalid subcommand"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
        if (!cmd.getName().equals(this.getName()))
            return null;
        if (args.length == 1)
            return ((exec instanceof Player) ? this.subcommands.stream().filter(x -> x.getPermission() == null || ((Player)exec).hasPermission(x.getPermission())) : this.subcommands.stream()).map(x -> x.getName()).collect(Collectors.toList());
        for (AbstractCommandHandler subcommand : this.subcommands)
            if (args[0].equals(subcommand.getName()))
                return subcommand.onTabComplete(exec, cmd, alias, args);
        return new ArrayList<String>();
    }

}
