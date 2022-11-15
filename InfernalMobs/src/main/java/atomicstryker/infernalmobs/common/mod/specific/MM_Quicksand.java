package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MM_Quicksand extends MobModifier {
    int ticker = 0;

    public MM_Quicksand(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.QUICKSAND;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        if (hasSteadyTarget()
                && InfernalMobsCore.instance().getIsEntityAllowedTarget(getMobTarget())
                && canMobSeeTarget(mob, getMobTarget())
                && ++ticker == 50) {
            ticker = 0;
            getMobTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 45, 0));
        }

        return super.onUpdate(mob);
    }

}
