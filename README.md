# Overview

When you complete an advancement, you can get a trophy.

![Trophies](https://github.com/hikoma0000/Advancement-Trophies-Reforged/blob/forge/1.20.1/gallery/trophies.png?raw=true)

This Mod is a unofficial successor to the original MOD ["Advancement Trophies"](https://modrinth.com/mod/mc-trophies), and the code and design have been rebuilt from scratch for other Minecraft versions.

---

## Features

* **4 Tiers of Rarity**: Trophies come in four different rarities, depending on the type of advancement you complete:
    * **Iron Trophy**: Awarded for `Task` advancements.
    * **Gold Trophy**: Awarded for `Goal` advancements.
    * **Diamond Trophy**: Awarded for `Challenge` advancements.
    * **Netherite Trophy**: Awarded for hidden advancements.
* **Detailed Information**: Each trophy stores the name of the achiever, the date and time of completion, the name of the advancement, and the mod it came from. Hold the details key (Left Shift by default) to view this information in the tooltip.
* **Visual Trophy Details**: Placed trophies will display the icon of the completed advancement. If enabled in the config, the achiever's name will also appear on the trophy's base.
* **Trophy Crate**: A special storage block that can hold up to 27 trophies. It can be crafted with two barrels. Like a barrel, it can be used both when placed as a block and when held as an item. If a Trophy Crate is in your inventory, any new trophies you earn will be placed directly into it, and any trophies you pick up will be automatically stored inside.

* **Carry On Compatibility**: Fully compatible with the Carry On mod. Pick up and move your trophies, and the advancement icon and achiever's name will stay visible while you carry them.
  ![CarryOn](https://github.com/hikoma0000/Advancement-Trophies-Reforged/blob/forge/1.20.1/gallery/CarryOn.png?raw=true)

---

## Configuration

You can configure client-side settings by editing the `advancementtrophies-client.toml` file in your `config` folder.

* `showAchieverLabel`
    * **Description**: If set to `true`, the achiever's name will be displayed on the base of the placed trophy block. This feature is experimental and may not display well with long names.
    * **Default**: `false`
* `dateFormat`
    * **Description**: Defines the format for the achievement date shown in the trophy's tooltip.
    * **Default**: `"yyyy/MM/dd HH:mm:ss"`

---

## Feedback

Please provide suggestions and bug reports here: [https://github.com/hikoma0000/Advancement-Trophies-Reforged/issues](https://github.com/hikoma0000/Advancement-Trophies-Reforged/issues)
