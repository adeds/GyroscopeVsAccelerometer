package id.sydev.gyroscopevsaccelerometer

data class SensorMotion(val upDown: Float = 0f, val sides: Float = 0f) {
    val getX = sides * 3
    val getY = upDown * 3
    val translationX = upDown * -10
    val translationY = sides * 10
}