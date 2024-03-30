package cz.meclondrej.telepad;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements CommandExecutor {
    
    public static String formatMessage(String content) {
        return "[telepad] %s".formatted(content);
    }

    private ConsoleCommandSender con = this.getServer().getConsoleSender();
    private AbstractCommandHandler[] commands = {};

    public boolean onCommand(CommandSender exec, Command cmd, String alias, String[] args) {
        for (AbstractCommandHandler commandHandler : this.commands)
            if (cmd.getName().equals(commandHandler.getName()))
                return commandHandler.handle(exec, cmd, alias, args);
        return true;
    }

    @Override
    public void onEnable() {
        con.sendMessage(Plugin.formatMessage("enabled"));
        for (AbstractCommandHandler commandHandler : this.commands)
            this.getCommand(commandHandler.getName()).setExecutor(this);
        con.sendMessage("initialized");
    }

    @Override
    public void onDisable() {
        con.sendMessage("disabled");
    }

}