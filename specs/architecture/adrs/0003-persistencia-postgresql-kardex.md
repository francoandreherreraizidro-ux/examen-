# ADR-0003: PostgreSQL como restricción de persistencia y decisiones pendientes

---

## Metadatos

| Campo | Valor |
|---|---|
| Número | ADR-0003 |
| Estado | `PROPOSED` — propuesta de persistencia detallada pendiente; PostgreSQL es restricción expresa |
| Decisores | Responsable del proyecto/examen, pendiente de confirmación |
| Fecha del borrador original | 2026-10-22 |
| Fecha de revisión | 2026-11-04 |
| Requisitos relacionados | `RNF-TEC-001`, `RF-INV-002`, `RF-VEN-001`, `RF-MIG-001`, `RF-VAL-001`, `RF-TRZ-001` |
| Documentos relacionados | [Análisis de requisitos](../../../01_Analisis_Requisitos/analisis_requisitos.md), [SPEC-002](../../features/002-sistema-comercial-facturacion.md), [arquitectura](../../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md), [matriz normativa](../../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md) |
| Reemplaza | No aplica |

## 1. Contexto y problema

El requisito `RNF-TEC-001` establece PostgreSQL como la base de datos del sistema diseñado. `RF-INV-002` exige conservar un historial de ingresos, salidas y otros movimientos de inventario. `RF-TRZ-001` exige conservar trazabilidad de los datos/operaciones que se acuerden como relevantes. El análisis no fija versión de PostgreSQL, esquema, patrón de persistencia, método de valoración, concurrencia ni límites transaccionales.

El borrador previo añadía POS de alta concurrencia, pedidos mayoristas, stock no negativo, costo promedio ponderado, precisión monetaria concreta, Outbox, JSONB para cargas tributarias, artefactos XML/CDR y una referencia normativa como fundamento. Esos supuestos exceden el alcance vigente y no se consideran decisiones aprobadas.

## 2. Qué decisión está justificada

| Elemento | Clasificación | Resultado |
|---|---|---|
| Usar PostgreSQL | Restricción expresa (`RNF-TEC-001`) | Forma parte del diseño requerido; este ADR no amplía ni altera el requisito. |
| Elegir PostgreSQL frente a otro motor | No es una decisión abierta del examen | La alternativa ya está fijada por el requisito. No se afirma una comparación ni aprobación adicional. |
| PostgreSQL 15 o superior | No especificado | Pendiente; no debe inferirse del ADR anterior. |
| Modelo relacional, JSONB, BYTEA, índices, particiones | Decisiones de diseño | Pendientes de contratos, volúmenes, operación y artefactos que efectivamente entren en alcance. |
| Modelo de movimientos históricos y conservación | Capacidad expresa; política técnica pendiente | Requiere definir qué se conserva, cómo se corrige y qué significa trazabilidad. |
| Costo promedio, saldos no negativos y atomicidad venta-inventario | Reglas de negocio no confirmadas | No incluir como invariantes sin autorización. |

## 3. Problema a resolver

Precisar cómo el almacenamiento PostgreSQL soportará el dominio y sus necesidades de integridad, historial, migración y trazabilidad una vez que dichas necesidades se acuerden. No existe información suficiente para definir ahora tablas, tipos numéricos, índices, bloqueos, transacciones ni formato de eventos.

## 4. Alternativas de diseño a evaluar

Estas son opciones de investigación y no decisiones:

1. **Persistencia relacional normalizada y registros de movimientos de negocio:** evaluar para datos estructurados e integridad referencial.
2. **Uso selectivo de campos semiestructurados:** evaluar sólo cuando contratos versionados o formatos externos justifiquen variabilidad; no asumir JSONB como requisito.
3. **Almacenamiento separado de artefactos documentales:** evaluar si el alcance final exige conservar XML, respuestas o archivos, con política de integridad, privacidad y retención.
4. **Control de concurrencia optimista o pesimista:** elegir según reglas de stock y pruebas de contención; no presuponer `SELECT ... FOR UPDATE`.
5. **Una transacción para venta e inventario, etapas compensables u otro límite:** comparar una vez aprobados ciclo de venta, stock, reversiones y consistencia requerida.

