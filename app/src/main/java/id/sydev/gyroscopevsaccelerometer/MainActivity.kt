package id.sydev.gyroscopevsaccelerometer

import android.hardware.Sensor
import android.hardware.Sensor.STRING_TYPE_ACCELEROMETER
import android.hardware.Sensor.STRING_TYPE_GYROSCOPE
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    private var gyroMotion by mutableStateOf(SensorMotion())
    private var accelerometerMotion by mutableStateOf(SensorMotion())
    private var gyroAccuracyLevel by mutableStateOf(0)
    private var accelerometerAccuracyLevel by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 30.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer(
                            rotationX = gyroMotion.getX,
                            rotationY = gyroMotion.getY,
                            translationX = gyroMotion.translationX,
                            translationY = gyroMotion.translationY
                        )
                        .background(Color.Red.observeEquatorBackGround(gyroMotion)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getString(R.string.gyroscope_label),
                        color = observeEquatorLabel(gyroMotion)
                    )
                }

                Text(
                    text = "${
                        getString(
                            R.string.gyroscope_accuracy_label,
                            gyroAccuracyLevel.getAccuracyLabel()
                        )
                    }\n${
                        getString(
                            R.string.accelerometer_accuracy_label,
                            accelerometerAccuracyLevel.getAccuracyLabel()
                        )
                    }",
                    color = Color.White
                )

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .graphicsLayer(
                            rotationX = accelerometerMotion.getX,
                            rotationY = accelerometerMotion.getY,
                            translationX = accelerometerMotion.translationX,
                            translationY = accelerometerMotion.translationY
                        )
                        .background(Color.Blue.observeEquatorBackGround(accelerometerMotion)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getString(R.string.accelerometer_label),
                        color = observeEquatorLabel(accelerometerMotion)
                    )
                }
            }
        }

        registerSensorListener()
    }

    private fun registerSensorListener() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(
            this,
            gyro,
            SensorManager.SENSOR_DELAY_FASTEST,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_FASTEST,
            SensorManager.SENSOR_DELAY_FASTEST
        )

    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_GYROSCOPE -> {
                gyroMotion = SensorMotion(event.values[0], event.values[1])
            }
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerMotion = SensorMotion(event.values[0], event.values[1])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        when (sensor?.stringType) {
            STRING_TYPE_GYROSCOPE -> gyroAccuracyLevel = accuracy
            STRING_TYPE_ACCELEROMETER -> accelerometerAccuracyLevel = accuracy
        }
    }

    private fun Color.observeEquatorBackGround(motion: SensorMotion): Color {
        return if (motion.upDown.toInt() == 0 && motion.sides.toInt() == 0) Color.Green else this
    }

    private fun observeEquatorLabel(motion: SensorMotion): Color {
        return if (motion.upDown.toInt() == 0 && motion.sides.toInt() == 0) Color.Black else Color.White
    }

    private fun Int.getAccuracyLabel(): String {
        return when (this) {
            1 -> getString(R.string.sensor_accuracy_low)
            2 -> getString(R.string.sensor_accuracy_medium)
            3 -> getString(R.string.sensor_accuracy_high)
            else -> getString(R.string.sensor_accuracy_unreliable)
        }
    }
}