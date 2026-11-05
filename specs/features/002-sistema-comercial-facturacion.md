# SPEC-002: Sistema comercial, inventario y comprobantes electrónicos

---

## Metadata

| Campo | Valor |
|---|---|
| ID | `SPEC-002` |
| Estado | `DRAFT` — no aprobado para implementación |
| Alcance fuente | [Análisis de requisitos vigente](../../01_Analisis_Requisitos/analisis_requisitos.md) |
| Modelo de dominio | [Modelo conceptual](../../02_Modelo_Dominio/modelo_dominio.md) |
| Contratos de datos | [DTOs y Schemas](../../03_DTOs_Schemas/dtos_schemas.md) |
| Contratos API | [Especificación API](../../05_Contratos_API/especificacion_contratos_api.md), [OpenAPI](../api/sistema-comercial.openapi.yaml) |
| Arquitectura propuesta | [MVC y arquitectura hexagonal](../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md) |
| Trazabilidad normativa | [Marco UBL/SUNAT](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md), [matriz](../../06_Matriz_Trazabilidad_Normativa/matriz_trazabilidad_normativa.csv), [brechas](../../06_Matriz_Trazabilidad_Normativa/informe_brechas_normativas.md) |

## 1. Fuente de verdad y estado de esta especificación

Esta versión reconcilia el borrador anterior con los requisitos vigentes. El [análisis de requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md) es la fuente normativa del alcance del examen; conserva los códigos `RF-*` y `RNF-*` tal como están allí. Esta especificación no crea códigos nuevos ni convierte inferencias o propuestas en requisitos aprobados.

Las versiones anteriores de este borrador describían como obligatorios POS, mayorista/crédito, cuotas, categorías de impuestos, códigos/catálogos SUNAT, lotes, reglas de stock, una modalidad SOAP/OSE, operaciones específicas, tiempos de rendimiento, esquemas de errores y patrones de persistencia. Esos detalles no están confirmados por el análisis vigente. Se retiran de los criterios obligatorios de esta versión; podrán estudiarse como propuestas separadas, con fuentes y aprobación explícitas.

Todos los contratos y decisiones relacionados siguen en `DRAFT`. Esta especificación no autoriza implementación, no certifica cumplimiento de SUNAT y no afirma que validar un XML contra XSD baste para cumplir obligaciones tributarias.

### Convenciones de certeza

- **Expreso:** requisito que figura explícitamente en el análisis vigente.
- **Inferido:** relación razonable, pero pendiente de confirmación por el responsable.
- **Propuesto:** alternativa de diseño no aprobada.
- **Pendiente:** falta información para precisar la regla, campo, proceso o criterio de aceptación.

## 2. Problema y objetivo

La empresa comercializa abarrotes y mercadería al por mayor y menor. El examen solicita diseñar un sistema que gestione productos, almacén e historial de movimientos de inventario; clientes, proveedores y ventas; emisión de comprobantes electrónicos e integración con SUNAT; y migración de datos históricos, validación y trazabilidad.

El objetivo de esta especificación es mantener trazables esas capacidades mientras se elaboran los documentos de dominio, DTOs/Schemas, arquitectura MVC con principios hexagonales, contratos API y matriz normativa. Se debe considerar PostgreSQL y UBL Invoice 2.1, y documentar las decisiones no establecidas antes de tratarlas como definitivas.

## 3. Alcance funcional trazado

| Código vigente | Requisito expresado en el análisis | Certeza | Límite o aclaración |
|---|---|---|---|
| `RF-PRO-001` | Gestionar información de productos comercializados. | Expreso | Atributos, identificadores y operaciones concretas pendientes. |
| `RF-INV-001` | Gestionar información necesaria de almacenes. | Expreso | Cantidad, estructura y reglas de almacén pendientes. |
| `RF-INV-002` | Registrar históricamente ingresos, salidas y otros movimientos de inventario. | Expreso | Tipos, valoración, saldo negativo, reversos y momento de contabilización pendientes. |
| `RF-COM-001` | Gestionar clientes y proveedores. | Expreso | Datos, identificación, reglas de duplicidad y estados pendientes. |
| `RF-VEN-001` | Registrar y consultar ventas. | Expreso | Canales, etapas, pagos, precios, devoluciones y relación temporal con inventario pendientes. |
| `RF-VEN-002` | Relacionar una venta con los datos necesarios para emitir su comprobante cuando corresponda. | Inferido | No establece obligatoriedad, cardinalidad ni emisión inmediata. |
| `RF-CPE-001` | Contemplar emisión electrónica conforme al alcance empresarial por definir y considerar UBL Invoice 2.1. | Expreso | Tipos documentales y perfil SUNAT vigente por confirmar. |
| `RF-SUN-001` | Integrarse con servicios SUNAT en el flujo de comprobantes. | Expreso | SEE, canal, protocolo, autenticación, ambientes y estados pendientes. |
| `RF-MIG-001` | Contemplar migración histórica desde el sistema anterior. | Expreso | Fuentes, periodo, formato, alcance y conciliación pendientes. |
| `RF-VAL-001` | Validar los datos tratados por los procesos incluidos. | Expreso | Reglas semánticas y reporte de errores pendientes. |
| `RF-TRZ-001` | Conservar trazabilidad de los datos y operaciones que se acuerden como relevantes. | Expreso | Eventos, retención, permisos y granularidad pendientes. |

