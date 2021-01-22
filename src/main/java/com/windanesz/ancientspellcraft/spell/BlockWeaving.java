package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class BlockWeaving extends SpellRay {
	public static final String TIMEOUT_COUNTDOWN = "pos_forget_countdown";

	public static final int COUNT = 1;

	public static final IStoredVariable<List<Location>> LOCATION_KEY = new IStoredVariable.StoredVariable<List<Location>, NBTTagList>("blockWeavingPrevPos",
			s -> NBTExtras.listToNBT(s, Location::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, Location::fromNBT)), Persistence.ALWAYS).setSynced();

	Random rand = new Random();

	public BlockWeaving() {
		super(AncientSpellcraft.MODID, "blockweaving", SpellActions.POINT, false);
		this.soundValues(0.7F, 1.2F, 0.4F);
		addProperties(TIMEOUT_COUNTDOWN);
		WizardData.registerStoredVariables(LOCATION_KEY);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		return false;
	}

	@Override
	public boolean cast(World world, double x, double y, double z, EnumFacing direction, int ticksInUse, int duration, SpellModifiers modifiers) {
		return false;
	}

	// Had to override electroblob.wizardry.spell.SpellRay.cast as block placement was weird with the casting Y offset which wasn't set to eye height
	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {

		Vec3d look = caster.getLookVec();
		Vec3d origin = new Vec3d(caster.posX, caster.getEntityBoundingBox().minY + caster.getEyeHeight(), caster.posZ);

		if (!shootSpell(world, origin, look, caster, ticksInUse, modifiers))
			return false;

		if (casterSwingsArm(world, caster, hand, ticksInUse, modifiers))
			caster.swingArm(hand);
		this.playSound(world, caster, ticksInUse, -1, modifiers);
		return true;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		if (caster instanceof EntityPlayer) {
			// fixes block pos
			if (side == EnumFacing.UP) {
				pos = pos.offset(side);
			}

			EntityPlayer player = (EntityPlayer) caster;

			if (!player.isSneaking() && player.world.isRemote) {
				ParticleBuilder.create(ParticleBuilder.Type.PATH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
						.time(180).clr(0, 1, 0.3f).spawn(world);
			}

			if (!player.world.isRemote) {

				WizardData data = WizardData.get(player);
				List<Location> locations = data.getVariable(BlockWeaving.LOCATION_KEY);

				// sneak clearing
				if (player.isSneaking()) {
					if (!world.isRemote && data.hasSpellBeenDiscovered(this))
						player.sendStatusMessage(new TextComponentTranslation("spell." + getRegistryName() + ".forget"), true);
					try {
						if (locations != null && !locations.isEmpty()) {
							locations.clear();
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					data.sync();
					return true;
				}

				// not sneaking
				else {

					// a second cast
					if (locations != null && !locations.isEmpty()) {

						boolean placedBlocks = false;
						Location startingLoc = locations.get(locations.size() - 1);

						// check if dim didn't change
						if (startingLoc.dimension == player.dimension) {
							// check for valid placeable item in the offhand
							if (player.getHeldItemOffhand().isEmpty() || (!player.getHeldItemOffhand().isEmpty() && !(player.getHeldItemOffhand().getItem() instanceof ItemBlock))) {
								if (!world.isRemote && data.hasSpellBeenDiscovered(this))
									player.sendStatusMessage(new TextComponentTranslation("spell." + getRegistryName() + ".offhand_empty"), true);
								return false;
							}
							ItemBlock itemBlock = (ItemBlock) player.getHeldItemOffhand().getItem();
							int i = itemBlock.getMetadata(player.getHeldItemOffhand().getMetadata());
							ItemStack stack = new ItemStack(player.getHeldItemOffhand().getItem(), 1, i);
							IBlockState iblockstate1 = itemBlock.getBlock().getStateForPlacement(player.world, pos, side, pos.getX(), pos.getY(), pos.getZ(), i, player, EnumHand.OFF_HAND);
							for (BlockPos currPos : BlockPos.getAllInBox(pos, startingLoc.pos)) {
								if (player.canPlayerEdit(currPos, side, stack) && player.world.mayPlace(((ItemBlock) stack.getItem()).getBlock(), currPos, false, side, player)) {
									if (player.isCreative() || ASUtils.shrinkInventoryStackByOne(player, stack)) {
										world.setBlockState(currPos, ((ItemBlock) stack.getItem()).getBlock().getExtendedState(iblockstate1, player.world, currPos));
										placedBlocks = true;
									} else {
										break;
									}
								}
							}
							data.setVariable(BlockWeaving.LOCATION_KEY, locations = new ArrayList<>(COUNT));
							locations.clear();
							data.sync();
							return true;
						} else {
							return false;
						}

					} else {
						locations = data.getVariable(BlockWeaving.LOCATION_KEY);
						if (locations == null)
							data.setVariable(BlockWeaving.LOCATION_KEY, locations = new ArrayList<>(BlockWeaving.COUNT));

						Location currLocation = new Location(pos, caster.dimension);
						locations.add(currLocation);
						data.sync();

						if (!world.isRemote) {
							player.sendStatusMessage(new TextComponentTranslation("spell." + getRegistryName() + ".first_pos_set", pos.getX(), pos.getY(), pos.getZ()), true);
						} else {
							ParticleBuilder.create(ParticleBuilder.Type.PATH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
									.time((120)).clr(0, 1, 0.3f).spawn(world);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	protected boolean onEntityHit(World world, Entity entity, Vec3d vec3d,
			@Nullable EntityLivingBase entityLivingBase, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		//noop
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase entityLivingBase, Vec3d vec3d, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		//noop
		return false;
	}

	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
	}


	// stops the annoying block placement of the offhand block
	@SubscribeEvent
	public static void onRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
//		if (event.getEntity() instanceof EntityPlayer && ((EntityPlayer) event.getEntity()).getHeldItemMainhand().getItem() instanceof ISpellCastingItem) {
//			if (event.getHand() == EnumHand.OFF_HAND) {
//				ItemStack stack = ((EntityPlayer) event.getEntity()).getHeldItemMainhand();
//				Spell spell = ((ISpellCastingItem) stack.getItem()).getCurrentSpell(stack);
//				if (spell == AncientSpellcraftSpells.blockweaving) {
//					event.setCanceled(true);
//				}
//			}
//		}
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}
