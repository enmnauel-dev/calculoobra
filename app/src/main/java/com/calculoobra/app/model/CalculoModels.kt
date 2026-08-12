package com.calculoobra.app.model

/**
 * Datos de un tramo de pared capturado en campo.
 * Los valores se guardan como texto para editarlos libremente.
 */
data class Muro(
    var largo: String = "",
    var alto: String = "2.70",
    var puertas: String = "0",
    var ventanas: String = "0"
)

/** Precios locales por unidad de material (opcional). */
data class Precios(
    var moneda: String = "$",
    var block: String = "",
    var cemento: String = "",
    var arena: String = "",
    var gravilla: String = "",
    var varilla: String = "",
    var alambre: String = "",
    var manoObra: String = "",
    var transporte: String = "",
    var otros: String = ""
)

/**
 * Ratios de cálculo editables (valores estándar de la especificación).
 * Se guardan como texto para editarlos en la pantalla de Ajustes.
 */
data class Ratios(
    var muroBlocksPerM2: String = "12.5",
    var muroCementoPerM2: String = "0.25",
    var muroArenaPerM2: String = "0.025",
    var paneteCementoPerCaraM2: String = "0.15",
    var paneteArenaPerCaraM2: String = "0.015",
    var platoCementoPerM3: String = "8.0",
    var platoArenaPerM3: String = "0.55",
    var platoGravillaPerM3: String = "0.85",
    var platoVarillasPerM2: String = "0.95",
    var vigasCementoPerM3: String = "8.5",
    var vigasArenaPerM3: String = "0.50",
    var vigasGravillaPerM3: String = "0.80",
    var vigasVarillasPerM3: String = "18.0",
    var alambreLbsPorVarilla: String = "0.26",
    var wasteBlocks: String = "5",
    var wasteNormal: String = "7",
    var wasteVarillas: String = "10"
) {
    companion object {
        val Default = Ratios()
    }
}

/** Entrada completa del motor de cálculo. */
data class ObraInput(
    val muros: List<Muro>,
    val anchoPuerta: Double,
    val altoPuerta: Double,
    val anchoVentana: Double,
    val altoVentana: Double,
    val paneteActivo: Boolean,
    val paneteCaras: Int,
    val platoActivo: Boolean,
    val largoPlato: Double,
    val anchoPlato: Double,
    val espesorPlato: Double,
    val vigasActivo: Boolean,
    val volumenVigas: Double,
    val precios: Precios,
    val ratios: Ratios
)

data class MurosFase(val blocks: Int, val cemento: Int, val arena: Double)

data class PaneteFase(val cemento: Int, val arena: Double)

data class PlatoFase(
    val cemento: Int,
    val arena: Double,
    val gravilla: Double,
    val varillas: Int
)

data class VigasFase(
    val cemento: Int,
    val arena: Double,
    val gravilla: Double,
    val varillas: Int
)

data class ResumenObra(
    val areaBruta: Double,
    val descuento: Double,
    val areaNeta: Double,
    val areaPlato: Double,
    val volumenPlato: Double,
    val volumenVigas: Double
)

data class Materiales(
    val blocks: Int,
    val cemento: Int,
    val arena: Double,
    val gravilla: Double,
    val varillas: Int,
    val alambre: Int
)

data class Presupuesto(
    val block: Double,
    val cemento: Double,
    val arena: Double,
    val gravilla: Double,
    val varillas: Double,
    val alambre: Double,
    val manoObra: Double,
    val transporte: Double,
    val otros: Double,
    val total: Double
) {
    val tienePrecios: Boolean
        get() = listOf(
            block, cemento, arena, gravilla, varillas, alambre,
            manoObra, transporte, otros
        ).any { it > 0 }
}

data class ResultadoObra(
    val resumen: ResumenObra,
    val murosFase: MurosFase,
    val paneteFase: PaneteFase?,
    val platoFase: PlatoFase?,
    val vigasFase: VigasFase?,
    val materiales: Materiales,
    val presupuesto: Presupuesto
)