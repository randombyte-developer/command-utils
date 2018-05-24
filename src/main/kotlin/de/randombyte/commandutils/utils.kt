package de.randombyte.commandutils

import de.randombyte.kosp.extensions.executeCommand
import de.randombyte.kosp.extensions.replace
import me.rojo8399.placeholderapi.PlaceholderService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

private const val EXECUTE_AS_CONSOLE_PREFIX = "*"

fun executeCommand(
        command: String,
        commandSource: CommandSource,
        target: Any = commandSource,
        replacements: Map<String, String> = emptyMap(),
        doPlaceholderProcessing: Boolean = true,
        commandResultCallback: (CommandResult) -> Unit = { }) {

    val unprefixedCommand = command.removePrefix(EXECUTE_AS_CONSOLE_PREFIX)
    val executeAsConsole = command.startsWith(EXECUTE_AS_CONSOLE_PREFIX)

    val processedCommand = if (doPlaceholderProcessing) {
        unprefixedCommand.tryProcessPlaceholders(CommandUtils.INSTANCE.placeholderApi, target)
    } else unprefixedCommand

    // Any additional custom replacements(like the alias arguments, or the legacy '$p')
    val replacedCommand = processedCommand.replace(replacements)

    val finalCommandSource = if (executeAsConsole) Sponge.getServer().console else commandSource

    Task.builder()
            .execute { ->
                val commandResult = finalCommandSource.executeCommand(replacedCommand)
                commandResultCallback(commandResult)
            }
            .delayTicks(1)
            .submit(CommandUtils.INSTANCE)
}

/**
 * Tries to process the placeholders if PlaceholderAPI is loaded.
 */
private fun String.tryProcessPlaceholders(placeholderApi: PlaceholderService?, commandSource: Any): String {
    if (placeholderApi == null) return this

    val placeholders = placeholderApi.defaultPattern.toRegex()
            .findAll(this)
            .map { matchResult -> matchResult.groupValues[1] }.toList()
    val replacements = placeholders.map { placeholder ->
        val replacement = placeholderApi.parse(placeholder, commandSource, null)
        val replacementString = if (replacement is Text) {
            TextSerializers.FORMATTING_CODE.serialize(replacement)
        } else {
            replacement.toString()
        }
        "%$placeholder%" to replacementString
    }.toMap()

    return this.replace(replacements)
}