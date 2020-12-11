package ru.jdeveloperapps.videomeeting.utilites

class Constants {
    companion object {
        const val KEY_COLLECTIONS_USERS = "users"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
        const val KEY_USER_ID = "user_id"
        const val KEY_FCM_TOKEN = "fcm_token"

        const val KEY_PREFERENCE_NAME = "videoMeetingPreference"
        const val KEY_IS_SIGNED_IN = "isSignedIn"

        const val REMOTE_MSG_AUTHORIZATION = "Authorization"
        const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"

        const val REMOTE_MSG_TYPE = "type"
        const val REMOTE_MSG_INVITATION = "invitation"
        const val REMOTE_MSG_MEETING_TYPE = "meetingType"
        const val REMOTE_MSG_INVITER_TOKEN = "inviterToken"
        const val REMOTE_MSG_DATA = "data"
        const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

        fun getRemoteMessageHeaders(): HashMap<String, String> {
            return hashMapOf(
                Pair(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAAtkkAQ8:APA91bHU6nS5NpYKoZ56GB-ello6QFU6K5GwlhhJA37k4hlo7Ys8r4eAQT5joCWmF5ZuqgLZcjnQ-UuEHOCbxZ2eHqsVBGeRo6srWz3bT0aZfXdbEyucUW1aYCR9YEp4AvKHaHk5EQjp"
                ),
                Pair(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
                )
            )
        }
    }
}