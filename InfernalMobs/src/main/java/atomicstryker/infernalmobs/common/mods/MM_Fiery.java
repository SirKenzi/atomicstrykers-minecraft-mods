package atomicstryker.infernalmobs.common.mods;

import atomicstryker.infernalmobs.common.MobModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;

public class MM_Fiery extends MobModifier {

    private static String[] suffix = {"ofConflagration", "thePhoenix", "ofCrispyness"};
    private static String[] prefix = {"burning", "toasting"};

    public MM_Fiery() {
        super();
    }

    public MM_Fiery(MobModifier next) {
        super(next);
    }

    @Override
    public String getModName() {
        return "Fiery";
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && (source.getEntity() instanceof LivingEntity)
                && !(source instanceof IndirectEntityDamageSource)) {
            source.getEntity().setSecondsOnFire(3);
        }

        mob.clearFire();
        return super.onHurt(mob, source, damage);
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        if (entity != null) {
            entity.setSecondsOnFire(3);
        }

        return super.onAttack(entity, source, damage);
    }

    @Override
    protected String[] getModNameSuffix() {
        return suffix;
    }

    @Override
    protected String[] getModNamePrefix() {
        return prefix;
    }

}
