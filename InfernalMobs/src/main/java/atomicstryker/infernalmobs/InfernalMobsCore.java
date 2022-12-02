package atomicstryker.infernalmobs;

import atomicstryker.infernalmobs.command.InfernalCommandFindEntityClass;
import atomicstryker.infernalmobs.command.InfernalCommandSpawnInfernal;
import atomicstryker.infernalmobs.common.effect.InfernalEffects;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.InfernalMonsterGenerator;
import atomicstryker.infernalmobs.common.network.packet.*;
import atomicstryker.infernalmobs.common.network.packet.information.HealthInformationPacket;
import atomicstryker.infernalmobs.common.network.packet.information.MobModifiersInformationPacket;
import atomicstryker.infernalmobs.common.network.packet.information.RequestHealthInformationPacket;
import atomicstryker.infernalmobs.common.network.packet.information.RequestMobModifiersInformationPacket;
import atomicstryker.infernalmobs.common.sound.InfernalSounds;
import atomicstryker.infernalmobs.event.EntityEventHandler;
import atomicstryker.infernalmobs.event.InfernalEntityDropHandler;
import atomicstryker.infernalmobs.config.*;
import atomicstryker.infernalmobs.common.network.*;
import atomicstryker.infernalmobs.event.SaveEventHandler;
import atomicstryker.infernalmobs.event.StunEventHandler;
import atomicstryker.infernalmobs.util.Helper;
import atomicstryker.infernalmobs.util.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    private long nextExistCheckTime;
    private Entity infCheckA;
    private Entity infCheckB;

    public InfernalMobsCore() {
        instance = this;

        nextExistCheckTime = System.currentTimeMillis();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        MinecraftForge.EVENT_BUS.register(new InfernalEntityDropHandler());
        MinecraftForge.EVENT_BUS.register(new SaveEventHandler());
        MinecraftForge.EVENT_BUS.register(new StunEventHandler());
        InfernalSounds.register(eventBus);
        InfernalEffects.register(eventBus);
        Network.register(MOD_ID, Set.of(RequestMobModifiersInformationPacket.class, RequestHealthInformationPacket.class,
                MobModifiersInformationPacket.class, HealthInformationPacket.class,
                VelocityPacket.class, KnockBackPacket.class, AirPacket.class)
        );

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
        PacketSender.sendHealthInformationPacketToSurroundingPlayers(entity);
    }

    /**
     * Converts a String to MobModifier instances and connects them to an Entity
     *
     * @param entity    Target Entity
     * @param savedMods String depicting the MobModifiers, equal to the ingame Display
     */
    public void addModifiersToEntityFromString(LivingEntity entity, String savedMods) {
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

    @SubscribeEvent
    public void onTick(TickEvent.LevelTickEvent tick) {
        if (System.currentTimeMillis() > nextExistCheckTime) {
            nextExistCheckTime = System.currentTimeMillis() + existCheckDelay;
            this.removeDeadMonstersFromCache(tick.level);
        }

        if (!tick.level.isClientSide) {
            infCheckA = null;
            infCheckB = null;
        }
    }

    private void removeDeadMonstersFromCache(Level dimension){
        Cache.getInfernalMonsters(dimension)
                .keySet()
                .removeIf( entity -> !entity.isAlive());
    }

    public float getLimitedDamage(float test) {
        return (float) Math.min(test, ConfigStore.getConfig().getMaxDamage());
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
