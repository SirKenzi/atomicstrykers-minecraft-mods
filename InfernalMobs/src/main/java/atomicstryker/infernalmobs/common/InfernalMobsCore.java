package atomicstryker.infernalmobs.common;

import atomicstryker.infernalmobs.command.InfernalCommandFindEntityClass;
import atomicstryker.infernalmobs.command.InfernalCommandSpawnInfernal;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.config.*;
import atomicstryker.infernalmobs.common.network.*;
import atomicstryker.infernalmobs.util.Helper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(InfernalMobsCore.MOD_ID)
@Mod.EventBusSubscriber(modid = InfernalMobsCore.MOD_ID)
public class InfernalMobsCore {

    public static final String MOD_ID = "infernalmobs";

    public static Logger LOGGER;
    private static InfernalMobsCore instance;
    private final long existCheckDelay = 5000L;
    public NetworkHelper networkHelper;
    private long nextExistCheckTime;
    private Entity infCheckA;
    private Entity infCheckB;
    private ArrayList<Enchantment> enchantmentList;
    /*
     * saves the last timestamp of long term affected players (eg choke) reset
     * the players by timer if the mod didn't remove them
     */
    private HashMap<String, Long> modifiedPlayerTimes;

    public InfernalMobsCore() {
        instance = this;

        nextExistCheckTime = System.currentTimeMillis();
        modifiedPlayerTimes = new HashMap<>();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        MinecraftForge.EVENT_BUS.register(new SaveEventHandler());

        networkHelper = new NetworkHelper("infernalmobs", MobModsPacket.class, HealthPacket.class, VelocityPacket.class, KnockBackPacket.class, AirPacket.class);

        LOGGER = LogManager.getLogger();
    }

    public static InfernalMobsCore instance() {
        return instance;
    }

    public static MobModifier getMobModifiers(LivingEntity ent) {
        return SidedCache.getInfernalMobs(ent.level).get(ent);
    }

    public static boolean isRareEntityOnline(LivingEntity ent) {
        return SidedCache.getInfernalMobs(ent.level).containsKey(ent);
    }

    public static boolean wasMobSpawnedBefore(LivingEntity ent) {
        // check if the entity previously passed infernal mob generation without getting a mod
        String storedInfernalTag = ent.getPersistentData().getString(instance().getNBTTag());
        boolean result = !storedInfernalTag.isEmpty() && instance().getNBTMarkerForNonInfernalEntities().equals(storedInfernalTag);
        if (result) {
            InfernalMobsCore.LOGGER.debug("entity {} was spawned in unmodified before, not modifying it", ent);
        }
        return result;
    }

    public static void setMobWasSpawnedBefore(LivingEntity ent) {
        // if infernal mobs decides not to give an entity mods, we still have to mark it to make sure it never goes through "spawning" again for infernal mobs
        ent.getPersistentData().putString(InfernalMobsCore.instance().getNBTTag(), instance().getNBTMarkerForNonInfernalEntities());
    }

    public static void clearAllElitesOfLevel(Level level) {
        SidedCache.getInfernalMobs(level).clear();
    }

    public static void removeEntFromElites(LivingEntity entity) {
        SidedCache.getInfernalMobs(entity.level).remove(entity);
    }

    public String getNBTTag() {
        return "InfernalMobsMod";
    }

    /**
     * as reloading worlds or savegames can "spawn" the same entities over and over, infernal mobs has to mark entities
     * nbt data as having passed through spawning before, even when no modifiers were applied
     */
    public String getNBTMarkerForNonInfernalEntities() {
        return "notInfernal";
    }

    @SubscribeEvent
    public void commonSetup(ServerStartedEvent evt) {
        // dedicated server starting point
        initIfNeeded(evt.getServer().getAllLevels().iterator().next());
    }

    /**
     * is triggered either by server start or by client login event from InfernalMobsClient
     */
    public void initIfNeeded(Level world) {
        if(Objects.isNull(ConfigStore.getConfig())){
            ConfigStore.load(world);
        }
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent evt) {
        evt.getDispatcher().register(InfernalCommandFindEntityClass.BUILDER);
        evt.getDispatcher().register(InfernalCommandSpawnInfernal.BUILDER);
    }

