package atomicstryker.infernalmobs.util;

import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import atomicstryker.infernalmobs.config.ConfigStore;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Helper {

    private static List<ModifierDefinition> modifierDefinitions;
    private static List<Class<? extends MobModifier>> mobModifierClasses;

    static {
        modifierDefinitions = Arrays.stream(ModifierDefinition.values()).collect(Collectors.toList());
        mobModifierClasses = modifierDefinitions.stream()
                .map(ModifierDefinition::getModifierImplementation)
                .collect(Collectors.toList());
    }
    public static boolean isDimensionBlacklisted(LivingEntity entity) {
        ResourceKey<Level> worldRegistryKey = entity.getCommandSenderWorld().dimension();
        ResourceLocation worldResourceLocation = worldRegistryKey.location();

        return ConfigStore.getConfig().getDimensionIDBlackList().contains(worldResourceLocation.toString());
    }

    public static String getEntityClassName(LivingEntity entity){
        return ConfigStore.getConfig().isUseSimpleEntityClassNames() ? entity.getClass().getSimpleName() : getEntityNameSafe(entity);
    }

    public static List<Class<? extends MobModifier>> getMobModifierClasses() {
        return mobModifierClasses;
    }

    public static List<ModifierDefinition> getModifierDefinitions() {
        return modifierDefinitions;
    }

    private static String getEntityNameSafe(Entity entity) {
        String result;
        try {
            result = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).getPath();
        } catch (Exception e) {
            result = entity.getClass().getSimpleName();
            System.err.println("Entity of class " + result + " crashed when EntityList.getEntityString was queried, for shame! Using classname instead.");
            System.err.println("If this message is spamming too much for your taste set useSimpleEntityClassnames true in your Infernal Mobs config");
        }
        return result;
    }
}
