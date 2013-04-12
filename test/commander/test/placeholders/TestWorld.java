package commander.test.placeholders;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation") 
public class TestWorld implements World {
	
	TestServer myserver;
	
	public TestWorld(TestServer server) {
		this.myserver = server;
	}
	
	////////////////////////////////////////////////////////////////////
	
	@Override public String getName() {
		return "TestWorld";
	}
	@Override public List<Player> getPlayers() {
		return Arrays.asList(myserver.getOnlinePlayers());
	}
	@Override public int getSeaLevel() {
		return 64;
	}
	@Override public long getTime() {
		return 1000203;
	}
	
	////////////////////////////////////////////////////////////////////
	
	@Override public Set<String> getListeningPluginChannels() {
		return null;
	}

	@Override public void sendPluginMessage(Plugin arg0, String arg1,
			byte[] arg2) {}

	@Override public List<MetadataValue> getMetadata(String arg0) {
		return null;
	}

	@Override public boolean hasMetadata(String arg0) {
		return false;
	}

	@Override public void removeMetadata(String arg0, Plugin arg1) {}

	@Override public void setMetadata(String arg0, MetadataValue arg1) {}

	@Override public boolean canGenerateStructures() {
		return false;
	}

	@Override public boolean createExplosion(Location arg0, float arg1) {
		return false;
	}

	@Override public boolean createExplosion(Location arg0, float arg1,
			boolean arg2) {
		return false;
	}

	@Override public boolean createExplosion(double arg0, double arg1,
			double arg2, float arg3) {
		return false;
	}

	@Override public boolean createExplosion(double arg0, double arg1,
			double arg2, float arg3, boolean arg4) {
		return false;
	}

	@Override public Item dropItem(Location arg0, ItemStack arg1) {
		return null;
	}

	@Override public Item dropItemNaturally(Location arg0, ItemStack arg1) {
		return null;
	}

	@Override public boolean generateTree(Location arg0, TreeType arg1) {
		return false;
	}

	@Override public boolean generateTree(Location arg0, TreeType arg1,
			BlockChangeDelegate arg2) {
		return false;
	}

	@Override public boolean getAllowAnimals() {
		return false;
	}

	@Override public boolean getAllowMonsters() {
		return false;
	}

	@Override public int getAnimalSpawnLimit() {
		return 0;
	}

	@Override public Biome getBiome(int arg0, int arg1) {
		return null;
	}

	@Override public Block getBlockAt(Location arg0) {
		return null;
	}

	@Override public Block getBlockAt(int arg0, int arg1, int arg2) {
		return null;
	}

	@Override public int getBlockTypeIdAt(Location arg0) {
		return 0;
	}

	@Override public int getBlockTypeIdAt(int arg0, int arg1, int arg2) {
		return 0;
	}

	@Override public Chunk getChunkAt(Location arg0) {
		return null;
	}

	@Override public Chunk getChunkAt(Block arg0) {
		return null;
	}

	@Override public Chunk getChunkAt(int arg0, int arg1) {
		return null;
	}

	@Override public Difficulty getDifficulty() {
		return null;
	}

	@Override public ChunkSnapshot getEmptyChunkSnapshot(int arg0, int arg1,
			boolean arg2, boolean arg3) {
		return null;
	}

	@Override public List<Entity> getEntities() {
		return null;
	}

	@Override @Deprecated public <T extends Entity> Collection<T> getEntitiesByClass(
			Class<T>... arg0) {
		return null;
	}

	@Override public <T extends Entity> Collection<T> getEntitiesByClass(
			Class<T> arg0) {
		return null;
	}

	@Override public Collection<Entity> getEntitiesByClasses(Class<?>... arg0) {
		return null;
	}

	@Override public Environment getEnvironment() {
		return null;
	}

	@Override public long getFullTime() {
		return 0;
	}

	@Override public ChunkGenerator getGenerator() {
		return null;
	}

	@Override public Block getHighestBlockAt(Location arg0) {
		return null;
	}

	@Override public Block getHighestBlockAt(int arg0, int arg1) {
		return null;
	}

	@Override public int getHighestBlockYAt(Location arg0) {
		return 0;
	}

	@Override public int getHighestBlockYAt(int arg0, int arg1) {
		return 0;
	}

	@Override public double getHumidity(int arg0, int arg1) {
		return 0;
	}

	@Override public boolean getKeepSpawnInMemory() {
		return false;
	}

	@Override public List<LivingEntity> getLivingEntities() {
		return null;
	}

	@Override public Chunk[] getLoadedChunks() {
		return null;
	}

	@Override public int getMaxHeight() {
		return 0;
	}

	@Override public int getMonsterSpawnLimit() {
		return 0;
	}

	@Override public boolean getPVP() {
		return false;
	}

	@Override public List<BlockPopulator> getPopulators() {
		return null;
	}

	@Override public long getSeed() {
		return 0;
	}

	@Override public Location getSpawnLocation() {
		return null;
	}

	@Override public double getTemperature(int arg0, int arg1) {
		return 0;
	}

	@Override public int getThunderDuration() {
		return 0;
	}

	@Override public long getTicksPerAnimalSpawns() {
		return 0;
	}

