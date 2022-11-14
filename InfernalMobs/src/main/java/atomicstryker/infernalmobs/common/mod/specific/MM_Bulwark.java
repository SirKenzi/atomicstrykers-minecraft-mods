package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Bulwark extends MobModifier {

    public MM_Bulwark() {
        super();
    }

    public MM_Bulwark(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.BULWARK;
    }
    @Override
    public String getModName() {
        return "Bulwark";
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        return super.onHurt(mob, source, Math.max(damage / 2, 1));
    }
}
