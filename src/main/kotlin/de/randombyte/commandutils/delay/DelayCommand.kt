package de.randombyte.commandutils.delay

import de.randombyte.commandutils.CommandUtils
import de.randombyte.kosp.config.serializers.duration.SimpleDurationTypeSerializer
import de.randombyte.kosp.extensions.executeCommand
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.scheduler.Task
import java.util.concurrent.TimeUnit

class DelayCommand(
        val plugin: CommandUtils
) : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val delayDurationString = args.getOne<String>(CommandUtils.DELAY_ARGUMENT).get()
        val command = args.getOne<String>(CommandUtils.COMMAND_ARG).get()

        val legacyFormatSeconds = delayDurationString.toIntOrNull()
        val delayMilliseconds = if (legacyFormatSeconds != null) {
            legacyFormatSeconds * 1000L
        } else {
            SimpleDurationTypeSerializer.deserialize(delayDurationString).toMillis()
        }

        if (delayMilliseconds < 1) throw CommandException(("'${CommandUtils.DELAY_ARGUMENT}' must be positive!").toText())

        Task.builder()
                .delay(delayMilliseconds, TimeUnit.MILLISECONDS)
                .execute { -> src.executeCommand(command) }
                .submit(plugin)

        return CommandResult.success()
    }
}