package atomicstryker.infernalmobs.common.mod.specific;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.ModifierDefinition;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class MM_Rust extends MobModifier {
    public MM_Rust(InfernalMonster infernalMonster) {
        super(infernalMonster);
    }

    public ModifierDefinition getModifierDefinition() {
        return ModifierDefinition.RUST;
    }

    @Override
    public float onHurt(LivingEntity mob, DamageSource source, float damage) {
        if (source.getEntity() != null
                && (source.getEntity() instanceof Player p)
                && !(source instanceof IndirectEntityDamageSource)
                && !isCreativePlayer(p)) {
            p.getInventory().getSelected();
            p.getInventory().getSelected().hurtAndBreak(4, (LivingEntity) source.getEntity(), (player) -> player.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }

        return super.onHurt(mob, source, damage);
    }

    @Override
    public float onAttack(LivingEntity entity, DamageSource source, float damage) {
        if (entity instanceof Player) {
            ((Player) entity).getInventory().hurtArmor(DamageSource.MAGIC, damage * 3, Inventory.ALL_ARMOR_SLOTS);
        }

        return super.onAttack(entity, source, damage);
    }

}
