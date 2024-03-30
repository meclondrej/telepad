package cz.meclondrej.telepad;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class TelepadManager {
    
    private static ArrayList<Telepad> telepads = new ArrayList<Telepad>();
    public static int horizontalSize, verticalReach;

    public static void load() {
        FileConfiguration config = Plugin.singleton.getConfig();

        if (!config.isSet("telepad_config.horizontal_size"))
            throw new Error("cannot read config file at telepad_config.horizontal_size");
        TelepadManager.horizontalSize = config.getInt("telepad_config.horizontal_size");
        if (TelepadManager.horizontalSize <= 0)
            throw new Error("telepad_config.horizontal_size must be greater than zero");
        
        if (!config.isSet("telepad_config.vertical_reach"))
            throw new Error("cannot read config file at telepad_config.vertical_reach");
        TelepadManager.verticalReach = config.getInt("telepad_config.vertical_reach");
        if (TelepadManager.verticalReach <= 0)
            throw new Error("telepad_config.vertical_reach must be greater than zero");
        
        if (config.isSet("telepads")) {
            ConfigurationSection telepads = config.getConfigurationSection("telepads");
            if (telepads != null) {
                for (String label : telepads.getKeys(false)) {
                    ConfigurationSection telepad = telepads.getConfigurationSection(label);

                    if (!telepad.isSet("x"))
                        throw new Error("cannot read config file at telepads.%s.x".formatted(label));
                    int x = telepad.getInt("x");

                    if (!telepad.isSet("y"))
                        throw new Error("cannot read config file at telepads.%s.y".formatted(label));
                    int y = telepad.getInt("y");

                    if (!telepad.isSet("z"))
                        throw new Error("cannot read config file at telepads.%s.z".formatted(label));
                    int z = telepad.getInt("z");

                    if (!telepad.isSet("world"))
                        throw new Error("cannot read config file at telepads.%s.world".formatted(label));
                    World world = Bukkit.getWorld(telepad.getString("world"));
                    if (world == null)
                        throw new Error("world name at telepads.%s.world does not exist".formatted(label));

                    TelepadManager.telepads.add(new Telepad(new Location(world, x, y, z), label));
                }
            }
        }
    }

    public static void save() {
        FileConfiguration config = Plugin.singleton.getConfig();
        for (Telepad telepad : TelepadManager.telepads) {
            config.set("telepads.%s.x".formatted(telepad.label()), telepad.location().getBlockX());
            config.set("telepads.%s.y".formatted(telepad.label()), telepad.location().getBlockY());
            config.set("telepads.%s.z".formatted(telepad.label()), telepad.location().getBlockZ());
            config.set("telepads.%s.world".formatted(telepad.label()), telepad.location().getWorld().getName());
        }
    }

}