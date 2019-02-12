package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.getPlayer
import de.randombyte.commandutils.execute.isTruthy
import de.randombyte.kosp.extensions.tryAsByteItem
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.query.QueryOperationTypes

class HasGivenFromInventoryCommand : HasInInventoryCommand() {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val hasInInventoryCommandResult = super.execute(src, args)
        if (!hasInInventoryCommandResult.isTruthy()) return CommandResult.empty()

        // safe call due to 'isTruthy()'
        val neededQuantity = hasInInventoryCommandResult.successCount.get()
        val requiredItem = args.getOne<String>(CommandUtils.ITEM_ARG).get().tryAsByteItem().createStack()

        val player = args.getPlayer()
        player.inventory
                .query<Inventory>(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(requiredItem))
                .poll(neededQuantity)

        return CommandResult.successCount(neededQuantity)
    }
}