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
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.query.QueryOperationTypes

open class HasInInventoryCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val player = args.getPlayer()
        val itemString = args.getOne<String>(CommandUtils.ITEM_ARG).get()
        var requiredQuantity = args.getOne<Int>(CommandUtils.QUANTITY_ARG).orNull()

        val requiredItem = itemString.tryAsByteItem().createStack()
        val foundSlots = player.inventory.query<Inventory>(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(requiredItem))

        if (requiredQuantity == null) requiredQuantity = requiredItem.quantity
        if (requiredQuantity < 1) throw CommandException("'quantity' must be greater than zero!".toText())

        if (foundSlots.totalItems() < requiredQuantity) return CommandResult.empty()

        return CommandResult.successCount(requiredQuantity)
    }
}