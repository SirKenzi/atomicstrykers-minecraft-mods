package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import atomicstryker.infernalmobs.common.network.PacketSender;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MM_Gravity extends MobModifier {

    private final static long coolDown = 5000L;
    private long nextAbilityUse = 0L;

    public MM_Gravity(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.GRAVITY;
    }

    public static void knockBack(LivingEntity target, double x, double z) {
        target.hasImpulse = true;
        float normalizedPower = Mth.sqrt((float) (x * x + z * z));
        float knockPower = 0.8F;

        double motionX = target.getDeltaMovement().x;
        double motionY = target.getDeltaMovement().x;
        double motionZ = target.getDeltaMovement().x;

        motionX /= 2.0D;
        motionY /= 2.0D;
        motionZ /= 2.0D;
        motionX -= x / (double) normalizedPower * (double) knockPower;
        motionY += (double) knockPower;
        motionZ -= z / (double) normalizedPower * (double) knockPower;

        if (motionY > 0.4000000059604645D) {
            motionY = 0.4000000059604645D;
        }
        target.setDeltaMovement(motionX, motionY, motionZ);
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        if (hasSteadyTarget() && getMobTarget() instanceof Player) {
            tryAbility(mob, getMobTarget());
        }

        return super.onUpdate(mob);
    }

    private void tryAbility(LivingEntity mob, LivingEntity target) {
        if (target == null || !canMobSeeTarget(mob, target)) {
            return;
        }

        long time = System.currentTimeMillis();
        if (time > nextAbilityUse) {
            nextAbilityUse = time + coolDown;

            double diffX = target.getX() - mob.getX();
            double diffZ;
            for (diffZ = target.getZ() - mob.getZ(); diffX * diffX + diffZ * diffZ < 1.0E-4D; diffZ = (Math.random() - Math.random()) * 0.01D) {
                diffX = (Math.random() - Math.random()) * 0.01D;
            }

            mob.level.playSound(null, mob.blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 1.0F + mob.getRandom().nextFloat(), mob.getRandom().nextFloat() * 0.7F + 0.3F);

            if (mob.level.isClientSide || !(target instanceof ServerPlayer)) {
                knockBack(target, diffX, diffZ);
            } else {
                PacketSender.sendKnockBackPacket((ServerPlayer) target, (float) diffX, (float) diffZ);
            }
        }
    }

}
