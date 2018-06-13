package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.execute.getPlayer
import de.randombyte.commandutils.execute.isTruthy
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.data.type.HandType

class HasGivenFromHandCommand(handType: HandType) : HasInHandCommand(handType) {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val hasInHandCommandResult = super.execute(src, args)
        if (!hasInHandCommandResult.isTruthy()) return CommandResult.empty()

        // safe call due to 'isTruthy()'
        val neededQuantity = hasInHandCommandResult.successCount.get()

        val player = args.getPlayer()
        val itemInHand = player.getItemInHand(handType).get()
        itemInHand.quantity = itemInHand.quantity - neededQuantity
        player.setItemInHand(handType, itemInHand)

        return CommandResult.successCount(neededQuantity)
    }
}