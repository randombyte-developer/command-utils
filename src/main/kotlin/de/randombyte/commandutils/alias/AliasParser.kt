package de.randombyte.commandutils.alias

import de.randombyte.commandutils.alias.AliasParser.SplitType.*

object AliasParser {

    private val ARG_REGEX = "\\{\\d*}".toRegex()
    private val VAR_ARG_REGEX = "\\{\\.{3}}".toRegex() // can only be the last thing in the command

    enum class SplitType { WORD, ARG, VAR_ARG }

    fun parse(alias: String, command: String): Map<String, String>? {
        val aliasSplits = alias.split(delimiters = " ")
        val commandSplits = command.split(delimiters = " ", limit = aliasSplits.size)

        if (commandSplits.size < aliasSplits.size) return null // command is to short for this alias

        val splitTypes = aliasSplits.mapIndexed { index, aliasSplit ->
            if (ARG_REGEX.matches(aliasSplit)) index to ARG
            else if (VAR_ARG_REGEX.matches(aliasSplit)) index to VAR_ARG
            else index to WORD
        }

        val aliasWords = splitTypes
                .filter { (_, type) -> type == WORD }
                .map { (index, _) -> aliasSplits[index] }

        val commandWords = splitTypes
                .filter { (_, type) -> type == WORD }
                .map { (index, _) -> commandSplits[index] }

        val wordsMatch = all(aliasWords, commandWords) { aliasWord, commandWord -> aliasWord == commandWord }
        if (!wordsMatch) return null

        return splitTypes.mapNotNull { (index, splitType) ->
            when (splitType) {
                ARG -> aliasSplits[index] to commandSplits[index]
                VAR_ARG -> aliasSplits[index] to (commandSplits.drop(index)).joinToString(separator = " ") // drop the first elements
                else -> null
            }
        }.toMap()
    }

    private fun <A, B> all(listA: List<A>, listB: List<B>, predicate: (A, B) -> Boolean): Boolean {
        var all = true
        AliasParser.forEach(listA, listB) { a, b ->
            if (!all) return@forEach // already failed, don't check the others
            if (!predicate(a, b)) all = false
        }
        return all
    }

    private fun <A, B> forEach(listA: List<A>, listB: List<B>, action: (A, B) -> Unit) {
        if (listA.size != listB.size) throw IllegalArgumentException("Both lists have to be of the same size!")
        listA.forEachIndexed { index, a -> action(a, listB[index]) }
    }
}