## 4. Restricciones no funcionales y arquitectónicas

| Código vigente | Restricción | Certeza | Estado |
|---|---|---|---|
| `RNF-TEC-001` | PostgreSQL será la base de datos del sistema diseñado. | Expreso | Versión, topología y operación pendientes. |
| `RNF-ARQ-001` | Arquitectura modular basada en MVC y principios hexagonales. | Expreso | Límites modulares y articulación se documentan como propuesta arquitectónica. |
| `RNF-SDD-001` | Definir contratos de datos y APIs antes de implementar código. | Expreso | Los contratos actuales siguen `DRAFT`. |
| `RNF-DOC-001` | Considerar UBL Invoice 2.1 en el diseño de comprobantes. | Expreso | Perfil y reglas SUNAT no se presumen; verificar fuentes vigentes. |
| `RNF-SEG-001` | Definir seguridad, privacidad y control de acceso. | Propuesto en análisis | No hay mecanismo, roles ni políticas aprobados. |
| `RNF-REL-001` | Definir disponibilidad y comportamiento ante fallos. | Propuesto en análisis | No hay SLA, patrón de colas ni operación offline aprobados. |
| `RNF-PER-001` | Definir rendimiento y capacidad. | Propuesto en análisis | No hay métricas, volúmenes o umbrales aprobados. |
| `RNF-MIG-001` | Definir integridad, conciliación y recuperación para migración. | Propuesto en análisis | Se precisa con las fuentes y el proceso legado. |

## 5. Actores

El análisis confirma como sistemas externos los servicios de SUNAT (`ACT-01`) y la fuente/sistema anterior (`ACT-02`). Los actores humanos son candidatos propuestos (`ACT-P01` a `ACT-P06`); no se fijan roles ni permisos como requisitos de esta especificación.

## 6. Reglas y comportamientos aún no establecidos

No hay reglas transaccionales específicas de venta, stock, tributación o seguridad aprobadas por el análisis actual. Deben permanecer pendientes:

- si se permite stock negativo, reservas, venta sin conexión o salidas parciales;
- política de confirmación de venta y relación con movimientos de inventario;
- método de valorización, ajuste, transferencia, devolución y corrección de historial;
- campos comerciales/fiscales, precios, descuentos, monedas, tributos y redondeo;
- tipos de comprobantes, numeración, anulaciones/notas y momento de emisión;
- modalidad SUNAT, plazos aplicables, credenciales, transporte, respuestas y reintentos;
- identidad de emisor/adquirente, catálogos y perfil UBL nacional vigente;
- fuentes legadas, entidades/periodos migrados, transformación, deduplicación y aceptación;
- autenticación, perfiles, autorizaciones, retención y auditoría por usuario.

Las reglas de una fuente normativa sólo se incorporan después de validar la fuente oficial vigente, su ámbito documental y su aplicabilidad al SEE elegido. Las restricciones del XSD OASIS se distinguen de las obligaciones SUNAT y de las decisiones internas.

## 7. Escenarios de aceptación preliminares

Los siguientes escenarios son criterios de cobertura de alto nivel derivados del análisis. No fijan políticas todavía abiertas ni sustituyen los contratos detallados. Los criterios de negocio precisos deben completarse antes de elevar la especificación a `REVIEW`.

### AC-01 — Gestión de productos y almacenes

- **Dado** un usuario autorizado una vez definidos los actores y permisos,
- **cuando** gestiona información de productos o almacenes dentro del alcance acordado,
- **entonces** el sistema registra y permite consultar la información conforme a los DTOs aprobados y conserva las validaciones acordadas.
- Relacionado: `RF-PRO-001`, `RF-INV-001`, `RF-VAL-001`.

### AC-02 — Historial de movimientos

- **Dado** un movimiento de inventario aceptado conforme a reglas por definir,
- **cuando** se registra y luego se consulta,
- **entonces** la información histórica requerida puede recuperarse con la trazabilidad acordada.
- Relacionado: `RF-INV-002`, `RF-VAL-001`, `RF-TRZ-001`.
- **Pendiente:** qué campos, estados, reglas de corrección y permisos definen la aceptación.

### AC-03 — Terceros y ventas

