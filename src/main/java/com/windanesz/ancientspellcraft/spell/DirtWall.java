package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityBuilder;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DirtWall extends SpellRay {

	private static final String BLOCK_LIFETIME = "block_lifetime";

	public DirtWall(){
		super(AncientSpellcraft.MODID, "wall_of_dirt", SpellActions.SUMMON, false);
		this.ignoreLivingEntities(true);
		addProperties(BLOCK_LIFETIME);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		
		if(caster.isSneaking() && world.getBlockState(pos).getBlock() == WizardryBlocks.spectral_block){

			if(!world.isRemote){
				// Dispelling of blocks
				world.setBlockToAir(pos);
			}else{
				ParticleBuilder.create(Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3)
				.clr(222, 92, 0).spawn(world);
			}
			
			return true;
		}
		
		pos = pos.offset(side);
		
		if(world.isRemote){
			ParticleBuilder.create(Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3)
			.clr(222, 92, 0).spawn(world);
		}

		if (!world.isRemote) {

			EnumFacing direction = caster.getHorizontalFacing().rotateY();

			List<BlockPos> wall = new ArrayList<>();
			wall.add(pos);

			int blastUpgradeCount = (int)((modifiers.get(WizardryItems.blast_upgrade) - 1) / Constants.BLAST_RADIUS_INCREASE_PER_LEVEL + 0.5f);
			for (int i = 0; i < 2+blastUpgradeCount; i++) {
				wall.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(direction, i));
				wall.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(direction,i).offset(EnumFacing.UP));
				wall.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(direction,i).offset(EnumFacing.UP, 2));
				wall.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(direction.getOpposite(),i));
				wall.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(direction.getOpposite(),i).offset(EnumFacing.UP));
				wall.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(direction.getOpposite(),i).offset(EnumFacing.UP,2));

			}

			EntityBuilder builder = new EntityBuilder(world);
			builder.setPosition(caster.getPosition().getX(), caster.getPosition().getY(), caster.getPosition().getZ());
			builder.setCaster(caster);
			builder.lifetime = getProperty(BLOCK_LIFETIME).intValue();
			builder.blockLifetime = getProperty(BLOCK_LIFETIME).intValue();
			builder.buildTickRate = 1;

			builder.batchSize = (int)  (2  * (modifiers.get(SpellModifiers.POTENCY))) + (int) (3 * modifiers.get(WizardryItems.blast_upgrade));
			wall.sort(Comparator.comparingInt(Vec3i::getY));
			builder.setBuildList(wall);
			builder.setBlockToBuild(AncientSpellcraftBlocks.CONJURED_DIRT.getDefaultState());
			builder.setIgnoreClaims(false);
			world.spawnEntity(builder);
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
