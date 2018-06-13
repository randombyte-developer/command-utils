package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.TimeStampUtils
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor

class IsBeforeCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val timestamp = args.getOne<String>(CommandUtils.TIMESTAMP_ARG).get()
        val date = TimeStampUtils.deserialize(timestamp)

        return TimeStampUtils.now().before(date).toCommandResult()
    }
}