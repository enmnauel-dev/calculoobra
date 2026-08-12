package com.calculoobra.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calculoobra.app.ui.theme.TextMuted

/**
 * Pantalla de Ajustes: permite editar los ratios de cálculo y los % de
 * desperdicio. Los valores estándar corresponden a la especificación técnica.
 */
@Composable
fun ConfiguracionScreen(vm: CalculoObraViewModel) {
    val r = vm.ratios

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        SectionCard(
            title = "Ratios de cálculo",
            subtitle = "Ajusta los rendimientos a tu máster o a los materiales de tu zona."
        ) {
            Text("Muros y pegado", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Blocks", r.muroBlocksPerM2, { n -> vm.updateRatio { it.copy(muroBlocksPerM2 = n) } }, Modifier.weight(1f), "uds/m²")
                NumField("Cemento", r.muroCementoPerM2, { n -> vm.updateRatio { it.copy(muroCementoPerM2 = n) } }, Modifier.weight(1f), "fundas/m²")
            }
            Spacer(Modifier.height(8.dp))
            NumField("Arena", r.muroArenaPerM2, { n -> vm.updateRatio { it.copy(muroArenaPerM2 = n) } }, Modifier.fillMaxWidth(), "m³/m²")

            Spacer(Modifier.height(14.dp))
            Text("Pañete / Repello (por cara)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Cemento", r.paneteCementoPerCaraM2, { n -> vm.updateRatio { it.copy(paneteCementoPerCaraM2 = n) } }, Modifier.weight(1f), "fundas/m²")
                NumField("Arena", r.paneteArenaPerCaraM2, { n -> vm.updateRatio { it.copy(paneteArenaPerCaraM2 = n) } }, Modifier.weight(1f), "m³/m²")
            }

            Spacer(Modifier.height(14.dp))
            Text("Plato / Losa", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Cemento", r.platoCementoPerM3, { n -> vm.updateRatio { it.copy(platoCementoPerM3 = n) } }, Modifier.weight(1f), "fundas/m³")
                NumField("Arena", r.platoArenaPerM3, { n -> vm.updateRatio { it.copy(platoArenaPerM3 = n) } }, Modifier.weight(1f), "m³/m³")
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Gravilla", r.platoGravillaPerM3, { n -> vm.updateRatio { it.copy(platoGravillaPerM3 = n) } }, Modifier.weight(1f), "m³/m³")
                NumField("Varillas", r.platoVarillasPerM2, { n -> vm.updateRatio { it.copy(platoVarillasPerM2 = n) } }, Modifier.weight(1f), "uds/m²")
            }

            Spacer(Modifier.height(14.dp))
            Text("Vigas y columnas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Cemento", r.vigasCementoPerM3, { n -> vm.updateRatio { it.copy(vigasCementoPerM3 = n) } }, Modifier.weight(1f), "fundas/m³")
                NumField("Arena", r.vigasArenaPerM3, { n -> vm.updateRatio { it.copy(vigasArenaPerM3 = n) } }, Modifier.weight(1f), "m³/m³")
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Gravilla", r.vigasGravillaPerM3, { n -> vm.updateRatio { it.copy(vigasGravillaPerM3 = n) } }, Modifier.weight(1f), "m³/m³")
                NumField("Varillas", r.vigasVarillasPerM3, { n -> vm.updateRatio { it.copy(vigasVarillasPerM3 = n) } }, Modifier.weight(1f), "uds/m³")
            }

            Spacer(Modifier.height(14.dp))
            Text("Alambre de amarre", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            NumField("Alambre #18", r.alambreLbsPorVarilla, { n -> vm.updateRatio { it.copy(alambreLbsPorVarilla = n) } }, Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(12.dp))

        SectionCard(
            title = "Desperdicio",
            subtitle = "Porcentaje extra añadido a las cantidades para cubrir roturas y mermas."
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Blocks", r.wasteBlocks, { n -> vm.updateRatio { it.copy(wasteBlocks = n) } }, Modifier.weight(1f), "%")
                NumField("Varillas", r.wasteVarillas, { n -> vm.updateRatio { it.copy(wasteVarillas = n) } }, Modifier.weight(1f), "%")
            }
            Spacer(Modifier.height(8.dp))
            NumField("Cemento / grava", r.wasteNormal, { n -> vm.updateRatio { it.copy(wasteNormal = n) } }, Modifier.fillMaxWidth(), "%")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { vm.resetRatios() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Restablecer valores estándar")
        }

        Spacer(Modifier.height(28.dp))
    }
}