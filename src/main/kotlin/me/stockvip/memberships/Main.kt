package me.stockvip.memberships

import com.gitlab.kordlib.common.Color
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.gateway.PrivilegedIntent
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.kordx.emoji.toReaction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.sendPrivateMessage
import me.jakejmattson.discordkt.api.extensions.toSnowflake

var counter = 0
var closedDmCounter = 0
val token = System.getenv("BOT_TOKEN") ?: null
val prefix = System.getenv("DEFAULT_PREFIX") ?: "memberships!"
val upgradeChannel = System.getenv("UPGRADE_CHAN_ID") ?: null
val upgradeMessage = System.getenv("REACT_MESSAGE") ?: null

@PrivilegedIntent
suspend fun main(args: Array<String>) {

    require(token != null) { "Expected the bot token as an environment variable" }
    require (upgradeChannel != null) { "Expected the upgrade channel as an environment variable" }
    require (upgradeMessage != null) { "Expected the upgrade message as an environment variable" }

    bot(token) {
        prefix {
            prefix
        }

        configure {
            allowMentionPrefix = false
            commandReaction = null
            intents = Intents.all.values.toSet()
        }

        permissions {
            true
        }

        onStart {
            evalLoop(this.api)
        }
    }
}

suspend fun evalLoop(api: Kord) {
    GlobalScope.launch {

        val message = api.defaultSupplier.getMessage(upgradeChannel!!.toSnowflake(), upgradeMessage!!.toSnowflake())
        message.addReaction(Emojis.whiteCheckMark.toReaction())

        val reactors = message.getReactors(Emojis.whiteCheckMark.toReaction()).filterNot { it.isBot }.toList()
        val firstReactor = reactors.firstOrNull()

        if (firstReactor == null) {
            delay(10000)
            evalLoop(api)
            return@launch
        }

        val member = firstReactor.asMemberOrNull(message.getGuild().id)

        if (member == null) {
            message.deleteReaction(firstReactor.id, Emojis.whiteCheckMark.toReaction())
            delay(10000)
            evalLoop(api)
            return@launch
        }

        sendDM(member)
        message.deleteReaction(member.id, Emojis.whiteCheckMark.toReaction())

        delay(10000)
        evalLoop(api)
    }
}

suspend fun sendDM(member: Member) {
    try {
        member.sendPrivateMessage {
            color = Color(255, 199, 0)
            title = "Please click the link below to purchase your role"
            description = ":point_right: https://donatebot.io/checkout/708542100873936947?buyer=${member.id.value}  :point_left: \n\n" +
                    "The role should be applied in the next 10 minutes, if you have any questions or problems please head over to <#760287662220771360> and support will be with you shortly."

            footer {
                icon = "https://i.imgur.com/07U8XSA.gif"
                text = "StockVIP - Firm Handshakes"
            }
        }
        counter++
        println("Sent dm to ${member.username} :: ${member.id.value}")
    } catch (ex: Exception) {
        closedDmCounter++
        println(ex.toString())
    }
}