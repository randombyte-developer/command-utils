package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.CommandUtils
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import kotlin.random.Random

class HasChanceCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val chance = args.getOne<Double>(CommandUtils.CHANCE_ARG).get()
        if (chance < 0.0 || chance > 1.0) throw CommandException("'chance' must be between 0.0 and 1.0!".toText())

        val random = Random.nextDouble(from = 0.0, until = 1.0)
        return (random < chance).toCommandResult()
    }
}