package atomicstryker.infernalmobs.event;


import atomicstryker.infernalmobs.Cache;
import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.mod.InfernalMonster;
import atomicstryker.infernalmobs.common.mod.MobModifier;
import atomicstryker.infernalmobs.common.mod.MobRarity;
import atomicstryker.infernalmobs.config.ConfigStore;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod.EventBusSubscriber(modid = InfernalMobsCore.MOD_ID)
public class InfernalEntityDropHandler {

    public static Logger LOGGER = LogManager.getLogger(InfernalEntityDropHandler.class);

    private final List<Enchantment> availableEnchantments = new ArrayList<>(26); // 26 is the vanilla enchantment count as of 1.9

    public InfernalEntityDropHandler(){
        ForgeRegistries.ENCHANTMENTS.forEach( enchantment -> {
            if (enchantment.getMinLevel() <= enchantment.getMaxLevel()) {
                getAvailableEnchantments().add(enchantment);
            } else {
                LOGGER.error("enchantment " + enchantment.getClass().getCanonicalName() + " has min level > max level which is invalid behaviour!");
            }
        });
    }

    @SubscribeEvent
    public void OnInfernalEntityDropItems(LivingDropsEvent event) {
        if (!event.getEntity().level.isClientSide) {
            InfernalMonster monster = Cache.getInfernalMonster(event.getEntity());
            if (Objects.nonNull(monster) && event.isRecentlyHit()) {
                MobRarity rarity = monster.getRarity();
                this.handleXpAwards(event.getEntity(), rarity);
                this.handleItemAwards(event.getEntity(), rarity);
                InfernalMobsCore.removeEntFromElites(event.getEntity());
            }
        }
    }

    public void handleXpAwards(LivingEntity mob, MobRarity rarity) {
        for(int xpToAward = rarity.getXp(); xpToAward > 0; ){
            int xpDrop = ExperienceOrb.getExperienceValue(xpToAward);
            mob.level.addFreshEntity(new ExperienceOrb(mob.level, mob.getX(), mob.getY(), mob.getZ(), xpDrop));
            xpToAward -= xpDrop;
        }
    }

    private void handleItemAwards(LivingEntity mob, MobRarity rarity) {
        ItemStack itemStack = this.getRandomItem(mob, rarity);
        if (Objects.isNull(itemStack)) {
            return;
        }

        Item item = itemStack.getItem();
        if (item instanceof EnchantedBookItem) {
            itemStack = EnchantedBookItem.createForEnchantment(getRandomBookEnchantment(mob.getRandom()));
        } else {
            if( item.isEnchantable(itemStack)){
                EnchantmentHelper.enchantItem(mob.level.random, itemStack, rarity.getEnchantLevel(), true);
            }
        }
        ItemEntity itemEnt = new ItemEntity(mob.level, mob.getX(), mob.getY(), mob.getZ(), itemStack);
        mob.level.addFreshEntity(itemEnt);
    }

    private EnchantmentInstance getRandomBookEnchantment(RandomSource rand) {
        Enchantment e = this.getAvailableEnchantments().get(rand.nextInt(this.getAvailableEnchantments().size()));
        int min = e.getMinLevel();
        int range = e.getMaxLevel() - min;
        int lvl = min + rand.nextInt(range + 1);
        return new EnchantmentInstance(e, lvl);
    }

    private ItemStack getRandomItem(LivingEntity mob, MobRarity rarity) {
        List<ItemStack> list = ConfigStore.getLootList(rarity);
        return list.size() > 0 ? list.get(mob.level.random.nextInt(list.size())).copy() : null;
    }

    private List<Enchantment> getAvailableEnchantments(){
        return this.availableEnchantments;
    }



}
