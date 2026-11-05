# Revisión de ADR-0002 y ADR-0003

| Metadato | Valor |
|---|---|
| Estado | `DRAFT` — revisión documental, no aprobación de decisiones |
| Fecha | 2026-11-04 |
| Fuente de alcance | [Análisis de requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md) |
| Especificación relacionada | [SPEC-002](../features/002-sistema-comercial-facturacion.md), `DRAFT` |
| Arquitectura relacionada | [MVC y arquitectura hexagonal](../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md), `DRAFT` |
| Documentos normativos relacionados | [Informe de brechas](../../06_Matriz_Trazabilidad_Normativa/informe_brechas_normativas.md), [registro de fuentes](../../06_Matriz_Trazabilidad_Normativa/registro_fuentes_oficiales.md), [marco normativo](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md), [matriz histórica supersedida](../../06_Matriz_Trazabilidad_Regulatoria/matriz_trazabilidad_regulatoria.md) |

## 1. Conclusión ejecutiva

La fuente de verdad para alcance es el análisis vigente; este no fue modificado. Los ADR anteriores estaban en `ACCEPTED`, pero incluían supuestos y políticas que allí aparecen expresamente pendientes o no existen. En esta revisión:

- **ADR-0002** conserva como expresos el enfoque modular MVC/hexagonal (`RNF-ARQ-001`) y el orden Spec-First (`RNF-SDD-001`). La articulación concreta «MVC como adaptador primario exterior», sus módulos e interfaces son una **propuesta**, no una decisión aprobada.
- **ADR-0003** registra PostgreSQL como **restricción expresa** (`RNF-TEC-001`). La versión, el esquema, los tipos de datos, la concurrencia y las reglas de persistencia son **pendientes**. El historial de movimientos se exige (`RF-INV-002`), pero no determina costo promedio, stock no negativo ni atomicidad venta-inventario.
- Ambos ADR se actualizaron a `PROPOSED` para sus detalles de diseño, sin cambiar ni reinterpretar los requisitos originales.
- La modalidad SEE, los tipos de comprobantes, la consistencia venta-inventario, los permisos y la estrategia de migración continúan pendientes de autorización. No se asignan protocolos, reglas tributarias o roles.

Esta revisión no aprueba los ADR, contratos API, arquitectura ni la matriz normativa. La matriz regulatoria antigua permanece en el repositorio y está marcada como histórica/supersedida; no es una fuente válida de reglas.

## 2. Método y fuentes examinadas

Se contrastaron:

- análisis de requisitos vigente, incluido el estado de cada RF/RNF y las ambigüedades;
- SPEC-002 y la arquitectura reconciliada;
- ADR-0001, ADR-0002 y ADR-0003;
- informe de brechas, registro de fuentes y marco normativo;
- matriz regulatoria histórica y estado que declara.

ADR-0001 documenta el uso de ADR como mecanismo de registro. No respalda por sí mismo las decisiones de negocio o tecnología contenidas en ADR-0002/0003. En los documentos inspeccionados no se encontró evidencia adicional de una autorización del responsable que sostenga el estado `ACCEPTED` de las propuestas en conflicto.

## 3. Evaluación por ADR

### 3.1 ADR-0001 — registro de decisiones

| Aspecto | Evaluación |
|---|---|
| Problema que resuelve | Mantener rationale y trazabilidad documental de decisiones arquitectónicas. |
| Respaldo RF/RNF | No es una capacidad de negocio. Es una convención de gobierno documental y de Spec-First; se alinea con `RNF-SDD-001`, sin que ese requisito prescriba específicamente ADR. |
| Certeza | El formato ADR es una decisión de proceso documentada en el propio ADR-0001, no un requisito del enunciado. |
| Alternativas registradas | ADR Markdown versionado; wiki externa; notas informales. |
| Efectos | Permite registrar alcance, status, consecuencias y pendientes; no convierte una propuesta registrada en requisito aprobado. |
| Contradicciones | Ninguna con el alcance funcional. Su status `ACCEPTED` corresponde a la convención documental; no valida los estados de ADR-0002/0003. |
| Acción | Sin cambio solicitado. Aplicar su formato y registrar explícitamente aprobador/fecha cuando se apruebe una decisión. |

### 3.2 ADR-0002 — arquitectura MVC y hexagonal

