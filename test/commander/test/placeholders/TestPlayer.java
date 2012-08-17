package commander.test.placeholders;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class TestPlayer implements Player {
	public String playerName;
	private Server server;
	
	public TestPlayer(String name, Server server) {
		this.playerName = name;
		this.server = server;
		((TestServer)server).addPlayer(this);
	}
	
	@Override public String toString() {
		return "TestPlayer["+playerName+"]";
	}
	
	/////////////////////////////////// Relevant Methods //////////////////////////////////////
	
	@Override public String getName() {
		return playerName;
	}
	
	@Override public Server getServer() {
		return server;
	}
	
	@Override public void sendMessage(String message) {
		System.out.println("["+playerName+"] sendMessage() : "+message);
	}

	@Override public void sendMessage(String[] messages) {
		System.out.println("["+playerName+"] sendMessage() :");
		for (String msg : messages) {
			System.out.println(" > "+msg);
		}
	}
	
	
	@Override public boolean isPermissionSet(String name) {
		System.out.println("["+playerName+"] isPermissionSet() : "+name);
		if (name.equals("commander.test1")) return true;
		return false;
	}

	@Override public boolean isPermissionSet(Permission perm) {
		System.out.println("["+playerName+"] isPermissionSet() : "+perm.getName());
		if (perm.getName().equals("commander.test1")) return true;
		return false;
	}

	@Override public boolean hasPermission(String name) {
		System.out.println("["+playerName+"] hasPermission() : "+name);
		if (name.equals("commander.test1")) return true;
		return false;
	}

	@Override public boolean hasPermission(Permission perm) {
		System.out.println("["+playerName+"] hasPermission() : "+perm.getName());
		if (perm.getName().equals("commander.test1")) return true;
		return false;
	}
	
	
	
	@Override public PlayerInventory getInventory() {
		return null;
	}
	
	@Override public ItemStack getItemInHand() {
		return null;
	}
	
	@Override public ItemStack getItemOnCursor() {
		return null;
	}
	
	@Override public int getHealth() {
		return 0;
	}
	
	@Override public boolean isOp() {
		return false;
	}
	
	@Override public boolean isConversing() {
		return false;
	}
	
	@Override public String getDisplayName() {
		return null;
	}
	
	@Override public boolean isSneaking() {
		return false;
	}
	
	@Override public int getLevel() {
		return 0;
	}
	
	@Override public float getExp() {
		return 0;
	}
	
	@Override public int getFoodLevel() {
		return 0;
	}
	
	@Override public boolean isFlying() {
		return false;
	}
	
	@Override public boolean canSee(Player player) {
		return false;
	}
	
	////////////////////////////////// Irrelevant Methods //////////////////////////////////////
	
	@Override public boolean setWindowProperty(Property prop, int value) {
		return false;
	}

	@Override public InventoryView getOpenInventory() {
		return null;
	}

	@Override public InventoryView openInventory(Inventory inventory) {
		return null;
	}

	@Override public InventoryView openWorkbench(Location location,
			boolean force) {
		return null;
	}

	@Override public InventoryView openEnchanting(Location location,
			boolean force) {
		return null;
	}

	@Override public void openInventory(InventoryView inventory) {}

	@Override public void closeInventory() {}

	@Override public void setItemInHand(ItemStack item) {}

	@Override public void setItemOnCursor(ItemStack item) {}

	@Override public boolean isSleeping() {
		return false;
	}

	@Override public int getSleepTicks() {
		return 0;
	}

	@Override public GameMode getGameMode() {
		return null;
	}

	@Override public void setGameMode(GameMode mode) {}

	@Override public boolean isBlocking() {
		return false;
	}

	@Override public int getExpToLevel() {
		return 0;
	}

	@Override public void setHealth(int health) {}

	@Override public int getMaxHealth() {
		return 0;
	}

	@Override public double getEyeHeight() {
		return 0;
	}

	@Override public double getEyeHeight(boolean ignoreSneaking) {
		return 0;
	}

	@Override public Location getEyeLocation() {
		return null;
	}

	@Override public List<Block> getLineOfSight(HashSet<Byte> transparent,
			int maxDistance) {
		return null;
	}

	@Override public Block getTargetBlock(HashSet<Byte> transparent,
			int maxDistance) {
		return null;
	}

	@Override public List<Block> getLastTwoTargetBlocks(
			HashSet<Byte> transparent, int maxDistance) {
		return null;
	}

	@Override @Deprecated public Egg throwEgg() {
		return null;
	}

	@Override @Deprecated public Snowball throwSnowball() {
		return null;
	}

	@Override @Deprecated public Arrow shootArrow() {
		return null;
	}

	@Override public <T extends Projectile> T launchProjectile(
			Class<? extends T> projectile) {
		return null;
	}

	@Override public int getRemainingAir() {
		return 0;
	}

	@Override public void setRemainingAir(int ticks) {}

	@Override public int getMaximumAir() {
		return 0;
	}

	@Override public void setMaximumAir(int ticks) {}

	@Override public void damage(int amount) {}

	@Override public void damage(int amount, Entity source) {}

	@Override public int getMaximumNoDamageTicks() {
		return 0;
	}

	@Override public void setMaximumNoDamageTicks(int ticks) {}

	@Override public int getLastDamage() {
		return 0;
	}

	@Override public void setLastDamage(int damage) {}

	@Override public int getNoDamageTicks() {
		return 0;
	}

	@Override public void setNoDamageTicks(int ticks) {}

	@Override public Player getKiller() {
		return null;
	}

	@Override public boolean addPotionEffect(PotionEffect effect) {
		return false;
	}

	@Override public boolean addPotionEffect(PotionEffect effect, boolean force) {
		return false;
	}

	@Override public boolean addPotionEffects(Collection<PotionEffect> effects) {
		return false;
	}

	@Override public boolean hasPotionEffect(PotionEffectType type) {
		return false;
	}

	@Override public void removePotionEffect(PotionEffectType type) {}

	@Override public Collection<PotionEffect> getActivePotionEffects() {
		return null;
	}

	@Override public boolean hasLineOfSight(Entity other) {
		return false;
	}

	@Override public Location getLocation() {
		return null;
	}

	@Override public void setVelocity(Vector velocity) {}

	@Override public Vector getVelocity() {
		return null;
	}

	@Override public World getWorld() {
		return null;
	}

	@Override public boolean teleport(Location location) {
		return false;
	}

	@Override public boolean teleport(Location location, TeleportCause cause) {
		return false;
	}

	@Override public boolean teleport(Entity destination) {
		return false;
	}

	@Override public boolean teleport(Entity destination, TeleportCause cause) {
		return false;
	}

	@Override public List<Entity> getNearbyEntities(double x, double y, double z) {
		return null;
	}

	@Override public int getEntityId() {
		return 0;
	}

	@Override public int getFireTicks() {
		return 0;
	}

	@Override public int getMaxFireTicks() {
		return 0;
	}

	@Override public void setFireTicks(int ticks) {}

	@Override public void remove() {}

	@Override public boolean isDead() {
		return false;
	}

	@Override public boolean isValid() {
		return false;
	}

	@Override public Entity getPassenger() {
		return null;
	}

	@Override public boolean setPassenger(Entity passenger) {
		return false;
	}

	@Override public boolean isEmpty() {
		return false;
	}

	@Override public boolean eject() {
		return false;
	}

	@Override public float getFallDistance() {
		return 0;
	}

	@Override public void setFallDistance(float distance) {}

	@Override public void setLastDamageCause(EntityDamageEvent event) {}

	@Override public EntityDamageEvent getLastDamageCause() {
		return null;
	}

	@Override public UUID getUniqueId() {
		return null;
	}

	@Override public int getTicksLived() {
		return 0;
	}

	@Override public void setTicksLived(int value) {}

	@Override public void playEffect(EntityEffect type) {}

	@Override public EntityType getType() {
		return null;
	}

	@Override public boolean isInsideVehicle() {
		return false;
	}

	@Override public boolean leaveVehicle() {
		return false;
	}

	@Override public Entity getVehicle() {
		return null;
	}

	@Override public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {}

	@Override public List<MetadataValue> getMetadata(String metadataKey) {
		return null;
	}

	@Override public boolean hasMetadata(String metadataKey) {
		return false;
	}

	@Override public void removeMetadata(String metadataKey, Plugin owningPlugin) {}

	@Override public PermissionAttachment addAttachment(Plugin plugin,
			String name, boolean value) {
		return null;
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin) {
		return null;
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin,
			String name, boolean value, int ticks) {
		return null;
	}

	@Override public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return null;
	}

	@Override public void removeAttachment(PermissionAttachment attachment) {}

	@Override public void recalculatePermissions() {}

	@Override public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	@Override public void setOp(boolean value) {}

	@Override public void acceptConversationInput(String input) {}

	@Override public boolean beginConversation(Conversation conversation) {
		return false;
	}

	@Override public void abandonConversation(Conversation conversation) {}

	@Override public void abandonConversation(Conversation conversation,
			ConversationAbandonedEvent details) {}

	@Override public boolean isOnline() {
		return false;
	}

	@Override public boolean isBanned() {
		return false;
	}

	@Override public void setBanned(boolean banned) {}

	@Override public boolean isWhitelisted() {
		return false;
	}

	@Override public void setWhitelisted(boolean value) {}

	@Override public Player getPlayer() {
		return null;
	}

	@Override public long getFirstPlayed() {
		return 0;
	}

	@Override public long getLastPlayed() {
		return 0;
	}

	@Override public boolean hasPlayedBefore() {
		return false;
	}

	@Override public Map<String, Object> serialize() {
		return null;
	}

	@Override public void sendPluginMessage(Plugin source, String channel,
			byte[] message) {}

	@Override public Set<String> getListeningPluginChannels() {
		return null;
	}

	@Override public void setDisplayName(String name) {}

	@Override public String getPlayerListName() {
		return null;
	}

	@Override public void setPlayerListName(String name) {}

	@Override public void setCompassTarget(Location loc) {}

	@Override public Location getCompassTarget() {
		return null;
	}

	@Override public InetSocketAddress getAddress() {
		return null;
	}

	@Override public void sendRawMessage(String message) {}

	@Override public void kickPlayer(String message) {}

	@Override public void chat(String msg) {}

	@Override public boolean performCommand(String command) {
		return false;
	}

	@Override public void setSneaking(boolean sneak) {}

	@Override public boolean isSprinting() {
		return false;
	}

	@Override public void setSprinting(boolean sprinting) {}

	@Override public void saveData() {}

	@Override public void loadData() {}

	@Override public void setSleepingIgnored(boolean isSleeping) {}

	@Override public boolean isSleepingIgnored() {
		return false;
	}

	@Override public void playNote(Location loc, byte instrument, byte note) {}

	@Override public void playNote(Location loc, Instrument instrument,
			Note note) {}

	@Override public void playEffect(Location loc, Effect effect, int data) {}

	@Override public <T> void playEffect(Location loc, Effect effect, T data) {}

	@Override public void sendBlockChange(Location loc, Material material,
			byte data) {}

	@Override public boolean sendChunkChange(Location loc, int sx, int sy,
			int sz, byte[] data) {
		return false;
	}

	@Override public void sendBlockChange(Location loc, int material, byte data) {}

	@Override public void sendMap(MapView map) {}

	@Override @Deprecated public void updateInventory() {}

	@Override public void awardAchievement(Achievement achievement) {}

	@Override public void incrementStatistic(Statistic statistic) {}

	@Override public void incrementStatistic(Statistic statistic, int amount) {}

	@Override public void incrementStatistic(Statistic statistic,
			Material material) {}

	@Override public void incrementStatistic(Statistic statistic,
			Material material, int amount) {}

	@Override public void setPlayerTime(long time, boolean relative) {}

	@Override public long getPlayerTime() {
		return 0;
	}

	@Override public long getPlayerTimeOffset() {
		return 0;
	}

	@Override public boolean isPlayerTimeRelative() {
		return false;
	}

	@Override public void resetPlayerTime() {}

	@Override public void giveExp(int amount) {}

	@Override public void setExp(float exp) {}

	@Override public void setLevel(int level) {}

	@Override public int getTotalExperience() {
		return 0;
	}

	@Override public void setTotalExperience(int exp) {}

	@Override public float getExhaustion() {
		return 0;
	}

	@Override public void setExhaustion(float value) {}

	@Override public float getSaturation() {
		return 0;
	}

	@Override public void setSaturation(float value) {}

	@Override public void setFoodLevel(int value) {}

	@Override public Location getBedSpawnLocation() {
		return null;
	}

	@Override public void setBedSpawnLocation(Location location) {}

	@Override public boolean getAllowFlight() {
		return false;
	}

	@Override public void setAllowFlight(boolean flight) {}

	@Override public void hidePlayer(Player player) {}

	@Override public void showPlayer(Player player) {}

	@Override public void setFlying(boolean value) {}

}
