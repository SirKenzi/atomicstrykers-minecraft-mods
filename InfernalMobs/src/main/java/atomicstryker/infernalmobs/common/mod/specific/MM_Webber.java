package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class MM_Webber extends MobModifier {

    private final static long coolDown = 15000L;
    private long lastAbilityUse = 0L;

    public MM_Webber(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.WEBBER;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        if (hasSteadyTarget()
                && getMobTarget() instanceof Player) {
            tryAbility(mob, getMobTarget());
        }

        return super.onUpdate(mob);
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && source.getEntity() instanceof LivingEntity) {
            tryAbility(mob, (LivingEntity) source.getEntity());
        }

        return super.onHurt(mob, source, damage);
    }

    private void tryAbility(LivingEntity mob, LivingEntity target) {
        if (target == null || !canMobSeeTarget(mob, target)) {
            return;
        }

        int x = Mth.floor(target.getX());
        int y = Mth.floor(target.getY());
        int z = Mth.floor(target.getZ());

        long time = System.currentTimeMillis();
        if (time > lastAbilityUse + coolDown) {
            int offset;
            if (target.level.getBlockState(new BlockPos(x, y - 1, z)).getBlock() == Blocks.AIR) {
                offset = -1;
            } else if (target.level.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.AIR) {
                offset = 0;
            } else {
                return;
            }

            lastAbilityUse = time;
            target.level.setBlockAndUpdate(new BlockPos(x, y + offset, z), Blocks.COBWEB.defaultBlockState());
            mob.level.playSound(null, mob.blockPosition(), SoundEvents.SPIDER_AMBIENT, SoundSource.HOSTILE, 1.0F + mob.getRandom().nextFloat(), mob.getRandom().nextFloat() * 0.7F + 0.3F);
        }
    }

}
