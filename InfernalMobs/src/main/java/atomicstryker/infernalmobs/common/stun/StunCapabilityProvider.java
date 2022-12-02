package atomicstryker.infernalmobs.common.stun;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StunCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<StunCapability> STUN_CAPABILITY = CapabilityManager.get(new CapabilityToken<StunCapability>(){});

    private StunCapability stun = null;
    private final LazyOptional<StunCapability> optionalStun = LazyOptional.of(this::createStunCapability);

    private StunCapability createStunCapability(){
        if(Objects.isNull(this.stun)){
            this.stun = new StunCapability();
        }
        return this.stun;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if( cap == STUN_CAPABILITY){
            return optionalStun.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        this.createStunCapability().saveNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.createStunCapability().loadNBT(nbt);
    }
}
