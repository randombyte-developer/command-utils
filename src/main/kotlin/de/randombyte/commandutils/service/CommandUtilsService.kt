package de.randombyte.commandutils.service

import java.util.*

interface CommandUtilsService {

    fun executeWhenOnline(playerUuid: UUID, newCommand: String)

    fun executeWhenOnline(playerUuid: UUID, commands: List<String>) = commands.forEach { executeWhenOnline(playerUuid, it) }

}