    /**
     * Called when an Entity is spawned by natural (Biome Spawning) means, turn
     * them into Elites here
     *
     * @param entity Entity in question
     */
    public void processEntitySpawn(LivingEntity entity) {
        if( entity.level.isClientSide || Objects.isNull(ConfigStore.getConfig())
                || Helper.isDimensionBlacklisted(entity)
                || isRareEntityOnline(entity)
                || wasMobSpawnedBefore(entity)
        ){
            return;
        }

        if (this.canEnemyTypeBecomeInfernal(entity) && (ConfigStore.isForcedToBecomeInfernal(entity) || entity.level.random.nextInt(ConfigStore.getConfig().getEliteRarity()) == 0)) {
            try {
                MobModifier mod = instance.createMobModifiers(entity);
                if (mod != null) {
                    SidedCache.getInfernalMobs(entity.level).put(entity, mod);
                    mod.onSpawningCompleteStoreModsAndBuffHealth(entity);
                }

            } catch (Exception e) {
                LOGGER.log(org.apache.logging.log4j.Level.ERROR, "processEntitySpawn() threw an exception");
                e.printStackTrace();
            }
        } else {
            setMobWasSpawnedBefore(entity);
        }
    }

    private boolean canEnemyTypeBecomeInfernal(LivingEntity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof TamableAnimal) {
                return false;
            }
            return ConfigStore.isEntityClassAllowedToBecomeInfernal(entity);
        }
        return false;
    }

    /**
     * Allows setting Entity Health past the hardcoded getMaxHealth() constraint
     *
     * @param entity Entity instance whose health you want changed
     * @param amount value to set
     */
    public void setEntityHealthPastMax(LivingEntity entity, float amount) {
        entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(amount);
        entity.setHealth(amount);
        instance.sendHealthPacket(entity);
    }

    /**
     * Decides on what, if any, of the possible Modifications to apply to the
     * Entity
     *
     * @param entity Target Entity
     * @return null or the first linked MobModifier instance for the Entity
     */
    private MobModifier createMobModifiers(LivingEntity entity) {
        /* 2-5 modifications standard */
        int number = 2 + entity.level.random.nextInt(3);
        /* lets just be lazy and scratch mods off a list copy */
        List<Class<? extends MobModifier>> possibleMods = new ArrayList<>(Helper.getMobModifierClasses());

        if (entity.level.random.nextInt(ConfigStore.getConfig().getUltraRarity()) == 0) // ultra mobs
        {
            number += 3 + entity.level.random.nextInt(2);

            if (entity.level.random.nextInt(ConfigStore.getConfig().getInfernoRarity()) == 0) // infernal
            // mobs
            {
                number += 3 + entity.level.random.nextInt(2);
            }
        }

        MobModifier lastMod = null;
        while (number > 0 && !possibleMods.isEmpty()) // so long we need more
        // and have some
        {
            /* random index of mod list */
            int index = entity.level.random.nextInt(possibleMods.size());
            MobModifier nextMod = null;

            /*
             * instanciate using one of the two constructors, chainlinking
             * modifiers as we go
             */
            try {
                if (lastMod == null) {
                    nextMod = possibleMods.get(index).getConstructor(new Class[]{}).newInstance();
                } else {
                    nextMod = possibleMods.get(index).getConstructor(new Class[]{MobModifier.class}).newInstance(lastMod);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean allowed = true;
            if (nextMod != null && nextMod.getBlackListMobClasses() != null) {
                for (Class<?> cl : nextMod.getBlackListMobClasses()) {
                    if (entity.getClass().isAssignableFrom(cl)) {
                        allowed = false;
                        break;
                    }
                }
            }
            if (lastMod != null) {
                if (lastMod.getModsNotToMixWith() != null) {
                    for (Class<?> cl : lastMod.getModsNotToMixWith()) {
                        if (lastMod.containsModifierClass(cl)) {
                            allowed = false;
                            break;
                        }
                    }
                }
            }

            /* scratch mod off list */
            possibleMods.remove(index);

            if (allowed) // so can we use it?
            {
                // link it, note that we need one less, repeat
                lastMod = nextMod;
                number--;
            }
        }

        return lastMod;
    }

    /**
     * Converts a String to MobModifier instances and connects them to an Entity
     *
     * @param entity    Target Entity
     * @param savedMods String depicting the MobModifiers, equal to the ingame Display
     */
    public void addEntityModifiersByString(LivingEntity entity, String savedMods) {
        if (!isRareEntityOnline(entity)) {
            // this can fire before the localhost client has logged in, loading a world save, need to init the mod!
            initIfNeeded(entity.level);
            MobModifier mod = stringToMobModifiers(savedMods);
            InfernalMobsCore.LOGGER.debug("reloading mods for {}: {}, mod instance {}", entity, savedMods, mod);
            if (mod != null) {
                SidedCache.getInfernalMobs(entity.level).put(entity, mod);
                mod.onSpawningCompleteStoreModsAndBuffHealth(entity);
            } else {
                System.err.println("Infernal Mobs error, could not instantiate modifier " + savedMods);
            }
        }
    }

    private MobModifier stringToMobModifiers(String buffer) {
        MobModifier lastMod = null;

        String[] tokens = buffer.split("\\s");
        for (int j = tokens.length - 1; j >= 0; j--) {
            String modName = tokens[j];

            MobModifier nextMod = null;
            for (Class<? extends MobModifier> c : Helper.getMobModifierClasses()) {
                /*
                 * instanciate using one of the two constructors, chainlinking
                 * modifiers as we go
                 */
                try {
                    if (lastMod == null) {
                        nextMod = c.getConstructor(new Class[]{}).newInstance();
                    } else {
                        nextMod = c.getConstructor(new Class[]{MobModifier.class}).newInstance(lastMod);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (nextMod != null && nextMod.getModName().equals(modName)) {
                    /*
                     * Only actually keep the new linked instance if it's what
                     * we wanted
                     */
                    lastMod = nextMod;
                    break;
                }
            }
        }

        return lastMod;
    }

    /**
     * Used by the client side to answer to a server packet carrying the Entity
     * ID and mod string
     *
     * @param world World the client is in, and the Entity aswell
     * @param entID unique Entity ID
     * @param mods  MobModifier compliant data String from the server
     */
    public void addRemoteEntityModifiers(Level world, int entID, String mods) {
        Entity ent = world.getEntity(entID);
        if (ent != null) {
            addEntityModifiersByString((LivingEntity) ent, mods);
            // System.out.println("Client added remote infernal mod on entity
            // "+ent+", is now "+mod.getModName());
        }
    }

    public void dropLootForEnt(LivingEntity mob, MobModifier mods) {
        int xpValue = 25;
        while (xpValue > 0) {
            int xpDrop = ExperienceOrb.getExperienceValue(xpValue);
            xpValue -= xpDrop;
            mob.level.addFreshEntity(new ExperienceOrb(mob.level, mob.getX(), mob.getY(), mob.getZ(), xpDrop));
        }

        dropRandomEnchantedItems(mob, mods);
    }

    private void dropRandomEnchantedItems(LivingEntity mob, MobModifier mods) {
        int modStr = mods.getModSize();
        /* 0 for elite, 1 for ultra, 2 for infernal */
        int prefix = (modStr <= 5) ? 0 : (modStr <= 10) ? 1 : 2;
        while (modStr > 0) {
            ItemStack itemStack = getRandomItem(mob, prefix);
            if (itemStack != null) {
                Item item = itemStack.getItem();
                if (item instanceof EnchantedBookItem) {
                    itemStack = EnchantedBookItem.createForEnchantment(getRandomEnchantment(mob.getRandom()));
                } else {
                    int usedStr = (modStr - 5 > 0) ? 5 : modStr;
                    enchantRandomly(mob.level.random, itemStack, item.getEnchantmentValue(), usedStr);
                    // EnchantmentHelper.addRandomEnchantment(mob.world.rand,
                    // itemStack, item.getItemEnchantability());
                }
                ItemEntity itemEnt = new ItemEntity(mob.level, mob.getX(), mob.getY(), mob.getZ(), itemStack);
                mob.level.addFreshEntity(itemEnt);
                modStr -= 5;
            } else {
                // fixes issue with empty drop lists
                modStr--;
            }
        }
    }

    private EnchantmentInstance getRandomEnchantment(RandomSource rand) {
        if (enchantmentList == null) {
            enchantmentList = new ArrayList<>(26); // 26 is the vanilla enchantment count as of 1.9
            for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
                if (enchantment != null) {
                    if (enchantment.getMinLevel() <= enchantment.getMaxLevel()) {
                        enchantmentList.add(enchantment);
                    } else {
                        LOGGER.error("enchantment " + enchantment.getClass().getCanonicalName() + " has min level > max level which is invalid behaviour!");
                    }
                }
            }
        }

        Enchantment e = enchantmentList.get(rand.nextInt(enchantmentList.size()));
        int min = e.getMinLevel();
        int range = e.getMaxLevel() - min;
        int lvl = min + rand.nextInt(range + 1);
        EnchantmentInstance ed = new EnchantmentInstance(e, lvl);
        return ed;
    }

    /**
     * Custom Enchanting Helper
     *
     * @param rand               Random gen to use
     * @param itemStack          ItemStack to be enchanted
     * @param itemEnchantability ItemStack max enchantability level
     * @param modStr             MobModifier strength to be used. Should be in range 2-5
     */
    private void enchantRandomly(RandomSource rand, ItemStack itemStack, int itemEnchantability, int modStr) {
        int remainStr = (modStr + 1) / 2; // should result in 1-3
        List<?> enchantments = EnchantmentHelper.selectEnchantment(rand, itemStack, itemEnchantability, true);
        Iterator<?> iter = enchantments.iterator();
        while (iter.hasNext() && remainStr > 0) {
            remainStr--;
            EnchantmentInstance eData = (EnchantmentInstance) iter.next();
            itemStack.enchant(eData.enchantment, eData.level);
        }
    }

    /**
     * @param mob    Infernal Entity
     * @param prefix 0 for Elite rarity, 1 for Ultra and 2 for Infernal
     * @return ItemStack instance to drop to the World
     */
    private ItemStack getRandomItem(LivingEntity mob, int prefix) {
        List<ItemStack> list = ConfigStore.getLootList(prefix);
        return list.size() > 0 ? list.get(mob.level.random.nextInt(list.size())).copy() : null;
    }

    public void sendVelocityPacket(ServerPlayer target, float xVel, float yVel, float zVel) {
        if (getIsEntityAllowedTarget(target)) {
            networkHelper.sendPacketToPlayer(new VelocityPacket(xVel, yVel, zVel), target);
        }
    }

    public void sendKnockBackPacket(ServerPlayer target, float xVel, float zVel) {
        if (getIsEntityAllowedTarget(target)) {
            networkHelper.sendPacketToPlayer(new KnockBackPacket(xVel, zVel), target);
        }
    }

    public void sendHealthPacket(LivingEntity mob) {
        networkHelper.sendPacketToAllAroundPoint(new HealthPacket("", mob.getId(), mob.getHealth(), mob.getMaxHealth()), new PacketDistributor.TargetPoint(mob.getX(), mob.getY(), mob.getZ(), 32d, mob.getCommandSenderWorld().dimension()));
    }

    public void sendHealthRequestPacket(String playerName, LivingEntity mob) {
        networkHelper.sendPacketToServer(new HealthPacket(playerName, mob.getId(), 0f, 0f));
    }

    public void sendAirPacket(ServerPlayer target, int lastAir) {
        if (getIsEntityAllowedTarget(target)) {
            networkHelper.sendPacketToPlayer(new AirPacket(lastAir), target);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.LevelTickEvent tick) {
        if (System.currentTimeMillis() > nextExistCheckTime) {
            nextExistCheckTime = System.currentTimeMillis() + existCheckDelay;
            Map<LivingEntity, MobModifier> mobsmap = SidedCache.getInfernalMobs(tick.level);
            // System.out.println("Removed unloaded Entity "+mob+" with ID
            // "+mob.getEntityId()+" from rareMobs");
            mobsmap.keySet().stream().filter(this::filterMob).forEach(InfernalMobsCore::removeEntFromElites);

            resetModifiedPlayerEntitiesAsNeeded(tick.level);
        }

        if (!tick.level.isClientSide) {
            infCheckA = null;
            infCheckB = null;
        }
    }

    private boolean filterMob(LivingEntity mob) {
        return !mob.isAlive();
    }

    private void resetModifiedPlayerEntitiesAsNeeded(Level world) {
        Iterator<Map.Entry<String, Long>> iterator = modifiedPlayerTimes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (System.currentTimeMillis() > entry.getValue() + (existCheckDelay * 2)) {
                String username = entry.getKey();
                for (Player player : world.players()) {
                    if (player.getName().getString().equals(username)) {
                        for (Class<? extends MobModifier> c : Helper.getMobModifierClasses()) {
                            try {
                                MobModifier mod = c.getConstructor(new Class[]{}).newInstance();
                                mod.resetModifiedVictim(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                iterator.remove();
            }
        }
    }

    public float getLimitedDamage(float test) {
        return (float) Math.min(test, ConfigStore.getConfig().getMaxDamage());
    }

    public boolean getIsEntityAllowedTarget(Entity entity) {
        return !(entity instanceof FakePlayer);
    }

    /**
     * By caching the last reflection pairing we make sure it doesn't trigger
     * more than once (reflections battling each other, infinite loop, crash)
     *
     * @return true when inf loop is suspected, false otherwise
     */
    public boolean isInfiniteLoop(LivingEntity mob, Entity entity) {
        if ((mob == infCheckA && entity == infCheckB) || (mob == infCheckB && entity == infCheckA)) {
            return true;
        }
        infCheckA = mob;
        infCheckB = entity;
        return false;
    }

    /**
     * add modified player entities to this map with the current time. a timer
     * will call a reset on the players to the modifier class. do not remove
     * players from here in a modifier as aliasing may occur (different mods
     * using this at the same time)
     */
    public HashMap<String, Long> getModifiedPlayerTimes() {
        return modifiedPlayerTimes;
    }

}
