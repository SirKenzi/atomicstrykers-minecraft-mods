package atomicstryker.infernalmobs.common.mod;

import atomicstryker.infernalmobs.Cache;
import atomicstryker.infernalmobs.InfernalMobsCore;
import atomicstryker.infernalmobs.common.network.PacketSender;
import atomicstryker.infernalmobs.config.ConfigStore;
import atomicstryker.infernalmobs.util.NameGenerator;
import atomicstryker.infernalmobs.util.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InfernalMonster {

    private final LivingEntity entity;
    private final MobRarity rarity;
    private final List<MobModifier> modifiers;

    private String entityName;
    private List<String> entityModifierNames;

    private float maxHealth;
    private float currentHealth;

    public InfernalMonster(LivingEntity entity, MobRarity rarity) {
        this.entity = entity;
        this.rarity = rarity;
        this.modifiers = new ArrayList<>();
    }


    /**
     * Post construct
     */
    public void init(){
        modifiers.forEach( modifier -> modifier.init(modifiers));
        Cache.getInfernalMonsters(entity.level).put(entity, this);
        this.storeEntityNbtData();
        this.storeEntityHealthData();

    }

    public LivingEntity getEntity() {
        return entity;
    }

    public List<MobModifier> getModifiers() {
        return modifiers;
    }

    public MobRarity getRarity() {
        return rarity;
    }

    public String getModifierNames(){
        return modifiers.stream()
                .map(MobModifier::getModifierDefinition)
                .map(ModifierDefinition::getId)
                .collect(Collectors.joining(" "));
    }

    public boolean handleDeath(LivingEntity entity){
        return !getModifiers().stream().allMatch(mobModifier -> mobModifier.onDeath(entity));
    }

    public boolean handleTargetChange(LivingEntity newTarget){
        return getModifiers().stream().allMatch(mobModifier -> mobModifier.onTargetChange(newTarget));
    }

    public boolean handleUpdate(LivingEntity livingEntity){
        return getModifiers().stream().allMatch(mobModifier -> mobModifier.onGeneralUpdate(livingEntity));
    }

    public float handleHurt(LivingEntity target, DamageSource source, Float amount){
        if (source.getEntity() == null) {
            return amount;
        }
        if (source.getEntity().level.isClientSide && source.getEntity() instanceof Player) {
            PacketSender.requestHealthInformationFromServer(target);
        }
        for( MobModifier modifier : getModifiers()){
            amount = modifier.onHurt(target, source, amount);
        }
        return amount;
    }

    public float handleAttack(LivingEntity target, DamageSource source, Float amount){
        for( MobModifier modifier : getModifiers()){
            amount = modifier.onAttack(target, source, amount);
        }
        return amount;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setCurrentHealth(float currentHealth) {
        this.currentHealth = currentHealth;
    }

    public float getMaxHealth(LivingEntity entity) {
        if (!entity.level.isClientSide && maxHealth < 1F) {
            maxHealth = entity.getPersistentData().getFloat(Tag.HEALTH_TAG.getId());
        }
        return maxHealth;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    private void storeEntityNbtData(){
        String oldTag = this.getEntity().getPersistentData().getString(Tag.NBT_TAG.getId());
        String tagValue = this.getModifierNames();

        if (!oldTag.isEmpty() && !oldTag.equals(tagValue)) {
            InfernalMobsCore.LOGGER.info("Infernal Mobs tag mismatch!! Was [{}}], now trying to set [{}}] \n", oldTag, tagValue);
        }
        this.getEntity().getPersistentData().putString(Tag.NBT_TAG.getId(), tagValue);
    }

    private void storeEntityHealthData() {
        if (this.getEntity().level.isClientSide) {
            return;
        }
        float storedMaxHealth = getEntity().getPersistentData().getFloat(Tag.HEALTH_TAG.getId());
        if (storedMaxHealth < 1F) {
            float maxHealth = (float) (ConfigStore.getMobClassMaxHealth(this.getEntity()) * this.getModifiers().size() * ConfigStore.getConfig().getModHealthFactor());
            this.setMaxHealth(maxHealth);
            this.setCurrentHealth(maxHealth);
            InfernalMobsCore.instance().setEntityHealthPastMax(this.getEntity(), this.maxHealth);
            this.getEntity().getPersistentData().putFloat(Tag.HEALTH_TAG.getId(), this.maxHealth);
        } else {
            this.setMaxHealth(storedMaxHealth);
        }
    }

    public String getEntityName() {
        if(Objects.isNull(entityName)){
            entityName = NameGenerator.getEntityDisplayName(this);
        }
        return entityName;
    }

    public List<String> getEntityModifierNames() {
        if(Objects.isNull(entityModifierNames)){
            entityModifierNames = NameGenerator.getDisplayedModifierNames(this);
        }
        return entityModifierNames;
    }
}
