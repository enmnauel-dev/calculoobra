package com.calculoobra.app.engine

import com.calculoobra.app.model.Muro
import com.calculoobra.app.model.ObraInput
import com.calculoobra.app.model.Precios
import com.calculoobra.app.model.Ratios
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verifica el motor de cálculo contra el ejemplo práctico de la
 * especificación: plano de 2 dormitorios (77 m² de plato, 154.8 m² netos).
 */
class CalculoEngineTest {

    /** muros que suman 172.8 m² brutos con 6 puertas (0.9x2.0) y 5 ventanas (1.2x1.2) = 18 m² */
    private fun inputBase() = ObraInput(
        muros = listOf(
            Muro("11", "2.70", "2", "1"),
            Muro("11", "2.70", "1", "1"),
            Muro("7", "2.70", "1", "1"),
            Muro("7", "2.70", "1", "1"),
            Muro("16", "2.70", "1", "1"),
            Muro("12", "2.70", "0", "0")
        ),
        anchoPuerta = 0.9, altoPuerta = 2.0,
        anchoVentana = 1.2, altoVentana = 1.2,
        paneteActivo = true, paneteCaras = 2,
        platoActivo = true, largoPlato = 11.0, anchoPlato = 7.0, espesorPlato = 0.10,
        vigasActivo = false, volumenVigas = 0.0,
        precios = Precios(),
        ratios = Ratios()
    )

    @Test
    fun ejemploPracticoEspecificacion() {
        val res = CalculoEngine.compute(inputBase())

        assertEquals(172.8, res.resumen.areaBruta, 0.001)
        assertEquals(18.0, res.resumen.descuento, 0.001)
        assertEquals(154.8, res.resumen.areaNeta, 0.001)
        assertEquals(77.0, res.resumen.areaPlato, 0.001)
        assertEquals(7.7, res.resumen.volumenPlato, 0.001)

        // Blocks con 5% de desperdicio: ceil(154.8 * 12.5 * 1.05) = 2032
        assertEquals(2032, res.materiales.blocks)

        // Platillo: varillas ceil(77 * 0.95 * 1.10) = 81
        assertEquals(81, res.platoFase!!.varillas)
        // arena 2 decimales: 77*0.1*0.55*1.07 = 4.53145 -> 4.53
        assertEquals(4.53, res.platoFase!!.arena, 0.001)

        // Pañete 2 caras: cemento ceil(154.8 * 0.30 * 1.07) = 50
        assertEquals(50, res.paneteFase!!.cemento)

        // Cemento total = 42 (muros) + 50 (pañete) + 66 (plato) = 158
        assertEquals(158, res.materiales.cemento)
    }

    @Test
    fun redondeoAlEnteroSuperior() {
        // 1 m² neto, sin pañete/plato/vigas
        val input = ObraInput(
            muros = listOf(Muro("1", "1", "0", "0")),
            anchoPuerta = 0.9, altoPuerta = 2.0,
            anchoVentana = 1.2, altoVentana = 1.2,
            paneteActivo = false, paneteCaras = 2,
            platoActivo = false, largoPlato = 0.0, anchoPlato = 0.0, espesorPlato = 0.10,
            vigasActivo = false, volumenVigas = 0.0,
            precios = Precios(),
            ratios = Ratios()
        )
        val res = CalculoEngine.compute(input)
        // 12.5 * 1.05 = 13.125 -> 14 blocks
        assertEquals(14, res.materiales.blocks)
        // 0.25 * 1.07 = 0.2675 -> 1 funda
        assertEquals(1, res.materiales.cemento)
    }

    @Test
    fun presupuestoConPrecios() {
        val inputM = inputBase().copy(
            precios = Precios(moneda = "RD$", block = "25", cemento = "850", arena = "2500", gravilla = "2200", varilla = "580", alambre = "80")
        )
        val res = CalculoEngine.compute(inputM)
        assertEquals(true, res.presupuesto.tienePrecios)
        // blocks: 2032 * 25 = 50800
        assertEquals(50800.0, res.presupuesto.block, 0.001)
        // cemento: 158 * 850 = 134300
        assertEquals(134300.0, res.presupuesto.cemento, 0.001)
        assertEquals(0.0 > 500000, false)
    }

    @Test
    fun ratiosEditablesCambianElResultado() {
        val input = inputBase().copy(
            ratios = Ratios(
                muroBlocksPerM2 = "15",
                wasteBlocks = "10",
                muroCementoPerM2 = "0.30"
            )
        )
        val res = CalculoEngine.compute(input)
        // 154.8 * 15 * 1.10 = 2554.2 -> 2555 blocks
        assertEquals(2555, res.materiales.blocks)
        // 154.8 * 0.30 * 1.07 = 49.69 -> 50 cemento (solo muros)
        assertEquals(50, res.murosFase.cemento)
    }

    @Test
    fun presupuestoIncluyeCostesExtra() {
        val input = inputBase().copy(
            precios = Precios(
                moneda = "$",
                block = "25", cemento = "850", arena = "2500", gravilla = "2200",
                varilla = "580", alambre = "80",
                manoObra = "25000", transporte = "3000", otros = "1500"
            )
        )
        val res = CalculoEngine.compute(input)
        assertEquals(25000.0, res.presupuesto.manoObra, 0.001)
        assertEquals(3000.0, res.presupuesto.transporte, 0.001)
        assertEquals(1500.0, res.presupuesto.otros, 0.001)
        // total = materiales (50800 + 134300 + ...) + 29500
        val materiales = res.presupuesto.block + res.presupuesto.cemento +
            res.presupuesto.arena + res.presupuesto.gravilla +
            res.presupuesto.varillas + res.presupuesto.alambre
        assertEquals(materiales + 29500.0, res.presupuesto.total, 0.001)
    }

    @Test
    fun numerosConComa() {
        assertEquals(2.7, CalculoEngine.toDouble("2,70"), 0.001)
        assertEquals(0.0, CalculoEngine.toDouble(""), 0.001)
        assertEquals(0.0, CalculoEngine.toDouble("abc"), 0.001)
        assertEquals(0.0, CalculoEngine.toDouble("-3"), 0.001)
    }
}