package atomicstryker.infernalmobs.util;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NameGenerator {

    public static List<String> getDisplayedModifierNames(InfernalMonster infernalMonster) {
        int groupSize = 4;
        List<String> modifiers = infernalMonster
                .getModifiers()
                .stream()
                .map(MobModifier::getModifierDefinition)
                .map(definition -> I18n.get(Tag.TRANSLATION_MOD_KEY.getId() + definition.getId()))
                .collect(Collectors.toList());

        //Split modifiers into rows with at most 5 modifiers in each row
        return IntStream.range(0, (modifiers.size() + groupSize - 1) / groupSize)
                .mapToObj(i -> modifiers.subList(groupSize * i, Math.min(groupSize * i + groupSize, modifiers.size())))
                .map( groups -> groups.stream().collect(Collectors.joining(" ")))
                .collect(Collectors.toList());
    }

    public static String getEntityDisplayName(InfernalMonster monster) {
        RandomSource randomSource = monster.getEntity().getRandom();
        String entityName = getEntityName(monster.getEntity());
        String type = getTypeName(monster.getRarity());

        if( monster.getModifiers().size() > 1 ) {
            List<String> prefixes = monster.getModifiers().get(0).getModifierDefinition().getPrefixes();
            String prefix = I18n.get(Tag.TRANSLATION_PREFIX_KEY.getId() + prefixes.get(randomSource.nextInt(prefixes.size())));
            List<String> suffixes = monster.getModifiers().get(1).getModifierDefinition().getSuffixes();
            String suffix = I18n.get(Tag.TRANSLATION_SUFFIX_KEY.getId() + suffixes.get(randomSource.nextInt(suffixes.size())));
            return String.format("%s %s %s %s", prefix, suffix, type, entityName);
        }

        return String.format("%s %s", type,  entityName);
    }

    private static String getEntityName(LivingEntity entity){
        if( entity.getDisplayName().getContents() instanceof TranslatableContents){
            return I18n.get(((TranslatableContents)entity.getDisplayName().getContents()).getKey());
        }
        if( entity.getDisplayName().getContents() instanceof LiteralContents){
            return I18n.get(((TranslatableContents)entity.getDisplayName().getContents()).getKey());
        }
        return entity.getDisplayName().toString();
    }

    private static String getTypeName(MobRarity rarity){
        return rarity == MobRarity.UNCOMMON ? ChatFormatting.AQUA + I18n.get("translation.infernalmobs:rareClass") :
               rarity == MobRarity.RARE ? ChatFormatting.YELLOW + I18n.get("translation.infernalmobs:ultraClass") :
               ChatFormatting.GOLD + I18n.get("translation.infernalmobs:infernalClass");
    }
}
