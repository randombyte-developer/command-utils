package de.randombyte.commandutils.executewhenonline

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.ConfigAccessor
import de.randombyte.commandutils.executeForPlayer
import de.randombyte.kosp.extensions.getPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.scheduler.Task

class PlayerJoinListener(val plugin: CommandUtils, val configAccessor: ConfigAccessor) {
    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        val config = configAccessor.get()
        val playerUuid = event.targetEntity.uniqueId
        val commands = config.executeWhenOnline.commands[playerUuid] ?: return
        if (commands.isEmpty()) return

        Task.builder()
                .delayTicks(config.executeWhenOnline.delayAfterJoin.toLong())
                .execute { ->
                    // player still online after the delay?
                    val player = playerUuid.getPlayer() ?: return@execute
                    commands.forEach { command ->
                        executeForPlayer(command, player)
                    }
                    val currentConfig = configAccessor.get()
                    configAccessor.save(currentConfig.copy(executeWhenOnline = currentConfig.executeWhenOnline.copy(commands = currentConfig.executeWhenOnline.commands - playerUuid)))
                }
                .submit(plugin)
    }
}