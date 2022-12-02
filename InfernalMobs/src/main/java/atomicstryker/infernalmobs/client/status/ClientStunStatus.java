package atomicstryker.infernalmobs.client.status;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class ClientStunStatus {
    private boolean stunned;
    private Vec3 lockedPosition;

    public ClientStunStatus(){
        this.setStunned(false);
    }

    private void stun(){
        this.stunned = true;
        this.lockedPosition = Minecraft.getInstance().player.position();
    }

    public boolean isStunned() {
        return stunned;
    }

    public void setStunned(boolean stunned) {
        if( stunned ){
            stun();
        } else {
            this.stunned = false;
        }
    }

    public Vec3 getLockedPosition() {
        return lockedPosition;
    }
}
