package atomicstryker.infernalmobs.common.mod;

public enum ModifierType {
    BASIC(false), DEFENSIVE(true), MANIPULATION(true), ULTIMATE(true);

    /*List.of(ALCHEMIST, BERSERK, CHOKE, CLOAKING, DARKNESS, ENDER, EXHAUST, FIERY, GHASTLY, NINJA, POISONOUS, QUICKSAND, REGEN,
                    RUST, SAPPER, SPRINT, VENGEANCE, WEAKNESS, WITHER),
            false
    ),
    DEFENSIVE(
            List.of(UNDYING, BULWARK, LIFESTEAL),
            true
    ),
    MANIPULATION(
            List.of(BLAST_OFF, GRAVITY, WEBBER),
            true
    ),
    ULTIMATE(
            List.of(STICKY, STORM),
            true
    );*/


    ModifierType(boolean isExclusive) {
        this.isExclusive = isExclusive;
    }
    private final boolean isExclusive;

    public boolean isExclusive() {
        return isExclusive;
    }
}
