package com.calculoobra.app.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.calculoobra.app.engine.CalculoEngine
import com.calculoobra.app.model.Muro
import com.calculoobra.app.model.ResultadoObra
import com.calculoobra.app.model.ObraInput
import com.calculoobra.app.model.Precios
import com.calculoobra.app.model.Ratios

class CalculoObraViewModel(application: Application) : AndroidViewModel(application) {

    val muros = mutableStateListOf<Muro>()

    var anchoPuerta by mutableStateOf("0.90")
    var altoPuerta by mutableStateOf("2.00")
    var anchoVentana by mutableStateOf("1.20")
    var altoVentana by mutableStateOf("1.20")

    var paneteActivo by mutableStateOf(true)
    var paneteCaras by mutableStateOf(2)

    var platoActivo by mutableStateOf(false)
    var largoPlato by mutableStateOf("")
    var anchoPlato by mutableStateOf("")
    var espesorPlato by mutableStateOf("0.10")

    var vigasActivo by mutableStateOf(false)
    var volumenVigas by mutableStateOf("")

    var precios by mutableStateOf(Precios())
    var ratios by mutableStateOf(Ratios())
    var resultado by mutableStateOf<ResultadoObra?>(null)

    private fun prefs(): SharedPreferences =
        getApplication<Application>().getSharedPreferences("calculoobra", Context.MODE_PRIVATE)

    init {
        cargar()
        if (muros.isEmpty()) muros.add(Muro())
    }

    /* ---------------- Gestión de muros ---------------- */

    fun addMuro() {
        muros.add(Muro())
        guardar()
    }

    fun removeMuro(index: Int) {
        if (muros.size > 1) {
            muros.removeAt(index)
            guardar()
        }
    }

    fun updateMuro(index: Int, transform: (Muro) -> Muro) {
        if (index in muros.indices) {
            muros[index] = transform(muros[index])
            guardar()
        }
    }

    /* ---------------- Input completo ---------------- */

    fun buildInput(): ObraInput = ObraInput(
        muros = muros.toList(),
        anchoPuerta = dbl(anchoPuerta),
        altoPuerta = dbl(altoPuerta),
        anchoVentana = dbl(anchoVentana),
        altoVentana = dbl(altoVentana),
        paneteActivo = paneteActivo,
        paneteCaras = paneteCaras,
        platoActivo = platoActivo,
        largoPlato = dbl(largoPlato),
        anchoPlato = dbl(anchoPlato),
        espesorPlato = dbl(espesorPlato).takeIf { it > 0 } ?: 0.10,
        vigasActivo = vigasActivo,
        volumenVigas = dbl(volumenVigas),
        precios = precios,
        ratios = ratios
    )

    fun calcular(): Boolean {
        if (muros.none { dbl(it.largo) > 0 && dbl(it.alto) > 0 }) return false
        resultado = CalculoEngine.compute(buildInput())
        guardar()
        return true
    }

    fun recalcular() {
        if (resultado != null) {
            resultado = CalculoEngine.compute(buildInput())
        }
    }

    fun updatePrecios(transform: (Precios) -> Precios) {
        precios = transform(precios)
        guardar()
        recalcular()
    }

    fun updateRatio(transform: (Ratios) -> Ratios) {
        ratios = transform(ratios)
        guardar()
        recalcular()
    }

    fun resetRatios() {
        ratios = Ratios()
        guardar()
        recalcular()
    }

    fun reset() {
        muros.clear()
        muros.add(Muro())
        anchoPuerta = "0.90"; altoPuerta = "2.00"
        anchoVentana = "1.20"; altoVentana = "1.20"
        paneteActivo = true; paneteCaras = 2
        platoActivo = false; largoPlato = ""; anchoPlato = ""; espesorPlato = "0.10"
        vigasActivo = false; volumenVigas = ""
        precios = Precios()
        ratios = Ratios()
        resultado = null
        guardar()
    }

    private fun dbl(v: String): Double =
        v.trim().replace(",", ".").toDoubleOrNull()?.takeIf { it > 0 } ?: 0.0

    /* ---------------- Persistencia ---------------- */

