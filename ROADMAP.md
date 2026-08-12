# CálculoObra — Hoja de ruta

Cubicación y presupuesto de materiales de construcción, 100% offline.
Versión actual: PWA (`../app contructor`) + app Android nativa (`./`).

## Principio rector

El valor es la utilidad práctica inmediata: resolver en segundos un cálculo
que a mano toma tiempo y causa errores costosos en obra. Todo lo demás
(offline, ratios editables, exportación rápida) apuntala esa base.

## Fase 1 — Validación local (actual)

- App gratuita **completa**, sin límites por tramos (evita frustrar al
  maestro de obra y la piratería trivial de un desbloqueo offline).
- Validar con maestros, ingenieros y ferreterías locales de RD qué están
  dispuestos a pagar y cómo usan la app a diario.
- Medir: retención de uso, exportaciones por WhatsApp/PDF, retroalimentación
  sobre ratios por defecto.

## Fase 2 — Monetización (el que paga es el contratista, no el maestro)

- **Pro = PDF con logo y encabezado personalizado** del ingeniero/contratista
  para entregar presupuestos formales al cliente (lead generation).
  Este es el producto de pago natural.
- Complementos Pro: múltiples proyectos guardados, exportaciones sin marca.
- Requiere integrar **Play Billing** (retirado en el MVP) + revisión en
  Play Console.
- La pestaña «Precios» sigue sumando mano de obra/transporte/otros para que
  el presupuesto sea completo y formal.

## Fase 3 — B2B con ferreterías / depósitos

- Corto plazo: la "integración" real es la **lista de compra por WhatsApp**
  y el PDF (ya implementado).
- Largo plazo (producto/backend/sales aparte): API para cotizar y enviar
  órdenes a proveedores cercanos. No construir antes de tener un depósito
  que valide la demanda.

## Fase 4 — Internacionalización (solo tras tracción en RD)

- Internamente las medidas siempre en m / m² / m³; la unidad escogida solo
  cambia la presentación (evitar conversiones hardcodeadas).
- Multimoneda ya soportada (campo «Moneda»).
- Glosa regional por país como Strings (ej. block/bloque, fundas/sacos,
  pañete/repello/revoque, plato/losa/placa).

## A futuro (nice-to-have)

- Asistente de captura con OCR on-device (ML Kit, offline, gratis): leer
  medidas anotadas en el plano y pre-rellenar los muros con confirmación.
- Visión generativa (VLM) para "leer el plano" completa: requiere internet,
  backend y privacidad; solo como modo Pro opcional y con confirmación
  obligatoria del usuario antes de cubicar.

## Notas técnicas

- Ratios y % de desperdicio son editables por el usuario (pestaña Ajustes).
- Firma: keystore `keystore/calculoobra-upload.jks` (hacer respaldo externo).
- Motor validado contra el ejemplo: plano 2 dormitorios → 2032 blocks, 6/6 tests.