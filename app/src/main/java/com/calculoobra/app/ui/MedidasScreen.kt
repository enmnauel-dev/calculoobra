package com.calculoobra.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calculoobra.app.model.Muro
import com.calculoobra.app.ui.theme.Green
import com.calculoobra.app.ui.theme.SurfaceAlt
import com.calculoobra.app.ui.theme.TextMuted

@Composable
fun MedidasScreen(
    vm: CalculoObraViewModel,
    onCalculado: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        /* -------- Tramos de pared -------- */
        SectionCard(
            title = "Tramos de pared",
            subtitle = "Largo y alto en metros. El número de puertas y ventanas se descuenta del área del tramo."
        ) {
            vm.muros.forEachIndexed { index, muro ->
                MuroCard(
                    index = index,
                    muro = muro,
                    onDelete = { vm.removeMuro(index) },
                    onUpdate = { m -> vm.updateMuro(index) { m } }
                )
                Spacer(Modifier.height(8.dp))
            }
            OutlinedButton(
                onClick = { vm.addMuro() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Añadir tramo de pared", modifier = Modifier.padding(start = 4.dp))
            }
        }

        Spacer(Modifier.height(12.dp))

        /* -------- Aberturas -------- */
        SectionCard(
            title = "Aberturas",
            subtitle = "Dimensiones estándar de puertas y ventanas."
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Puerta: ancho", vm.anchoPuerta, { vm.anchoPuerta = it }, Modifier.weight(1f), "m")
                NumField("Puerta: alto", vm.altoPuerta, { vm.altoPuerta = it }, Modifier.weight(1f), "m")
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Ventana: ancho", vm.anchoVentana, { vm.anchoVentana = it }, Modifier.weight(1f), "m")
                NumField("Ventana: alto", vm.altoVentana, { vm.altoVentana = it }, Modifier.weight(1f), "m")
            }
        }

        Spacer(Modifier.height(12.dp))

        /* -------- Pañete -------- */
        SectionCard(title = "Pañete / Repello") {
            ToggleRow(
                title = "Incluir pañete",
                subtitle = "Sobre el área neta de pared",
                checked = vm.paneteActivo,
                onCheckedChange = { vm.paneteActivo = it }
            )
            if (vm.paneteActivo) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1 to "1 cara", 2 to "2 caras").forEach { (n, label) ->
                        FilterChip(
                            selected = vm.paneteCaras == n,
                            onClick = { vm.paneteCaras = n },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        /* -------- Plato / Losa -------- */
        SectionCard(title = "Losa / Plato de techo") {
            ToggleRow(
                title = "Incluir losa",
                subtitle = "Hormigón armado de 10 cm (estándar)",
                checked = vm.platoActivo,
                onCheckedChange = { vm.platoActivo = it }
            )
            if (vm.platoActivo) {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    NumField("Largo", vm.largoPlato, { vm.largoPlato = it }, Modifier.weight(1f), "m")
                    NumField("Ancho", vm.anchoPlato, { vm.anchoPlato = it }, Modifier.weight(1f), "m")
                }
                Spacer(Modifier.height(8.dp))
                NumField("Espesor", vm.espesorPlato, { vm.espesorPlato = it }, Modifier.fillMaxWidth(), "m")
            }
        }

        Spacer(Modifier.height(12.dp))

        /* -------- Vigas / Columnas -------- */
        SectionCard(title = "Vigas y columnas") {
            ToggleRow(
                title = "Incluir vigas/columnas",
                subtitle = "Volumen total de hormigón",
                checked = vm.vigasActivo,
                onCheckedChange = { vm.vigasActivo = it }
            )
            if (vm.vigasActivo) {
                Spacer(Modifier.height(10.dp))
                NumField("Volumen total (m³)", vm.volumenVigas, { vm.volumenVigas = it }, Modifier.fillMaxWidth(), "m³")
            }
        }

        Spacer(Modifier.height(20.dp))

        /* -------- Calcular -------- */
        Button(
            onClick = {
                if (vm.calcular()) onCalculado()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green)
        ) {
            Icon(Icons.Filled.Calculate, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Calcular materiales", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF062C12))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF062C12))
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun MuroCard(
    index: Int,
    muro: Muro,
    onDelete: () -> Unit,
    onUpdate: (Muro) -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = SurfaceAlt)
    ) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Tramo ${index + 1}",
                    color = TextMuted,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Close, contentDescription = "Eliminar tramo", tint = Color(0xFFEF4444))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Largo", muro.largo, { onUpdate(muro.copy(largo = it)) }, Modifier.weight(1f), "m")
                NumField("Alto", muro.alto, { onUpdate(muro.copy(alto = it)) }, Modifier.weight(1f), "m")
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Puertas", muro.puertas, { onUpdate(muro.copy(puertas = it)) }, Modifier.weight(1f))
                NumField("Ventanas", muro.ventanas, { onUpdate(muro.copy(ventanas = it)) }, Modifier.weight(1f))
            }
        }
    }
}