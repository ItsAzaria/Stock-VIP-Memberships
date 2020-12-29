package me.stockvip.memberships.listeners

import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import kotlinx.coroutines.channels.ClosedSendChannelException
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.sendPrivateMessage
import me.jakejmattson.discordkt.api.extensions.toSnowflake
import java.awt.Color

fun upgradeListener() = listeners {
    on<MessageCreateEvent> {
        getGuild() ?: return@on
        val channelID = System.getenv("UPGRADE_CHAN_ID") ?: return@on
        val channel = discord.api.getChannel(channelID.toSnowflake()) ?: return@on

        if (channel.id.longValue != message.channelId.longValue) return@on

        message.delete()

        val member = member ?: return@on

        if (message.content.toLowerCase().startsWith("upgrade")) {
            try {
                member.sendPrivateMessage {
                    color = Color.decode("#FFC700")
                    title = "Please click the link below to purchase your role"
                    description = ":point_right: https://donatebot.io/checkout/708542100873936947?buyer=${member.id.value}  :point_left: \n\n" +
                            "The role should be applied in the next 10 minutes, if you have any questions or problems please head over to <#760287662220771360> and support will be with you shortly."

                    footer {
                        icon = "https://i.imgur.com/07U8XSA.gif"
                        text = "StockVIP - Firm Handshakes"
                    }
                }
            } catch (ex: ClosedSendChannelException) {
                return@on
            }

        }




    }
}