package com.vmenon.mpo.util;

import com.vmenon.mpo.api.Episode;

public class MediaHelper {

    public static class MediaType {
        int mediaType;
        long id;

        public MediaType(int mediaType, long id) {
            this.mediaType = mediaType;
            this.id = id;
        }

        public int getMediaType() {
            return mediaType;
        }

        public long getId() {
            return id;
        }
    }

    public static final int MEDIA_TYPE_EPISODE = 0;

    private static final String EPISODE_MEDIA_PREFIX = "episode";

    public static String createMediaId(Episode episode) {
        return EPISODE_MEDIA_PREFIX + ":" + episode.id;
    }

    public static MediaType getMediaTypeFromMediaId(String mediaId) {
        String[] mediaIdParts = mediaId.split(":");
        if (EPISODE_MEDIA_PREFIX.equals(mediaIdParts[0])) {
            long episodeId = Long.parseLong(mediaIdParts[1]);
            return new MediaType(MEDIA_TYPE_EPISODE, episodeId);
        }

        return null;
    }
}
