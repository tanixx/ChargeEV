package com.example.chargeev

data class ApiResponse(
    val stations: List<Map<String, Any>>,
    val fourWheeler: List<Map<String, Any>>,
    val twoWheeler: List<Map<String, Any>>
)
