package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class MM_1UP extends MobModifier {
    private static Class<?>[] disallowed = {Creeper.class};
    private boolean healed;

    public MM_1UP(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    @Override
    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.UNDYING;
    }

    @Override
    public boolean onUpdate(LivingEntity mob) {
        if (!healed && !mob.level.isClientSide && mob.getHealth() < (this.getInfernalMonster().getMaxHealth(mob) * 0.25)) {
            mob.setHealth(this.getInfernalMonster().getCurrentHealth());
            mob.level.playSound(null, mob.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.HOSTILE, 1.0F + mob.getRandom().nextFloat(), mob.getRandom().nextFloat() * 0.7F + 0.3F);
            healed = true;
        }
        return true;
    }

    @Override
    public Class<?>[] getBlackListMobClasses() {
        return disallowed;
    }
}
