package com.example.sensors

import android.annotation.SuppressLint
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.sensors.ui.theme.SensorsTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorsTheme {
                SensorApp()
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SensorData(modifier: Modifier = Modifier) {
    val sensorManager = LocalContext.current.getSystemService(SENSOR_SERVICE) as SensorManager
    val tempSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    val lightSensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    val tempItems = remember {
        mutableStateListOf<String>()
    }
    val lightItems = remember {
        mutableStateListOf<String>()
    }

    val tempSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(p0: SensorEvent?) {
            if (p0 != null) {
                tempItems.add(p0.values[0].toString())
            }
        }
    }

    val lightSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        override fun onSensorChanged(p0: SensorEvent?) {
            if (p0 != null) {
                lightItems.add(p0.values[0].toString())
            }
        }
    }

    sensorManager.registerListener(tempSensorEventListener, tempSensor, SensorManager.SENSOR_DELAY_NORMAL)
    sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = "Temp: ")
            MyList(itemList = tempItems)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(text = "Light: ")
            MyList(itemList = lightItems)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyList(itemList: List<String>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val composableScope = rememberCoroutineScope()

    LazyColumn (state = listState) {
        items(itemList) {
            item -> Text(text = item)
        }
        composableScope.launch {
            listState.animateScrollToItem(itemList.size)
        }
    }
}

@Preview
@Composable
fun SensorApp() {
    val scrollState = rememberScrollState()
    SensorData(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
        .scrollable(state = scrollState, orientation = Orientation.Vertical)
    )
}