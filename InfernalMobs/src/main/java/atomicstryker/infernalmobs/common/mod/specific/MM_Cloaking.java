package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Cloaking extends MobModifier {

    private final static long coolDown = 10000L;
    private static Class<?>[] disallowed = {Spider.class};
    private long nextAbilityUse = 0L;

    public MM_Cloaking(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.CLOAKING;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        if (hasSteadyTarget()
                && getMobTarget() instanceof Player) {
            tryAbility(mob);
        }

        return super.onUpdate(mob);
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && source.getEntity() instanceof LivingEntity) {
            tryAbility(mob);
        }

        return super.onHurt(mob, source, damage);
    }

    private void tryAbility(LivingEntity mob) {
        long time = System.currentTimeMillis();
        if (time > nextAbilityUse) {
            nextAbilityUse = time + coolDown;
            mob.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200));
        }
    }

    @Override
    public Class<?>[] getBlackListMobClasses() {
        return disallowed;
    }

}
