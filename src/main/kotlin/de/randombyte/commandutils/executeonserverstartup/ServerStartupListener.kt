package de.randombyte.commandutils.executeonserverstartup

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.executeCommand
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.scheduler.Task

class ServerStartupListener {
    @Listener
    fun onServerStartup(event: GameStartedServerEvent) {
        Task.builder()
                .delayTicks(1)
                .execute { ->
                    CommandUtils.INSTANCE.configAccessor.executeOnServerStartup.get().commands
                            .forEachIndexed { index, command -> executeCommand(
                                    command = command,
                                    commandSource = Sponge.getServer().console,
                                    commandIndex = index
                            ) }
                }
                .submit(CommandUtils.INSTANCE)
    }
}