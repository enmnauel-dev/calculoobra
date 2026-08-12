package com.calculoobra.app.engine

import com.calculoobra.app.model.Muro
import com.calculoobra.app.model.ObraInput
import com.calculoobra.app.model.ResultadoObra
import com.calculoobra.app.model.ResumenObra
import com.calculoobra.app.model.Materiales
import com.calculoobra.app.model.MurosFase
import com.calculoobra.app.model.PaneteFase
import com.calculoobra.app.model.PlatoFase
import com.calculoobra.app.model.VigasFase
import com.calculoobra.app.model.Presupuesto
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Motor de cubicación de materiales de construcción.
 *
 * Mismas fórmulas que la versión web (especificación técnica). Los ratios
 * y % de desperdicio se leen de [ObraInput.ratios], por lo que el usuario
 * puede ajustarlos en la pantalla de Ajustes.
 *
 * Valores estándar: block 15x20x40 = 12.5 uds/m², mortero 0.25 fundas/m² y
 * 0.025 m³ arena/m², pañete por cara 0.15 fundas/m² y 0.015 m³/m², losa 10 cm
 * 8.0 fundas/m³, 0.55 arena/m³, 0.85 gravilla/m³, 0.95 varillas/m²; vigas 8.5
 * fundas/m³, 0.50 arena/m³, 0.80 gravilla/m³, 18 varillas/m³; alambre 0.26
 * lbs/varilla. Desperdicio: 5% blocks, 7% cemento/arena/gravilla, 10% varillas.
 * Redondeo: fundas y varillas al entero superior; m³ a 2 decimales.
 */
object CalculoEngine {

    /** Convierte texto a número no negativo (acepta coma como separador decimal). */
    fun toDouble(v: String): Double = v.trim().replace(",", ".").toDoubleOrNull()?.takeIf { it > 0 } ?: 0.0

    private fun round2(v: Double): Double = (v * 100).roundToInt() / 100.0

    private fun areaBruta(muros: List<Muro>): Double =
        muros.sumOf { toDouble(it.largo) * toDouble(it.alto) }

    private fun descontarAberturas(
        muros: List<Muro>,
        ap: Double, hp: Double, av: Double, hv: Double
    ): Double {
        val puertas = muros.sumOf { it.puertas.trim().toIntOrNull() ?: 0 }
        val ventanas = muros.sumOf { it.ventanas.trim().toIntOrNull() ?: 0 }
        return puertas * ap * hp + ventanas * av * hv
    }

    private fun areaNeta(muros: List<Muro>, descuento: Double): Double {
        val bruta = areaBruta(muros)
        return if (bruta - descuento > 0) bruta - descuento else 0.0
    }

