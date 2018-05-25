package de.randombyte.commandutils.conditions

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.execute.getUserUuid
import de.randombyte.kosp.extensions.toText
import de.randombyte.kosp.getServiceOrFail
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.cause.EventContext
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.economy.transaction.ResultType
import java.math.BigDecimal

class HasPayedCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val playerUuid = args.getUserUuid()
        val price = args.getOne<Double>(CommandUtils.PRICE_ARG).get()

        if (price < 0.0) {
            throw CommandException("'price' must be greater than 0.0!".toText())
        }

        val economyService = getServiceOrFail(EconomyService::class)
        val transactionResult = economyService.getOrCreateAccount(playerUuid).get().withdraw(
                economyService.defaultCurrency,
                BigDecimal.valueOf(price),
                Cause.of(EventContext.empty(), CommandUtils.INSTANCE))

        return if (transactionResult.result == ResultType.SUCCESS) {
            CommandResult.success()
        } else {
            CommandResult.empty()
        }
    }
}