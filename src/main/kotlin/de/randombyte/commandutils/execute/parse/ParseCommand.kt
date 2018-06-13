package de.randombyte.commandutils.execute.parse

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.getUserUuid
import de.randombyte.commandutils.executeCommand
import de.randombyte.kosp.extensions.getUser
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor

class ParseCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val user = args.getUserUuid().getUser()!!
        val command = args.getOne<String>(CommandUtils.COMMAND_ARG).get()

        val target = if (user.isOnline) user.player.get() else user

        executeCommand(
                command = command,
                commandSource = src,
                target = target
        )

        return CommandResult.success()
    }
}