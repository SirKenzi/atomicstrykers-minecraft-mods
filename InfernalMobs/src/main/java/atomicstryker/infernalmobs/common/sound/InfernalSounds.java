package atomicstryker.infernalmobs.common.sound;

import atomicstryker.infernalmobs.InfernalMobsCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class InfernalSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, InfernalMobsCore.MOD_ID);

    public static final RegistryObject<SoundEvent> MODIFIER_PETRIFY_REMINDER = registerSoundEvent("modifier_petrify_reminder");
    public static final RegistryObject<SoundEvent> MODIFIER_PETRIFY_ABILITY = registerSoundEvent("modifier_petrify_ability");

    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }

    private static RegistryObject<SoundEvent> registerSoundEvent(String name){
        System.out.println(name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(InfernalMobsCore.MOD_ID, name)));
    }
}
