package atomicstryker.infernalmobs.common.mod;

import atomicstryker.infernalmobs.common.mod.specific.*;

import java.util.Arrays;
import java.util.List;

public enum ModifierDefinition {
    UNDYING(
            MM_1UP.class,
            "1UP",
            List.of("recurring", "undying", "twinlived"),
            List.of("ofRebirth", "theUndying", "ofTwinLives"),
            ModifierType.DEFENSIVE
    ),
    ALCHEMIST(
            MM_Alchemist.class,
            "Alchemist",
            List.of("witchkin", "brewing", "singed"),
            List.of("theWitchkin", "theBrewmaster", "theSinged")
    ),
    BERSERK(
            MM_Berserk.class,
            "Berserk",
            List.of("reckless", "raging", "smashing"),
            List.of("ofRecklessness", "theRaging", "ofSmashing")
    ),
    BLAST_OFF(
            MM_Blastoff.class,
            "Blastoff",
            List.of("thumping", "trolling", "byebye"),
            List.of("ofMissionControl", "theNASA", "ofWEE"),
            ModifierType.MANIPULATION
    ),
    BULWARK(
            MM_Bulwark.class,
            "Bulwark",
            List.of("turtling", "defensive", "armoured"),
            List.of("ofTurtling", "theDefender", "ofeffingArmor"),
            ModifierType.DEFENSIVE
    ),
    CHOKE(
            MM_Choke.class,
            "Choke",
            List.of("Sith Lord", "Dark Lord", "Darth"),
            List.of("ofBreathlessness", "theAnaerobic", "ofDeprivation")
    ),
    CLOAKING(
            MM_Cloaking.class,
            "Cloaking",
            List.of("stalking", "unseen", "hunting"),
            List.of("ofStalking", "theUnseen", "thePredator")
    ),
    DARKNESS(
            MM_Darkness.class,
            "Darkness",
            List.of("dark", "shadowkin", "eclipsed"),
            List.of("ofDarkness", "theShadow", "theEclipse")
    ),
    ENDER(
            MM_Ender.class,
            "Ender",
            List.of("enderborn", "tricky"),
            List.of("theEnderborn", "theTrickster")
    ),
    EXHAUST(
            MM_Exhaust.class,
            "Exhaust",
            List.of("exhausting", "draining"),
            List.of("ofFatigue", "theDrainer")
    ),
    FIERY(
            MM_Fiery.class,
            "Fiery",
            List.of("burning", "toasting"),
            List.of("ofConflagration", "thePhoenix", "ofCrispyness")
    ),
    GHASTLY(
            MM_Ghastly.class,
            "Ghastly",
            List.of("bombing", "fireballsy"),
            List.of("OMFGFIREBALLS", "theBomber", "ofBallsofFire")
    ),
    GRAVITY(
            MM_Gravity.class,
            "Gravity",
            List.of("repulsing", "sproing"),
            List.of("ofRepulsion", "theFlipper"),
            ModifierType.MANIPULATION
    ),
    LIFESTEAL(
            MM_Lifesteal.class,
            "LifeSteal",
            List.of("vampiric", "transfusing", "bloodsucking"),
            List.of("theVampire", "ofTransfusion", "theBloodsucker"),
            ModifierType.DEFENSIVE
    ),
    NINJA(
            MM_Ninja.class,
            "Ninja",
            List.of("totallyzen", "innerlypeaceful", "Ronin"),
            List.of("theZenMaster", "ofEquilibrium", "ofInnerPeace")
    ),
    PETRIFY(
            MM_Petrify.class,
            "Petrify",
            List.of("stony", "stunning", "paralyzing"),
            List.of("theMedusa", "ofParalysis", "theParalyzer")
    ),
    POISONOUS(
            MM_Poisonous.class,
            "Poisonous",
            List.of("poisonous", "stinging", "despoiling"),
            List.of("ofVenom", "thedeadlyChalice")
    ),
    QUICKSAND(
            MM_Quicksand.class,
            "Quicksand",
            List.of("slowing", "Quicksand"),
            List.of("ofYouCantRun", "theSlowingB")
    ),
    REGEN(
            MM_Regen.class,
            "Regen",
            List.of("regenerating", "healing", "nighunkillable"),
            List.of("ofWTFIMBA", "theCancerous", "ofFirstAid")
    ),
    RUST(
            MM_Rust.class,
            "Rust",
            List.of("rusting", "decaying"),
            List.of("ofDecay", "theEquipmentHaunter")
    ),
    SAPPER(
            MM_Sapper.class,
            "Sapper",
            List.of("hungering", "starving"),
            List.of("ofHunger", "thePaleRider")
    ),
    SPRINT(
            MM_Sprint.class,
            "Sprint",
            List.of("sprinting", "swift", "charging"),
            List.of("ofBolting", "theSwiftOne", "ofbeinginyourFace")
    ),
    STICKY(
            MM_Sticky.class,
            "Sticky",
            List.of("thieving", "snagging", "quickfingered"),
            List.of("ofSnagging", "theQuickFingered", "ofPettyTheft", "yoink"),
            ModifierType.ULTIMATE
    ),
    STORM(
            MM_Storm.class,
            "Storm",
            List.of("striking", "thundering", "electrified"),
            List.of("ofLightning", "theRaiden"),
            ModifierType.ULTIMATE
    ),
    VENGEANCE(
            MM_Vengeance.class,
            "Vengeance",
            List.of("thorned", "thorny", "spiky"),
            List.of("ofRetribution", "theThorned", "ofStrikingBack")
    ),
    WEAKNESS(
            MM_Weakness.class,
            "Weakness",
            List.of("apathetic", "deceiving"),
            List.of("ofApathy", "theDeceiver")
    ),
    WEBBER(
            MM_Webber.class,
            "Webber",
            List.of("ensnaring", "webbing"),
            List.of("ofTraps", "theMutated", "theSpider"),
            ModifierType.MANIPULATION
    ),
    WITHER(
            MM_Wither.class,
            "Wither",
            List.of("withering"),
            List.of("ofDarkSkulls", "Doomskull")
    );

    ModifierDefinition(Class<? extends MobModifier> aClass, String id, List<String> prefixes, List<String> suffixes) {
        this(aClass, id, prefixes, suffixes, ModifierType.BASIC);
    }

    ModifierDefinition(Class<? extends MobModifier> aClass, String id, List<String> prefixes, List<String> suffixes, ModifierType type) {
        this.aClass = aClass;
        this.id = id;
        this.type = type;
        this.prefixes = prefixes;
        this.suffixes = suffixes;
    }

    private final Class<? extends MobModifier> aClass;
    private final String id;
    private final ModifierType type;
    private final List<String> prefixes;
    private final List<String> suffixes;

    public Class<? extends MobModifier> getModifierImplementation() {
        return aClass;
    }

    public String getId() {
        return id;
    }
    public List<String> getPrefixes() {
        return prefixes;
    }

    public List<String> getSuffixes() {
        return suffixes;
    }

    public ModifierType getType() {
        return type;
    }

    public static ModifierDefinition fromId(String id){
        return Arrays.stream(values()).filter(modDefinition -> modDefinition.getId().equals(id)).findAny().orElseThrow(IllegalArgumentException::new);
    }
}
