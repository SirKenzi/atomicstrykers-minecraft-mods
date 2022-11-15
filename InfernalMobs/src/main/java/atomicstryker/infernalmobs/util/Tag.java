package atomicstryker.infernalmobs.util;

public enum Tag {
    NBT_TAG("InfernalMobsMod"),
    HEALTH_TAG("infernalMaxHealth"),
    TRANSLATION_MOD_KEY("translation.infernalmobs:mod."),
    TRANSLATION_ENTITY_KEY("translation.infernalmobs:entity."),
    TRANSLATION_PREFIX_KEY("translation.infernalmobs:prefix."),
    TRANSLATION_SUFFIX_KEY("translation.infernalmobs:suffix.");

    private final String id;

    Tag(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
