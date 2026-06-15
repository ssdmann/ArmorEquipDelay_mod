package com.armordelay.armorequipdelay;

import net.minecraftforge.common.ForgeConfigSpec;

public class ArmorDelayConfig {

    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec.BooleanValue DEBUFFS_ENABLED;

    public static final ForgeConfigSpec.DoubleValue HELMET_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue CHESTPLATE_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue LEGGINGS_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue BOOTS_DELAY_SECONDS;

    public static final ForgeConfigSpec.IntValue SLOWNESS_LEVEL;
    public static final ForgeConfigSpec.IntValue WEAKNESS_LEVEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("=== ArmorEquipDelay Configuration ===")
               .push("general");

        DEBUFFS_ENABLED = builder
            .comment("Enable or disable debuff effects (Slowness & Weakness).",
                     "If false - the action bar message still appears, but no effects are applied.")
            .define("debuffs_enabled", true);

        builder.pop();

        builder.comment("=== Debuff duration in seconds per armor piece ===")
               .push("delay_seconds");

        HELMET_DELAY_SECONDS = builder
            .comment("Helmet")
            .defineInRange("helmet", 2.0, 0.5, 60.0);

        CHESTPLATE_DELAY_SECONDS = builder
            .comment("Chestplate")
            .defineInRange("chestplate", 2.0, 0.5, 60.0);

        LEGGINGS_DELAY_SECONDS = builder
            .comment("Leggings")
            .defineInRange("leggings", 2.0, 0.5, 60.0);

        BOOTS_DELAY_SECONDS = builder
            .comment("Boots")
            .defineInRange("boots", 1.5, 0.5, 60.0);

        builder.pop();

        builder.comment("=== Effect levels ===",
                        "Level 1 = I, 2 = II, 3 = III, etc. Set to 0 to disable.")
               .push("effect_levels");

        SLOWNESS_LEVEL = builder
            .comment("Slowness level. 0 = disabled.")
            .defineInRange("slowness_level", 3, 0, 10);

        WEAKNESS_LEVEL = builder
            .comment("Weakness level. 0 = disabled.")
            .defineInRange("weakness_level", 2, 0, 10);

        builder.pop();

        SERVER_SPEC = builder.build();
    }
}
