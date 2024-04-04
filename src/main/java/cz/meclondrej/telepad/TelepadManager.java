package cz.meclondrej.telepad;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

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
                for (String encodedLabel : telepads.getKeys(false)) {
                    ConfigurationSection telepad = telepads.getConfigurationSection(encodedLabel);

                    if (!telepad.isSet("x"))
                        throw new Error("cannot read config file at telepads.%s.x".formatted(encodedLabel));
                    int x = telepad.getInt("x");

                    if (!telepad.isSet("y"))
                        throw new Error("cannot read config file at telepads.%s.y".formatted(encodedLabel));
                    int y = telepad.getInt("y");

                    if (!telepad.isSet("z"))
                        throw new Error("cannot read config file at telepads.%s.z".formatted(encodedLabel));
                    int z = telepad.getInt("z");

                    if (!telepad.isSet("world"))
                        throw new Error("cannot read config file at telepads.%s.world".formatted(encodedLabel));
                    World world = Bukkit.getWorld(telepad.getString("world"));
                    if (world == null)
                        throw new Error("world name at telepads.%s.world does not exist".formatted(encodedLabel));

                    TelepadManager.telepads.add(new Telepad(new Location(world, x, y, z), new String(Base64.getDecoder().decode(encodedLabel), StandardCharsets.UTF_8)));
                }
            }
        }
    }

    public static void save() throws IOException {
        FileConfiguration config = Plugin.singleton.getConfig();
        for (Telepad telepad : TelepadManager.telepads) {
            String encodedLabel = Base64.getEncoder().encodeToString(telepad.label().getBytes(StandardCharsets.UTF_8));
            config.set("telepads.%s.x".formatted(encodedLabel), telepad.location().getBlockX());
            config.set("telepads.%s.y".formatted(encodedLabel), telepad.location().getBlockY());
            config.set("telepads.%s.z".formatted(encodedLabel), telepad.location().getBlockZ());
            config.set("telepads.%s.world".formatted(encodedLabel), telepad.location().getWorld().getName());
        }
        config.save(new File(Plugin.singleton.getDataFolder(), "config.yml"));
    }

    public static class TelepadCommand extends AbstractCommandHandler {

        private static class TelepadCreateCommand extends AbstractCommandHandler {

            public TelepadCreateCommand() {
                super("create", "telepad.create");
            }

            @Override
            public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
                if (!(exec instanceof Player)) {
                    exec.sendMessage(Plugin.formatMessage("create only supports player executors"));
                    return true;
                }
                if (args.length < 2) {
                    exec.sendMessage(Plugin.formatMessage("telepad label required"));
                    return true;
                }
                Player player = (Player) exec;
                for (Telepad telepad : TelepadManager.telepads)
                    if (telepad.label().equals(args[1])) {
                        player.sendMessage(Plugin.formatMessage("conflicting label found"));
                        return true;
                    }
                Location location = player.getLocation();
                int halfSize = TelepadManager.horizontalSize / 2;
                TelepadManager.telepads.add(new Telepad(new Location(player.getWorld(),
                        location.getBlockX() - halfSize,
                        location.getBlockY(),
                        location.getBlockZ() - halfSize), args[1]));
                player.sendMessage(Plugin.formatMessage("telepad created"));
                return true;
            }
            
            public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
                return new ArrayList<String>();
            }
            
        }

        private static class TelepadRemoveCommand extends AbstractCommandHandler {

            public TelepadRemoveCommand() {
                super("remove", "telepad.remove");
            }

            @Override
            public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
                if (args.length < 2) {
                    exec.sendMessage(Plugin.formatMessage("telepad label required"));
                    return true;
                }
                for (int i = 0; i < TelepadManager.telepads.size(); i++)
                    if (telepads.get(i).label().equals(args[1])) {
                        TelepadManager.telepads.remove(i);
                        exec.sendMessage(Plugin.formatMessage("telepad removed"));
                        return true;
                    }
                exec.sendMessage(Plugin.formatMessage("cannot find telepad with given label"));
                return true;
            }

            public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
                if (args.length == 2)
                    return TelepadManager.telepads.stream().map(x -> x.label()).collect(Collectors.toList());
                return new ArrayList<String>();
            }

        }

        private static class TelepadListCommand extends AbstractCommandHandler {

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
                    exec.sendMessage(Plugin.formatMessage("%s: %d %d %d".formatted(telepad.label(),
                            telepad.location().getBlockX() + halfSize,
                            telepad.location().getBlockY(),
                            telepad.location().getBlockZ() + halfSize)));
                return true;
            }

            public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
                return new ArrayList<String>();
            }

        }

        private static class TelepadRingCommand extends AbstractCommandHandler {

            public TelepadRingCommand() {
                super("ring", "telepad.ring");
            }

            @Override
            public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
                if (!(exec instanceof Player)) {
                    exec.sendMessage(Plugin.formatMessage("ring only supports player executors"));
                    return true;
                }
                Player player = (Player)exec;
                Location location = player.getLocation();
                if (args.length < 2) {
                    player.sendMessage(Plugin.formatMessage("telepad label required"));
                    return true;
                }
                Telepad localTelepad = null, remoteTelepad = null;
                for (Telepad telepad : TelepadManager.telepads) {
                    Location telepadLocation = telepad.location();
                    if (
                        // X in range
                           location.getBlockX() >= telepadLocation.getBlockX()
                        && location.getBlockX() <= telepadLocation.getBlockX() + TelepadManager.horizontalSize - 1
                        // Y in range
                        && location.getBlockY() >= telepadLocation.getBlockY()
                        && location.getBlockY() <= telepadLocation.getBlockY() + TelepadManager.verticalReach - 1
                        // Z in range
                        && location.getBlockZ() >= telepadLocation.getBlockZ()
                        && location.getBlockZ() <= telepadLocation.getBlockZ() + TelepadManager.horizontalSize - 1
                    ) localTelepad = telepad;
                    if (telepad.label().equals(args[1]))
                        remoteTelepad = telepad;
                    if (localTelepad != null && remoteTelepad != null)
                        break;
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
                for (int x = 0; x < TelepadManager.horizontalSize; x++)
                    for (int y = 0; y < TelepadManager.verticalReach; y++)
                        for (int z = 0; z < TelepadManager.horizontalSize; z++) {
                            Location localBlock = new Location(localTelepad.location().getWorld(),
                                                               localTelepad.location().getBlockX() + x,
                                                               localTelepad.location().getBlockY() + y,
                                                               localTelepad.location().getBlockZ() + z),
                                     remoteBlock = new Location(remoteTelepad.location().getWorld(),
                                                                remoteTelepad.location().getBlockX() + x,
                                                                remoteTelepad.location().getBlockY() + y,
                                                                remoteTelepad.location().getBlockZ() + z);
                            if (!localBlock.getBlock().isEmpty()) {
                                player.sendMessage(Plugin.formatMessage("local telepad obstructed"));
                                return true;
                            }
                            if (!remoteBlock.getBlock().isEmpty()) {
                                player.sendMessage(Plugin.formatMessage("remote telepad obstructed"));
                                return true;
                            }
                        }
                BoundingBox localBox = new BoundingBox(localTelepad.location().getBlockX(),
                                                       localTelepad.location().getBlockY(),
                                                       localTelepad.location().getBlockZ(),
                                                       localTelepad.location().getBlockX() + TelepadManager.horizontalSize - 1,
                                                       localTelepad.location().getBlockY() + TelepadManager.verticalReach - 1,
                                                       localTelepad.location().getBlockZ() + TelepadManager.horizontalSize - 1),
                            remoteBox = new BoundingBox(remoteTelepad.location().getBlockX(),
                                                        remoteTelepad.location().getBlockY(),
                                                        remoteTelepad.location().getBlockZ(),
                                                        remoteTelepad.location().getBlockX() + TelepadManager.horizontalSize - 1,
                                                        remoteTelepad.location().getBlockY() + TelepadManager.verticalReach - 1,
                                                        remoteTelepad.location().getBlockZ() + TelepadManager.horizontalSize - 1);
                Collection<Entity> localTargets = localTelepad.location().getWorld().getNearbyEntities(localBox),
                                   remoteTargets = remoteTelepad.location().getWorld().getNearbyEntities(remoteBox);
                for (Entity localTarget : localTargets) {
                    Location target = new Location(remoteTelepad.location().getWorld(),
                                                   (double)(remoteTelepad.location().getBlockX()) + (localTarget.getLocation().getX() - (double)(localTelepad.location().getBlockX())),
                                                   (double)(remoteTelepad.location().getBlockY()) + (localTarget.getLocation().getY() - (double)(localTelepad.location().getBlockY())),
                                                   (double)(remoteTelepad.location().getBlockZ()) + (localTarget.getLocation().getZ() - (double)(localTelepad.location().getBlockZ())));
                    target.setYaw(localTarget.getLocation().getYaw());
                    target.setPitch(localTarget.getLocation().getPitch());
                    localTarget.teleport(target);
                }
                for (Entity remoteTarget : remoteTargets) {
                    Location target = new Location(localTelepad.location().getWorld(),
                                                   (double)(localTelepad.location().getBlockX()) + (remoteTarget.getLocation().getX() - (double)(remoteTelepad.location().getBlockX())),
                                                   (double)(localTelepad.location().getBlockY()) + (remoteTarget.getLocation().getY() - (double)(remoteTelepad.location().getBlockY())),
                                                   (double)(localTelepad.location().getBlockZ()) + (remoteTarget.getLocation().getZ() - (double)(remoteTelepad.location().getBlockZ())));
                    target.setYaw(remoteTarget.getLocation().getYaw());
                    target.setPitch(remoteTarget.getLocation().getPitch());
                    remoteTarget.teleport(target);
                }
                return true;
            }

            public List<String> onTabComplete(CommandSender exec, Command cmd, String alias, String[] args) {
                if (args.length == 2)
                    return TelepadManager.telepads.stream().map(x -> x.label()).collect(Collectors.toList());
                return new ArrayList<String>();
            }

        }

        ArrayList<AbstractCommandHandler> subcommands = new ArrayList<AbstractCommandHandler>();

        public TelepadCommand() {
            super("telepad", null);
            this.subcommands.add(new TelepadCreateCommand());
            this.subcommands.add(new TelepadRemoveCommand());
            this.subcommands.add(new TelepadListCommand());
            this.subcommands.add(new TelepadRingCommand());
        }

        @Override
        public boolean handle(CommandSender exec, Command cmd, String alias, String[] args) {
            if (args.length < 1) {
                exec.sendMessage(Plugin.formatMessage("must specify a subcommand"));
                return true;
            }

            for (AbstractCommandHandler subcommand : this.subcommands)
                if (cmd.getName().equals(subcommand.getName())) {
                    if ((exec instanceof Player) && !((Player)exec).hasPermission(subcommand.getPermission())) {
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
                return ((exec instanceof Player) ? this.subcommands.stream().filter(x -> ((Player)exec).hasPermission(x.getPermission())) : this.subcommands.stream()).map(x -> x.getName()).collect(Collectors.toList());
            for (AbstractCommandHandler subcommand : this.subcommands)
                if (args[0].equals(subcommand.getName()))
                    return subcommand.onTabComplete(exec, cmd, alias, args);
            return new ArrayList<String>();
        }

    }

    public static TelepadCommand telepadCommand = new TelepadCommand();

}