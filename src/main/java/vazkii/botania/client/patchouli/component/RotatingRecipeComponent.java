/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.patchouli.component;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import vazkii.botania.common.crafting.ModRecipeTypes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Patchouli custom component that draws a rotating circle of items from the defined recipe.
 */
public class RotatingRecipeComponent extends RotatingItemListComponentBase {
	@SerializedName("recipe_name") public String recipeName;

	@SerializedName("recipe_type") public String recipeType;

	@Override
	protected List<Ingredient> makeIngredients() {
		Map<ResourceLocation, ? extends IRecipe<?>> map;
		if ("runic_altar".equals(recipeType)) {
			map = Minecraft.getInstance().world.getRecipeManager().getRecipes(ModRecipeTypes.RUNE_TYPE);
		} else if ("petal_apothecary".equals(recipeType)) {
			map = Minecraft.getInstance().world.getRecipeManager().getRecipes(ModRecipeTypes.PETAL_TYPE);
		} else {
			throw new IllegalArgumentException("Type must be 'runic_altar' or 'petal_apothecary'!");
		}
		IRecipe<?> recipe = map.get(new ResourceLocation(recipeName));
		if (recipe == null) {
			throw new RuntimeException("Missing recipe " + recipeName);
		}
		return recipe.getIngredients();
	}

	@Override
	public void onVariablesAvailable(Function<String, String> lookup) {
		recipeName = lookup.apply(recipeName);
		recipeType = lookup.apply(recipeType);
	}
}
