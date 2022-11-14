package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Exhaust extends MobModifier {

    public MM_Exhaust() {
        super();
    }

    public MM_Exhaust(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.EXHAUST;
    }
    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && (source.getEntity() instanceof Player)) {
            ((Player) source.getEntity()).causeFoodExhaustion(1F);
        }

        return super.onHurt(mob, source, damage);
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        if (entity instanceof Player p && !isCreativePlayer(p)) {
            p.causeFoodExhaustion(1F);
        }

        return super.onAttack(entity, source, damage);
    }

}
