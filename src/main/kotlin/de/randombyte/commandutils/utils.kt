package de.randombyte.commandutils

import de.randombyte.kosp.extensions.executeCommand
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player

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