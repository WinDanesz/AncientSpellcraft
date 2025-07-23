package com.windanesz.ancientspellcraft.entity.living;

import com.windanesz.ancientspellcraft.entity.ai.EntityAIAttackSpellImproved;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import net.minecraft.world.World;

/**
 * Enhanced version of EntityEvilWizard with improved spell casting AI.
 * 
 * Fixes applied to prevent crashes:
 * 1. Fixed generic type mismatch in EntityAIAttackSpellImproved (Issue #276)
 * 2. Added thread-safe task management to prevent concurrent modification during spawning
 * 3. Enhanced null safety checks in AI classes
 * 
 * @author WinDanesz
 */
public class EntityEvilWizardAS extends EntityEvilWizard {

	private EntityAIAttackSpellImproved<EntityEvilWizardAS> spellCastingAIImproved = new EntityAIAttackSpellImproved<>(this, 0.5D, 14.0F, 30, 50);

	public EntityEvilWizardAS(World world) {
		super(world);
		// Thread-safe task management to prevent crashes during entity spawning
		synchronized (this.tasks) {
			this.tasks.taskEntries.removeIf(t -> t.action instanceof EntityAIAttackSpell);
			this.tasks.addTask(3, this.spellCastingAIImproved);
		}
	}
}
