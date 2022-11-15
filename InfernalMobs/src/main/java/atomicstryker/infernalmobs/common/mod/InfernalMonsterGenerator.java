package atomicstryker.infernalmobs.common.mod;

import atomicstryker.infernalmobs.util.Helper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InfernalMonsterGenerator {

    public static InfernalMonster generate(LivingEntity entity) throws Exception{
        RandomSource randomSource = entity.level.random;
        InfernalMonster infernalMonster = new InfernalMonster(entity, MobRarity.calculate(randomSource));
        int numberOfModifiers = infernalMonster.getRarity().calculateNumberOfModifiers(randomSource);

        //todo: blacklisted mods entity

        List<ModifierDefinition> modPool = new ArrayList<>(Helper.getModifierDefinitions());
        while( numberOfModifiers-- > 0){
            ModifierDefinition modifierDefinition = modPool.remove(randomSource.nextInt(modPool.size()));
            if( modifierDefinition.getType().isExclusive()){
                modPool.removeIf( unassignedModifier -> unassignedModifier.getType().equals(modifierDefinition.getType()));
            }
            infernalMonster.getModifiers().add(modifierDefinition.getModifierImplementation().getConstructor(new Class[]{InfernalMonster.class}).newInstance(infernalMonster));
        }


        return infernalMonster;
    }

    public static InfernalMonster fromString(LivingEntity entity, String modifiers) throws Exception{
            String[] tokens = modifiers.split("\\s");
            InfernalMonster infernalMonster = new InfernalMonster(entity, MobRarity.fromNumberOfModifiers(tokens.length));
            List<ModifierDefinition> definitions = Arrays.stream(tokens).map(ModifierDefinition::fromId).collect(Collectors.toList());
            for( ModifierDefinition definition : definitions ){
                    infernalMonster.getModifiers().add(definition.getModifierImplementation().getConstructor(new Class[]{InfernalMonster.class})
                            .newInstance(infernalMonster)
                    );
            }
            return infernalMonster;
    }

}
