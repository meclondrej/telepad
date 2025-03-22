package cz.meclondrej.telepad;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

public record Telepad(Location location, String label) {

	public static class RingException extends Exception {
		public static enum RingExceptionType {
			LocalObstructed, RemoteObstructed
		}

		private RingExceptionType type;

		public RingException(RingExceptionType type) {
			this.type = type;
		}

		public RingExceptionType getRingExceptionType() {
			return this.type;
		}
	}

	public void ring(Telepad target) throws RingException {
		if (this.isObstructed())
			throw new RingException(RingException.RingExceptionType.LocalObstructed);
		if (target.isObstructed())
			throw new RingException(RingException.RingExceptionType.RemoteObstructed);
		Collection<Entity> localEntities = this.getEntities(), remoteEntities = target.getEntities();
		this.teleportEntities(target, localEntities);
		target.teleportEntities(this, remoteEntities);
	}

	public void teleportEntities(Telepad target, Collection<Entity> entities) {
		for (Entity entity : entities) {
			Location location = new Location(target.location().getWorld(),
					(double) (target.location().getBlockX())
							+ (entity.getLocation().getX() - (double) (this.location().getBlockX())),
					(double) (target.location().getBlockY())
							+ (entity.getLocation().getY() - (double) (this.location().getBlockY())),
					(double) (target.location().getBlockZ())
							+ (entity.getLocation().getZ() - (double) (this.location().getBlockZ())));
			location.setYaw(entity.getLocation().getYaw());
			location.setPitch(entity.getLocation().getPitch());
			entity.teleport(location);
		}
	}

	public BoundingBox getBoundingBox() {
		Location loc = this.location();
		return new BoundingBox(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
				loc.getBlockX() + TelepadManager.horizontalSize - 1, loc.getBlockY() + TelepadManager.verticalReach - 1,
				loc.getBlockZ() + TelepadManager.horizontalSize - 1);
	}

	public Collection<Entity> getEntities() {
		return this.location().getWorld().getNearbyEntities(this.getBoundingBox());
	}

	public boolean inTelepad(Location location) {
		return this.location().getWorld() == location.getWorld() && this.getBoundingBox().contains(location.toVector());
	}

	public boolean isObstructed() {
		for (int x = 0; x < TelepadManager.horizontalSize; x++)
			for (int y = 0; y < TelepadManager.verticalReach; y++)
				for (int z = 0; z < TelepadManager.horizontalSize; z++)
					if (!this.location().getWorld().getBlockAt(this.location().getBlockX() + x,
							this.location().getBlockY() + y, this.location().getBlockZ() + z).isEmpty())
						return true;
		return false;
	}

}
