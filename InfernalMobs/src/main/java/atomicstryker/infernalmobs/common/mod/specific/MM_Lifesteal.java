package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class MM_Lifesteal extends MobModifier {

    private final static long cooldown = 5000L;
    private long nextAbilityUse = 0;
    private static Class<?>[] disallowed = {Creeper.class};

    public MM_Lifesteal(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.LIFESTEAL;
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        long time = System.currentTimeMillis();
        if( time > nextAbilityUse + cooldown ) {
            nextAbilityUse = time + cooldown;
            LivingEntity mob = (LivingEntity) source.getEntity();
            if (entity != null
                    && !mob.level.isClientSide && mob.getHealth() < this.getInfernalMonster().getMaxHealth(mob)) {
                mob.setHealth(mob.getHealth() + damage);
            }
        }

        return super.onAttack(entity, source, damage);
    }

    @Override
    public Class<?>[] getBlackListMobClasses() {
        return disallowed;
    }

}