- **Dado** información de clientes/proveedores y datos de una venta,
- **cuando** se administran terceros o se registra/consulta una venta conforme al flujo aprobado,
- **entonces** se conservan las relaciones e información de negocio que hayan sido acordadas.
- Relacionado: `RF-COM-001`, `RF-VEN-001`, `RF-VAL-001`, `RF-TRZ-001`.
- **Pendiente:** campos requeridos, etapas de venta, cliente opcional/obligatorio y efectos sobre inventario.

### AC-04 — Comprobante electrónico

- **Dado** que se hayan aprobado tipo documental, datos y perfil SUNAT aplicable,
- **cuando** se solicite la emisión de un comprobante,
- **entonces** el diseño considera UBL Invoice 2.1 y conserva la diferencia entre emisión local, transmisión y resultado externo.
- Relacionado: `RF-CPE-001`, `RF-VEN-002` (inferido), `RF-SUN-001`, `RNF-DOC-001`, `RF-TRZ-001`.
- **Pendiente:** no se define aceptación por SUNAT ni plazo, firma, canal o estado sin la decisión y las fuentes correspondientes.

### AC-05 — Integración SUNAT

- **Dado** un comprobante y una modalidad de emisión seleccionados,
- **cuando** se integre con el servicio aplicable,
- **entonces** el sistema registra los resultados y conserva su trazabilidad de acuerdo con el contrato externo que se apruebe.
- Relacionado: `RF-SUN-001`, `RF-CPE-001`, `RF-TRZ-001`.
- **Pendiente:** operación, autenticación, respuesta, fallos y reintento.

### AC-06 — Migración

- **Dado** que se conozcan las fuentes y el alcance histórico autorizado,
- **cuando** se ejecute el proceso de migración definido,
- **entonces** se validan los registros y se conserva la procedencia y conciliación acordadas.
- Relacionado: `RF-MIG-001`, `RF-VAL-001`, `RF-TRZ-001`, `RNF-MIG-001`.
- **Pendiente:** muestra de origen, mapeos, granularidad, umbrales y aceptación.

## 8. Mapeo a entregables

| Necesidad/entregable | Documento actual | Estado |
|---|---|---|
| Análisis de requisitos y trazabilidad inicial | [01_Analisis_Requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md) | DRAFT |
| Modelo del dominio y diagramas | [02_Modelo_Dominio](../../02_Modelo_Dominio/modelo_dominio.md) | Propuesta para revisión |
| DTOs y Schemas | [03_DTOs_Schemas](../../03_DTOs_Schemas/dtos_schemas.md) | DRAFT |
| MVC con visión hexagonal | [04_Arquitectura_MVC_Hexagonal](../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md) | DRAFT tras reconciliación |
| Contratos API | [05_Contratos_API](../../05_Contratos_API/especificacion_contratos_api.md) | DRAFT; 25 operaciones en 15 rutas |
| Trazabilidad normativa | [06_Matriz_Trazabilidad_Normativa](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md) | DRAFT; aplicabilidad parcial y condicional |

La carpeta `06_Matriz_Trazabilidad_Regulatoria` contiene un borrador histórico no vigente. Usar únicamente la matriz de `06_Matriz_Trazabilidad_Normativa` y su registro de fuentes.

## 9. Riesgos y brechas para revisión

1. Los tipos de comprobante y la modalidad SEE no se han decidido; por tanto, no hay mapeo tributario completo.
2. Los contratos DTO/API aún no incluyen datos fiscales/UBL suficientes para producir una factura; no deben interpretarse como una solicitud lista para implementar.
3. Las resoluciones consultadas y el XSD base no sustituyen una verificación consolidada de anexos, catálogos y reglas vigentes.
4. La arquitectura detallada anterior mezclaba opciones (Outbox, SOAP, XMLDSig, costo promedio, roles, POS/crédito y tiempos) con requisitos. La versión reconciliada los identifica como alternativas pendientes, no como decisiones aprobadas.
5. Se necesita resolver las preguntas abiertas del análisis y revisar esta especificación con el responsable académico antes de `REVIEW` o `APPROVED`.

## 10. Criterio de avance

Antes de aprobar esta especificación se requiere:

1. revisar y confirmar el alcance de cada RF/RNF y resolver dudas que afecten criterios de aceptación;
2. acordar qué decisiones de arquitectura se elevan a ADR y conservarlas separadas de obligaciones legales;
3. cerrar DTOs/API y matriz normativa aplicable al tipo documental y SEE seleccionados;
4. verificar fuentes oficiales vigentes y completar las pruebas con reglas y datos de referencia aprobados;
5. obtener autorización expresa para cambiar el estado.

Hasta entonces, `SPEC-002`, los contratos API y los documentos relacionados permanecen en `DRAFT`. No se autoriza implementación ni avance a tareas de código.
