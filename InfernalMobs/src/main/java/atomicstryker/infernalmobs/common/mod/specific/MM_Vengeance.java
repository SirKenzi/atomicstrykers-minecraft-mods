package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Vengeance extends MobModifier {

    public MM_Vengeance(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.VENGEANCE;
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
