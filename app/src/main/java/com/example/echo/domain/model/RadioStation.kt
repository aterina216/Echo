package com.example.echo.domain.model

data class RadioStation(
    val id: String,
    val title: String,
    val description: String,
    val url: String
)

fun defaultStations(): List<RadioStation> = listOf(
    RadioStation(
        id = "spacestation",
        title = "Space Station Soma",
        description = "Ambient, space music",
        url = "http://ice1.somafm.com/spacestation-128-mp3"
    ),
    RadioStation(
        id = "dronezone",
        title = "Drone Zone",
        description = "Droning, atmospheric soundscapes",
        url = "http://ice1.somafm.com/dronezone-128-mp3"
    ),
    RadioStation(
        id = "deepspaceone",
        title = "Deep Space One",
        description = "Deep, dark ambient",
        url = "http://ice1.somafm.com/deepspaceone-128-mp3"
    )
)