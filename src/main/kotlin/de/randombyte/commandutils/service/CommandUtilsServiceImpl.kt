package de.randombyte.commandutils.service

import de.randombyte.commandutils.ConfigAccessor
import de.randombyte.commandutils.executeForPlayer
import de.randombyte.kosp.extensions.getPlayer
import java.util.*

class CommandUtilsServiceImpl(val configAccessor: ConfigAccessor) : CommandUtilsService {
    override fun executeWhenOnline(playerUuid: UUID, newCommand: String) {
        val player = playerUuid.getPlayer()
        // shortcut, the player is already online
        if (player != null) {
            executeForPlayer(newCommand, player)
            return
        }

        val config = configAccessor.get()
        val newExecuteWhenOnlineConfig = config.executeWhenOnline.addCommand(playerUuid, newCommand)
        val newConfig = config.copy(executeWhenOnline = newExecuteWhenOnlineConfig)
        configAccessor.save(newConfig)
    }
}