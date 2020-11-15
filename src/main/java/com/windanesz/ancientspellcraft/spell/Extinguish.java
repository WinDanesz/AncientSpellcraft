package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class Extinguish extends Spell {
	public Extinguish(String modID, String name) {
		super(modID, name, EnumAction.NONE, false);
		addProperties(EFFECT_RADIUS);
		soundValues(1.0f, 1.2f, 0.2f);
	}

	@Override
	public boolean requiresPacket() {
		return false;
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		double radius = getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade);

		playSound(world, caster.posX, caster.posY, caster.posZ, 0, 0, modifiers);

			List<BlockPos> sphere = BlockUtils.getBlockSphere(caster.getPosition(),
				getProperty(EFFECT_RADIUS).floatValue() * modifiers.get(WizardryItems.blast_upgrade));

		List<EntityLivingBase> targets = EntityUtils.getEntitiesWithinRadius(radius, caster.posX, caster.posY, caster.posZ, world, EntityLivingBase.class);

		for (EntityLivingBase target : targets) {
			if (target.isBurning()) {
				target.extinguish();
			}
		}
		for (BlockPos pos : sphere) {

			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof BlockFire) {
				world.setBlockToAir(pos);
			}
		}

		if (caster.isBurning()) {
			caster.extinguish();
		}

		if (!caster.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
			caster.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 60));
		}

		if (world.isRemote) {

			float particleCount = 10;
			final float r, g, b;
			r = 0;
			g = 0;
			b = 237;
			for (int i = 0; i < particleCount; i++) {
				double x = caster.posX + world.rand.nextDouble() * 2 - 1;
				double y = caster.getEntityBoundingBox().minY + caster.getEyeHeight() - 1 + world.rand.nextDouble();
				double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_BUBBLE).pos(x, y, z).vel(0, 0.1, 0.4).clr(r, g, b).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_BUBBLE).pos(x, y, z).vel(0, 0.1, -0.4).clr(r, g, b).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_BUBBLE).pos(x, y, z).vel(0.4, 0.1, 0).clr(r, g, b).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_BUBBLE).pos(x, y, z).vel(-0.4, 0.1, 0).clr(r, g, b).spawn(world);
			}

			ParticleBuilder.create(ParticleBuilder.Type.SPHERE)
					.pos(caster.posX, caster.getEntityBoundingBox().minY + 0.1, caster.posZ)
					.scale((float) radius * 0.8f)
					.clr(0.1f, 0.1f, 1)
					.spawn(world);
		}
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}

}
