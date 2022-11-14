package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MM_Quicksand extends MobModifier {
    int ticker = 0;

    public MM_Quicksand() {
        super();
    }

    public MM_Quicksand(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.QUICKSAND;
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
