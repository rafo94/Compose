package com.rafo.composeexample.utils

import com.rafo.composeexample.R

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    var selectedIcon: Int,
    var screen_route: String
) {

    object Home : BottomNavItem(
        "Home",
        R.drawable.ic_home,
        R.drawable.ic_outline_home,
        "home"
    )

    object MyNetwork : BottomNavItem(
        "Network",
        R.drawable.ic_group,
        R.drawable.ic_outline_group,
        "my_network"
    )

    object Movie : BottomNavItem(
        "Post",
        R.drawable.ic_movies,
        R.drawable.ic_outline_movies,
        "movie"
    )

    object Notification : BottomNavItem(
        "Notify",
        R.drawable.ic_notification,
        R.drawable.ic_outline_notification,
        "notification"
    )

    object Jobs : BottomNavItem(
        "Jobs",
        R.drawable.ic_job,
        R.drawable.ic_outline_job,
        "jobs"
    )
}