package de.randombyte.commandutils.execute.after

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.TimeStampUtils
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor

class IsAfterCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val timestamp = args.getOne<String>(CommandUtils.TIMESTAMP_ARG).get()
        val date = TimeStampUtils.deserialize(timestamp)

        return if (TimeStampUtils.now().after(date)) {
            CommandResult.success()
        } else {
            CommandResult.empty()
        }
    }
}