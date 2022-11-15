package atomicstryker.infernalmobs.common.mod;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;

public abstract class MobModifier {
    private final static int TARGETING_TICKS_BEFORE_ATTACK = 30;

    public abstract ModifierDefinition getModifierDefinition();

    private InfernalMonster infernalMonster;

    /**
     * internal mob attack target
     */
    private LivingEntity attackTarget;

    /**
     * previous attack target, to compare across ticks and prevent nonagressive
     * mobs popping mod effects
     */
    private LivingEntity previousAttackTarget;

    /**
     * how many ticks the mob is targeting something without interruption
     */
    private int targetingTicksSteadyTarget = 0;

    public void init(List<MobModifier> modifiers){

    }

    public MobModifier(InfernalMonster infernalMonster) {
        this.infernalMonster = infernalMonster;
    }

    /**
     * Passes the death event to the modifier list
     *
     * @return true if death should be aborted
     */
    public boolean onDeath() {
        attackTarget = null;
        return true;
    }

    /**
     * passes the setAttackTarget event to the modifier list
     *
     * @param target being passed from the event
     */
    public boolean onTargetChange(LivingEntity target) {
        previousAttackTarget = attackTarget;
        attackTarget = target;
        if (previousAttackTarget != target) {
            targetingTicksSteadyTarget = 0;
        }
        return true;
    }

    public float onAttack(LivingEntity entity, DamageSource source, float amount) {
        return amount;
    }

    public float onHurt(LivingEntity mob, DamageSource source, float amount) {
        return amount;
    }

    public boolean onUpdate(LivingEntity mob){
        return true;
    }

    public final boolean onGeneralUpdate(LivingEntity mob) {
        if (Objects.isNull(attackTarget)) {
            attackTarget = mob.level.getNearestPlayer(mob, 7.5f);
        } else if( shouldDropTarget(mob)) {
            onDropTarget();
        }
        return this.onUpdate(mob);
    }

    public boolean shouldDropTarget(LivingEntity mob){
        return !attackTarget.isAlive() || attackTarget.distanceTo(mob) > 15f;
    }

    public void onDropTarget(){
        attackTarget = null;
    }

    /**
     * used by mods with offensive onUpdate functions - increments the steady
     * target ticker which is wiped when a target is reset and checks the amount
     */
    public boolean hasSteadyTarget() {
        if (attackTarget != null) {
            if (isCreativePlayer(attackTarget)) {
                targetingTicksSteadyTarget = 0;
            } else {
                targetingTicksSteadyTarget++;
            }
            return targetingTicksSteadyTarget > TARGETING_TICKS_BEFORE_ATTACK;
        }
        return false;
    }

    /**
     * players in creative mode are not considered valid targets
     */
    protected boolean isCreativePlayer(Entity entity) {
        if (entity instanceof Player player) {
            return player.isCreative();
        }
        return false;
    }

    protected LivingEntity getMobTarget() {
        return attackTarget;
    }

    /**
     * @return Array of classes an EntityLiving cannot equal, implement or
     * extend in order for this MobModifier to be applied to it
     */
    public Class<?>[] getBlackListMobClasses() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MobModifier) && ((MobModifier) o).getModifierDefinition().getId().equals(this.getModifierDefinition().getId());
    }

    /**
     * helper method to check for target visibility
     */
    protected boolean canMobSeeTarget(LivingEntity mob, LivingEntity target) {
        return target.getVisibilityPercent(mob) >= 0.25D;
    }

    protected InfernalMonster getInfernalMonster() {
        return infernalMonster;
    }
}
