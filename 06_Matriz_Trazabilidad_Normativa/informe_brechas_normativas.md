# Informe de brechas y decisiones pendientes — UBL 2.1 / SUNAT

| Metadato | Valor |
|---|---|
| Estado | `DRAFT` — hallazgos de diseño, sin aprobación tributaria |
| Matriz | [matriz_trazabilidad_normativa.csv](./matriz_trazabilidad_normativa.csv) |
| Marco explicativo | [marco_normativo_ubl_sunat.md](./marco_normativo_ubl_sunat.md) |
| Registro de fuentes | [registro_fuentes_oficiales.md](./registro_fuentes_oficiales.md) |
| Contratos API | `API-SISTEMA-COMERCIAL` v0.1.0, continúa en `DRAFT` |
| Fecha de revisión documental | 2026-11-04 |

## 1. Conclusión

El proyecto tiene un alcance expreso para comprobantes electrónicos, integración SUNAT y UBL Invoice 2.1, pero todavía no define qué comprobantes emitirá ni por qué sistema. En consecuencia, es posible trazar restricciones genéricas del XSD OASIS y algunas reglas condicionales de resoluciones oficiales, pero **no es posible cerrar una matriz de cumplimiento SUNAT por documento**.

La brecha principal es de especificación: la API solo solicita una emisión con `ventaId` y `tipoDocumento`; no transporta los datos necesarios para construir o comprobar una factura UBL. La matriz marca esos elementos como pendientes, en vez de deducir campos fiscales de las entidades comerciales.

No se afirma que el sistema cumpla SUNAT por usar UBL 2.1 o validar un XML contra XSD. La resolución/aceptación externa y la respuesta local son resultados separados.

## 2. Hallazgos principales

| ID | Hallazgo | Evidencia | Impacto | Prioridad |
|---|---|---|---|---|
| B-01 | El tipo de comprobante no está acordado. | `RF-CPE-001` pospone tipos; `SolicitarEmisionComprobanteDto.tipoDocumento` es texto libre. | No se puede determinar una matriz completa de reglas, anexos, XML ni respuestas por tipo. | Bloqueante |
| B-02 | La modalidad SEE no está elegida. | `RF-SUN-001` deja abierta la modalidad; el contrato API es neutral. | Plazos, canales y respuestas difieren entre SEE del contribuyente, SEE-OSE, SEE-SOL u otro aplicable. | Bloqueante |
| B-03 | No existe texto consolidado verificado a la fecha del examen. | Se revisaron RS 117-2017 y RS 193-2020 originales/modificatoria; no se verificó toda la cadena normativa posterior. | Las reglas y plazos citados no deben considerarse una conclusión de vigencia actual completa. | Bloqueante |
| B-04 | No se inspeccionó el Anexo SUNAT vigente de requisitos mínimos y validaciones para Invoice. | RS 097-2012 original está escaneada; el Anexo 10 nacional y anexos modificados requieren obtenerse en forma legible/consolidada. | Faltan condiciones nacionales por campo, catálogos y restricciones más estrictas que OASIS. | Bloqueante |
| B-05 | Faltan datos fiscales y partes UBL en el modelo/DTO. | No hay entidad/configuración de emisor; `TerceroDto` no define identificación tributaria; no existe DTO de factura/línea UBL. | No se puede derivar un XML suficiente ni validar identidad de partes desde el contrato actual. | Bloqueante |
| B-06 | Venta y DTO no tienen valores comerciales/fiscales requeridos para importes y tributos. | `CrearVentaDto` contiene producto/cantidad; no incluye precio, moneda, descuentos, tributos o importes. | No hay base para armar o conciliar `TaxTotal`, `LegalMonetaryTotal` y valores de línea. | Bloqueante |
| B-07 | Pagos y crédito están fuera del modelo vigente. | RS 193-2020 art. 2.1 modifica una regla de SEE-SOL; DTOs no expresan forma de pago o cuotas. | La aplicabilidad de campos de pago depende del SEE y de las condiciones comerciales aún no aprobadas. | Alta |
| B-08 | Identificador fiscal y fecha de emisión no están definidos como datos tributarios. | UUID local no es el `cbc:ID`; `fechaEmision` en `ComprobanteDto` no define el XML ni evento de transmisión. | Riesgo de confundir identidad interna, numeración fiscal, fecha del documento y marcas de transmisión. | Alta |
| B-09 | Los estados externos no tienen catálogo ni detalle de evidencia. | `ResultadoIntegracionDto` tiene campos genéricos; la respuesta se modela como opcional. | No se puede probar de forma contractual CDR, inconsistencias, aceptación, rechazo o transmisión incierta. | Alta |
| B-10 | La idempotencia es una propuesta API, no una política SUNAT. | `Idempotency-Key` está en el borrador API; la identidad fiscal/reintentos externos siguen abiertos. | Un replay HTTP seguro no resuelve por sí solo un timeout durante el envío al servicio externo. | Alta |
| B-11 | Existían propuestas técnicas en documentos históricos/ADR que no eran requisitos aprobados. | ADR-0002/0003 se revisaron y pasaron a `PROPOSED`; el uso de PostgreSQL se separó como restricción expresa, y las decisiones detalladas siguen pendientes. La arquitectura vigente está en `DRAFT`; la matriz regulatoria anterior está marcada histórica/supersedida. | La revisión reduce el riesgo de confundir propuestas con requisitos; el responsable aún debe decidir modalidad SUNAT, consistencia de ventas/inventario, permisos, migración y políticas de persistencia. | Media |
| B-12 | No se ejecutó una validación de instancia UBL. | Se inspeccionaron declaraciones XSD; no se tiene XML de ejemplo ni paquete completo de esquemas/imports ni validador nacional. | Solo se documentan restricciones estáticas; no existe evidencia de validación efectiva de documentos. | Alta |