| Aspecto | Evaluación |
|---|---|
| Problema declarado | Aislar el dominio de UI, transporte, persistencia e integración externa. |
| Respaldo | `RNF-ARQ-001` requiere arquitectura modular basada en MVC y considerar principios hexagonales; `RNF-SDD-001` requiere definir contratos antes de implementar. |
| Certeza | Los patrones generales son expresos. MVC como adaptador primario exterior y la división exacta de módulos/puertos son propuestas. |
| Alternativas | MVC de tres capas; hexagonal con MVC exterior; microservicios. La comparación de la versión anterior se apoyaba en flujos y actores no confirmados. La versión revisada conserva opciones sin presentar una evaluación de rendimiento o coste como evidencia medida. |
| Consecuencias para dominio | Mantener el dominio independiente de frameworks es una orientación de diseño propuesta; las entidades e invariantes concretas dependen del modelo y reglas confirmados. |
| Consecuencias para arquitectura | Sugiere puertos/adaptadores y fronteras MVC; no determina framework, HTTP, CLI, scheduler, Outbox ni topología. |
| Consecuencias para API | Si se autoriza, controladores traducirían DTOs a casos de uso y presentarían resultados; no implica que REST, autenticación, DTOs concretos o endpoints estén aprobados. |
| Contradicciones detectadas | El ADR anterior afirmó POS, ventas a crédito, validación tributaria concreta, SOAP, XMLDSig y workers. Estos aspectos contradicen los pendientes del análisis y la arquitectura reconciliada. También afirmaba cobertura de 100% de pruebas sin alcance o métrica aprobada. |
| Resultado | Actualizado a `PROPOSED`; mantiene la articulación MVC exterior/hexagonal como opción para revisión. |

### 3.3 ADR-0003 — PostgreSQL y persistencia de inventario

| Aspecto | Evaluación |
|---|---|
| Problema declarado | Persistencia de Kardex/ventas con concurrencia, integridad y artefactos documentales. |
| Respaldo | `RNF-TEC-001` exige PostgreSQL. `RF-INV-002` exige registrar históricamente movimientos; `RF-TRZ-001` exige trazabilidad de elementos por determinar. |
| Certeza | PostgreSQL como tecnología es expreso. «PostgreSQL 15+», estructura de Kardex, precisión numérica, Outbox, JSONB, BYTEA y modelo de concurrencia no se especifican. |
| Alternativas | El original comparaba MongoDB, MySQL/MariaDB y PostgreSQL. Como PostgreSQL ya está fijado por un requisito, no corresponde presentarlas como opciones operativas equivalentes. Las estrategias de esquema, datos semiestructurados, concurrencia y límites transaccionales sí requieren evaluación futura. |
| Consecuencias para dominio | No se deduce costo promedio, prohibición de saldo negativo, inmutabilidad contable ni cuándo una venta produce movimientos. |
| Consecuencias para arquitectura | Persistencia PostgreSQL detrás de infraestructura es compatible con la propuesta hexagonal. No exige Outbox, bloqueo `FOR UPDATE`, `SKIP LOCKED` o almacenamiento de archivos dentro de la base. |
| Consecuencias para API | No deben exponerse columnas/escalas del almacenamiento. Idempotencia HTTP y reintentos SUNAT requieren decisiones contractuales independientes. |
| Contradicciones detectadas | `RNF-TEC-001` deja la versión pendiente; el ADR fijaba 15+. `RF-INV-002` no prohíbe saldo negativo ni requiere costeo promedio. La API anterior expresaba atomicidad venta-inventario como propuesta y la arquitectura la dejaba pendiente, mientras que el ADR la hacía obligatoria. El análisis clasifica esas reglas como pendientes, no aprobadas. La referencia tributaria y las reglas de cuotas/Outbox no justifican el esquema actual. |
| Resultado | Actualizado a `PROPOSED`; PostgreSQL queda consignado como restricción expresa, y las decisiones específicas de persistencia se difieren. |

## 4. Decisiones transversales solicitadas

