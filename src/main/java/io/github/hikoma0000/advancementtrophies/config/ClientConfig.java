package io.github.hikoma0000.advancementtrophies.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue SHOW_ACHIEVER_LABEL;
    public static final ForgeConfigSpec.ConfigValue<String> DATE_FORMAT;

    static {
        BUILDER.push("Advancement Trophies Client Settings");

        BUILDER.comment("Settings related to the appearance of the trophy block in the world.").push("Trophy Label");

        SHOW_ACHIEVER_LABEL = BUILDER
                .comment("BETA: If true, the achiever's name is displayed on the trophy's base. This feature is experimental and may not display well with long names.")
                .translation("config.advancementtrophies.showAchieverLabel")
                .define("showAchieverLabel", false);

        BUILDER.pop();

        BUILDER.comment("Settings related to the trophy item's tooltip.").push("Tooltip");

        DATE_FORMAT = BUILDER
                .comment("Defines the format for the achievement date in the tooltip. Uses Java's SimpleDateFormat pattern (e.g., yyyy/MM/dd HH:mm:ss).")
                .translation("config.advancementtrophies.dateFormat")
                .define("dateFormat", "yyyy/MM/dd HH:mm:ss");

        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}