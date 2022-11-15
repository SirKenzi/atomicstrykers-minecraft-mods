package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;


public class MM_Storm extends MobModifier {

    private final static long coolDown = 25000L;
    private final static float MIN_DISTANCE = 3F;
    private long nextAbilityUse = 0L;
    public MM_Storm(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.STORM;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        if (hasSteadyTarget()
                && getMobTarget() instanceof Player) {
            tryAbility(mob, getMobTarget());
        }

        return super.onUpdate(mob);
    }

    private void tryAbility(LivingEntity mob, LivingEntity target) {
        if (target == null || target.getVehicle() != null || !canMobSeeTarget(mob, target)) {
            return;
        }

        long time = System.currentTimeMillis();
        if (time > nextAbilityUse
                && mob.distanceTo(target) > MIN_DISTANCE
                && target.level.canSeeSkyFromBelowWater(new BlockPos(Mth.floor(target.getX()), Mth.floor(target.getY()), Mth.floor(target.getZ())))) {
            nextAbilityUse = time + coolDown;
            LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(mob.level);
            lightningboltentity.moveTo(target.getX(), target.getY(), target.getZ());
            lightningboltentity.setVisualOnly(false);
            mob.level.addFreshEntity(lightningboltentity);
        }
    }

}
