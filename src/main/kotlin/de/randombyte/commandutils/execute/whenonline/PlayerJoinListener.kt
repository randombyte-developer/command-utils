package de.randombyte.commandutils.execute.whenonline

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.executeCommand
import de.randombyte.kosp.extensions.getPlayer
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.scheduler.Task

class PlayerJoinListener {
    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        val commandUtils = CommandUtils.INSTANCE
        val config = commandUtils.configAccessor.executeWhenOnline.get()
        val playerUuid = event.targetEntity.uniqueId
        val commands = config.commands[playerUuid] ?: return
        if (commands.isEmpty()) return

        Task.builder()
                .delayTicks(config.delayAfterJoin.toLong())
                .execute { ->
                    // player still online after the delay?
                    val player = playerUuid.getPlayer() ?: return@execute

                    val currentConfig = commandUtils.configAccessor.executeWhenOnline.get()

                    currentConfig.commands[playerUuid]?.forEachIndexed { index, command ->
                        executeCommand(
                                command = command,
                                commandSource = player,
                                replacements = mapOf("\$p" to player.name),
                                commandIndex = index
                        )
                    }

                    val newConfig = currentConfig.copy(commands = currentConfig.commands - playerUuid)
                    commandUtils.configAccessor.executeWhenOnline.save(newConfig)
                }
                .submit(commandUtils)
    }
}