package cz.meclondrej.telepad;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements CommandExecutor {
    
    public static String formatMessage(String content) {
        return "[telepad] %s".formatted(content);
    }

    public static Plugin singleton;
    
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
        Plugin.singleton = this;
        con.sendMessage(Plugin.formatMessage("enabled"));
        this.saveDefaultConfig();
        TelepadManager.load();
        for (AbstractCommandHandler commandHandler : this.commands)
            this.getCommand(commandHandler.getName()).setExecutor(this);
        con.sendMessage(Plugin.formatMessage("initialized"));
    }

    @Override
    public void onDisable() {
        try {
            TelepadManager.save();
        } catch (IOException ex) {
            con.sendMessage(Plugin.formatMessage("config save failed!"));
            con.sendMessage(ex.getStackTrace().toString());
        }
        con.sendMessage(Plugin.formatMessage("disabled"));
    }

}