package atomicstryker.infernalmobs.common.stun;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class StunCapability {

    private boolean stunned;
    private int ticks;
    private int duration;

    public void stun(int duration){
        this.stunned = true;
        this.ticks = 0;
        this.duration = duration;
    }

    public void removeStun(){
        this.stunned = false;
        this.ticks = 0;
        this.duration = 0;
    }

    public void tick(){
        this.ticks++;
    }

    public boolean isStunned() {
        return stunned;
    }

    public int getTicks() {
        return ticks;
    }

    public int getDuration() {
        return duration;
    }

    public void copyFrom(StunCapability stunCapability){
        this.stunned = stunCapability.isStunned();
        this.ticks = stunCapability.getTicks();
        this.duration = stunCapability.getDuration();
    }

    public void saveNBT(CompoundTag tag){
        tag.putBoolean("stun_active", this.isStunned());
        tag.putInt("stun_ticks", this.getTicks());
        tag.putInt("stun_duration", this.getDuration());
    }

    public void loadNBT(CompoundTag tag){
        this.stunned = tag.getBoolean("stun_active");
        this.ticks = tag.getInt("stun_ticks");
        this.duration = tag.getInt("stun_duration");
    }
}
