package atomicstryker.infernalmobs.common.stun;

import atomicstryker.infernalmobs.common.stun.StunCapability;
import atomicstryker.infernalmobs.common.stun.StunCapabilityProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PetrifyEffect extends MobEffect {

    public PetrifyEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int pAmplifier){
        if(livingEntity.level.isClientSide){
            return;
        }

        Double x = livingEntity.getX();
        Double y = livingEntity.getY();
        Double z = livingEntity.getZ();

        livingEntity.teleportTo(x,y,z);
        livingEntity.setDeltaMovement(0,0,0);
        livingEntity.getCapability(StunCapabilityProvider.STUN_CAPABILITY).ifPresent( stunCapability -> {
            stunCapability.stun(20, livingEntity.getPosition(1f), livingEntity.getRotationVector());
        });

        super.applyEffectTick(livingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier){
        return true;
    }
}
