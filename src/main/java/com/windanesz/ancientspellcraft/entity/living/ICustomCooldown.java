package com.windanesz.ancientspellcraft.entity.living;

public interface ICustomCooldown {

	int getCooldown();

	void setCooldown(int cooldown);

	int incrementCooldown();

	int decrementCooldown();
}
