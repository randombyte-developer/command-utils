package de.randombyte.commandutils.executeonserverstartup

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.ConfigAccessor
import de.randombyte.kosp.executeAsConsole
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent
import org.spongepowered.api.scheduler.Task

class ServerStartupListener(val plugin: CommandUtils, val configAccessor: ConfigAccessor) {
    @Listener
    fun onServerStartup(event: GameStartedServerEvent) {
        Task.builder()
                .delayTicks(1)
                .execute { ->
                    configAccessor.get().executeOnServerStartup.commands.forEach { println(it); executeAsConsole(it) }
                }
                .submit(plugin)
    }
}