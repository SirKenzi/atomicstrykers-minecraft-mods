package atomicstryker.infernalmobs.util;

import atomicstryker.infernalmobs.config.ConfigStore;
import atomicstryker.infernalmobs.event.EntityEventHandler;
import atomicstryker.infernalmobs.event.MobFarmDetectedEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MobFarmGuard {

    private static final HashMap<Tuple<Integer, Integer>, Float> damageMap = new HashMap<>();
    private static long nextMapEvaluation = System.currentTimeMillis();

    private static List<DamageSource> suspiciousDamageSources = List.of(DamageSource.CACTUS, DamageSource.DROWN,
            DamageSource.FALL, DamageSource.IN_WALL, DamageSource.LAVA);

    public static void observeSuspiciousDamage(LivingHurtEvent event){
        if (!ConfigStore.getConfig().isAntiMobFarm()){
            return;
        }
        if (isDamageLegitimate(event.getSource()) && !(event.getSource().getEntity() instanceof FakePlayer)) {
            return;
        }

        Tuple<Integer, Integer> entityPosition = new Tuple<>((int) event.getEntity().getX(), (int) event.getEntity().getZ());
        Float damageValue = damageMap.get(entityPosition);
        if(Objects.isNull(damageValue)){
            for (Map.Entry<Tuple<Integer, Integer>, Float> e : damageMap.entrySet()) {
                if (Math.abs(e.getKey().getA() - entityPosition.getA()) < 3) {
                    if (Math.abs(e.getKey().getB() - entityPosition.getB()) < 3) {
                        e.setValue(e.getValue() + event.getAmount());
                        break;
                    }
                }
            }
        } else {
            damageMap.put(entityPosition, damageValue + event.getAmount());
            ConfigStore.saveConfig();
        }
    }

    public static void detectMobFarm(LivingEvent.LivingTickEvent event){
        if (!ConfigStore.getConfig().isAntiMobFarm() || System.currentTimeMillis() < nextMapEvaluation){
            return;
        }
        nextMapEvaluation = System.currentTimeMillis() + ConfigStore.getConfig().getMobFarmCheckIntervals();

        if (damageMap.isEmpty()) {
            return;
        }

        float maxDamage = 0f;
        float val;
        Tuple<Integer, Integer> maxC = null;
        for (Map.Entry<Tuple<Integer, Integer>, Float> e : damageMap.entrySet()) {
            val = e.getValue();
            if (val > maxDamage) {
                maxC = e.getKey();
                maxDamage = val;
            }
        }

        if (maxC != null) {
            System.out.println("Infernal Mobs AntiMobFarm damage check, max detected chunk damage value " + maxDamage + " near coords " + maxC.getA() + ", " + maxC.getB());
            if (maxDamage > ConfigStore.getConfig().getMobFarmDamageTrigger()) {
                MinecraftForge.EVENT_BUS.post(
                        new MobFarmDetectedEvent(event.getEntity().level.getChunk(maxC.getA(), maxC.getB()), ConfigStore.getConfig().getMobFarmCheckIntervals(), maxDamage));
            }
        }
        damageMap.clear();
    }

    private static boolean isDamageLegitimate(DamageSource damageSource){
        return !suspiciousDamageSources.contains(damageSource);
    }



}
