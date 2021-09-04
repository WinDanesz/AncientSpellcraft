package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellImproved;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import net.minecraft.world.World;

public class EntityEvilWizardAS extends EntityEvilWizard {

	private EntityAIAttackSpellImproved<EntityEvilWizard> spellCastingAIImproved = new EntityAIAttackSpellImproved<>(this, 0.5D, 14.0F, 30, 50);

	public EntityEvilWizardAS(World world) {
		super(world);
		this.tasks.taskEntries.removeIf(t -> t.action instanceof EntityAIAttackSpell);
		this.tasks.addTask(3, this.spellCastingAIImproved);

	}
}