Como el motor PostgreSQL ya está fijado, comparar PostgreSQL con MongoDB o MySQL no resuelve una decisión vigente. Si el examen exige comparar motores, documentar la comparación académica como tal, sin presentarla como alternativa operativa aprobada.

## 5. Consecuencias de la restricción PostgreSQL

### Dominio

- Los movimientos de inventario requieren historia consultable (`RF-INV-002`), pero no se deriva de ello inmutabilidad absoluta, valoración o política de existencias.
- El modelo de venta y su relación con inventario deben permanecer neutrales hasta confirmar cuándo se registra cada efecto y cómo se corrigen.
- La migración debe preservar procedencia/relaciones según el alcance que se apruebe, no mediante columnas o tablas inventadas en este ADR.

### Arquitectura

- La persistencia debe quedar detrás del límite de infraestructura en la propuesta hexagonal.
- El diseño de esquemas y acceso a datos se elaborará después de aprobar modelo de dominio y contratos, conforme a `RNF-SDD-001`.
- PostgreSQL no implica por sí mismo uso de Outbox, JSONB, bloqueo de filas, procedimientos almacenados ni un esquema de despliegue.

### Contratos API/DTO

- DTOs y contratos no deben exponer tipos de columna o entidades persistentes.
- Los contratos actuales son preliminares y no definen importes fiscales completos; no deben deducirse escalas numéricas ni campos tributarios desde este ADR.
- La estrategia de idempotencia de API y repetición remota debe tratarse en contratos/decisiones separadas, no como consecuencia automática de PostgreSQL.

## 6. Contradicciones corregidas respecto de documentos anteriores

- Se eliminan del alcance de este ADR el costo promedio, la prohibición de saldo negativo, la referencia tributaria como justificación de diseño y la precisión numérica fija.
- Se retiran las afirmaciones de atomicidad obligatoria venta-inventario-Outbox, alta concurrencia y necesidad de `SKIP LOCKED`.
- Se quitan los estados de acción completada que afirmaban DDL ya definido; `03_DTOs_Schemas` es documentación de contratos y no autoriza implementar tablas.
- `ACCEPTED` se cambia a `PROPOSED` para las decisiones de persistencia no establecidas. El uso de PostgreSQL permanece confirmado exclusivamente por `RNF-TEC-001`.

## 7. Preguntas pendientes

1. ¿Qué entidades y periodos cubre el historial de movimientos y qué correcciones se permiten?
2. ¿Se requiere saldo por producto/almacén, reservas, lotes o vencimientos? ¿Cuál es la política ante insuficiencia?
3. ¿La venta afecta existencias al registrarse, confirmarse, despacharse o en otra etapa?
4. ¿Qué nivel de atomicidad o consistencia se requiere y cómo se manejan fallos/reversiones?
5. ¿Qué volúmenes, concurrencia, retención, disponibilidad y recuperación se esperan?
6. ¿Se conservarán XML, respuestas SUNAT u otros artefactos, y bajo qué reglas de acceso/retención?
7. ¿Cuál es el sistema legado y qué estrategia de mapeo, deduplicación, conciliación y carga se necesita?
8. ¿Qué versión y configuración operativa de PostgreSQL exige el entorno del examen?

## 8. Acciones

- [x] Reconciliar este ADR con las restricciones y capacidades vigentes.
- [ ] Confirmar las reglas de negocio y requisitos de calidad que condicionan persistencia.
- [ ] Elaborar la propuesta de esquema/consistencia sólo después de aprobar dominio, DTOs y API.
- [ ] Cambiar estado a `ACCEPTED` únicamente tras autorización expresa de las decisiones detalladas.
