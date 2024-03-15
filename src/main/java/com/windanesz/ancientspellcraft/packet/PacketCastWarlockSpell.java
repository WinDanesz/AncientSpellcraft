package com.windanesz.ancientspellcraft.packet;

import com.windanesz.ancientspellcraft.registry.ASBlocks;
import com.windanesz.ancientspellcraft.registry.ASItems;
import com.windanesz.ancientspellcraft.spell.AbsorbSpell;
import com.windanesz.ancientspellcraft.util.SpellcastUtils;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Optional;

/**
 * <b>[Client -> Server]</b> This packet is for handling the bauble-slot item casting buttons.
 */
public class PacketCastWarlockSpell implements IMessageHandler<PacketCastWarlockSpell.Message, IMessage> {

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {

		// Just to make sure that the side is correct
		if (ctx.side.isServer()) {

			final EntityPlayerMP player = ctx.getServerHandler().player;

			player.getServerWorld().addScheduledTask(() -> {

				switch (message.controlType) {

					case START:
						castSpell(player, true);
						break;

					case END:
						castSpell(player, false);
						break;
				}
			});
		}

		return null;
	}

	private void castSpell(EntityPlayerMP player, boolean shouldCast) {
		if (shouldCast) {
			Optional<Spell> spellOptional = AbsorbSpell.getSpell(player);
			SpellModifiers spellModifier = new SpellModifiers();
			if (spellOptional.isPresent() && AbsorbSpell.canCast(player)) {
				if (SpellcastUtils.tryCastSpellAsPlayer(player, spellOptional.get(), EnumHand.MAIN_HAND, SpellCastEvent.Source.WAND, spellModifier, 40)) {
					player.getCooldownTracker().setCooldown(ItemSpellBook.getItemFromBlock(ASBlocks.DIMENSION_FOCUS), spellOptional.get().getCooldown());
					if (!player.isCreative()) {
						int hunger = Math.max(1, (int) ((spellOptional.get().getCost() * spellModifier.get(SpellModifiers.COST) + 0.1f) / 5)); // Weird floaty rounding
						if (ItemArtefact.isArtefactActive(player, ASItems.amulet_spellbinding)) {
							hunger = (Math.max(1, hunger / 2));
						}
						if (player.getFoodStats().getFoodLevel() == 0) {
							player.attackEntityFrom(DamageSource.STARVE, (float) spellOptional.get().getCost() / 5);
						} else if (player.getFoodStats().getFoodLevel() >= hunger) {
							player.getFoodStats().addStats(-hunger, 0);
						} else {
							player.getFoodStats().addStats(-player.getFoodStats().getFoodLevel(), 0);
						}
					}
				}
			}
		}
	}

	public enum ControlType {
		START,
		END
	}

	public static class Message implements IMessage {

		private ControlType controlType;

		// This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
		public Message() {
		}

		public Message(ControlType type) {
			this.controlType = type;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			// The order is important
			this.controlType = ControlType.values()[buf.readInt()];
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(controlType.ordinal());
		}
	}
}
