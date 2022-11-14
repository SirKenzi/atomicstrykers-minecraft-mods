package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobModifierType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

public class MM_Sticky extends MobModifier {

    private final static long coolDown = 15000L;
    private static Class<?>[] modBans = {MM_Storm.class};
    private long nextAbilityUse = 0L;
    private Class<?>[] disallowed = {Creeper.class};

    public MM_Sticky() {
        super();
    }

    public MM_Sticky(MobModifier next) {
        super(next);
    }

    protected MobModifierType getMonsterModifierType() {
        return MobModifierType.STICKY;
    }

    @Override
    public String getModName() {
        return "Sticky";
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && (source.getEntity() instanceof Player p) && !isCreativePlayer(p)) {
            long time = System.currentTimeMillis();
            if (time > nextAbilityUse
                    && source.getEntity() != null
                    && !(source instanceof IndirectEntityDamageSource)) {
                nextAbilityUse = time + coolDown;
                ItemEntity drop = p.drop(p.getInventory().removeItem(p.getInventory().selected, 1), false);
                if (drop != null) {
                    drop.setPickUpDelay(50);
                    mob.level.playSound(null, mob.blockPosition(), SoundEvents.SLIME_ATTACK, SoundSource.HOSTILE, 1.0F + mob.getRandom().nextFloat(), mob.getRandom().nextFloat() * 0.7F + 0.3F);
                }
            }
        }

        return super.onHurt(mob, source, damage);
    }

    @Override
    public Class<?>[] getBlackListMobClasses() {
        return disallowed;
    }

    @Override
    public Class<?>[] getModsNotToMixWith() {
        return modBans;
    }

}
