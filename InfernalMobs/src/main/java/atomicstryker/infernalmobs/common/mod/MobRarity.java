package atomicstryker.infernalmobs.common.mod;

import atomicstryker.infernalmobs.config.ConfigStore;
import net.minecraft.util.RandomSource;

public enum MobRarity {
    UNCOMMON(2,3, 50, 12),
    RARE(5,4, 150, 30),
    EPIC(8, 12, 300, 62);

    MobRarity(int baseNumberOfModifiers, int maximumNumberOfAdditionalModifiers, int xp, int enchantLevel) {
        this.baseNumberOfModifiers = baseNumberOfModifiers;
        this.maximumNumberOfAdditionalModifiers = maximumNumberOfAdditionalModifiers;
        this.xp = xp;
        this.enchantLevel = enchantLevel;
    }

    private final int baseNumberOfModifiers;
    private final int maximumNumberOfAdditionalModifiers;
    private final int xp;
    private final int enchantLevel;

    public static MobRarity calculate(RandomSource randomSource){
        if( isEntityRare(randomSource)){
            return isEntityEpic(randomSource) ? MobRarity.EPIC : MobRarity.RARE;
        }
        return MobRarity.UNCOMMON;
    }

    public int calculateNumberOfModifiers(RandomSource randomSource){
        return getBaseNumberOfModifiers() + randomSource.nextInt(getMaximumNumberOfAdditionalModifiers());
    }

    private int getBaseNumberOfModifiers() {
        return baseNumberOfModifiers;
    }

    private int getMaximumNumberOfAdditionalModifiers() {
        return maximumNumberOfAdditionalModifiers;
    }

    private static boolean isEntityRare(RandomSource random){
        return random.nextInt(ConfigStore.getConfig().getUltraRarity()) == 0;
    }

    private static boolean isEntityEpic(RandomSource random){
        return random.nextInt(ConfigStore.getConfig().getInfernoRarity()) == 0;
    }

    public int getXp() {
        return xp;
    }

    public int getEnchantLevel() {
        return enchantLevel;
    }

    public static MobRarity fromNumberOfModifiers(int numberOfModifiers){
        if( numberOfModifiers > RARE.baseNumberOfModifiers + RARE.maximumNumberOfAdditionalModifiers){
            return MobRarity.EPIC;
        } else if( numberOfModifiers > UNCOMMON.baseNumberOfModifiers + UNCOMMON.maximumNumberOfAdditionalModifiers) {
            return MobRarity.RARE;
        }
        return MobRarity.UNCOMMON;
    }
}