	@Override public long getTicksPerMonsterSpawns() {
		return 0;
	}

	@Override public UUID getUID() {
		return null;
	}

	@Override public int getWaterAnimalSpawnLimit() {
		return 0;
	}

	@Override public int getWeatherDuration() {
		return 0;
	}

	@Override public File getWorldFolder() {
		return null;
	}

	@Override public WorldType getWorldType() {
		return null;
	}

	@Override public boolean hasStorm() {
		return false;
	}

	@Override public boolean isAutoSave() {
		return false;
	}

	@Override public boolean isChunkInUse(int arg0, int arg1) {
		return false;
	}

	@Override public boolean isChunkLoaded(Chunk arg0) {
		return false;
	}

	@Override public boolean isChunkLoaded(int arg0, int arg1) {
		return false;
	}

	@Override public boolean isThundering() {
		return false;
	}

	@Override public void loadChunk(Chunk arg0) {}

	@Override public void loadChunk(int arg0, int arg1) {}

	@Override public boolean loadChunk(int arg0, int arg1, boolean arg2) {
		return false;
	}

	@Override public void playEffect(Location arg0, Effect arg1, int arg2) {}

	@Override public <T> void playEffect(Location arg0, Effect arg1, T arg2) {}

	@Override public void playEffect(Location arg0, Effect arg1, int arg2,
			int arg3) {}

	@Override public <T> void playEffect(Location arg0, Effect arg1, T arg2,
			int arg3) {}

	@Override public boolean refreshChunk(int arg0, int arg1) {
		return false;
	}

	@Override public boolean regenerateChunk(int arg0, int arg1) {
		return false;
	}

	@Override public void save() {}

	@Override public void setAnimalSpawnLimit(int arg0) {}

	@Override public void setAutoSave(boolean arg0) {}

	@Override public void setBiome(int arg0, int arg1, Biome arg2) {}

	@Override public void setDifficulty(Difficulty arg0) {}

	@Override public void setFullTime(long arg0) {}

	@Override public void setKeepSpawnInMemory(boolean arg0) {}

	@Override public void setMonsterSpawnLimit(int arg0) {}

	@Override public void setPVP(boolean arg0) {}

	@Override public void setSpawnFlags(boolean arg0, boolean arg1) {}

	@Override public boolean setSpawnLocation(int arg0, int arg1, int arg2) {
		return false;
	}

	@Override public void setStorm(boolean arg0) {}

	@Override public void setThunderDuration(int arg0) {}

	@Override public void setThundering(boolean arg0) {}

	@Override public void setTicksPerAnimalSpawns(int arg0) {}

	@Override public void setTicksPerMonsterSpawns(int arg0) {}

	@Override public void setTime(long arg0) {}

	@Override public void setWaterAnimalSpawnLimit(int arg0) {}

	@Override public void setWeatherDuration(int arg0) {}

	@Override public <T extends Entity> T spawn(Location arg0, Class<T> arg1)
			throws IllegalArgumentException {
		return null;
	}

	@Override public Arrow spawnArrow(Location arg0, Vector arg1, float arg2,
			float arg3) {
		return null;
	}

	@Override @Deprecated public LivingEntity spawnCreature(Location arg0,
			EntityType arg1) {
		return null;
	}

	@Override @Deprecated public LivingEntity spawnCreature(Location arg0,
			CreatureType arg1) {
		return null;
	}

	@Override public Entity spawnEntity(Location arg0, EntityType arg1) {
		return null;
	}

	@Override public FallingBlock spawnFallingBlock(Location arg0,
			Material arg1, byte arg2) throws IllegalArgumentException {
		return null;
	}

	@Override public FallingBlock spawnFallingBlock(Location arg0, int arg1,
			byte arg2) throws IllegalArgumentException {
		return null;
	}

	@Override public LightningStrike strikeLightning(Location arg0) {
		return null;
	}

	@Override public LightningStrike strikeLightningEffect(Location arg0) {
		return null;
	}

	@Override public boolean unloadChunk(Chunk arg0) {
		return false;
	}

	@Override public boolean unloadChunk(int arg0, int arg1) {
		return false;
	}

	@Override public boolean unloadChunk(int arg0, int arg1, boolean arg2) {
		return false;
	}

	@Override public boolean unloadChunk(int arg0, int arg1, boolean arg2,
			boolean arg3) {
		return false;
	}

	@Override public boolean unloadChunkRequest(int arg0, int arg1) {
		return false;
	}

	@Override public boolean unloadChunkRequest(int arg0, int arg1, boolean arg2) {
		return false;
	}

	@Override public boolean createExplosion(double arg0, double arg1,
			double arg2, float arg3, boolean arg4, boolean arg5) {
		return false;
	}

	@Override public int getAmbientSpawnLimit() {
		return 0;
	}

	@Override public String getGameRuleValue(String arg0) {
		return null;
	}

	@Override public String[] getGameRules() {
		return null;
	}

	@Override public boolean isGameRule(String arg0) {
		return false;
	}

	@Override public void playSound(Location arg0, Sound arg1, float arg2,
			float arg3) {}

	@Override public void setAmbientSpawnLimit(int arg0) {}

	@Override public boolean setGameRuleValue(String arg0, String arg1) {
		return false;
	}

}
