package cz.meclondrej.telepad;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class TelepadManager {

	public static ArrayList<Telepad> telepads = new ArrayList<Telepad>();
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

					TelepadManager.telepads.add(new Telepad(new Location(world, x, y, z),
							new String(Base64.getDecoder().decode(encodedLabel), StandardCharsets.UTF_8)));
				}
			}
		}
	}

	public static void save() throws IOException {
		FileConfiguration config = Plugin.singleton.getConfig();
		config.set("telepads", null);
		for (Telepad telepad : TelepadManager.telepads) {
			String encodedLabel = Base64.getEncoder().encodeToString(telepad.label().getBytes(StandardCharsets.UTF_8));
			config.set("telepads.%s.x".formatted(encodedLabel), telepad.location().getBlockX());
			config.set("telepads.%s.y".formatted(encodedLabel), telepad.location().getBlockY());
			config.set("telepads.%s.z".formatted(encodedLabel), telepad.location().getBlockZ());
			config.set("telepads.%s.world".formatted(encodedLabel), telepad.location().getWorld().getName());
		}
		config.save(new File(Plugin.singleton.getDataFolder(), "config.yml"));
	}

}
