package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.windanesz.ancientspellcraft.util.ASUtils.pickRandomStackFromItemStackList;

public class DispelItemCurse extends SpellBuff {

	public DispelItemCurse(String modID, String name) {
		super(modID, name, 1, 1, 0.3f);
		addProperties(EFFECT_RADIUS);
		soundValues(1.0f, 1.2f, 0.2f);
	}

	@Override
	protected boolean applyEffects(EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) caster;
			if (!player.getHeldItemOffhand().isEmpty()) {
				ItemStack offHandItemStack = player.getHeldItemOffhand();
				if (attemptRemoveCurseFromItemStack(offHandItemStack)) {
					return true;
				} else {
					List<ItemStack> itemStackList = new ArrayList<>();
					for (ItemStack stack : player.getArmorInventoryList()) {
						if (!stack.isEmpty()) {
							itemStackList.add(stack);
						}
					}
					if (!itemStackList.isEmpty()) {
						ItemStack stackToDispel = pickRandomStackFromItemStackList(itemStackList);
						return attemptRemoveCurseFromItemStack(stackToDispel);
					}
				}
			}
		}
		return false;
	}

	private static boolean attemptRemoveCurseFromItemStack(ItemStack stack) {
		if (EnchantmentHelper.hasBindingCurse(stack)) {
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			enchantments.remove(Enchantments.BINDING_CURSE);
			EnchantmentHelper.setEnchantments(enchantments, stack);
			return true;
		} else if (EnchantmentHelper.hasVanishingCurse(stack)) {
			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
			enchantments.remove(Enchantments.VANISHING_CURSE);
			EnchantmentHelper.setEnchantments(enchantments, stack);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers) {

		super.spawnParticles(world, caster, modifiers);

		for (int i = 0; i < particleCount * 2; i++) {
			double x = caster.posX + world.rand.nextDouble() * 2 - 1;
			double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
			double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
			ParticleBuilder.create(Type.SPARKLE).pos(x, y, z).vel(0, 0.14, 0).clr(0x0f001b)
					.time(20 + world.rand.nextInt(12)).spawn(world);
			ParticleBuilder.create(Type.DARK_MAGIC).pos(x, y, z).clr(0x0f001b).spawn(world);
		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
