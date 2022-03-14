package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityBuilder;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.item.SpellActions;
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

public class LightningWall extends SpellRay {

	public LightningWall(){
		super(AncientSpellcraft.MODID, "lightning_wall", SpellActions.SUMMON, false);
		this.ignoreLivingEntities(true);
		addProperties(EFFECT_DURATION, DAMAGE);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		
		if(caster.isSneaking() && world.getBlockState(pos).getBlock() == ASBlocks.lightning_block){

			if(!world.isRemote){
				// Dispelling of blocks
				world.setBlockToAir(pos);
			}else{
				ParticleBuilder.create(Type.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).scale(3)
				.clr(58, 78, 176).spawn(world);
			}
			
			return true;
		}

		pos = pos.offset(side);
		
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
			builder.blockLifetime = (int) ((getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
			builder.buildTickRate = 1;
			builder.damageMultiplier = getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY);
			builder.batchSize = (int)  (2  * (modifiers.get(SpellModifiers.POTENCY))) + (int) (3 * modifiers.get(WizardryItems.blast_upgrade));
			wall.sort(Comparator.comparingInt(Vec3i::getY));
			builder.setBuildList(wall);
			builder.setBlockToBuild(ASBlocks.lightning_block.getDefaultState());
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
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