| Tema | Requisitos relacionados | Estado justificable | Alternativas de diseño para evaluar (no aprobadas) | Documentos afectados |
|---|---|---|---|---|
| Modalidad de emisión SEE | `RF-SUN-001`, `RF-CPE-001`, `RNF-DOC-001` | Integración y consideración de UBL 2.1 son expresas; modalidad no especificada. | SEE del contribuyente, SEE-OSE, SEE-SOL u otra modalidad aplicable, sujeta a alcance y verificación normativa vigente. | Análisis de requisitos (solo si se autoriza cambio), SPEC-002, ADR, arquitectura, DTO/API, matriz normativa, pruebas. |
| Tipos de comprobantes | `RF-CPE-001`; `RF-VEN-002` es inferido | Emisión de comprobantes está expresada; tipo, cantidad y operaciones (notas, bajas, etc.) pendientes. UBL Invoice 2.1 no decide por sí solo el alcance empresarial. | Reducir a Invoice según el alcance del examen; escoger factura/boleta u otros documentos después de confirmar negocio y SEE. | SPEC-002, modelo de dominio, DTO/Schemas, OpenAPI, fuentes/matriz normativa, criterios de aceptación. |
| Consistencia ventas-inventario | `RF-VEN-001`, `RF-INV-002`; relación/efecto precisa pendiente | Registrar ventas y movimientos históricos son expresos; atomicidad, etapa de afectación, disponibilidad, reservas y reversos no lo son. | Una transacción atómica; proceso por etapas con compensaciones; u otra política definida según ciclo de venta y operación. | Análisis (pendientes existentes), SPEC-002, modelo de dominio, ADR-0003, arquitectura, contratos API, pruebas. |
| Autenticación, roles y permisos | Actores propuestos; `RNF-SEG-001` figura como propuesta | No hay mecanismo ni perfiles humanos aprobados. Los actores `ACT-P02`–`ACT-P05` son candidatos, no roles de autorización. | Autenticación local o federada; RBAC u otra matriz de permisos; definir solo tras confirmar usuarios, datos y riesgos. | Análisis si se autoriza actualizar; SPEC-002, casos de uso, OpenAPI, arquitectura, seguridad y pruebas. |
| Migración histórica | `RF-MIG-001`, `RF-VAL-001`, `RF-TRZ-001` | Migrar datos históricos es expreso; fuente, contenido, alcance temporal, ejecución, conciliación y corte no están definidos. | Carga por lotes; importación asistida; migración inicial con validación y conciliación; decidir según acceso y calidad del legado. | Modelo de dominio, DTO/API/importación, arquitectura, ADR-0002, ADR-0003, pruebas y aceptación de migración. |

## 5. Registro de preguntas y decisiones para autorización

Las alternativas siguientes son propuestas de trabajo, no recomendaciones aprobadas. El responsable del examen/proyecto debe confirmar el alcance antes de modificar contratos o reglas de dominio.

| ID | Pregunta/decisión pendiente | Por qué hace falta | Propuestas a evaluar | Documentos afectados |
|---|---|---|---|---|
| P-01 | ¿Qué SEE/modalidad y responsabilidad de integración se incluyen? | Determina obligaciones aplicables, operaciones, respuestas, autenticación, contingencia y pruebas. | Evaluar modalidades legalmente aplicables con fuente consolidada y alcance operativo. | SPEC-002, ADR, arquitectura, API/DTO, matriz SUNAT. |
| P-02 | ¿Qué comprobantes y operaciones se deben diseñar? | Define tipo de datos, perfiles UBL, relaciones con ventas y pruebas por documento. | Mantener alcance mínimo de Invoice o añadir tipos solo con autorización. | SPEC-002, dominio, DTO/Schemas, API, matriz normativa. |
| P-03 | ¿En qué momento se afecta inventario por una venta y qué consistencia se exige? | Sin esto no se puede fijar atomicidad, reservas, reversos ni comportamiento de errores. | Transacción única, etapas/compensación o reglas distintas por operación; evaluar con reglas de negocio reales. | Análisis, SPEC-002, dominio, ADR-0003, arquitectura, API y pruebas. |
| P-04 | ¿Qué usuarios, autenticación y permisos existen? | Controla el acceso a operaciones, clientes, datos tributarios, migración y emisión. | Confirmar usuarios primero; luego comparar RBAC u otra política. | Análisis, SPEC-002, arquitectura, API, seguridad y pruebas. |
| P-05 | ¿Qué fuente/periodo/datos se migran y cómo se acepta la conciliación? | Determina adaptador, validaciones, identificadores origen, duplicados, recuperación y trazabilidad. | Lotes controlados, carga asistida o proceso por fases; solicitar muestra/inventario del legado. | Análisis, SPEC-002, dominio, DTO/API, arquitectura, ADR y plan de pruebas. |
| P-06 | ¿Qué historial y trazabilidad se deben conservar, y qué correcciones se permiten? | `RF-INV-002`/`RF-TRZ-001` expresan el resultado sin definir campos, inmutabilidad o retención. | Historial de movimientos con referencias; auditoría de cambios adicional solo si se requiere. | Dominio, ADR-0003, DTO/API, persistencia, retención y pruebas. |
| P-07 | ¿Se requiere una versión/configuración mínima de PostgreSQL o requisito de operación? | El análisis fija el motor, no versión, hosting, backup, disponibilidad ni recuperación. | Mantener versión neutral hasta que entorno/rúbrica lo determine; evaluar opciones documentadas. | ADR-0003, arquitectura, despliegue y RNF, sin modificar `RNF-TEC-001` sin autorización. |
| P-08 | ¿Se aprueba MVC como adaptador primario en el límite hexagonal? | `RNF-ARQ-001` exige ambos enfoques, pero deja abierta su articulación. | Aprobar la propuesta del ADR-0002, pedir alternativa diagramada o ajustar los límites MVC. | ADR-0002, arquitectura, API/DTO. |

