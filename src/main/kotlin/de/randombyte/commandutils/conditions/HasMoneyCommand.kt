package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.getUserUuid
import de.randombyte.kosp.extensions.getServiceOrFail
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.service.economy.EconomyService

class HasMoneyCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val playerUuid = args.getUserUuid()
        val money = args.getOne<Double>(CommandUtils.MONEY_ARG).get()

        if (money < 0.0) {
            throw CommandException("'money' must be greater than 0.0!".toText())
        }

        val economyService = EconomyService::class.getServiceOrFail()
        val balance = economyService
                .getOrCreateAccount(playerUuid).get()
                .getBalance(economyService.defaultCurrency)

        return (balance >= money.toBigDecimal()).toCommandResult()
    }
}