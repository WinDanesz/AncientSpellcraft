package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.item.ItemBattlemageSword;
import com.windanesz.ancientspellcraft.item.ItemGlyphArtefact;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.registry.ASPotions;
import com.windanesz.ancientspellcraft.tileentity.TileMageLight;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Magelight extends SpellBuff {

	//	public static final IStoredVariable<Location> LIGHT_POS = IStoredVariable.StoredVariable.ofInt("magelight_pos", Persistence.NEVER).withTicker(Magelight::update);

	public static final IStoredVariable<NBTTagCompound> LIGHT_POS = IStoredVariable.StoredVariable.ofNBT("light_pos", Persistence.NEVER).withTicker(Magelight::update);

	private static NBTTagCompound update(EntityPlayer player, NBTTagCompound compound) {

		if (compound == null) {
			if (ItemArtefact.isArtefactActive(player, ASItems.charm_glyph_illumination) || (ItemGlyphArtefact.isArtefactActive(player, ASItems.charm_glyph_illumination) && player.getHeldItemMainhand().getItem() instanceof ItemBattlemageSword)) {
				if (player.world.isAirBlock(player.getPosition().up())) {
					player.world.setBlockState(player.getPosition().up(), ASBlocks.MAGELIGHT.getDefaultState());
					return (new Location(player.getPosition().up(), player.dimension)).toNBT();
				}
			}
		}

		if (compound == null) { return compound; }

		Location location = Location.fromNBT(compound);
		if (player.getDistanceSq(location.pos) > 20) {
			//if (player.world.getBlockState(location.pos).getBlock() instanceof BlockMageLight) {
			player.world.setBlockToAir(location.pos);

			if (!(player.isPotionActive(ASPotions.magelight))) {
				return null;
			}

			if (player.world.isAirBlock(player.getPosition().up())) {
				if (player.world.getTileEntity(player.getPosition().up()) instanceof TileMageLight) {
					((TileMageLight) player.world.getTileEntity(player.getPosition().up())).setPlayer(player);
				}

				player.world.setBlockState(player.getPosition().up(), ASBlocks.MAGELIGHT.getDefaultState());
				location = new Location(player.getPosition().up(), player.dimension);
				compound = location.toNBT();
			}
			//}
		}
		return compound;
	}

	public Magelight() {
		super(AncientSpellcraft.MODID, "magelight", 216, 26, 11, () -> ASPotions.magelight);
		soundValues(0.7f, 1.2f, 0.4f);
		WizardData.registerStoredVariables(LIGHT_POS);
	}

	/**
	 * <b>Overriding as we don't want to spawn particles.</b>
	 */
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster.isPotionActive(ASPotions.candlelight)) {
			caster.removePotionEffect(ASPotions.candlelight);
		}

		for (Potion potion : potionSet) {
			caster.addPotionEffect(new PotionEffect(potion, potion.isInstant() ? 1 :
					(int) (getProperty(getDurationKey(potion)).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
					(int) getProperty(getStrengthKey(potion)).floatValue(),
					false, false));

			if (caster instanceof EntityPlayer) {
				if (caster.world.isAirBlock(caster.getPosition().up())) {
					WizardData.get((EntityPlayer) caster).setVariable(LIGHT_POS, (new Location(caster.getPosition().up(), caster.dimension)).toNBT());
				}
			}
		}

		if (caster instanceof EntityPlayer && !caster.world.isRemote && caster.world.isAirBlock(caster.getPosition().up())) {
			if (caster.world.getTileEntity(caster.getPosition().up()) instanceof TileMageLight) {
				((TileMageLight) caster.world.getTileEntity(caster.getPosition().up())).setPlayer((EntityPlayer) caster);
			}

			caster.world.setBlockState(caster.getPosition().up(), ASBlocks.MAGELIGHT.getDefaultState());
		}

		return true;
	}

	@Override
	protected void spawnParticles(World world, EntityLivingBase entity, SpellModifiers modifiers) {
		//noop
	}

	/**
	 * Returns the number to be added to the potion amplifier(s) based on the given potency modifier. Override
	 * to define custom modifier handling. Delegates to {@link SpellBuff#getStandardBonusAmplifier(float)} by
	 * default.
	 * <b>Maximum level of this buff is 0</>
	 */
	protected int getBonusAmplifier(float potencyModifier) {
		return 0;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
