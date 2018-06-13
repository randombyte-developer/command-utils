package de.randombyte.commandutils

import de.randombyte.kosp.extensions.executeCommand
import de.randombyte.kosp.extensions.replace
import de.randombyte.kosp.extensions.tryReplacePlaceholders
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.scheduler.Task

private const val EXECUTE_AS_CONSOLE_PREFIX = "*"

fun executeCommand(
        command: String,
        commandSource: CommandSource,
        replacements: Map<String, String> = emptyMap(),
        target: Any = commandSource,
        commandResultCallback: (CommandResult) -> Unit = { }) {

    val unprefixedCommand = command.removePrefix(EXECUTE_AS_CONSOLE_PREFIX)
    val executeAsConsole = command.startsWith(EXECUTE_AS_CONSOLE_PREFIX)

    // Any additional custom replacements(like the alias arguments, or the legacy '$p')
    val replacedCommand = unprefixedCommand.replace(replacements)

    // cu execute parsed
    val splits = replacedCommand.split(" ")
    val processedCommand = if (splits[1] == "execute" && splits[2] == "parsed") {
        // This command doesn't need external placeholder processing, it is done in the command itself
        replacedCommand
    } else {
        // No internal placeholder parsing will done, so we do it here
        replacedCommand.tryReplacePlaceholders(source = target)
    }

    val finalCommandSource = if (executeAsConsole) Sponge.getServer().console else commandSource

    Task.builder()
            .execute { ->
                val commandResult = finalCommandSource.executeCommand(processedCommand)
                commandResultCallback(commandResult)
            }
            .delayTicks(1)
            .submit(CommandUtils.INSTANCE)
}