    fun compute(input: ObraInput): ResultadoObra {
        val r = input.ratios
        val blocksPerM2 = toDouble(r.muroBlocksPerM2)
        val muroCementoPerM2 = toDouble(r.muroCementoPerM2)
        val muroArenaPerM2 = toDouble(r.muroArenaPerM2)
        val paneteCementoM2 = toDouble(r.paneteCementoPerCaraM2)
        val paneteArenaM2 = toDouble(r.paneteArenaPerCaraM2)
        val platoCementoM3 = toDouble(r.platoCementoPerM3)
        val platoArenaM3 = toDouble(r.platoArenaPerM3)
        val platoGravillaM3 = toDouble(r.platoGravillaPerM3)
        val platoVarillasM2 = toDouble(r.platoVarillasPerM2)
        val vigasCementoM3 = toDouble(r.vigasCementoPerM3)
        val vigasArenaM3 = toDouble(r.vigasArenaPerM3)
        val vigasGravillaM3 = toDouble(r.vigasGravillaPerM3)
        val vigasVarillasM3 = toDouble(r.vigasVarillasPerM3)
        val alambrePorVarilla = toDouble(r.alambreLbsPorVarilla)
        val wasteBlocks = toDouble(r.wasteBlocks) / 100.0
        val wasteNormal = toDouble(r.wasteNormal) / 100.0
        val wasteRebar = toDouble(r.wasteVarillas) / 100.0

        val bruta = areaBruta(input.muros)
        val descuento = descontarAberturas(
            input.muros,
            input.anchoPuerta, input.altoPuerta,
            input.anchoVentana, input.altoVentana
        )
        val neta = areaNeta(input.muros, descuento)

        val areaPlato = if (input.platoActivo) input.largoPlato * input.anchoPlato else 0.0
        val volumenPlato = areaPlato * input.espesorPlato
        val volumenVigas = if (input.vigasActivo) input.volumenVigas else 0.0

        /* Fase muros y pegado */
        val muros = MurosFase(
            blocks = ceil(neta * blocksPerM2 * (1 + wasteBlocks)).toInt(),
            cemento = ceil(neta * muroCementoPerM2 * (1 + wasteNormal)).toInt(),
            arena = round2(neta * muroArenaPerM2 * (1 + wasteNormal))
        )

        /* Fase pañete (por caras) */
        val panete = if (input.paneteActivo) {
            PaneteFase(
                cemento = ceil(neta * paneteCementoM2 * input.paneteCaras * (1 + wasteNormal)).toInt(),
                arena = round2(neta * paneteArenaM2 * input.paneteCaras * (1 + wasteNormal))
            )
        } else null

        /* Fase plato / losa */
        val plato = if (input.platoActivo) {
            PlatoFase(
                cemento = ceil(volumenPlato * platoCementoM3 * (1 + wasteNormal)).toInt(),
                arena = round2(volumenPlato * platoArenaM3 * (1 + wasteNormal)),
                gravilla = round2(volumenPlato * platoGravillaM3 * (1 + wasteNormal)),
                varillas = ceil(areaPlato * platoVarillasM2 * (1 + wasteRebar)).toInt()
            )
        } else null

        /* Fase vigas y columnas */
        val vigas = if (input.vigasActivo) {
            VigasFase(
                cemento = ceil(volumenVigas * vigasCementoM3 * (1 + wasteNormal)).toInt(),
                arena = round2(volumenVigas * vigasArenaM3 * (1 + wasteNormal)),
                gravilla = round2(volumenVigas * vigasGravillaM3 * (1 + wasteNormal)),
                varillas = ceil(volumenVigas * vigasVarillasM3 * (1 + wasteRebar)).toInt()
            )
        } else null

        val varillasTotal = (plato?.varillas ?: 0) + (vigas?.varillas ?: 0)

        val materiales = Materiales(
            blocks = muros.blocks,
            cemento = muros.cemento +
                (panete?.cemento ?: 0) +
                (plato?.cemento ?: 0) +
                (vigas?.cemento ?: 0),
            arena = round2(
                muros.arena +
                    (panete?.arena ?: 0.0) +
                    (plato?.arena ?: 0.0) +
                    (vigas?.arena ?: 0.0)
            ),
            gravilla = round2((plato?.gravilla ?: 0.0) + (vigas?.gravilla ?: 0.0)),
            varillas = varillasTotal,
            alambre = ceil(varillasTotal * alambrePorVarilla * (1 + wasteRebar)).toInt()
        )

        /* Presupuesto */
        val p = input.precios
        val presupuesto = Presupuesto(
            block = round2(materiales.blocks * toDouble(p.block)),
            cemento = round2(materiales.cemento * toDouble(p.cemento)),
            arena = round2(materiales.arena * toDouble(p.arena)),
            gravilla = round2(materiales.gravilla * toDouble(p.gravilla)),
            varillas = round2(materiales.varillas * toDouble(p.varilla)),
            alambre = round2(materiales.alambre * toDouble(p.alambre)),
            manoObra = round2(toDouble(p.manoObra)),
            transporte = round2(toDouble(p.transporte)),
            otros = round2(toDouble(p.otros)),
            total = 0.0
        ).let {
            it.copy(
                total = round2(
                    (it.block + it.cemento + it.arena + it.gravilla + it.varillas + it.alambre +
                        it.manoObra + it.transporte + it.otros)
                )
            )
        }

        return ResultadoObra(
            resumen = ResumenObra(
                areaBruta = round2(bruta),
                descuento = round2(descuento),
                areaNeta = round2(neta),
                areaPlato = round2(areaPlato),
                volumenPlato = round2(volumenPlato),
                volumenVigas = round2(volumenVigas)
            ),
            murosFase = muros,
            paneteFase = panete,
            platoFase = plato,
            vigasFase = vigas,
            materiales = materiales,
            presupuesto = presupuesto
        )
    }
}

/** Formato numérico en estilo español (p. ej. 1.234,56 / 12.500 uds). */
object Fmt {
    private val locale = Locale("es")

    fun numero(v: Double, dec: Int = 2): String =
        String.format(locale, "%,.${dec}f", v)

    fun entero(v: Int): String =
        String.format(locale, "%,d", v)
}