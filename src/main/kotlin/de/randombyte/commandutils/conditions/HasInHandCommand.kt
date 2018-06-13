package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.getPlayer
import de.randombyte.kosp.extensions.orNull
import de.randombyte.kosp.extensions.toText
import de.randombyte.kosp.extensions.tryAsByteItem
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.item.inventory.ItemStack

open class HasInHandCommand(val handType: HandType) : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val player = args.getPlayer()
        val itemString = args.getOne<String>(CommandUtils.ITEM_ARG).get()
        val quantity = args.getOne<Int>(CommandUtils.QUANTITY_ARG).orNull()

        val requiredItem = itemString.tryAsByteItem().createStack()
        val itemInHand = player.getItemInHand(handType).orElse(ItemStack.empty())

        if (quantity != null) {
            if (quantity < 1) throw CommandException("'quantity' must be greater than zero!".toText())
            requiredItem.quantity = quantity
        }

        val desiredQuantity = requiredItem.quantity

        // the quantity of the item in hand has to be at least as large as the req. quantity
        if (desiredQuantity > itemInHand.quantity) return CommandResult.empty()

        // ignore quantity
        val singleItemInHand = itemInHand.copy().apply { setQuantity(1) }
        val singleRequiredItem = requiredItem.copy().apply { setQuantity(1) }

        // perform 'deep' equals(with data)
        return if (singleItemInHand.equalTo(singleRequiredItem)) {
            CommandResult.successCount(desiredQuantity)
        } else {
            CommandResult.empty()
        }
    }
}