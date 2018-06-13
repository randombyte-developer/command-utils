package de.randombyte.commandutils.execute

import de.randombyte.commandutils.CommandUtils
import de.randombyte.kosp.extensions.getUser
import de.randombyte.kosp.extensions.orNull
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import java.util.*

fun CommandResult.isTruthy() = successCount.isPresent // CommandResult.EMPTY.successCount is absent

val userUuidFromNameOrUuid = firstParsing(
        uuid(CommandUtils.PLAYER_UUID_ARG.toText()),
        user(CommandUtils.PLAYER_NAME_ARG.toText()))

fun CommandContext.getUserUuid(): UUID {
    val user = getOne<User>(CommandUtils.PLAYER_NAME_ARG).orNull()
    val playerUuid = getOne<UUID>(CommandUtils.PLAYER_UUID_ARG).orNull()

    return if (user != null) user.uniqueId else playerUuid!!
}

fun CommandContext.getUser(): User {
    val userUuid = getUserUuid()
    return userUuid.getUser() ?: throw CommandException("User '$userUuid' not found!".toText())
}

fun CommandContext.getPlayer(): Player {
    val user = getUser()
    if (!user.isOnline) {
        throw CommandException("User '${user.name}' must be online to execute this command!".toText())
    }
    return user.player.get()
}