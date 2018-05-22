package de.randombyte.commandutils.execute.whenonline

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.getUserUuid
import de.randombyte.commandutils.service.CommandUtilsService
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.getServiceOrFail
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor

class ExecuteWhenOnlineCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val command = args.getOne<String>(CommandUtils.COMMAND_ARG).get()
        val playerUuid = args.getUserUuid()

        getServiceOrFail(CommandUtilsService::class).executeWhenOnline(playerUuid, command)

        src.sendMessage("Successfully added command!".green())

        return CommandResult.success()
    }
}