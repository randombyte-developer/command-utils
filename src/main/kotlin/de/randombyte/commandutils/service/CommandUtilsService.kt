package de.randombyte.commandutils.service

import java.util.*

@Deprecated("This will be removed at some point in the future. Use the '/cu execute whenOnline' command instead.")
interface CommandUtilsService {

    fun executeWhenOnline(playerUuid: UUID, newCommand: String)

    fun executeWhenOnline(playerUuid: UUID, commands: List<String>) = commands.forEach { executeWhenOnline(playerUuid, it) }

}