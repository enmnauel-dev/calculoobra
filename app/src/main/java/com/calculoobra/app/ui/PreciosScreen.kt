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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calculoobra.app.engine.Fmt
import com.calculoobra.app.ui.theme.Green
import com.calculoobra.app.ui.theme.TextMuted

@Composable
fun PreciosScreen(vm: CalculoObraViewModel) {
    val p = vm.precios

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        /* -------- Resumen de presupuesto -------- */
        val res = vm.resultado
        if (res != null && res.presupuesto.tienePrecios) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Green)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Presupuesto total estimado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        "${p.moneda.ifBlank { "$" }} ${Fmt.numero(res.presupuesto.total)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        SectionCard(
            title = "Precios locales",
            subtitle = "Ingresa los precios de tu zona para calcular el presupuesto total. Se aplican las cantidades con desperdicio."
        ) {
            NumField("Moneda", p.moneda, { nuevo -> vm.updatePrecios { it.copy(moneda = nuevo.trim()) } }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Block (c/u)", p.block, { nuevo -> vm.updatePrecios { it.copy(block = nuevo) } }, Modifier.weight(1f))
                NumField("Cemento 42.5kg (funda)", p.cemento, { nuevo -> vm.updatePrecios { it.copy(cemento = nuevo) } }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Arena (m³)", p.arena, { nuevo -> vm.updatePrecios { it.copy(arena = nuevo) } }, Modifier.weight(1f))
                NumField("Gravilla (m³)", p.gravilla, { nuevo -> vm.updatePrecios { it.copy(gravilla = nuevo) } }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Varilla 3/8\" 20' (c/u)", p.varilla, { nuevo -> vm.updatePrecios { it.copy(varilla = nuevo) } }, Modifier.weight(1f))
                NumField("Alambre (libra)", p.alambre, { nuevo -> vm.updatePrecios { it.copy(alambre = nuevo) } }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
            Text("Otros costes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = com.calculoobra.app.ui.theme.TextMuted)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                NumField("Mano de obra", p.manoObra, { nuevo -> vm.updatePrecios { it.copy(manoObra = nuevo) } }, Modifier.weight(1f), "${p.moneda.ifBlank { "$" }} total")
                NumField("Transporte", p.transporte, { nuevo -> vm.updatePrecios { it.copy(transporte = nuevo) } }, Modifier.weight(1f), "${p.moneda.ifBlank { "$" }} total")
            }
            Spacer(Modifier.height(8.dp))
            NumField("Otros gastos", p.otros, { nuevo -> vm.updatePrecios { it.copy(otros = nuevo) } }, Modifier.fillMaxWidth(), "${p.moneda.ifBlank { "$" }} total")
        }

        Text(
            "Los precios se muestran cuando hayas calculado los materiales.",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(Modifier.height(28.dp))
    }
}