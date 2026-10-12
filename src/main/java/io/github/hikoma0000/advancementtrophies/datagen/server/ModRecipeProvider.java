package io.github.hikoma0000.advancementtrophies.datagen.server;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.TROPHY_CRATE.get())
                .requires(Items.BARREL)
                .requires(Items.BARREL)
                .unlockedBy(getHasName(Items.BARREL), has(Items.BARREL))
                .save(writer);

        trophy(writer, ModItems.IRON_TROPHY.get(), Items.IRON_INGOT);
        trophy(writer, ModItems.GOLD_TROPHY.get(), Items.GOLD_INGOT);
        trophy(writer, ModItems.DIAMOND_TROPHY.get(), Items.DIAMOND);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        StrictNBTIngredient.of(new ItemStack(ModItems.DIAMOND_TROPHY.get())),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.DECORATIONS, ModItems.NETHERITE_TROPHY.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(writer, new ResourceLocation(AdvancementTrophies.MOD_ID,
                        getItemName(ModItems.NETHERITE_TROPHY.get()) + "_smithing"));
    }

    private static void trophy(Consumer<FinishedRecipe> writer, Item result, Item material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result)
                .pattern("X X")
                .pattern("XXX")
                .pattern(" S ")
                .define('X', material)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy(getHasName(material), has(material))
                .save(writer);
    }
}
