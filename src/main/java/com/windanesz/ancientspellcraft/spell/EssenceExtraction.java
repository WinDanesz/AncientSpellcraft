package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.Settings;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftBlocks;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSounds;
import com.windanesz.ancientspellcraft.util.ASUtils;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockBookshelf;
import electroblob.wizardry.block.BlockImbuementAltar;
import electroblob.wizardry.block.BlockPedestal;
import electroblob.wizardry.block.BlockRunestone;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public class EssenceExtraction extends SpellRay {

	public static final IStoredVariable<BlockPos> EXTRACTION_LOCATION = IStoredVariable.StoredVariable.ofBlockPos("extractionLocation", Persistence.NEVER).setSynced();
	public static final IStoredVariable<Integer> EXTRACT_TICKS = IStoredVariable.StoredVariable.ofInt("extractTicks", Persistence.NEVER);

	public EssenceExtraction() {
		super(AncientSpellcraft.MODID, "essence_extraction", SpellActions.POINT, true);
		this.particleVelocity(-0.5);
		this.particleSpacing(0.4);
		WizardData.registerStoredVariables(EXTRACTION_LOCATION, EXTRACT_TICKS);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		Block block = world.getBlockState(pos).getBlock();

		if (!(block instanceof BlockImbuementAltar || block instanceof BlockBookshelf || block instanceof BlockRunestone || block instanceof BlockPedestal)) {
			return false; // for non-valid blocks
		}

		if (caster instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) caster;

			if (world.isRemote && ticksInUse == 0 || ticksInUse % 50 == 0) {
				this.playSound(world, caster, ticksInUse, -1, modifiers);
			}

			if (block instanceof BlockImbuementAltar && (player.getHeldItemOffhand().isEmpty() || player.getHeldItemOffhand().getItem() != WizardryItems.astral_diamond)) {
				if (!world.isRemote && world.getTotalWorldTime() % 20 == 0) {
					player.sendMessage(new TextComponentTranslation("spell.ancientspellcraft:essence_extraction.no_diamond"));
				}
				return true;
			}

			WizardData data = WizardData.get(player);
			if (data != null) {

				Integer extractTicks = data.getVariable(EXTRACT_TICKS);
				BlockPos extractionLocation = data.getVariable(EXTRACTION_LOCATION);

				if (extractTicks == null) {
					extractTicks = 0;
				}

				if (extractionLocation != null) {
					if (extractionLocation != pos && (extractionLocation.getX() != pos.getX() || extractionLocation.getY() != pos.getY() || extractionLocation.getZ() != pos.getZ())) {
						data.setVariable(EXTRACTION_LOCATION, pos);
						extractTicks = 0;
					}
				} else {
					data.setVariable(EXTRACTION_LOCATION, pos);
					extractTicks = 0;
				}

				if (world.isRemote && Settings.generalSettings.shake_screen) {
					int tick = extractTicks;
					if (world.getTotalWorldTime() % 10 == 0 && extractTicks > 0) {
						EntityUtils.getEntitiesWithinRadius(15, origin.x, origin.y, origin.z, world, EntityPlayer.class)
								.forEach(p -> Wizardry.proxy.shakeScreen(p, (tick * 0.025f)));
					}
				}

				extractTicks++;
				data.setVariable(EXTRACT_TICKS, extractTicks);

				if (world.rand.nextInt(4) == 0) {
					world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + 0.5, pos.getY() + 1.2D, pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
				}

				if (block instanceof BlockImbuementAltar) {

					if (extractTicks >= 200) {
						if (!world.isRemote) {
						world.createExplosion(null, pos.getX() + 2, pos.getY(), pos.getZ(), (float) 1, true);
						world.createExplosion(null, pos.getX() - 2, pos.getY(), pos.getZ(), (float) 1, true);
						world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ() + 2, (float) 1, true);
						world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ() - 2, (float) 1, true);
						world.createExplosion(null, pos.getX(), pos.getY() + 1, pos.getZ(), (float) 4, true);

						}
						data.setVariable(EXTRACT_TICKS, 0);
						data.setVariable(EXTRACTION_LOCATION, null);

						if (!world.isRemote) {
							world.playSound(pos.getX(), pos.getY(),pos.getZ(), AncientSpellcraftSounds.IMBUEMENT_TABLE_BREAK, SoundCategory.HOSTILE, 1, 1, false);

							if (player.getHeldItemOffhand().getItem() == WizardryItems.astral_diamond) {
								player.getHeldItemOffhand().shrink(1);
								ItemStack newStack = new ItemStack(AncientSpellcraftItems.astral_diamond_charged);
								if (player.getHeldItemOffhand().isEmpty()) {
									player.setHeldItem(EnumHand.OFF_HAND, newStack);
								} else if (!player.inventory.addItemStackToInventory(newStack)) {
									player.dropItem(newStack, false);
								}
							}

							// disabled for now
							//
							//							Element[] elements = Element.values();
							//
							//							for (int i = 1; i < 7; i++) {
							//								EntityRemnant remnant = new EntityRemnant(world);
							//								remnant.setElement(elements[i]);
							//								remnant.setPosition(pos.getX(), pos.getY(), pos.getZ());
							//								world.spawnEntity(remnant);
							//							}

						}
						world.setBlockState(pos, AncientSpellcraftBlocks.IMBUEMENT_ALTAR_RUINED.getDefaultState());
						return false;
					}

				} else if (block instanceof BlockBookshelf) {
					if (extractTicks >= 100) {
						world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 1f, false);
						world.setBlockToAir(pos);
						for (int i = 0; i < ASUtils.getRandomNumberInRange(1, 3); i++) {
							Block.spawnAsEntity(world, new BlockPos(pos.getX(), pos.getY() + 0.5, pos.getZ()), new ItemStack(Items.STICK));
						}
						Block.spawnAsEntity(world, pos, new ItemStack(WizardryItems.grand_crystal));

					}

				} else if (block instanceof BlockRunestone || block instanceof BlockPedestal) {
					if (extractTicks >= 100) {
						world.createExplosion(null, pos.getX(), pos.getY() + 1, pos.getZ(), 1f, false);

						Element element;
						if (block instanceof BlockRunestone) {
							element = world.getBlockState(pos).getValue(BlockRunestone.ELEMENT);
							Block.spawnAsEntity(world, pos, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(AncientSpellcraft.MODID + ":crystal_shard_" + element.getName()))));
						} else {
							element = world.getBlockState(pos).getValue(BlockPedestal.ELEMENT);
							Block.spawnAsEntity(world, pos, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(AncientSpellcraft.MODID + ":crystal_shard_" + element.getName())), ASUtils.getRandomNumberInRange(3, 6)));
						}
						Block.spawnAsEntity(world, new BlockPos(pos.getX(), pos.getY() + 0.5, pos.getZ()), new ItemStack(Item.getItemFromBlock(Blocks.COBBLESTONE), 1));
						world.setBlockToAir(pos);

					}
				}
			}
		}
		return true;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz) {
		if (world.rand.nextInt(5) == 0)
			ParticleBuilder.create(ParticleBuilder.Type.DUST).pos(x, y, z).clr(1, 1, 0.65f).fade(0.7f, 0, 1).spawn(world);
		// This used to multiply the velocity by the distance from the caster
		ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(0.2f).pos(x, y, z).vel(vx, vy, vz).time(8 + world.rand.nextInt(6))
				.clr(1, 1, 0.65f).fade(0.7f, 0, 1).spawn(world);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spell_book;  // No scroll!
	}
}