    private fun guardar() {
        val sp = prefs().edit()
        sp.putString("muros", muros.joinToString(";") { m ->
            "${m.largo}|${m.alto}|${m.puertas}|${m.ventanas}"
        })
        sp.putString("anchoPuerta", anchoPuerta)
        sp.putString("altoPuerta", altoPuerta)
        sp.putString("anchoVentana", anchoVentana)
        sp.putString("altoVentana", altoVentana)
        sp.putBoolean("paneteActivo", paneteActivo)
        sp.putInt("paneteCaras", paneteCaras)
        sp.putBoolean("platoActivo", platoActivo)
        sp.putString("largoPlato", largoPlato)
        sp.putString("anchoPlato", anchoPlato)
        sp.putString("espesorPlato", espesorPlato)
        sp.putBoolean("vigasActivo", vigasActivo)
        sp.putString("volumenVigas", volumenVigas)
        sp.putString("precios", precios.run {
            "moneda=$moneda;block=$block;cemento=$cemento;arena=$arena;gravilla=$gravilla;" +
                "varilla=$varilla;alambre=$alambre;manoObra=$manoObra;transporte=$transporte;otros=$otros"
        })
        sp.putString("ratios", ratios.run {
            "muroBlocks=$muroBlocksPerM2;muroCemento=$muroCementoPerM2;muroArena=$muroArenaPerM2;" +
                "paneteCemento=$paneteCementoPerCaraM2;paneteArena=$paneteArenaPerCaraM2;" +
                "platoCemento=$platoCementoPerM3;platoArena=$platoArenaPerM3;platoGravilla=$platoGravillaPerM3;" +
                "platoVarillas=$platoVarillasPerM2;vigasCemento=$vigasCementoPerM3;vigasArena=$vigasArenaPerM3;" +
                "vigasGravilla=$vigasGravillaPerM3;vigasVarillas=$vigasVarillasPerM3;alambre=$alambreLbsPorVarilla;" +
                "wasteBlocks=$wasteBlocks;wasteNormal=$wasteNormal;wasteVarillas=$wasteVarillas"
        })
        sp.apply()
    }

    private fun cargar() {
        val sp = prefs()
        sp.getString("muros", null)?.let { raw ->
            if (raw.isNotBlank()) {
                muros.clear()
                raw.split(";").forEach { item ->
                    val p = item.split("|")
                    if (p.size >= 4) {
                        muros.add(Muro(p[0], p[1], p[2], p[3]))
                    }
                }
            }
        }
        anchoPuerta = sp.getString("anchoPuerta", "0.90") ?: "0.90"
        altoPuerta = sp.getString("altoPuerta", "2.00") ?: "2.00"
        anchoVentana = sp.getString("anchoVentana", "1.20") ?: "1.20"
        altoVentana = sp.getString("altoVentana", "1.20") ?: "1.20"
        paneteActivo = sp.getBoolean("paneteActivo", true)
        paneteCaras = sp.getInt("paneteCaras", 2)
        platoActivo = sp.getBoolean("platoActivo", false)
        largoPlato = sp.getString("largoPlato", "") ?: ""
        anchoPlato = sp.getString("anchoPlato", "") ?: ""
        espesorPlato = sp.getString("espesorPlato", "0.10") ?: "0.10"
        vigasActivo = sp.getBoolean("vigasActivo", false)
        volumenVigas = sp.getString("volumenVigas", "") ?: ""
        sp.getString("precios", null)?.let { raw ->
            val map = raw.split(";").mapNotNull {
                val kv = it.split("=", limit = 2)
                if (kv.size == 2) kv[0] to kv[1] else null
            }.toMap()
            precios = Precios(
                moneda = map["moneda"] ?: "$",
                block = map["block"] ?: "",
                cemento = map["cemento"] ?: "",
                arena = map["arena"] ?: "",
                gravilla = map["gravilla"] ?: "",
                varilla = map["varilla"] ?: "",
                alambre = map["alambre"] ?: "",
                manoObra = map["manoObra"] ?: "",
                transporte = map["transporte"] ?: "",
                otros = map["otros"] ?: ""
            )
        }
        sp.getString("ratios", null)?.let { raw ->
            val map = raw.split(";").mapNotNull {
                val kv = it.split("=", limit = 2)
                if (kv.size == 2) kv[0] to kv[1] else null
            }.toMap()
            ratios = Ratios(
                muroBlocksPerM2 = map["muroBlocks"] ?: Ratios().muroBlocksPerM2,
                muroCementoPerM2 = map["muroCemento"] ?: Ratios().muroCementoPerM2,
                muroArenaPerM2 = map["muroArena"] ?: Ratios().muroArenaPerM2,
                paneteCementoPerCaraM2 = map["paneteCemento"] ?: Ratios().paneteCementoPerCaraM2,
                paneteArenaPerCaraM2 = map["paneteArena"] ?: Ratios().paneteArenaPerCaraM2,
                platoCementoPerM3 = map["platoCemento"] ?: Ratios().platoCementoPerM3,
                platoArenaPerM3 = map["platoArena"] ?: Ratios().platoArenaPerM3,
                platoGravillaPerM3 = map["platoGravilla"] ?: Ratios().platoGravillaPerM3,
                platoVarillasPerM2 = map["platoVarillas"] ?: Ratios().platoVarillasPerM2,
                vigasCementoPerM3 = map["vigasCemento"] ?: Ratios().vigasCementoPerM3,
                vigasArenaPerM3 = map["vigasArena"] ?: Ratios().vigasArenaPerM3,
                vigasGravillaPerM3 = map["vigasGravilla"] ?: Ratios().vigasGravillaPerM3,
                vigasVarillasPerM3 = map["vigasVarillas"] ?: Ratios().vigasVarillasPerM3,
                alambreLbsPorVarilla = map["alambre"] ?: Ratios().alambreLbsPorVarilla,
                wasteBlocks = map["wasteBlocks"] ?: Ratios().wasteBlocks,
                wasteNormal = map["wasteNormal"] ?: Ratios().wasteNormal,
                wasteVarillas = map["wasteVarillas"] ?: Ratios().wasteVarillas
            )
        }
    }
}