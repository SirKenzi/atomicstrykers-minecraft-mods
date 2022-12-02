package atomicstryker.infernalmobs.common.stun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public class StunCapability {

    private boolean stunned;
    private int ticks;
    private int duration;
    private Vec3 lockedPosition;
    private Vec2 lockedRotation;

    public void stun(int duration, Vec3 lockedPosition, Vec2 lockedRotation){
        this.stunned = true;
        this.ticks = 0;
        this.duration = duration;
        this.lockedPosition = lockedPosition;
        this.lockedRotation = lockedRotation;

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

    public Vec3 getLockedPosition() {
        return lockedPosition;
    }

    public Vec2 getLockedRotation() {
        return lockedRotation;
    }

    public void copyFrom(StunCapability stunCapability){
        this.stunned = stunCapability.isStunned();
        this.ticks = stunCapability.getTicks();
        this.duration = stunCapability.getDuration();
        this.lockedPosition = stunCapability.getLockedPosition();
        this.lockedRotation = stunCapability.getLockedRotation();
    }

    public void saveNBT(CompoundTag tag){
        tag.putBoolean("stun_active", this.isStunned());
        tag.putInt("stun_ticks", this.getTicks());
        tag.putInt("stun_duration", this.getDuration());
        tag.putDouble("stun_x", this.getLockedPosition().x);
        tag.putDouble("stun_y", this.getLockedPosition().y);
        tag.putDouble("stun_z", this.getLockedPosition().z);
        tag.putFloat("rotation_x", this.getLockedRotation().x);
        tag.putFloat("rotation_y", this.getLockedRotation().y);
    }

    public void loadNBT(CompoundTag tag){
        this.stunned = tag.getBoolean("stun_active");
        this.ticks = tag.getInt("stun_ticks");
        this.duration = tag.getInt("stun_duration");
        this.lockedPosition = new Vec3(tag.getDouble("stun_x"),tag.getDouble("stun_y"),tag.getDouble("stun_z"));
        this.lockedRotation = new Vec2(tag.getFloat("rotation_x"), tag.getFloat("rotation_y"));
    }
}
