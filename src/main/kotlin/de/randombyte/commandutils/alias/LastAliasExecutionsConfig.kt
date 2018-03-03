package de.randombyte.commandutils.alias

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.time.Duration
import java.time.Instant
import java.util.*

@ConfigSerializable
data class LastAliasExecutionsConfig(
        @Setting val aliases: Map<UUID, Map<String, Date>> = emptyMap()
) {
    fun add(player: UUID, alias: String, date: Date): LastAliasExecutionsConfig {
        val newPlayerCooldowns = (aliases[player] ?: emptyMap()).plus(alias to date)
        return copy(aliases = aliases.plus(player to newPlayerCooldowns))
    }

    fun get(player: UUID, alias: String): Date? = aliases[player]?.get(alias)

    fun remainingCooldown(lastExecution: Date, cooldown: Duration) = Duration.ofMillis(lastExecution.time + cooldown.toMillis() - Date.from(Instant.now()).time)
}