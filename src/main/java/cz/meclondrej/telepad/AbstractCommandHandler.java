package cz.meclondrej.telepad;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommandHandler {
    
    private String name;

    public AbstractCommandHandler(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean handle(CommandSender exec, Command cmd, String alias, String[] args);

}
