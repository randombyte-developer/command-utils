package de.randombyte.commandutils.execute

import de.randombyte.commandutils.CommandUtils
import de.randombyte.kosp.extensions.orNull
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.entity.living.player.User
import java.util.*

val userUuidFromNameOrUuid = firstParsing(
        uuid(CommandUtils.PLAYER_UUID_ARG.toText()),
        user(CommandUtils.PLAYER_NAME_ARG.toText()))

fun CommandContext.getUserUuid(): UUID {
    val user = getOne<User>(CommandUtils.PLAYER_NAME_ARG).orNull()
    val playerUuid = getOne<UUID>(CommandUtils.PLAYER_UUID_ARG).orNull()

    return if (user != null) user.uniqueId else playerUuid!!
}
