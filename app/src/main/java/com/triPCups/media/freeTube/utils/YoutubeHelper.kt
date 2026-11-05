package com.triPCups.media.freeTube.utils

class YoutubeHelper {

    companion object {

        fun extractVideoIdFromUrl(url: String): String? {
            // Handle youtu.be short links and /embed/ and /shorts/
            val shortRegex = Regex("(?:https?://)?(?:www\\.|m\\.)?(?:youtu\\.be/|youtube\\.com/(?:embed/|shorts/))([\\w-]{11})")
            shortRegex.find(url)?.groupValues?.getOrNull(1)?.let { return it }
            // Handle youtube.com/watch?... where v= may not be the first parameter
            val watchRegex = Regex("[?&]v=([\\w-]{11})")
            return watchRegex.find(url)?.groupValues?.getOrNull(1)
        }

        fun extractTimestampFromUrl(url: String): Int? {
            val regex = Regex("[?&](t|start)=([^&]+)")
            val matchResult = regex.find(url)
            val tValue = matchResult?.groups?.get(2)?.value ?: return null

            // Long accumulator + toIntOrNull: values come from external links, so an oversized
            // number must yield null instead of throwing NumberFormatException or overflowing.
            var totalSeconds = 0L
            val timeRegex = Regex("(\\d+)([hms])")
            val matches = timeRegex.findAll(tValue)

            if (matches.any()) {
                for (m in matches) {
                    val amount = m.groupValues[1].toIntOrNull() ?: return null
                    when (m.groupValues[2]) {
                        "h" -> totalSeconds += amount * 3600L
                        "m" -> totalSeconds += amount * 60L
                        "s" -> totalSeconds += amount
                    }
                    if (totalSeconds > Int.MAX_VALUE) return null
                }
                return totalSeconds.toInt()
            } else {
                // no time unit, parse as plain seconds
                return tValue.toIntOrNull()
            }
        }
    }
}