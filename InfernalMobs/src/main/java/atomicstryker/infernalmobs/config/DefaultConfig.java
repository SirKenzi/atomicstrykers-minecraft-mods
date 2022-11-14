package atomicstryker.infernalmobs.config;

import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class DefaultConfig {

    public static InfernalMobsConfig getDefaultConfig(){
        InfernalMobsConfig defaultConfig = new InfernalMobsConfig();
        defaultConfig.setEliteRarity(15);
        defaultConfig.setUltraRarity(7);
        defaultConfig.setInfernoRarity(7);
        defaultConfig.setUseSimpleEntityClassNames(true);
        defaultConfig.setDisableHealthBar(false);
        defaultConfig.setModHealthFactor(1.0D);

        List<String> dropsElite = new ArrayList<>();
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_SHOVEL)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_PICKAXE)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_AXE)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_SWORD)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_HOE)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_HELMET)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_BOOTS)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_CHESTPLATE)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_LEGGINGS)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_HELMET)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_BOOTS)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_CHESTPLATE)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_LEGGINGS)));
        dropsElite.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.COOKIE, 5)));
        defaultConfig.setDroppedItemIDsElite(dropsElite);

        List<String> dropsUltra = new ArrayList<>();
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_HOE)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.BOW)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_HELMET)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_BOOTS)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_CHESTPLATE)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_LEGGINGS)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_HELMET)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_BOOTS)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_CHESTPLATE)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.IRON_LEGGINGS)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.GOLDEN_HELMET)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.GOLDEN_BOOTS)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.GOLDEN_CHESTPLATE)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.GOLDEN_LEGGINGS)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.GOLDEN_APPLE, 3)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.BLAZE_POWDER, 5)));
        dropsUltra.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.ENCHANTED_BOOK)));
        defaultConfig.setDroppedItemIDsUltra(dropsUltra);

        List<String> dropsInfernal = new ArrayList<>();
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.ENCHANTED_BOOK)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND, 3)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_SWORD)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_AXE)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_HOE)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_PICKAXE)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_SHOVEL)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_HELMET)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_BOOTS)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_CHESTPLATE)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.CHAINMAIL_LEGGINGS)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_HELMET)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_BOOTS)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_CHESTPLATE)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.DIAMOND_LEGGINGS)));
        dropsInfernal.add(ItemConfigHelper.fromItemStack(new ItemStack(Items.ENDER_PEARL, 3)));
        defaultConfig.setDroppedItemIDsInfernal(dropsInfernal);

        defaultConfig.setMaxDamage(10D);
        defaultConfig.setDimensionIDBlackList(new ArrayList<>());

        Map<String, Boolean> modsEnabledMap = new HashMap<>();
        Arrays.stream(MobModifierType.values()).map(MobModifierType::getModifierTypeClass).forEach(aClass -> {
            modsEnabledMap.put(aClass.getSimpleName(), true);
        });
        defaultConfig.setModsEnabled(modsEnabledMap);
        return defaultConfig;
    }

}
