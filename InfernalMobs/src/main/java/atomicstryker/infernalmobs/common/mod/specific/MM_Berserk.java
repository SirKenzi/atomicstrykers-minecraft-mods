package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.damagesource.DamageSource;

public class MM_Berserk extends MobModifier {

    private static Class<?>[] disallowed = {Creeper.class};

    public MM_Berserk(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.BERSERK;
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        if (entity != null) {
            source.getEntity().hurt(DamageSource.GENERIC, damage);
            damage *= 2;
            damage = InfernalMobsCore.instance().getLimitedDamage(damage);
        }

        return super.onAttack(entity, source, damage);
    }

    @Override
    public Class<?>[] getBlackListMobClasses() {
        return disallowed;
    }

}