## 6. Contradicciones documentales y correcciones

1. **Estado frente a evidencia:** ADR-0002/0003 declaraban `ACCEPTED`, aunque incluían detalles expresamente pendientes y no se encontró un registro de aprobación asociado. Se cambió a `PROPOSED` para esos detalles; no se modificaron RF/RNF.
2. **ADR-0002 frente al análisis:** POS, mayorista/crédito, reglas de inventario/impuestos, SOAP, XMLDSig, workers, CLI y PDF se trataban como alcance. Se retiraron como decisiones.
3. **ADR-0003 frente al análisis:** PostgreSQL 15+, stock no negativo, costo promedio, precisión fija, Outbox y atomicidad venta-inventario no se derivan de `RNF-TEC-001` ni de `RF-INV-002`. Se eliminaron como decisiones establecidas.
4. **ADR-0003 frente a entregables Spec-First:** sus acciones afirmaban que el DDL estaba documentado, aunque la fase prohíbe generar base de datos y el entregable de DTOs no debe confundirse con un esquema persistente. Se retiró esa afirmación.
5. **Matriz histórica:** se conserva sin borrar, pero está advertida como supersedida. Sus afirmaciones tributarias no son evidencia ni se usan para aprobar decisiones.
6. **ADR-0001:** su decisión sobre dónde registrar ADR es documental, no una aprobación heredada de las políticas contenidas en ADR posteriores.

## 7. Cambios realizados y estado

- [ADR-0002](./adrs/0002-arquitectura-hexagonal-mvc.md): ahora `PROPOSED`; delimitado a una opción de articulación MVC/hexagonal.
- [ADR-0003](./adrs/0003-persistencia-postgresql-kardex.md): ahora `PROPOSED`; PostgreSQL registrado solo como requisito expreso, sin fijar versión ni modelo de datos.
- [Informe de brechas normativas](../../06_Matriz_Trazabilidad_Normativa/informe_brechas_normativas.md): actualizado para reflejar que los ADR fueron reconciliados y que las decisiones de negocio siguen pendientes.
- [Registro de fuentes](../../06_Matriz_Trazabilidad_Normativa/registro_fuentes_oficiales.md) y [marco normativo](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md): actualizados para no afirmar que los ADR están aceptados.
- [Revisión de contratos API](../../05_Contratos_API/revision_final_contratos_api.md): actualizado el hallazgo sobre ADR-0003.
- El análisis vigente y la matriz regulatoria histórica **no se modificaron ni eliminaron**.

## 8. Recomendación

Mantener `SPEC-002`, la arquitectura, los contratos API, la matriz normativa y ADR-0002/0003 en `DRAFT`/`PROPOSED` hasta recibir autorización expresa. Las únicas condiciones actualmente justificadas por requisitos son el uso de PostgreSQL, el diseño modular MVC con principios hexagonales, la definición previa de contratos, la consideración de UBL Invoice 2.1, la integración con SUNAT y la migración histórica; estas declaraciones no completan las decisiones abiertas enumeradas en este informe.

No implementar ni cambiar el análisis original hasta que el responsable confirme qué preguntas se resuelven y autorice los documentos afectados.
