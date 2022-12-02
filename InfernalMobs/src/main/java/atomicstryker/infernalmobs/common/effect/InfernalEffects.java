package atomicstryker.infernalmobs.common.effect;

import atomicstryker.infernalmobs.InfernalMobsCore;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InfernalEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, InfernalMobsCore.MOD_ID);

    public static final RegistryObject<MobEffect> PETRIFY =
            MOB_EFFECT.register("petrify", () -> new PetrifyEffect(MobEffectCategory.HARMFUL, 5592405));

    public static void register(IEventBus eventBus){
        MOB_EFFECT.register(eventBus);
    }


}
