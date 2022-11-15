package atomicstryker.infernalmobs;

import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

    private static final ConcurrentHashMap<LivingEntity, InfernalMonster> rareMobsClient = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<LivingEntity, InfernalMonster> rareMobsServer = new ConcurrentHashMap<>();

    public static Map<LivingEntity, InfernalMonster> getInfernalMonsters(Level world) {
        return world.isClientSide() ? rareMobsClient : rareMobsServer;
    }

    public static InfernalMonster getInfernalMonster(LivingEntity entity){
        return getInfernalMonsters(entity.level).get(entity);
    }
}
