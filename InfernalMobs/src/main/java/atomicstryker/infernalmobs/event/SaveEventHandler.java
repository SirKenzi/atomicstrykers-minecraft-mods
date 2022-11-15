package atomicstryker.infernalmobs.event;

import atomicstryker.infernalmobs.Cache;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SaveEventHandler {

    @SubscribeEvent
    public void onWorldSave(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            Cache.getInfernalMonsters(level).clear();
        }
    }

}
