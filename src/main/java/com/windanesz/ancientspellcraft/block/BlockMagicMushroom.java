package com.windanesz.ancientspellcraft.block;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.packet.ASPacketHandler;
import com.windanesz.ancientspellcraft.packet.PacketMushroomActivation;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASSpells;
import com.windanesz.ancientspellcraft.tileentity.TileEntityMagicMushroom;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/// TODO overgrown large mushrooms??? (maybe through an artefact or higher tier spells)

/**
 * Base class for the magical mushroom blocks. Mushrooms should extend this and override applyHarmfulEffect and applyHarmfulEffect to do implement their own effects.
 *
 * @Since v1.4
 * @Author WinDanesz
 */
@Mod.EventBusSubscriber
public abstract class BlockMagicMushroom extends BlockBush implements ITileEntityProvider {

	protected static final AxisAlignedBB MUSH_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.6000000238418579D, 0.699999988079071D);

	public static final int GROWTH_STAGES = 8;
	public static final int GROWTH_STAGE_DURATION = 2;
	public static final int POTION_DURATION = 140;
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, GROWTH_STAGES - 1);

	public BlockMagicMushroom() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 7));
		this.setHardness(4);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(null);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) { return this.withAge(meta); }

	protected PropertyInteger getAgeProperty() { return AGE; }

	protected int getAge(IBlockState state) { return (Integer) state.getValue(this.getAgeProperty()); }

	public IBlockState withAge(int age) { return this.getDefaultState().withProperty(this.getAgeProperty(), age); }

	@Override
	public int getMetaFromState(IBlockState state) {
		return this.getAge(state);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {

		// Copied from BlockFlowerPot on authority of the Forge docs, which say it needs to be here
		TileEntity tileentity = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);

		if (tileentity instanceof TileEntityMagicMushroom) {
			return state.withProperty(AGE, ((TileEntityMagicMushroom) tileentity).getAge());
		} else {
			return state.withProperty(AGE, 7);
		}
	}

	@Override
	protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, new IProperty[] {AGE}); }

	public void placeAt(World world, BlockPos lowerPos, int flags) {
		world.setBlockState(lowerPos, this.getDefaultState().withProperty(AGE, 0), flags);
		//		world.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(AGE, 0), flags);
	}

	//	@Override
	//	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
	//		world.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
	//	}

	//	@Override
	//	public void breakBlock(World world, BlockPos pos, IBlockState state) {
	//		super.breakBlock(world, pos, state);
	//		if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.LOWER) {
	//			if (world.getBlockState(pos.up()).getBlock() == this) {
	//				world.destroyBlock(pos.up(), false);
	//			}
	//		} else {
	//			if (world.getBlockState(pos.down()).getBlock() == this) {
	//				world.destroyBlock(pos.down(), false);
	//			}
	//		}
	//	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return super.canBlockStay(worldIn, pos, state);
		//		IBlockState iblockstate = worldIn.getBlockState(pos.up());
		//		return iblockstate.getBlock() == this && this.canSustainBush(worldIn.getBlockState(pos.down()));
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		IMessage msg = new PacketMushroomActivation.Message(entity.posX, entity.posY, entity.posZ, pos, entity);
		ASPacketHandler.net.sendToDimension(msg, world.provider.getDimension());

		if (applyEffect(world, this, pos, state, entity)) {
			world.setBlockToAir(pos);
		}
	}

	public static boolean applySpecialMushroomEffects(World world, @Nullable Block block, BlockPos pos, IBlockState state, Entity target) {
		if (block instanceof BlockMagicMushroom) {
			return applySpecialMushroomEffects(world, block, pos, state, target);
		}
		return false;
	}

	public abstract boolean applyHarmfulEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency);

	public boolean applyBeneficialEffect(World world,
			@Nullable Block block, BlockPos pos, DamageSource source, float damage, IBlockState state,
			@Nullable EntityLivingBase caster, EntityLivingBase target, float potency) {
		return false;
	}

	public boolean applyEffect(World world, @Nullable Block block, BlockPos pos, IBlockState state, Entity target) {
		if (block == null) {
			block = world.getBlockState(pos).getBlock();
			if (!(block instanceof BlockMagicMushroom)) {
				return false;
			}
		}

		BlockMagicMushroom mushroom = (BlockMagicMushroom) block;
		Entity casterEntity = null;
		int potionDuration = 80;
		int effectAmplifier = 0;
		DamageSource source = DamageSource.CACTUS;
		float damage = ASSpells.fairy_ring.getProperty(Spell.DAMAGE).floatValue();

		TileEntity tileentity = world.getTileEntity(pos);

		if (tileentity instanceof TileEntityMagicMushroom) {

			float potency = ((TileEntityMagicMushroom) tileentity).potency;
			damage *= potency;
			effectAmplifier = (int) ((potency - 1) / Constants.POTENCY_INCREASE_PER_TIER + 0.5f);

			potionDuration *= ((TileEntityMagicMushroom) tileentity).durationMultiplier;

			EntityLivingBase caster = ((TileEntityMagicMushroom) tileentity).getCaster();
			casterEntity = ((TileEntityMagicMushroom) tileentity).getCaster();

			if (target instanceof EntityLivingBase) {
				if (!AllyDesignationSystem.isValidTarget(caster, target) || block == ASBlocks.MUSHROOM_HEALING) {
					// the beneficial mushrooms should affect allies!
					return applyBeneficialEffect(world, block, pos, source, damage, state, caster, (EntityLivingBase) target, potency);
				} else {
					// affect hostile entities
					return applyHarmfulEffect(world, block, pos, source, damage, state, caster, (EntityLivingBase) target, potency);
				}
			}
		}

		return false;
	}

	/**
	 * This handles the random positioning of the block for e.g. flowers
	 */
	@Override
	public Block.EnumOffsetType getOffsetType() { return Block.EnumOffsetType.XZ; }

	/**
	 * Aligns the AABB to the XZ offseted block
	 */
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return super.getBoundingBox(state, source, pos).offset(state.getOffset(source, pos));
	}

	@Override
	public boolean hasTileEntity(IBlockState state) { return true; }

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) { return new TileEntityMagicMushroom(); }

	@Override
	public boolean isReplaceable(IBlockAccess world, BlockPos pos) { return false; }

	@Override
	protected boolean canSustainBush(IBlockState state) { return state.isNormalCube(); }

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Items.AIR; }

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) { return false; }

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return this == ASBlocks.MUSHROOM_FORCE ? BlockRenderLayer.TRANSLUCENT : super.getRenderLayer();
	}

	@SubscribeEvent
	public static void onLeftClickBlockEvent(PlayerInteractEvent.LeftClickBlock event) {
		if (!event.getWorld().isRemote && event.getWorld().getBlockState(event.getPos()).getBlock() == WizardryBlocks.thorns) {
			applySpecialMushroomEffects(event.getWorld(), null, event.getPos(), event.getWorld().getBlockState(event.getPos()), event.getEntity());
		}
	}

	public enum MushroomType {
		BUFF,
		COMBAT,
		ANY
	}

	/**
	 * Returns a random mushroom
	 *
	 * @param rareChance chance to return a special mushroom
	 * @return
	 */
	public static BlockMagicMushroom getRandomMushroom(float rareChance) {
		return getRandomMushroom(rareChance, 0);
	}

	public static BlockMagicMushroom getRandomMushroom(float rareChance, float epicChance) {
		return getRandomMushroom(rareChance, epicChance, MushroomType.ANY);
	}

	public static BlockMagicMushroom getRandomMushroom(float rareChance, float epicChance, MushroomType type) {

		// regular
		List<Block> mushrooms = new ArrayList<>();
		if (type == MushroomType.ANY || type == MushroomType.COMBAT) {
			mushrooms.add(ASBlocks.MUSHROOM_FIRE);
			mushrooms.add(ASBlocks.MUSHROOM_ICE);
			mushrooms.add(ASBlocks.MUSHROOM_POISON);
			mushrooms.add(ASBlocks.MUSHROOM_WITHER);
			mushrooms.add(ASBlocks.MUSHROOM_FORCE);
			mushrooms.add(ASBlocks.MUSHROOM_SHOCKING);
		}

		if (type == MushroomType.ANY || type == MushroomType.BUFF) {
			mushrooms.add(ASBlocks.MUSHROOM_HEALING);
		}

		// rare
		List<Block> rareMushrooms = new ArrayList<>();
		if (type == MushroomType.ANY || type == MushroomType.COMBAT) {
			rareMushrooms.add(ASBlocks.MUSHROOM_MIND);
			rareMushrooms.add(ASBlocks.MUSHROOM_EXPLOSIVE);
		}

		if (type == MushroomType.ANY || type == MushroomType.BUFF) {
			rareMushrooms.add(ASBlocks.MUSHROOM_CLEANSING);
		}

		// epic
		List<Block> epicMushrooms = new ArrayList<>();
		if (type == MushroomType.ANY || type == MushroomType.BUFF) {
			epicMushrooms.add(ASBlocks.MUSHROOM_EMPOWERING);
		}

		// calculating this from the "highest end"
		if (!epicMushrooms.isEmpty() && AncientSpellcraft.rand.nextFloat() > 1 - epicChance) {
			return (BlockMagicMushroom) epicMushrooms.get(AncientSpellcraft.rand.nextInt(epicMushrooms.size()));
		}

		// calculating this from the "lower end"
		if (!rareMushrooms.isEmpty() && AncientSpellcraft.rand.nextFloat() < rareChance) {
			return (BlockMagicMushroom) rareMushrooms.get(AncientSpellcraft.rand.nextInt(rareMushrooms.size()));
		}

		return (BlockMagicMushroom) mushrooms.get(AncientSpellcraft.rand.nextInt(mushrooms.size()));
	}

	public static boolean tryPlaceMushroom(World world, BlockPos pos, EntityLivingBase caster, BlockMagicMushroom mushroom, int lifetime) {
		return tryPlaceMushroom(world, pos, caster, mushroom, lifetime, new SpellModifiers());
	}

	/**
	 * Attempts to place the given mushroom block as the caster.
	 *
	 * @return True if successful (even if it was on a protected chunk/block, but in this case the lifetime is limited to prevent griefing). False, if placement fails.
	 */
	public static boolean tryPlaceMushroom(World world, BlockPos pos, EntityLivingBase caster, BlockMagicMushroom mushroom, int lifetime, SpellModifiers modifiers) {

		if (BlockUtils.canBlockBeReplaced(world, pos)) {

			if (lifetime == -1) {
				if (caster != null) {
					if (!BlockUtils.canPlaceBlock(caster, world, pos)) {
						lifetime = 120; // reducing lifetime to two minutes as this BlockPos doesn't belong to this player...
					}
				} else {
					lifetime = 120; // reducing lifetime to two minutes even if the caster is missing...
				}
			}

			mushroom.placeAt(world, pos, 3);

			TileEntity tileentity = world.getTileEntity(pos);

			if (tileentity instanceof TileEntityMagicMushroom) {
				TileEntityMagicMushroom tileMushroom = (TileEntityMagicMushroom) tileentity;
				tileMushroom.setLifetime(lifetime);

				if (caster != null)
					tileMushroom.setCaster(caster);

				tileMushroom.potency = modifiers.get(SpellModifiers.POTENCY);
				tileMushroom.durationMultiplier = modifiers.get(WizardryItems.blast_upgrade);
				tileMushroom.sync();
			}
			return true;
		}
		return false;
	}

}
