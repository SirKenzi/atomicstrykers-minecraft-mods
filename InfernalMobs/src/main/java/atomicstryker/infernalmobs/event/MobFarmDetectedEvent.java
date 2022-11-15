package atomicstryker.infernalmobs.event;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkEvent;

public class MobFarmDetectedEvent extends ChunkEvent {
        public final long triggeringInterval;
        public final float triggeringDamage;

        public MobFarmDetectedEvent(LevelChunk chunk, long ti, float td) {
            super(chunk);
            triggeringInterval = ti;
            triggeringDamage = td;
        }
    }