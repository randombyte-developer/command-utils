package de.randombyte.commandutils.service

import de.randombyte.commandutils.config.ConfigAccessor
import de.randombyte.commandutils.executeCommand
import de.randombyte.kosp.extensions.getPlayer
import java.util.*

class CommandUtilsServiceImpl(val configAccessor: ConfigAccessor) : CommandUtilsService {
    override fun executeWhenOnline(playerUuid: UUID, newCommand: String) {
        val player = playerUuid.getPlayer()
        // shortcut, the player is already online
        if (player != null) {
            executeCommand(
                    command = newCommand,
                    commandSource = player,
                    replacements = mapOf("\$p" to player.name)
            )
            return
        }

        val config = configAccessor.executeWhenOnline.get()
        val newExecuteWhenOnlineConfig = config.addCommand(playerUuid, newCommand)
        configAccessor.executeWhenOnline.save(newExecuteWhenOnlineConfig)
    }
}