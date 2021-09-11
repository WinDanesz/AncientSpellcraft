package com.windanesz.ancientspellcraft.entity;

public interface ICustomCooldown {

	int getCooldown();

	void setCooldown(int cooldown);

	int incrementCooldown();

	int decrementCooldown();
}
