package de.randombyte.commandutils

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.util.*

@ConfigSerializable
data class Config(
        @Setting val executeWhenOnline: ExecuteWhenOnline = Config.ExecuteWhenOnline()
) {
    @ConfigSerializable
    data class ExecuteWhenOnline(
            @Setting val commands: Map<UUID, List<String>> = emptyMap(),
            @Setting(comment = "In ticks") val delayAfterJoin: Int = 3
    ) {
        fun addCommand(playerUUID: UUID, command: String) =
                copy(commands = commands + (playerUUID to ((commands[playerUUID] ?: emptyList()) + command)))

        fun  removeCommand(playerUUID: UUID) = copy(commands = commands - playerUUID)
    }
}