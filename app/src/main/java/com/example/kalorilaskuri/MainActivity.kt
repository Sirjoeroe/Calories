package com.example.kalorilaskuri

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kalorilaskuri.ui.theme.KalorilaskuriTheme
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KalorilaskuriTheme {
                KalorilaskuriApp()
            }
        }
    }
}

@Composable
fun KalorilaskuriApp() {
    var paino by remember { mutableStateOf("") }
    var sukupuoli by remember { mutableStateOf("Mies") }
    var fyysinenAktiivisuus by remember { mutableStateOf("Matala") }
    var kalorikulutus by remember { mutableStateOf<Double?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kalorilaskuri",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
        PainonSyotto(paino) { paino = it }
        Spacer(modifier = Modifier.height(16.dp))
        SukupuolenValinta(sukupuoli) { sukupuoli = it }
        Spacer(modifier = Modifier.height(16.dp))
        FyysinenAktiivisuusDropdown(fyysinenAktiivisuus) { fyysinenAktiivisuus = it }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            kalorikulutus = laskeKalorit(paino, sukupuoli, fyysinenAktiivisuus)
        }) {
            Text("Laske")
        }
        Spacer(modifier = Modifier.height(16.dp))
        KalorikulutusTulokset(kalorikulutus)
    }
}

@Composable
fun PainonSyotto(paino: String, onPainoMuuttunut: (String) -> Unit) {
    OutlinedTextField(
        value = paino,
        onValueChange = onPainoMuuttunut,
        label = { Text("Paino (kg)") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SukupuolenValinta(valittuSukupuoli: String, onSukupuoliValittu: (String) -> Unit) {
    Column {
        Text("Sukupuoli")
        Row {
            RadioButton(
                selected = valittuSukupuoli == "Mies",
                onClick = { onSukupuoliValittu("Mies") }
            )
            Text("Mies")
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(
                selected = valittuSukupuoli == "Nainen",
                onClick = { onSukupuoliValittu("Nainen") }
            )
            Text("Nainen")
        }
    }
}

@Composable
fun FyysinenAktiivisuusDropdown(valittuAktiivisuus: String, onAktiivisuusValittu: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val aktiivisuustasot = listOf("Matala","Tavallinen", "Keskitaso", "Korkea", "Erittäin korkea")

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valittuAktiivisuus,
            onValueChange = {},
            label = { Text("Fyysinen aktiivisuus") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            aktiivisuustasot.forEach { aktiivisuus ->
                DropdownMenuItem(
                    text = { Text(aktiivisuus) },
                    onClick = {
                        onAktiivisuusValittu(aktiivisuus)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun KalorikulutusTulokset(kalorikulutus: Double?) {
    if (kalorikulutus != null) {
        Text("Arvioidut poltetut kalorit: $kalorikulutus kcal")
    } else {
        Text("Anna kelvolliset tiedot.")
    }
}

fun laskeKalorit(paino: String, sukupuoli: String, fyysinenAktiivisuus: String): Double? {
    val painoKg = paino.toDoubleOrNull() ?: return null

    val perusKalorit = when (sukupuoli) {
        "Mies" -> 88.362 + (13.397 * painoKg)
        "Nainen" -> 447.593 + (9.247 * painoKg)
        else -> return null
    }

    val aktiivisuusKerroin = when (fyysinenAktiivisuus) {
        "Matala" -> 1.3
        "Tavallinen" -> 1.5
        "Keskitaso" -> 1.7
        "Korkea" -> 2.0
        "Erittäin korkea" -> 2.2
        else -> return null
    }

    val kalorikulutus = perusKalorit * aktiivisuusKerroin
    return round(kalorikulutus)

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KalorilaskuriTheme {
        KalorilaskuriApp()
    }
}