package de.randombyte.commandutils.executewhenonline

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.ConfigAccessor
import de.randombyte.kosp.extensions.executeCommand
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player

object ExecuteWhenOnlineHandler {

    private const val EXECUTE_AS_CONSOLE_PREFIX = "*"

    fun setup(plugin: CommandUtils, configAccessor: ConfigAccessor) {
        Sponge.getEventManager().registerListeners(plugin, Listener(plugin, configAccessor))
    }

    fun execute(player: Player, command: String) {
        val fullCommand = command.replace("\$p", player.name)
        val unprefixedCommand = fullCommand.removePrefix(EXECUTE_AS_CONSOLE_PREFIX)

        val executeAsConsole = fullCommand.startsWith(EXECUTE_AS_CONSOLE_PREFIX)
        val commandSource = if (executeAsConsole) Sponge.getServer().console else player

        commandSource.executeCommand(unprefixedCommand)
    }
}