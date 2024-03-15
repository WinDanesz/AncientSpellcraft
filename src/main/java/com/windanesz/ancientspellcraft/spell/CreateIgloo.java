package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.construct.EntityBuilder;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CreateIgloo extends Spell {

	private static Random rand = new Random();

	public CreateIgloo() {
		super(AncientSpellcraft.MODID, "create_igloo", SpellActions.SUMMON, false);
		this.soundValues(0.5f, 1.1f, 0.2f);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		if (!caster.onGround) {
			return false;
		}

		BlockPos pos = caster.getPosition().down();

		List<BlockPos> blockPosList = new ArrayList<>();
				BlockPos layer1Center = pos.offset(EnumFacing.UP);
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 2));
		//		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 1));
		///

		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.EAST));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST));
		//		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH,3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 3));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer1Center.offset(EnumFacing.NORTH, 3).offset(EnumFacing.WEST, 1));

		BlockPos layer2Center = layer1Center.offset(EnumFacing.UP);

		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.EAST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 1));
		//		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH, 3).offset(EnumFacing.WEST, 1));

		blockPosList.add(layer2Center.offset(EnumFacing.WEST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 2));
		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST, 1));

		blockPosList.add(layer2Center.offset(EnumFacing.NORTH, 3));
		blockPosList.add(layer2Center.offset(EnumFacing.EAST, 3));
		//		blockPosList.add(layer2Center.offset(EnumFacing.SOUTH,3));
		blockPosList.add(layer2Center.offset(EnumFacing.WEST, 3));

		BlockPos layer3Center = layer2Center.offset(EnumFacing.UP);

		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 3));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 1).offset(EnumFacing.WEST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.WEST, 1));
		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 1).offset(EnumFacing.WEST, 1));

		blockPosList.add(layer3Center.offset(EnumFacing.NORTH, 2));
		blockPosList.add(layer3Center.offset(EnumFacing.EAST, 2));
		blockPosList.add(layer3Center.offset(EnumFacing.SOUTH, 2));
		blockPosList.add(layer3Center.offset(EnumFacing.WEST, 2));

		BlockPos layer4Center = layer3Center.offset(EnumFacing.UP);
		blockPosList.add(layer4Center);
		blockPosList.add(layer4Center.offset(EnumFacing.NORTH));
		blockPosList.add(layer4Center.offset(EnumFacing.EAST));
		blockPosList.add(layer4Center.offset(EnumFacing.SOUTH));
		blockPosList.add(layer4Center.offset(EnumFacing.WEST));

//		for (BlockPos currPos : blockPosList) {
//			world.setBlockState(currPos, Blocks.SNOW.getDefaultState());
//			world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double) currPos.getX() + world.rand.nextDouble(), (double) currPos.getY() + world.rand.nextDouble() * 2.5D, (double) currPos.getZ() + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
//		}

		EntityBuilder builder = new EntityBuilder(world);
		builder.setPosition(caster.getPosition().getX(), caster.getPosition().getY(), caster.getPosition().getZ());
		builder.setCaster(caster);
		builder.lifetime = 120;
		builder.blockLifetime = 6000;
		builder.buildTickRate = 1;

		// builder.batchSize = (int)  (2  * (modifiers.get(SpellModifiers.POTENCY))) + (int) (3 * modifiers.get(WizardryItems.blast_upgrade));
		blockPosList.sort(Comparator.comparingInt(Vec3i::getY));
		builder.setBuildList(blockPosList);
		builder.setBlockToBuild(ASBlocks.CONJURED_SNOW.getDefaultState());
		if (world.getBiome(pos).getTempCategory() == Biome.TempCategory.COLD) {
			builder.blockLifetime = 24000;
		}
		// check claims because we are using the non reverting blocks here...!
		builder.setIgnoreClaims(false);
		world.spawnEntity(builder);

		//		// "snow carpet"
		//		List<BlockPos> blockPosFloor = new ArrayList<>();
		//		blockPosFloor.add(layer1Center);
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.EAST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.EAST, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 3));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 4));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.EAST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 4).offset(EnumFacing.WEST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.WEST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.WEST, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.EAST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.EAST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.EAST, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.EAST, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.EAST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.WEST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.SOUTH).offset(EnumFacing.WEST, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.WEST));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH).offset(EnumFacing.WEST, 2));
		//		blockPosFloor.add(layer1Center.offset(EnumFacing.NORTH, 2).offset(EnumFacing.WEST));
		//
		//		for (BlockPos currPos : blockPosFloor) {
		//			world.setBlockState(currPos, Blocks.SNOW_LAYER.getDefaultState());
		//		}
		//
		//		EntityBuilder builderFloor = new EntityBuilder(world);
		//		builderFloor.setPosition(caster.getPosition().getX(), caster.getPosition().getY() + 2, caster.getPosition().getZ());
		//		builderFloor.setCaster(caster);
		//		builderFloor.lifetime = 40;
		//		builderFloor.blockLifetime = 600;//(int) ((getProperty(EFFECT_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
		//		builderFloor.buildTickRate = 1;
		//
		//		builderFloor.batchSize = (int)  (2  * (modifiers.get(SpellModifiers.POTENCY))) + (int) (3 * modifiers.get(WizardryItems.blast_upgrade));
		//		blockPosList.sort(Comparator.comparingInt(Vec3i::getY));
		//		builderFloor.setBuildList(blockPosList);
		//		builderFloor.setBlockToBuild(Blocks.SNOW.getDefaultState());
		//		// check claims because we are using the non reverting blocks here...!
		//		builderFloor.setIgnoreClaims(false);
		//		world.spawnEntity(builderFloor);

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == ASItems.ancient_spellcraft_spell_book || item == ASItems.ancient_spellcraft_scroll;
	}
}
