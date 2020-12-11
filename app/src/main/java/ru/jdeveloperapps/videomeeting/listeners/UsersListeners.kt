package ru.jdeveloperapps.videomeeting.listeners

import ru.jdeveloperapps.videomeeting.models.User

interface UsersListeners {

    fun initiateVideoMeeting(user: User)

    fun initiateAudioMeeting(user: User)
}