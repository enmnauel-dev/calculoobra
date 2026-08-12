package com.calculoobra.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.content.ContentValues
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.calculoobra.app.engine.Fmt
import com.calculoobra.app.model.Materiales
import com.calculoobra.app.model.ResultadoObra
import com.calculoobra.app.ui.theme.Green
import com.calculoobra.app.ui.theme.Surface
import com.calculoobra.app.ui.theme.SurfaceAlt
import java.io.File

@Composable
fun ResultadoScreen(
    vm: CalculoObraViewModel,
    onIrAMedidas: () -> Unit
) {
    val res = vm.resultado
    val context = LocalContext.current

    if (res == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Icon(
                    Icons.Filled.Construction,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text("Aún no hay resultados", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Captura las medidas de la obra y pulsa Calcular.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onIrAMedidas) { Text("Ir a Medidas") }
            }
        }
        return
    }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        ResumenCard(res)

        Spacer(Modifier.height(18.dp))
        Text("Materiales totales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        MaterialGrid(res.materiales)

        Spacer(Modifier.height(18.dp))
        Text("Desglose por fase", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        PhaseCard(
            title = "Muros y pegado",
            rows = listOf(
                "Blocks (15×20×40)" to "${Fmt.entero(res.murosFase.blocks)} uds",
                "Cemento" to "${Fmt.entero(res.murosFase.cemento)} fundas",
                "Arena" to "${Fmt.numero(res.murosFase.arena)} m³"
            )
        )

        res.paneteFase?.let { fase ->
            Spacer(Modifier.height(8.dp))
            PhaseCard(
                title = "Pañete / Repello (${vm.paneteCaras} cara${if (vm.paneteCaras > 1) "s" else ""})",
                rows = listOf(
                    "Cemento" to "${Fmt.entero(fase.cemento)} fundas",
                    "Arena" to "${Fmt.numero(fase.arena)} m³"
                )
            )
        }

        res.platoFase?.let { fase ->
            Spacer(Modifier.height(8.dp))
            PhaseCard(
                title = "Plato / Losa (${Fmt.numero(vm.espesorPlato.toDoubleOrNull() ?: 0.10, 2)} m)",
                rows = listOf(
                    "Cemento" to "${Fmt.entero(fase.cemento)} fundas",
                    "Arena" to "${Fmt.numero(fase.arena)} m³",
                    "Gravilla" to "${Fmt.numero(fase.gravilla)} m³",
                    "Varillas 3/8\"" to "${Fmt.entero(fase.varillas)} uds"
                )
            )
        }

        res.vigasFase?.let { fase ->
            Spacer(Modifier.height(8.dp))
            PhaseCard(
                title = "Vigas y columnas",
                rows = listOf(
                    "Cemento" to "${Fmt.entero(fase.cemento)} fundas",
                    "Arena" to "${Fmt.numero(fase.arena)} m³",
                    "Gravilla" to "${Fmt.numero(fase.gravilla)} m³",
                    "Varillas 3/8\"" to "${Fmt.entero(fase.varillas)} uds"
                )
            )
        }

        if (res.presupuesto.tienePrecios) {
            Spacer(Modifier.height(18.dp))
            Text("Presupuesto total estimado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            val moneda = vm.precios.moneda.ifBlank { "$" }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Green)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "$moneda ${Fmt.numero(res.presupuesto.total)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    val p = res.presupuesto
                    Text(
                        "Blocks: $moneda ${Fmt.numero(p.block)} · Cemento: $moneda ${Fmt.numero(p.cemento)} · Arena: $moneda ${Fmt.numero(p.arena)} · Gravilla: $moneda ${Fmt.numero(p.gravilla)} · Varillas: $moneda ${Fmt.numero(p.varillas)} · Alambre: $moneda ${Fmt.numero(p.alambre)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    val extras = mutableListOf<String>()
                    if (p.manoObra > 0) extras += "Mano de obra: $moneda ${Fmt.numero(p.manoObra)}"
                    if (p.transporte > 0) extras += "Transporte: $moneda ${Fmt.numero(p.transporte)}"
                    if (p.otros > 0) extras += "Otros: $moneda ${Fmt.numero(p.otros)}"
                    if (extras.isNotEmpty()) {
                        Text(
                            extras.joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Exportar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { compartirWhatsApp(context, res, vm.precios.moneda.ifBlank { "$" }) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
        ) {
            Icon(Icons.Filled.Send, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Compartir por WhatsApp", color = Color(0xFF062C12), fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = { guardarReporte(context, res, vm) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Filled.FileDownload, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Guardar reporte en Descargas")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                copiarTexto(context, buildResumenTexto(res, vm.precios.moneda.ifBlank { "$" }))
                toast(context, "Resumen copiado al portapapeles")
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Filled.CopyAll, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text("Copiar resumen")
        }

        Spacer(Modifier.height(28.dp))
    }
}

/* ================== Subcomponentes ================== */

@Composable
private fun ResumenCard(res: ResultadoObra) {
    val r = res.resumen
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceAlt
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Resumen de obra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Straighten, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("  Área neta paredes: ${Fmt.numero(r.areaNeta)} m²", Modifier.weight(1f))
            }
            Spacer(Modifier.height(4.dp))
            Text("  Área bruta: ${Fmt.numero(r.areaBruta)} m² · Aberturas: −${Fmt.numero(r.descuento)} m²", Modifier.padding(start = 16.dp))
            if (r.areaPlato > 0) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Layers, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("  Área plato: ${Fmt.numero(r.areaPlato)} m² (${Fmt.numero(r.volumenPlato)} m³)", Modifier.weight(1f))
                }
            }
            if (r.volumenVigas > 0) {
                Spacer(Modifier.height(6.dp))
                Text("  Volumen vigas/columnas: ${Fmt.numero(r.volumenVigas)} m³", Modifier.padding(start = 12.dp))
            }
        }
    }
}

@Composable
private fun MaterialGrid(m: Materiales) {
    val tiles = listOf(
        MaterialesItem("Blocks 15×20×40", "${Fmt.entero(m.blocks)} uds", Icons.Filled.GridView),
        MaterialesItem("Cemento 42.5 kg", "${Fmt.entero(m.cemento)} fundas", Icons.Filled.Layers),
        MaterialesItem("Arena", "${Fmt.numero(m.arena)} m³", Icons.Filled.Landscape),
        MaterialesItem("Gravilla", "${Fmt.numero(m.gravilla)} m³", Icons.Filled.Terrain),
        MaterialesItem("Varilla 3/8\" (20')", "${Fmt.entero(m.varillas)} uds", Icons.Filled.LinearScale),
        MaterialesItem("Alambre #18", "${Fmt.entero(m.alambre)} lbs", Icons.Filled.Link)
    )
    val rows = tiles.chunked(2)
    rows.forEach { pair ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            pair.forEach { item ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Icon(item.icon, null, Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(6.dp))
                        Text(item.valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Green)
                        Text(item.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

private data class MaterialesItem(val label: String, val valor: String, val icon: ImageVector)

@Composable
private fun PhaseCard(title: String, rows: List<Pair<String, String>>) {
    var expanded by remember { mutableStateOf(false) }
Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(if (expanded) "▲" else "▼", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                rows.forEach { (k, v) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Text(k, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        Text(v, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/* ================== Acciones de exportación ================== */

private fun buildResumenTexto(res: ResultadoObra, moneda: String): String {
    val m = res.materiales
    val r = res.resumen
    return buildString {
        append("CálculoObra — Lista de materiales\n\n")
        append("· Área neta paredes: ${Fmt.numero(r.areaNeta)} m²\n")
        if (r.areaPlato > 0) append("· Área plato: ${Fmt.numero(r.areaPlato)} m² (${Fmt.numero(r.volumenPlato)} m³)\n")
        if (r.volumenVigas > 0) append("· Vigas/columnas: ${Fmt.numero(r.volumenVigas)} m³\n")
        append("\nMateriales:\n")
        append("· Blocks: ${Fmt.entero(m.blocks)} uds\n")
        append("· Cemento 42.5kg: ${Fmt.entero(m.cemento)} fundas\n")
        append("· Arena: ${Fmt.numero(m.arena)} m³\n")
        append("· Gravilla: ${Fmt.numero(m.gravilla)} m³\n")
        append("· Varilla 3/8\" 20 pies: ${Fmt.entero(m.varillas)} uds\n")
        append("· Alambre #18: ${Fmt.entero(m.alambre)} lbs\n")
        if (res.presupuesto.tienePrecios) {
            append("\nPresupuesto total estimado: $moneda ${Fmt.numero(res.presupuesto.total)}\n")
            append("· Blocks: $moneda ${Fmt.numero(res.presupuesto.block)}\n")
            append("· Cemento: $moneda ${Fmt.numero(res.presupuesto.cemento)}\n")
            append("· Arena: $moneda ${Fmt.numero(res.presupuesto.arena)}\n")
            append("· Gravilla: $moneda ${Fmt.numero(res.presupuesto.gravilla)}\n")
            append("· Varillas: $moneda ${Fmt.numero(res.presupuesto.varillas)}\n")
            append("· Alambre: $moneda ${Fmt.numero(res.presupuesto.alambre)}\n")
            if (res.presupuesto.manoObra > 0) append("· Mano de obra: $moneda ${Fmt.numero(res.presupuesto.manoObra)}\n")
            if (res.presupuesto.transporte > 0) append("· Transporte: $moneda ${Fmt.numero(res.presupuesto.transporte)}\n")
            if (res.presupuesto.otros > 0) append("· Otros gastos: $moneda ${Fmt.numero(res.presupuesto.otros)}\n")
        }
        append("\n— Generado con CálculoObra (offline)")
    }
}

private fun buildJson(res: ResultadoObra): String = buildString {
    append("{\n")
    append("  \"resumen_obra\": {\n")
    append("    \"area_paredes_bruta_m2\": ${res.resumen.areaBruta},\n")
    append("    \"descuento_aberturas_m2\": ${res.resumen.descuento},\n")
    append("    \"area_paredes_neta_m2\": ${res.resumen.areaNeta},\n")
    append("    \"area_plato_m2\": ${res.resumen.areaPlato},\n")
    append("    \"volumen_plato_m3\": ${res.resumen.volumenPlato}\n")
    append("  },\n")
    append("  \"materiales_totales\": {\n")
    append("    \"blocks_unidades\": ${res.materiales.blocks},\n")
    append("    \"cemento_fundas_42_5kg\": ${res.materiales.cemento},\n")
    append("    \"arena_m3\": ${res.materiales.arena},\n")
    append("    \"gravilla_m3\": ${res.materiales.gravilla},\n")
    append("    \"varillas_3_8_piezas_20pie\": ${res.materiales.varillas},\n")
    append("    \"alambre_amarre_libras\": ${res.materiales.alambre}\n")
    append("  }\n")
    append("}")
}

private fun compartirWhatsApp(context: Context, res: ResultadoObra, moneda: String) {
    val texto = buildResumenTexto(res, moneda)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
        setPackage("com.whatsapp")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        context.startActivity(
            Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texto)
            }, "Compartir CálculoObra")
        )
    }
}

private fun guardarReporte(context: Context, res: ResultadoObra, vm: CalculoObraViewModel) {
    val fileName = "calculoobra_${System.currentTimeMillis()}.txt"
    val contenido = buildResumenTexto(res, vm.precios.moneda.ifBlank { "$" }) +
        "\n\n--- JSON ---\n" + buildJson(res)
    try {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { it.write(contenido.toByteArray()) }
            }
            toast(context, "Reporte guardado: $fileName")
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            File(dir, fileName).writeText(contenido)
            toast(context, "Reporte guardado: $fileName")
        }
    } catch (e: Exception) {
        toast(context, "Error al guardar: ${e.message}")
    }
}

private fun copiarTexto(context: Context, texto: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("CálculoObra", texto))
}

private fun toast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}