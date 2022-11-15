package atomicstryker.infernalmobs;

import atomicstryker.infernalmobs.command.InfernalCommandFindEntityClass;
import atomicstryker.infernalmobs.command.InfernalCommandSpawnInfernal;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.InfernalMonsterGenerator;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import atomicstryker.infernalmobs.event.EntityEventHandler;
import atomicstryker.infernalmobs.event.InfernalEntityDropHandler;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.config.*;
import atomicstryker.infernalmobs.common.network.*;
import atomicstryker.infernalmobs.event.SaveEventHandler;
import atomicstryker.infernalmobs.util.Helper;
import atomicstryker.infernalmobs.util.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

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

    public InfernalMobsCore() {
        instance = this;

        nextExistCheckTime = System.currentTimeMillis();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        MinecraftForge.EVENT_BUS.register(new InfernalEntityDropHandler());
        MinecraftForge.EVENT_BUS.register(new SaveEventHandler());

        networkHelper = new NetworkHelper("infernalmobs", MobModsPacket.class, HealthPacket.class, VelocityPacket.class, KnockBackPacket.class, AirPacket.class);

        LOGGER = LogManager.getLogger();
    }

    public static InfernalMobsCore instance() {
        return instance;
    }

    public static boolean isRareEntityOnline(LivingEntity ent) {
        return Cache.getInfernalMonsters(ent.level).containsKey(ent);
    }

    public static boolean wasMobSpawnedBefore(LivingEntity ent) {
        // check if the entity previously passed infernal mob generation without getting a mod
        String storedInfernalTag = ent.getPersistentData().getString(Tag.NBT_TAG.getId());
        boolean result = !storedInfernalTag.isEmpty() && instance().getNBTMarkerForNonInfernalEntities().equals(storedInfernalTag);
        if (result) {
            InfernalMobsCore.LOGGER.debug("entity {} was spawned in unmodified before, not modifying it", ent);
        }
        return result;
    }

    public static void setMobWasSpawnedBefore(LivingEntity ent) {
        // if infernal mobs decides not to give an entity mods, we still have to mark it to make sure it never goes through "spawning" again for infernal mobs
        ent.getPersistentData().putString(Tag.NBT_TAG.getId(), instance().getNBTMarkerForNonInfernalEntities());
    }

    public static void removeEntFromElites(LivingEntity entity) {
        Cache.getInfernalMonsters(entity.level).remove(entity);
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
        ConfigStore.load(evt.getServer().getAllLevels().iterator().next());
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
                InfernalMonster infernalMonster = InfernalMonsterGenerator.generate(entity);
                infernalMonster.init();
            } catch (Exception e) {
                LOGGER.error("processEntitySpawn() threw an exception", e);
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
     * Converts a String to MobModifier instances and connects them to an Entity
     *
     * @param entity    Target Entity
     * @param savedMods String depicting the MobModifiers, equal to the ingame Display
     */
    public void addEntityModifiersByString(LivingEntity entity, String savedMods) {
        if (isRareEntityOnline(entity)) {
            return;
        }
        try{
            // this can fire before the localhost client has logged in, loading a world save, need to init the mod!
            ConfigStore.load(entity.level);
            InfernalMonster monster = InfernalMonsterGenerator.fromString(entity, savedMods);
            InfernalMobsCore.LOGGER.debug("reloading mods for {}: {}, mod instance {}", entity, savedMods, monster);
            monster.init();
        } catch (Exception e) {
            LOGGER.error("addEntityModifiersByString() threw an exception", e);
        }
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
        }
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
            Map<LivingEntity, InfernalMonster> mobsmap = Cache.getInfernalMonsters(tick.level);
            mobsmap.keySet().stream().filter(this::filterMob).forEach(InfernalMobsCore::removeEntFromElites);
        }

        if (!tick.level.isClientSide) {
            infCheckA = null;
            infCheckB = null;
        }
    }

    private boolean filterMob(LivingEntity mob) {
        return !mob.isAlive();
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
}
