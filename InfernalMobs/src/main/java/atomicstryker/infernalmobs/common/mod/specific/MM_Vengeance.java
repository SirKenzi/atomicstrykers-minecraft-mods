package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Vengeance extends MobModifier {

    public MM_Vengeance() {
        super();
    }

    public MM_Vengeance(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.VENGEANCE;
    }

    @Override
    public String getModName() {
        return "Vengeance";
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && source.getEntity() != mob
                && !InfernalMobsCore.instance().isInfiniteLoop(mob, source.getEntity())) {
            source.getEntity().hurt(DamageSource.mobAttack(mob),
                    InfernalMobsCore.instance().getLimitedDamage(Math.max(damage / 2, 1)));
        }

        return super.onHurt(mob, source, damage);
    }

}
