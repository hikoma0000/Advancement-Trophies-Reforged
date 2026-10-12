package io.github.hikoma0000.advancementtrophies.datagen.server;

import io.github.hikoma0000.advancementtrophies.AdvancementTrophies;
import io.github.hikoma0000.advancementtrophies.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.TROPHY_CRATE.get())
                .requires(Items.BARREL)
                .requires(Items.BARREL)
                .unlockedBy(getHasName(Items.BARREL), has(Items.BARREL))
                .save(recipeOutput);

        trophy(recipeOutput, ModItems.IRON_TROPHY.get(), Items.IRON_INGOT);
        trophy(recipeOutput, ModItems.GOLD_TROPHY.get(), Items.GOLD_INGOT);
        trophy(recipeOutput, ModItems.DIAMOND_TROPHY.get(), Items.DIAMOND);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        DataComponentIngredient.of(true, DataComponentPredicate.EMPTY, ModItems.DIAMOND_TROPHY.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.DECORATIONS, ModItems.NETHERITE_TROPHY.get())
                .unlocks(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(AdvancementTrophies.MOD_ID,
                        getItemName(ModItems.NETHERITE_TROPHY.get()) + "_smithing"));
    }

    private static void trophy(RecipeOutput recipeOutput, Item result, Item material) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, result)
                .pattern("X X")
                .pattern("XXX")
                .pattern(" S ")
                .define('X', material)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }
}
