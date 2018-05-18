package de.randombyte.commandutils.executewhenonline

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.service.CommandUtilsService
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.orNull
import de.randombyte.kosp.getServiceOrFail
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.entity.living.player.User
import java.util.*

class ExecuteWhenOnlineCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val user = args.getOne<User>(CommandUtils.PLAYER_NAME_ARG).orNull()
        var playerUuid = args.getOne<UUID>(CommandUtils.PLAYER_UUID_ARG).orNull()
        val command = args.getOne<String>(CommandUtils.COMMAND_ARG).get()

        if (user != null) playerUuid = user.uniqueId

        getServiceOrFail(CommandUtilsService::class).executeWhenOnline(playerUuid!!, command)

        src.sendMessage("Successfully added command!".green())

        return CommandResult.success()
    }
}