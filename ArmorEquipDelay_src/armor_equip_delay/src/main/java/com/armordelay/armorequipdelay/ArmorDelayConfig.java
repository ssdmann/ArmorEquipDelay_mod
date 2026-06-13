package com.armordelay.armorequipdelay;

import net.minecraftforge.common.ForgeConfigSpec;

public class ArmorDelayConfig {

    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ForgeConfigSpec.BooleanValue DEBUFFS_ENABLED;

    // Тривалість дебафу в секундах для кожної частини броні
    public static final ForgeConfigSpec.DoubleValue HELMET_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue CHESTPLATE_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue LEGGINGS_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue BOOTS_DELAY_SECONDS;

    // Рівні ефектів (0 = вимкнено, 1 = I, 2 = II, 3 = III і т.д.)
    public static final ForgeConfigSpec.IntValue SLOWNESS_LEVEL;
    public static final ForgeConfigSpec.IntValue WEAKNESS_LEVEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("=== ArmorEquipDelay Configuration ===")
               .comment("Налаштування затримки та дебафів при зміні броні")
               .push("general");

        DEBUFFS_ENABLED = builder
            .comment("Вмикає/вимикає ефекти дебафу (Повільність і Слабкість).")
            .comment("Якщо false - напис на екрані все одно з'являється, але ефектів не буде.")
            .define("debuffs_enabled", true);

        builder.pop();

        builder.comment("=== Тривалість дебафу в секундах ===")
               .comment("Скільки секунд триватимуть Повільність і Слабкість після одягання/знімання броні.")
               .push("delay_seconds");

        HELMET_DELAY_SECONDS = builder
            .comment("Шолом (Helmet)")
            .defineInRange("helmet", 2.0, 0.5, 60.0);

        CHESTPLATE_DELAY_SECONDS = builder
            .comment("Кіраса (Chestplate)")
            .defineInRange("chestplate", 2.0, 0.5, 60.0);

        LEGGINGS_DELAY_SECONDS = builder
            .comment("Штани (Leggings)")
            .defineInRange("leggings", 2.0, 0.5, 60.0);

        BOOTS_DELAY_SECONDS = builder
            .comment("Чоботи (Boots)")
            .defineInRange("boots", 1.5, 0.5, 60.0);

        builder.pop();

        builder.comment("=== Рівні ефектів ===")
               .comment("Рівень I = 1, II = 2, III = 3 і т.д.")
               .push("effect_levels");

        SLOWNESS_LEVEL = builder
            .comment("Рівень Повільності (Slowness). 0 = вимкнено.")
            .defineInRange("slowness_level", 3, 0, 10);

        WEAKNESS_LEVEL = builder
            .comment("Рівень Слабкості (Weakness). 0 = вимкнено.")
            .defineInRange("weakness_level", 2, 0, 10);

        builder.pop();

        SERVER_SPEC = builder.build();
    }
}
