package com.windanesz.ancientspellcraft.command;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.util.ASUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class CommandListBiomes extends CommandBase {

	public String getName() {
		return "listbiomes";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		return "commands." + AncientSpellcraft.MODID + ":listbiomes.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException {

		EntityPlayerMP player = null;
		try {
			player = getCommandSenderAsPlayer(sender);
		}
		catch (PlayerNotFoundException exception) {
		}
		// assemble biome name list
		List<String> listObj = ASUtils.getAllBiomes();
		StringBuilder list = new StringBuilder();
		for (String s : listObj) {
			list.append(s).append(",");
		}
		TextComponentTranslation TextComponentTranslation2 = new TextComponentTranslation(list.toString());
		player.sendMessage(TextComponentTranslation2);
		return;
	}
}
