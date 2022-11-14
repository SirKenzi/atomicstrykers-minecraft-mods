package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class MM_Lifesteal extends MobModifier {

    private static Class<?>[] disallowed = {Creeper.class};

    public MM_Lifesteal() {
        super();
    }

    public MM_Lifesteal(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.LIFESTEAL;
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        LivingEntity mob = (LivingEntity) source.getEntity();
        if (entity != null
                && !mob.level.isClientSide && mob.getHealth() < getActualMaxHealth(mob)) {
            mob.setHealth(mob.getHealth() + damage);
        }

        return super.onAttack(entity, source, damage);
    }

    @Override
    public Class<?>[] getBlackListMobClasses() {
        return disallowed;
    }

}
