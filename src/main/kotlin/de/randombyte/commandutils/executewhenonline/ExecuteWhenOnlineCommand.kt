package de.randombyte.commandutils.executewhenonline

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.service.CommandUtilsService
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.toText
import de.randombyte.kosp.extensions.toUUID
import de.randombyte.kosp.getServiceOrFail
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.service.user.UserStorageService

class ExecuteWhenOnlineCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val playerOrUuid = args.getOne<String>(CommandUtils.PLAYER_OR_UUID_ARG).get()
        val command = args.getOne<String>(CommandUtils.COMMAND_ARG).get()

        // try parsing as UUID first
        val parsedUuid = try { playerOrUuid.toUUID() } catch (_: IllegalArgumentException) { null }
        val playerUuid = if (parsedUuid != null) parsedUuid else {
            // then as a user/player name
            val userStorageService = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
            userStorageService.get(playerOrUuid).orElseThrow {
                CommandException("'$playerOrUuid' is not a valid UUID or user/player name!".toText())
            }.uniqueId
        }

        getServiceOrFail(CommandUtilsService::class).executeWhenOnline(playerUuid, command)

        src.sendMessage("Successfully added command!".green())

        return CommandResult.success()
    }
}