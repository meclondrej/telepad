package cz.meclondrej.telepad;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public abstract class AbstractCommandHandler implements TabCompleter {
    
    private String name;
    private String permission;

    public AbstractCommandHandler(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public abstract boolean handle(CommandSender exec, Command cmd, String alias, String[] args);

    public abstract List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args);

}
