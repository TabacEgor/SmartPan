package com.example.smartpan

data class SmartPanData(
    val current_temperature: Float,
    val set_temperature: Float,
    val current_pwm: Int,
    val work_mode: WorkMode
)

enum class WorkMode(mode: Int) {
    NO_SET(0),
    TEMPERATURE_MAINTENANCE(1),
    MILK(2)
}