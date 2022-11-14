package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.entity.LivingEntity;

public class MM_Regen extends MobModifier {

    private final static long coolDown = 1000L;
    private long nextAbilityUse = 0L;

    public MM_Regen() {
        super();
    }

    public MM_Regen(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.REGEN;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        float health = mob.getHealth();
        float actualMaxHealth = getActualMaxHealth(mob);
        if (!mob.level.isClientSide && health < actualMaxHealth) {
            long time = System.currentTimeMillis();
            if (time > nextAbilityUse) {
                nextAbilityUse = time + coolDown;
                if (!mob.isOnFire()) {
                    mob.setHealth(Math.min(health + 1, actualMaxHealth));
                }
            }
        }
        return super.onUpdate(mob);
    }
}
