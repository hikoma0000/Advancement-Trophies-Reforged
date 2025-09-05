package io.github.hikoma0000.advancementtrophies.config.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class KeyBindings {
    public static final String KEY_CATEGORY_TROPHIES = "key.categories.advancementtrophies";
    public static final String KEY_SHOW_DETAILS = "key.advancementtrophies.show_details";

    public static final KeyMapping SHOW_DETAILS_KEY = new KeyMapping(
            KEY_SHOW_DETAILS,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_LSHIFT,
            KEY_CATEGORY_TROPHIES
    );

    public static boolean isDetailsKeyDown = false;
}