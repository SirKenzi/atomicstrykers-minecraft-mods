package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Bulwark extends MobModifier {

    public MM_Bulwark(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.BULWARK;
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        return super.onHurt(mob, source, Math.max(damage / 2, 1));
    }
}
