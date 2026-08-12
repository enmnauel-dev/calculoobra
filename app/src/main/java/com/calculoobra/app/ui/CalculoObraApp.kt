package com.calculoobra.app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculoobra.app.ui.theme.BlueDark
import com.calculoobra.app.ui.theme.TextMuted

private enum class Tab(val label: String, val icon: ImageVector) {
    Medidas("Medidas", Icons.Filled.Straighten),
    Materiales("Materiales", Icons.Filled.Construction),
    Precios("Precios", Icons.Filled.AttachMoney),
    Ajustes("Ajustes", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculoObraApp(vm: CalculoObraViewModel = viewModel()) {
    var tab by rememberSaveable { mutableStateOf(Tab.Medidas) }
    var confirmarReset by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CálculoObra", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Cubicación y presupuesto de materiales", fontSize = 11.sp, color = TextMuted)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlueDark,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { confirmarReset = true }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Nueva obra", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, contentDescription = null) },
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                Tab.Medidas -> MedidasScreen(vm, onCalculado = { tab = Tab.Materiales })
                Tab.Materiales -> ResultadoScreen(vm, onIrAMedidas = { tab = Tab.Medidas })
                Tab.Precios -> PreciosScreen(vm)
                Tab.Ajustes -> ConfiguracionScreen(vm)
            }
        }
    }

    if (confirmarReset) {
        AlertDialog(
            onDismissRequest = { confirmarReset = false },
            title = { Text("¿Nueva obra?") },
            text = { Text("Se borrarán las medidas y los precios actuales.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.reset()
                    tab = Tab.Medidas
                    confirmarReset = false
                    Toast.makeText(context, "Obra reiniciada", Toast.LENGTH_SHORT).show()
                }) { Text("Sí, borrar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarReset = false }) { Text("Cancelar") }
            }
        )
    }
}