# Tareas: generador Invoice UBL 2.1

**Especificación:** [SPEC-003](../features/003-generador-invoice-ubl21.md)
**Estado:** `IMPLEMENTED`
**Aprobación:** confirmación explícita del responsable en conversación, 2026-11-04.

## Checklist

- [x] Aprobar alcance UBL 2.1 genérico, contrato mínimo Invoice y toolchain Java/Maven/JAXP/StAX/JUnit 5.
- [x] Verificar la secuencia raíz de Invoice y los componentes usados contra los XSD oficiales OASIS.
- [x] Obtener el paquete oficial UBL 2.1.
- [x] Incorporar la estructura completa de `xsd/` para validación offline.
- [x] Crear la estructura Maven en el repositorio existente, manteniendo `src/` y `tests/`.
- [x] Escribir primero pruebas unitarias que analicen nodos, namespaces, atributos, valores, líneas, tributos, escape y entradas inválidas.
- [x] Agregar prueba estructural complementaria contra el XSD oficial completo.
- [x] Implementar modelo inmutable Invoice y tipos asociados sin dependencias XML.
- [x] Implementar el adaptador `InvoiceXmlGenerator` con StAX y orden UBL.
- [x] Crear un XML de ejemplo con datos sintéticos.
- [x] Ejecutar todas las pruebas con Java/Maven y registrar resultados reales.
- [x] Crear guía técnica con comandos, decisiones y límites SUNAT.
- [x] Ejecutar `git diff --check` y revisar el diff completo.

## Fuera de alcance

- Cambios a requisitos originales, SPEC-002, DTO, OpenAPI u otros ADR.
- Integración, firma o envío a SUNAT/OSE.
- Identificadores fiscales, categorías/códigos de impuesto, tasas o `TaxSubtotal` sin fuentes y aprobación específicas.
- Cálculos, redondeos, validación tributaria o reglas comerciales.
