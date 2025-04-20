package dev.slne.surf.event.bmbf.command.sub

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.multiLiteralArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.event.bmbf.BmbfCategory
import dev.slne.surf.event.bmbf.BmbfChallenge
import dev.slne.surf.event.bmbf.BmbfManager
import dev.slne.surf.event.bmbf.permission.BmbfPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.selectCommand() = subcommand("select") {
    withPermission(BmbfPermissionRegistry.BMBF_SELECT_COMMAND)
    withRequirement { !BmbfManager.running }

    multiLiteralArgument("category", *BmbfCategory.entries.map { it.toString().lowercase().replace("_", "-") }.toTypedArray())
    multiLiteralArgument("challenge", "1", "10", "30")

    anyExecutor { sender, args ->
        val category: String by args
        val challenge: String by args

        val enumeratedCategory = category.replace("-", "_").uppercase()
        val parsedCategory = BmbfCategory.entries.first { it.name.equals(enumeratedCategory, true) }
        val parsedChallenge = when (challenge) {
            "1" -> BmbfChallenge.MINUTE_1
            "10" -> BmbfChallenge.MINUTE_10
            "30" -> BmbfChallenge.MINUTE_30
            else -> throw CommandAPI.failWithString("Invalid challenge: $challenge")
        }

        BmbfManager.selectNextCategory(parsedCategory, parsedChallenge)
        sender.sendText {
            appendPrefix()
            success("Die Kategorie wurde auf ${parsedCategory.displayName} und die Herausforderung auf $parsedChallenge gesetzt.")
        }
    }
}