## 3. Decisiones que debe confirmar el responsable

1. **Tipos de documento y operaciones:** factura, boleta, notas u otros; emisión ordinaria, corrección, baja/anulación, entrega al adquirente y límites del examen.
2. **SEE y canal de integración:** SEE del contribuyente, SEE-OSE, SEE-SOL u otra alternativa. Identificar responsabilidades del emisor y del operador.
3. **Marco vigente:** obtener texto consolidado de cada resolución aplicable y anexos técnicos/catálogos vigentes. Confirmar expresamente los plazos, causas, estados y procedimientos de envío.
4. **Datos del emisor y adquirente:** fuente autorizada, tipos de identificación, domicilios y excepciones por tipo de documento.
5. **Datos comerciales:** código/identificación fiscal de línea, descripción, unidad, cantidad, precio, descuentos/cargos, moneda, pagos y condiciones de crédito cuando correspondan.
6. **Reglas de importes/impuestos:** bases, categorías, tasas, redondeo, percepción/retención/detracción u otros tratamientos si fueran pertinentes. No añadir ninguno sin un caso de negocio y fuente oficial vigente.
7. **Numeración y firma:** formato/serie/correlativo, asignación, certificado, firma y conservación de artefactos, de acuerdo con SEE y fuentes aplicables.
8. **Estados e idempotencia:** ciclo local, transmisión, respuesta, fallos inciertos, duplicidad y forma de reconciliar reintentos.
9. **Trazabilidad/retención:** XML emitido, respuesta externa, fechas, identificadores, usuario y política de consulta/retención.
10. **Gobierno documental — reconciliación realizada; autorización restante pendiente:** ADR-0002/0003 están en `PROPOSED`, la arquitectura vigente está en `DRAFT` y la matriz regulatoria anterior está marcada histórica/supersedida. Consultar el [informe de revisión de ADR](../specs/architecture/revision_ADR_0002_0003.md) para preguntas, alternativas y documentos afectados. La revisión no aprueba políticas de negocio ni sustituye la verificación normativa.

## 4. Brechas entre regla y contrato actual

### CPE/API

- `POST /api/v1/comprobantes` acepta solo `ventaId` y `tipoDocumento`; no define un perfil versionado de emisión o datos a congelar al emitir.
- `GET /api/v1/comprobantes/{comprobanteId}` no especifica XML, CDR, comunicación de inconsistencias, estados enumerados ni fechas de transmisión/respuesta.
- `VentaDto` y `DetalleVenta` no son datos fiscales suficientes. No debe hacerse una conversión implícita de un DTO comercial mínimo a un XML fiscal.
- No existe una ruta o flujo contractual para transmitir, consultar un estado remoto, corregir, anular o gestionar documentos distintos a Invoice.

### Dominio y arquitectura

- El modelo conceptual incluye `ComprobanteElectronico` y `Venta`, pero no el emisor/configuración fiscal, datos de identificación tributaria, perfil UBL ni agregado/documento XML.
- El diseño MVC/hexagonal sí es adecuado para mantener un adaptador externo aislado como opción de arquitectura. No confirma el protocolo, un OSE ni la propiedad de una firma o worker.
- La regla de confirmar venta y movimientos atómicamente de la revisión API es una propuesta arquitectónica. No es una obligación tributaria ni se debe mezclar con la validación del XML.

## 5. Alcance de validación alcanzado

- **Fuentes:** consultas directas a OASIS y descarga de resoluciones PDF alojadas en SUNAT. RS 117-2017 y RS 193-2020 fueron extraídas para lectura de los artículos citados. RS 097-2012 resultó escaneada/no legible en la extracción y RS 007-99 solo permitió identificar portada/índice en esta revisión.
- **XSD:** se inspeccionaron declaraciones y cardinalidades en `UBL-Invoice-2.1.xsd`. No se ejecutó un validador con instancia XML ni se resolvieron todos los esquemas importados como paquete.
- **Anexos/catálogos SUNAT:** no se validó una instancia contra el Anexo 10 o reglas nacionales actuales.
- **Entorno oficial de pruebas:** no se transmitieron comprobantes ni se usaron credenciales/servicios de prueba.
- **Documentos de trabajo:** los contratos API permanecen en `DRAFT`; la matriz y este informe también son `DRAFT`.

## 6. Recomendación y secuencia de cierre

**No aprobar todavía la matriz normativa ni diseñar el DTO tributario como definitivo.** La secuencia de cierre recomendada es:

1. confirmar tipos de comprobantes y SEE;
2. conseguir fuentes SUNAT legibles/consolidadas y verificar anexos/catálogos vigentes;
3. realizar revisión con responsable contable/tributario del caso;
4. acordar modelo fiscal y datos de ventas que faltan;
5. mapear cada regla oficial al campo DTO, ruta, elemento XML y criterio de prueba;
6. ejecutar validación XSD y perfil SUNAT con fixtures válidos/invalidos y, cuando proceda, entorno de certificación/pruebas;
7. revisar y aprobar los contratos sin confundir validación estructural con aceptación externa.

No se eliminó ni implementó ningún archivo de aplicación, no se instalaron dependencias y no se cambió el estado `DRAFT` de los contratos API.
