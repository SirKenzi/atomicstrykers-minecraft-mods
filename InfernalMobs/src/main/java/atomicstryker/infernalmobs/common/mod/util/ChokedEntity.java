package atomicstryker.infernalmobs.common.mod.util;


import net.minecraft.world.entity.LivingEntity;

public class ChokedEntity {

    private final LivingEntity target;
    private int air;

    public ChokedEntity(LivingEntity target) {
        this.target = target;
        this.setAir(target.getAirSupply());
    }

    public LivingEntity getTarget() {
        return target;
    }

    public int getAir() {
        return air;
    }

    public void setAir(int air) {
        this.air = air;
    }

    public String toString(){
        return String.format("Entity[name:%s,air:%d]",this.getTarget().getName(),this.getAir());
    }
}
