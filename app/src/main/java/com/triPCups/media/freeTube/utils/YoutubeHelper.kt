package com.triPCups.media.freeTube.utils

class YoutubeHelper {

    companion object {

        fun extractVideoIdFromUrl(url: String): String? {
            val regex = "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/\\S*?(?:(?:\\/e(?:mbed))?\\/|watch\\/|v\\/|watch\\?v=|embed\\/|youtu.be\\/))([\\w\\-_]*)"
            val pattern = Regex(regex)

            val matchResult = pattern.find(url)
            return matchResult?.groupValues?.getOrNull(1)
        }

        // Extracts timestamp from YouTube URL
        fun extractTimestampFromUrl(url: String): Int? {
            val regex = "([?&]t=|[?&]start=)(\\d+)"
            val pattern = Regex(regex)
            val matchResult = pattern.find(url)
            return matchResult?.groupValues?.getOrNull(2)?.toIntOrNull()
        }
    }
}