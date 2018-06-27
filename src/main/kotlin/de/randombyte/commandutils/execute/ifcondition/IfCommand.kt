package de.randombyte.commandutils.execute.ifcondition

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.isTruthy
import de.randombyte.commandutils.executeCommand
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor

class IfCommand(val inverted: Boolean) : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val conditionCommand = args.getOne<String>(CommandUtils.CONDITION_COMMAND_ARG).get()
        val commands = args.getAll<String>(CommandUtils.COMMAND_ARG)

        executeCommand(
                command = conditionCommand,
                commandSource = src,
                commandIndex = 0
        ) { commandResult -> // workaround for https://github.com/SpongePowered/SpongeCommon/issues/1922
            var truthy = commandResult.isTruthy()

            if (inverted) truthy = !truthy

            if (truthy) {
                commands.forEach { executeCommand(
                        command = it,
                        commandSource = src,
                        commandIndex = 0
                ) }
            }
        }

        return CommandResult.success()
    }
}