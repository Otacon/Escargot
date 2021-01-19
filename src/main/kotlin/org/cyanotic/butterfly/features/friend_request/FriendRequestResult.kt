package org.cyanotic.butterfly.features.friend_request

sealed class FriendRequestResult {
    object Accepted: FriendRequestResult()
    object Rejected: FriendRequestResult()
}