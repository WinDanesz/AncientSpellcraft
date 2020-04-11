package com.windanesz.ancientspellcraft.spell;

import com.windanesz.ancientspellcraft.registry.AncientSpellcraftItems;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TransportOther extends SpellRay {

	public static final String TELEPORT_COUNTDOWN = "teleport_countdown";

	public static final int MAX_REMEMBERED_LOCATIONS = 4;

	// For some reason 'the diamond' doesn't work if I chain methods onto this. Type inference is weird.
	public static final IStoredVariable<List<Location>> LOCATIONS_KEY = new IStoredVariable.StoredVariable<List<Location>, NBTTagList>("stoneCirclePos",
			s -> NBTExtras.listToNBT(s, Location::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, Location::fromNBT)), Persistence.ALWAYS).setSynced();

	public TransportOther(String modID, String name, EnumAction action, boolean isContinuous) {
		super(modID, name, isContinuous, action);
	}

	@Override
	protected boolean onEntityHit(World world, Entity entity, Vec3d vec3d,
			@Nullable EntityLivingBase entityLivingBase, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase entityLivingBase, Vec3d vec3d, Vec3d vec3d1, int i, SpellModifiers spellModifiers) {
		return false;
	}

	// TODO: add check if AW is present, add lang string for AW. If AW is present, some spell properties should be overridden
	//	e.g.
	//	@Override
	//	protected String getDescriptionTranslationKey() {
	//		return "spell." + getUnlocalisedName() + ".desc.aw";
	//	}
	@Override
	public boolean applicableForItem(Item item) {
		return item == AncientSpellcraftItems.ancient_spellcraft_spell_book || item == AncientSpellcraftItems.ancient_spellcraft_scroll;
	}
}




