package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.getUser
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor

class HasPermissionCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val user = args.getUser()
        val permission = args.getOne<String>(CommandUtils.PERMISSION_ARG).get()

        return user.hasPermission(permission).toCommandResult()
    }
}