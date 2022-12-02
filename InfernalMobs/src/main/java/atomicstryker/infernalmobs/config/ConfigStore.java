package atomicstryker.infernalmobs.config;

import atomicstryker.infernalmobs.common.mod.MobRarity;
import atomicstryker.infernalmobs.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigStore {

    public static Logger LOGGER = LogManager.getLogger(ConfigStore.class);
    private static final String CONFIG_DIRECTORY_NAME = "config";
    private static final String CONFIG_FILE_NAME =  "infernalmobs.cfg";

    private static InfernalMobsConfig config;
    private static File configFile;
    private static ItemConfigHelper lootItemDropsElite;
    private static ItemConfigHelper lootItemDropsUltra;
    private static ItemConfigHelper lootItemDropsInfernal;

    private static final HashMap<String, Boolean> classesAllowedMap = new HashMap<>();
    private static final HashMap<String, Boolean> classesForcedMap = new HashMap<>();
    private static final HashMap<String, Double> classesHealthMap = new HashMap<>();

    public static void load(Level world){
        if(Objects.isNull(config)) {
            configFile = new File(getMinecraftFolder(world), File.separatorChar + CONFIG_DIRECTORY_NAME + File.separatorChar + CONFIG_FILE_NAME);
            loadConfig();
        }
    }

    private static File getMinecraftFolder(Level world){
        if (world.isClientSide()) {
            return Minecraft.getInstance().gameDirectory;
        } else {
            return ServerLifecycleHooks.getCurrentServer().getFile("");
        }
    }

    private static void loadConfig() {
        config = GsonConfig.loadConfigWithDefault(InfernalMobsConfig.class, configFile, DefaultConfig.getDefaultConfig());

        lootItemDropsElite = new ItemConfigHelper(config.getDroppedItemIDsElite(), LOGGER);
        lootItemDropsUltra = new ItemConfigHelper(config.getDroppedItemIDsUltra(), LOGGER);
        lootItemDropsInfernal = new ItemConfigHelper(config.getDroppedItemIDsInfernal(), LOGGER);

        Helper.getMobModifierClasses().removeIf(c ->
                !config.getModsEnabled().containsKey(c.getSimpleName()) || !config.getModsEnabled().get(c.getSimpleName())
        );
    }

    public static InfernalMobsConfig getConfig() {
        return config;
    }

    public static List<ItemStack> getLootList(MobRarity rarity){
        return switch (rarity) {
            case UNCOMMON -> lootItemDropsElite.getItemStackList();
            case RARE -> lootItemDropsUltra.getItemStackList();
            case EPIC -> lootItemDropsInfernal.getItemStackList();
        };
    }

    public static void saveConfig(){
        GsonConfig.saveConfig(config, configFile);
    }


    public static boolean isEntityClassAllowedToBecomeInfernal(LivingEntity entity) {
        String entName = Helper.getEntityClassName(entity);
        if (classesAllowedMap.containsKey(entName)) {
            return classesAllowedMap.get(entName);
        }

        boolean result = true;
        if (!getConfig().getPermittedentities().containsKey(entName)) {
            getConfig().getPermittedentities().put(entName, true);
            saveConfig();
        } else {
            result = getConfig().getPermittedentities().get(entName);
        }
        classesAllowedMap.put(entName, result);
        return result;
    }

    public static boolean isForcedToBecomeInfernal(LivingEntity entity) {
        String entName = Helper.getEntityClassName(entity);
        if (classesForcedMap.containsKey(entName)) {
            return classesForcedMap.get(entName);
        }

        boolean result = false;
        if (!getConfig().getEntitiesalwaysinfernal().containsKey(entName)) {
            getConfig().getEntitiesalwaysinfernal().put(entName, false);
            saveConfig();
        } else {
            result = getConfig().getEntitiesalwaysinfernal().get(entName);
        }
        classesForcedMap.put(entName, result);
        return result;
    }

    public static double getMobClassMaxHealth(LivingEntity entity) {
        String entName = Helper.getEntityClassName(entity);
        if (classesHealthMap.containsKey(entName)) {
            return classesHealthMap.get(entName);
        }

        double result;
        if (getConfig().getEntitybasehealth().containsKey(entName)) {
            result = getConfig().getEntitybasehealth().get(entName);
        } else {
            result = entity.getMaxHealth();
            getConfig().getEntitybasehealth().put(entName, result);
            saveConfig();
        }
        classesHealthMap.put(entName, result);
        return result;
    }
}