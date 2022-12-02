package atomicstryker.infernalmobs.client;

import atomicstryker.infernalmobs.Cache;
import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobRarity;
import atomicstryker.infernalmobs.common.network.PacketSender;
import atomicstryker.infernalmobs.config.ConfigStore;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = InfernalMobsCore.MOD_ID)
public class OverlayBossBar {

    private static final double NAME_VISION_DISTANCE = 32D;
    private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");

    private static Minecraft mc;

    private static long healthBarRetainTime;
    private static LivingEntity retainedTarget;
    private static long nextPacketTime;

    private static LinkedHashMap<UUID, LerpingBossEvent> vanillaBossEventsMap = null;

    @SubscribeEvent
    public static void onRegisterGuis(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(InfernalMobsCore.MOD_ID + "_bossbar", new InfernalMobsHealthBarGuiOverlay());
        mc = Minecraft.getInstance();
        healthBarRetainTime = 0;
        retainedTarget = null;
        nextPacketTime = 0;
    }

    public static class InfernalMobsHealthBarGuiOverlay implements IGuiOverlay {
        @Override
        public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int width, int height) {
            if (ConfigStore.getConfig().isDisableHealthBar() || mc.gui.getBossOverlay().shouldPlayMusic()) {
                return;
            }

            LivingEntity ent = getEntityCrosshairOver(partialTick, mc);
            boolean retained = false;

            if (ent == null && System.currentTimeMillis() < healthBarRetainTime) {
                ent = retainedTarget;
                retained = true;
            } else if (retainedTarget != null) {
                vanillaBossEventsMap.remove(retainedTarget.getUUID());
                retainedTarget = null;
            }

            if (vanillaBossEventsMap == null) {
                boolean hackSuccess = false;
                for (Field declaredField : BossHealthOverlay.class.getDeclaredFields()) {
                    if (declaredField.getType() == Map.class) {
                        declaredField.setAccessible(true);
                        try {
                            vanillaBossEventsMap = (LinkedHashMap<UUID, LerpingBossEvent>) declaredField.get(mc.gui.getBossOverlay());
                            hackSuccess = true;
                        } catch (IllegalAccessException e) {
                            hackSuccess = false;
                        }
                    }
                }
                if (!hackSuccess) {
                    vanillaBossEventsMap = new LinkedHashMap<>();
                }
            }

            if (Objects.isNull(ent)) {
                return;
            }
            InfernalMonster monster = Cache.getInfernalMonster(ent);
            if (Objects.nonNull(monster)) {
                askServerHealth(ent);
                UUID uuid = ent.getUUID();
                Component name = Component.literal(monster.getEntityName());
                float progress = monster.getCurrentHealth() / monster.getMaxHealth(ent);
                if (ent.isDeadOrDying()) {
                    progress = 0.01F;
                }

                BossEvent.BossBarColor color =
                        monster.getRarity() == MobRarity.UNCOMMON ? BossEvent.BossBarColor.GREEN :
                        monster.getRarity() == MobRarity.RARE ? BossEvent.BossBarColor.YELLOW :
                        BossEvent.BossBarColor.RED;

                if (!vanillaBossEventsMap.containsKey(uuid)) {
                    // last 3 param bools are darkenScreen, playBossMusic and worldFog
                    vanillaBossEventsMap.put(uuid, new LerpingBossEvent(uuid, name, progress, color, BossEvent.BossBarOverlay.PROGRESS, false, false, false));
                } else {
                    LerpingBossEvent bossEvent = vanillaBossEventsMap.get(uuid);
                    bossEvent.setProgress(progress);
                }

                // MC supports multiple bosses. Infernal Mobs does not. hide the modifier subdisplay in multi case
                if (vanillaBossEventsMap.size() == 1) {
                    drawModifiersUnderHealthBar(poseStack, ent, monster);
                }

                if (!retained) {
                    retainedTarget = ent;
                    healthBarRetainTime = System.currentTimeMillis() + 3000L;
                }

            } else {
                askServerMods(ent);
            }
        }
    }

    private static void drawModifiersUnderHealthBar(PoseStack matrixStack, LivingEntity ent, InfernalMonster mod) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_BARS_LOCATION);

        int screenwidth = mc.getWindow().getGuiScaledWidth();
        Font fontR = mc.font;

        int yCoord = 10;
        List<String> display = mod.getEntityModifierNames();
        int i = 0;
        while (i < display.size()) {
            yCoord += 10;
            fontR.drawShadow(matrixStack, display.get(i), screenwidth / 2 - fontR.width(display.get(i)) / 2, yCoord, 0xffffff);
            i++;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
    }

    private static LivingEntity getEntityCrosshairOver(float partialTicks, Minecraft mc) {

        Entity entity = mc.getCameraEntity();
        if (entity != null && mc.level != null) {

            double distance = NAME_VISION_DISTANCE;
            HitResult result = entity.pick(distance, partialTicks, false);
            Vec3 vec3d = entity.getEyePosition(partialTicks);

            double distanceToHit = result.getLocation().distanceToSqr(vec3d);

            Vec3 vec3d1 = entity.getViewVector(1.0F);
            Vec3 vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
            AABB axisalignedbb = entity.getBoundingBox().expandTowards(vec3d1.scale(distance)).inflate(1.0D, 1.0D, 1.0D);
            EntityHitResult entityraytraceresult = ProjectileUtil.getEntityHitResult(entity, vec3d, vec3d2, axisalignedbb, (p_lambda$getMouseOver$0_0_) -> !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.isPickable(), distanceToHit);
            if (entityraytraceresult != null) {
                Entity entity1 = entityraytraceresult.getEntity();
                Vec3 vec3d3 = entityraytraceresult.getLocation();
                double d2 = vec3d.distanceToSqr(vec3d3);
                if (d2 < distanceToHit && entity1 instanceof LivingEntity) {
                    return (LivingEntity) entity1;
                }
            }
        }
        return null;
    }

    private static void askServerMods(Entity ent) {
        if (System.currentTimeMillis() > nextPacketTime && (ent instanceof Mob || (ent instanceof LivingEntity && ent instanceof Enemy))) {
            PacketSender.requestMobModifiersInformationFromServer((LivingEntity) ent);
            nextPacketTime = System.currentTimeMillis() + 250L;
        }
    }

    private static void askServerHealth(Entity ent) {
        if (System.currentTimeMillis() > nextPacketTime && ent instanceof LivingEntity) {
            PacketSender.requestHealthInformationFromServer((LivingEntity) ent);
            nextPacketTime = System.currentTimeMillis() + 250L;
        }
    }
}
