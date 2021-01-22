package com.windanesz.ancientspellcraft.item;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHorn extends ItemASArtefact {

	private static final String NBT_KEY = "fearedEntity";
	private final int SEARCH_RADIUS = 15;
	private final int DURATION = 200;
	private final int AMPLIFIER = 1;
	private final int COOLDOWN = 1200;

	public ItemHorn(EnumRarity rarity, Type type) {
		super(rarity, type);
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		ItemStack itemstack = player.getHeldItem(handIn);

		player.getCooldownTracker().setCooldown(this, 1200);

		if (!world.isRemote) {

			List<EntityCreature> entities = EntityUtils.getEntitiesWithinRadius(
					SEARCH_RADIUS,
					player.posX, player.posY, player.posZ, world, EntityCreature.class);

			for (EntityCreature target : entities) {

				if (!AllyDesignationSystem.isAllied(player, target)) {

					NBTTagCompound entityNBT = target.getEntityData();
					if (entityNBT != null)
						entityNBT.setUniqueId(NBT_KEY, player.getUniqueID());

					target.addPotionEffect(new PotionEffect(WizardryPotions.fear, DURATION, AMPLIFIER));
					target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, DURATION * 2, AMPLIFIER));

				}
			}

		} else {
			// particles
			for (int i = 0; i < 8; i++) {
				double x = player.posX - 1 + world.rand.nextDouble() * 2;
				double y = player.getEntityBoundingBox().minY + 1.5 + world.rand.nextDouble() * 0.5;
				double z = player.posZ - 1 + world.rand.nextDouble() * 2;

				world.spawnParticle(EnumParticleTypes.NOTE, x, y, z, 24.0D, 0.0D, 0.0D);
				//	ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0.9f, 0.1f, 0).spawn(world);
			}
		}

		player.swingArm(handIn);
		world.playSound(null, player.posX, player.posY, player.posZ, AncientSpellcraftSounds.WAR_HORN, SoundCategory.PLAYERS, 1.0F, 1.0F);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){
		super.addInformation(stack, world, tooltip, advanced);
		Wizardry.proxy.addMultiLineDescription(tooltip, "tooltip.ancientspellcraft:artefact_use.usage");
	}

}
