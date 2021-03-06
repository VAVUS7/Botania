/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import vazkii.botania.common.advancements.UseItemSuccessTrigger;
import vazkii.botania.common.entity.EntityPinkWither;

import javax.annotation.Nonnull;

import java.util.List;

public class ItemPinkinator extends Item {

	public ItemPinkinator(Properties builder) {
		super(builder);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		int range = 16;
		List<WitherEntity> withers = world.getEntitiesWithinAABB(WitherEntity.class, new AxisAlignedBB(player.getX() - range, player.getY() - range, player.getZ() - range, player.getX() + range, player.getY() + range, player.getZ() + range));
		for (WitherEntity wither : withers) {
			if (!world.isRemote && wither.isAlive() && !(wither instanceof EntityPinkWither)) {
				wither.remove();
				EntityPinkWither pink = new EntityPinkWither(world);
				pink.setLocationAndAngles(wither.getX(), wither.getY(), wither.getZ(), wither.rotationYaw, wither.rotationPitch);
				pink.setNoAI(wither.isAIDisabled());
				if (wither.hasCustomName()) {
					pink.setCustomName(wither.getCustomName());
					pink.setCustomNameVisible(wither.isCustomNameVisible());
				}
				pink.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(pink)), SpawnReason.CONVERSION, null, null);
				world.addEntity(pink);
				pink.spawnExplosionParticle();
				pink.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 4F, (1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
				UseItemSuccessTrigger.INSTANCE.trigger((ServerPlayerEntity) player, stack, (ServerWorld) world, player.getX(), player.getY(), player.getZ());
				stack.shrink(1);
				return ActionResult.success(stack);
			}
		}

		return ActionResult.pass(stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flags) {
		tooltip.add(new TranslationTextComponent("botaniamisc.pinkinatorDesc").applyTextStyle(TextFormatting.GRAY));
	}

}
