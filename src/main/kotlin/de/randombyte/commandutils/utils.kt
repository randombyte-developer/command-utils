package de.randombyte.commandutils

import de.randombyte.kosp.extensions.executeCommand
import de.randombyte.kosp.extensions.replace
import me.rojo8399.placeholderapi.PlaceholderService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

private const val EXECUTE_AS_CONSOLE_PREFIX = "*"

fun execute(command: String, commandSource: CommandSource) {
    val unprefixedCommand = command.removePrefix(EXECUTE_AS_CONSOLE_PREFIX)

    val executeAsConsole = command.startsWith(EXECUTE_AS_CONSOLE_PREFIX)
    val finalCommandSource = if (executeAsConsole) Sponge.getServer().console else commandSource

    finalCommandSource.executeCommand(unprefixedCommand)
}

fun executeForPlayer(command: String, player: Player) {
    val fullCommand = command.replace("\$p", player.name)
    execute(fullCommand, commandSource = player)
}

fun String.replace(values: Map<String, String>): String {
    var string = this
    values.forEach { (argument, value) ->
        string = string.replace(argument, value)
    }
    return string
}

/**
 * Tries to process the placeholders if PlaceholderAPI is loaded.
 */
fun String.process(placeholderApi: PlaceholderService, player: Player): String {
    val placeholders = placeholderApi.defaultPattern.toRegex().findAll(this)
            .map { matchResult -> matchResult.groupValues[1] }.toList()
    val replacements = placeholders.map { placeholder ->
        val replacement = placeholderApi.parse(placeholder, player, null)
        val replacementString = if (replacement is Text) replacement.toPlain() else replacement.toString()
        "%$placeholder%" to replacementString
    }.toMap()

    return this.replace(replacements)
}