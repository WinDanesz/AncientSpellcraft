package com.windanesz.ancientspellcraft.item;

import com.google.common.collect.Multimap;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import com.windanesz.ancientspellcraft.registry.AncientSpellcraftSpells;
import com.windanesz.ancientspellcraft.spell.ConjureShadowBlade;
import electroblob.wizardry.data.IVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

public class ItemShadowBlade extends ItemSword implements IConjuredItem {
	private EnumRarity rarity = EnumRarity.COMMON;

	private final float attackDamage;

	public static final IVariable<Integer> SHADOW_CHARGE_TIME = new IVariable.Variable<Integer>(Persistence.NEVER).withTicker(ItemShadowBlade::update);
	public static final IVariable<Boolean> SHADOW_CHARGE_UPWARDS = new IVariable.Variable<>(Persistence.NEVER);

	public static final String CHARGE_SPEED = "charge_speed";

	private static final double EXTRA_HIT_MARGIN = 1;

	public ItemShadowBlade() {
		super(ToolMaterial.IRON);
		setMaxDamage(1200);
		setNoRepair();
		this.attackDamage = 6.0F;
		addAnimationPropertyOverrides();
		setCreativeTab(null);
	}


	@Override
	public EnumRarity getRarity(ItemStack stack){
		return rarity;
	}

	@Override
	public int getMaxDamage(ItemStack stack){
		return this.getMaxDamageFromNBT(stack, Spells.conjure_sword);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {

		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(POTENCY_MODIFIER,
					"Potency modifier", IConjuredItem.getDamageMultiplier(stack) - 1, WizardryUtilities.Operations.MULTIPLY_CUMULATIVE));
		}

		return multimap;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
		if (!isImmuneToWither(target)) {
			applyWitherDamage(target);
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		if (ItemArtefact.isArtefactActive(player, AncientSpellcraftItems.charm_shadow_blade)) {

			// sneak-casting causes the player to move upwards
			startShadowForm(player, player.isSneaking());
			player.getCooldownTracker().setCooldown(this, 80);
			player.setEntityInvulnerable(true);
		}

		return super.onItemRightClick(world, player, hand);
	}

	public Item setRarity(EnumRarity rarity) {
		this.rarity = rarity;
		return this;
	}

	/**
	 * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
	 * the damage on the stack.
	 */
	//	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
	//		super.hitEntity(stack, target, attacker);
	//		if(WizardryUtilities.isLiving(target)){
	//
	//			// Has no effect on withers or wither skeletons.
	//			if(MagicDamage.isEntityImmune(MagicDamage.DamageType.WITHER, target)){
	//				if(!target.world.isRemote && attacker instanceof EntityPlayer) ((EntityPlayer)attacker).sendStatusMessage(
	//						new TextComponentTranslation("spell.resist", target.getName(), target.getDisplayName()), true);
	//			}else{
	//				target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, MagicDamage.DamageType.WITHER),
	//						AncientSpellcraftSpells.shadow_blade.getProperty(DAMAGE).floatValue());
	//				((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.WITHER,
	//						(int)(AncientSpellcraftSpells.shadow_blade.getProperty(EFFECT_DURATION).floatValue()),
	//						AncientSpellcraftSpells.shadow_blade.getProperty(AMPLIFIER).intValue()));
	//			}
	//		}
	//	}

	private static int getRandomNumberInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	private void startShadowForm(EntityPlayer player, Boolean upwards) {
		WizardData.get(player).setVariable(SHADOW_CHARGE_TIME, 6);
		WizardData.get(player).setVariable(SHADOW_CHARGE_UPWARDS, upwards);
	}

	private static int update(EntityPlayer player, Integer chargeTime) {

		if (WizardData.get(player) != null) {
			boolean upwards = false;
			if (WizardData.get(player).getVariable(SHADOW_CHARGE_UPWARDS) != null) {
				upwards = WizardData.get(player).getVariable(SHADOW_CHARGE_UPWARDS);
			}

			if (chargeTime == null)
				chargeTime = 0;

			if (chargeTime > 0) {
				player.setInvisible(true);
				Vec3d look = player.getLookVec();

				float speed = 1.5F;

				if (upwards) {
					player.motionY = 1F;
					//				player.motionX = look.x * (1.1F);
					//				player.motionZ = look.z * (1.1F);
					player.motionX = look.x / 2;
					player.motionZ = look.z / 2;

				} else {
					player.motionX = look.x * speed;
					player.motionZ = look.z * speed;

				}

				List<EntityLivingBase> collided = player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(EXTRA_HIT_MARGIN));
				collided.remove(player);

				if (player.world.isRemote) {

					for (int i = 0; i < 5; i++) {
						float x = player.world.rand.nextFloat();
						x = player.world.rand.nextBoolean() ? x : x * -1;
						float y = player.world.rand.nextFloat();
						y = player.world.rand.nextBoolean() ? y : y * -1;
						float z = player.world.rand.nextFloat();
						z = player.world.rand.nextBoolean() ? z : z * -1;

						ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(player).pos(0, player.height / 2, 0).time(6).vel(player.world.rand.nextGaussian() / 40, player.world.rand.nextDouble() / 40,
								player.world.rand.nextGaussian() / 40).clr(0, 0, 0).collide(false).scale(2F).spawn(player.world);

						ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(player).pos(x, player.height / 2 + y, z).time(6 + getRandomNumberInRange(0, 4)).vel(player.world.rand.nextGaussian() / 40, player.world.rand.nextDouble() / 40,
								player.world.rand.nextGaussian() / 40).clr(0, 0, 0).collide(false).
								scale(1F).spawn(player.world);
					}
				}

				if (!player.world.isRemote && !collided.isEmpty()) {
					for (EntityLivingBase entity : collided) {
						if (!isImmuneToWither(entity)) {
							applyWitherDamage(entity);
						}
					}
				}

				chargeTime--;
				if (chargeTime == 0) {
					if (!player.isPotionActive(MobEffects.INVISIBILITY)) {
						player.setInvisible(false);
					}
					player.setEntityInvulnerable(false);
				}
			}
		}
		return chargeTime;
	}

	private static boolean isImmuneToWither(EntityLivingBase entity) {
		return entity.isPotionActive(MobEffects.WITHER) || MagicDamage.isEntityImmune(MagicDamage.DamageType.WITHER, entity) ||
				(entity instanceof EntityWitherSkeleton || entity instanceof EntityWither);
	}

	private static void applyWitherDamage(EntityLivingBase target) {
		target.addPotionEffect(new PotionEffect(MobEffects.WITHER, AncientSpellcraftSpells.conjure_shadow_blade.getProperty(ConjureShadowBlade.WITHER_DURATION).intValue()));

	}

	@Override
	// This method allows the code for the item's timer to be greatly simplified by damaging it directly from
	// onUpdate() and removing the workaround that involved WizardData and all sorts of crazy stuff.
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

		if (!oldStack.isEmpty() || !newStack.isEmpty()) {
			// We only care about the situation where we specifically want the animation NOT to play.
			if (oldStack.getItem() == newStack.getItem() && !slotChanged)
				return false;
		}

		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		int damage = stack.getItemDamage();
		if (damage > stack.getMaxDamage() || damage < 0) {
			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).getHeldItemOffhand().getItem() instanceof ItemShadowBlade) {
				((EntityPlayer) entity).setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
			} else {
				entity.replaceItemInInventory(slot, ItemStack.EMPTY);
			}
		} else {
			stack.setItemDamage(damage + 1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack par2ItemStack) {
		return false;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	// Cannot be dropped
	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return false;
	}
}






































