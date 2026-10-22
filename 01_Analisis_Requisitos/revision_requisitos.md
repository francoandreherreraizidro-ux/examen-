# Informe de Revisión Técnica y Evaluación de Requisitos de Software

| Parámetro | Detalle |
| :--- | :--- |
| **Documento Evaluado** | `01_Analisis_Requisitos/analisis_requisitos.md` (Versión 1.0.0) |
| **Rol del Evaluador** | Revisor Técnico y Docente Evaluador de Arquitectura de Software |
| **Dominio Evaluado** | Sistema Comercial, Inventarios (Kardex), Facturación Electrónica UBL 2.1 y SUNAT |
| **Estándar de Evaluación** | ISO/IEC/IEEE 29148:2018 (Requirements Engineering) y Principios DDD / Hexagonal |
| **Fecha de Revisión** | Octubre 2026 |
| **Dictamen General** | **APROBADO CON OBSERVACIONES MENORES (Condicionado a correcciones formales)** |

---

## Índice de la Revisión

1. [Tabla de Hallazgos por Criterio de Calidad](#1-tabla-de-hallazgos-por-criterio-de-calidad)
2. [Requerimientos que Requieren Corrección o Ajuste](#2-requerimientos-que-requieren-corrección-o-ajuste)
3. [Propuestas de Redacción Mejorada](#3-propuestas-de-redacción-mejorada)
4. [Preguntas Pendientes para el Responsable del Sistema (Stakeholder)](#4-preguntas-pendientes-para-el-responsable-del-sistema)
5. [Evaluación Final y Dictamen Técnico](#5-evaluación-final-y-dictamen-técnico)

---

## 1. Tabla de Hallazgos por Criterio de Calidad

A continuación se resume el examen exhaustivo del documento bajo los siete criterios formales de ingeniería de requisitos:

| Criterio | Estado | Severidad | Resumen del Hallazgo |
| :--- | :---: | :---: | :--- |
| **Completitud (Completeness)** | ⚠️ Parcial | Media | Falta formalizar el flujo de discrepancias en recepciones interalmacén (pérdidas/roturas en transporte), el manejo de pagos mixtos en el POS minorista y la estrategia de reversión (*rollback*) ante fallos en la migración de saldos. |
| **Consistencia (Consistency)** | ❌ Discrepancia | **Alta** | **Conflicto directo entre `RF-ALM-004` y `RN-05`**: `RF-ALM-004` sugiere permitir salidas con sobregiro de stock ("política explícita de sobregiro controlado"), mientras que `RN-05` prohíbe de forma terminante los saldos negativos en Kardex. En la tributación peruana (Kardex formal SUNAT), un saldo negativo constituye una infracción tributaria grave. |
| **No Ambigüedad (Unambiguity)** | ⚠️ Parcial | Media | Términos imprecisos en la redacción: en `RF-VEN-001` ("adición rápida", "emisión instantánea"), en `RF-VEN-004` ("promociones específicas de mercadería" sin definir tipologías) y en `RF-MIG-003` ("almacenar o permitir consultar" sin fijar la arquitectura de persistencia histórica). |
| **Trazabilidad (Traceability)** | ❌ Defecto Menor | Media | **Requerimiento huérfano en matriz**: En la Sección 9, la necesidad `NN-10` referencia a `RF-SUN-005` ("Patrón Outbox"), pero en el cuerpo de la Sección 4.6 solo se listan los requerimientos del `RF-SUN-001` al `RF-SUN-004`. Falta formalizar `RF-SUN-005` en el cuerpo del texto. |
| **Verificabilidad (Verifiability)** | ⚠️ Parcial | Media | `RF-MIG-001` carece de umbrales cuantitativos de éxito o tasa admisible de error en la ingesta ETL. `RF-TRZ-002` no define cómo se reportan las fallas de validación hacia la interfaz de usuario. |
| **Factibilidad (Feasibility)** | ✅ Conforme | Baja | Todos los requerimientos son técnica y arquitectónicamente viables con PostgreSQL 15+, Arquitectura Hexagonal y servicios REST/SOAP. Se advierte que la consulta síncrona a padrones externos (`RF-DIR-004`) debe aislarse con fallback para no degradar el POS. |
| **Separación de Certeza (Separation)**| ✅ Conforme | Baja | Excelente clasificación y delimitación explícita entre demandas del enunciado (`[Explícito]`), deducciones técnicas (`[Inferencia Razonable]`) y recomendaciones del arquitecto (`[Propuesta de Diseño]`). |

---

## 2. Requerimientos que Requieren Corrección o Ajuste

Se identifican los siguientes puntos críticos que deben ser subsanados en el análisis:

### 2.1. Inconsistencia Crítica: Control de Inventario y Kardex Negativo
* **Requerimiento Afectado**: `RF-ALM-004` (Registro de Salidas) frente a `RN-05` (Restricción de Kardex Negativo).
* **Defecto**: `RF-ALM-004` estipula: *"No se permitirá confirmar salidas si la cantidad solicitada excede el stock disponible (a menos que se habilite una política explícita de sobregiro controlado)"*. 
* **Justificación de la Corrección**: En el régimen tributario peruano (Resolución de Superintendencia N° 234-2006/SUNAT), el Registro de Inventario Permanente Valorizado (Kardex) no admite bajo ninguna circunstancia saldos físicos ni valorizados negativos. Permitir "sobregiro controlado" distorsiona el Costo Promedio Ponderado ($C_{promedio}$) provocando divisiones entre cero o costos unitarios negativos, además de contingencias de presunción de ventas no declaradas ante una fiscalización de SUNAT.
* **Acción Requerida**: Eliminar la cláusula de sobregiro controlado en `RF-ALM-004` y alinearla estrictamente con `RN-05`.

### 2.2. Defecto de Trazabilidad: Requerimiento Omitido en el Módulo SUNAT
* **Requerimiento Afectado**: `RF-SUN-005` (Mecanismo de Contingencia y Reintentos Asíncronos / Outbox).
* **Defecto**: Aparece referenciado en la Matriz de Trazabilidad (Sección 9, `NN-10`), pero fue omitido en la enumeración formal del Módulo 6 (Sección 4.6), el cual concluye abruptamente en `RF-SUN-004`.
* **Acción Requerida**: Incorporar formalmente `RF-SUN-005` en el cuerpo de requisitos funcionales con su descripción y criterios de aceptación.

### 2.3. Ambigüedad en la Migración de Datos Legados
* **Requerimiento Afectado**: `RF-MIG-003` (Repositorio de Consulta de Histórico de Ventas y Movimientos Legados).
* **Defecto**: La frase *"El sistema debe almacenar o permitir consultar las transacciones de ventas..."* es condicional y ambigua. No define si el sistema nuevo alojará las tablas históricas en su propio esquema de PostgreSQL, si creará un esquema de solo lectura (`legacy_archive`), o si mantendrá un enlace a una base de datos externa.
* **Acción Requerida**: Redactar con precisión la estrategia de persistencia histórica (esquema segregado de solo lectura en PostgreSQL).

### 2.4. Generalidad Excesiva en Descuentos y Promociones
* **Requerimiento Afectado**: `RF-VEN-004` (Gestión de Descuentos y Promociones).
* **Defecto**: Habla genéricamente de *"promociones específicas de mercadería"* sin delimitar si abarca combos, bonificaciones en producto (que requieren comprobante con afectación de retiro de bienes según Catálogo 07 de SUNAT) o únicamente descuentos monetarios porcentuales/fijos.
* **Acción Requerida**: Delimitar el alcance funcional inicial a descuentos comerciales en porcentaje o importe fijo que afecten la base imponible, dejando las bonificaciones en especie como propuesta sujeta a confirmación contable.

---

## 3. Propuestas de Redacción Mejorada

A continuación se presentan las formulaciones corregidas bajo criterios formales de verificabilidad, rigor técnico y eliminación de contradicciones:

---

### Corrección 1: `RF-ALM-004` - Registro de Salidas de Mercadería (Alineado con Normativa SUNAT)

> #### `RF-ALM-004` - Registro de Salidas de Mercadería y Control Estricto de Disponibilidad
> * **Clasificación**: `[Explícito]`
> * **Descripción**: El sistema debe registrar las salidas de inventario originadas por ventas comerciales (mayoristas o minoristas), transferencias hacia otros almacenes, o bajas por merma y desmedro debidamente autorizadas, rebajando el stock físico y disponible en el almacén de origen.
> * **Criterios de Aceptación (Verificables)**:
>   1. El sistema debe validar que la cantidad a despachar sea menor o igual al saldo físico actual ($Q_{despacho} \le Q_{disponible}$).
>   2. Queda estrictamente prohibida la confirmación de transacciones que generen saldo negativo en el inventario o en el Kardex permanente, abortando la transacción con error `ERR_INSUFFICIENT_STOCK`.
>   3. Toda salida por concepto de venta debe vincular de forma obligatoria el identificador de la orden de venta o comprobante de pago emitido.

---

### Corrección 2: `RF-SUN-005` - Incorporación Formal del Requerimiento Omitido

> #### `RF-SUN-005` - Desacoplamiento Asíncrono de Transmisión a SUNAT (Patrón Transactional Outbox)
> * **Clasificación**: `[Propuesta de Diseño / Inferencia]`
> * **Descripción**: El sistema debe desacoplar la confirmación de la venta en el punto de atención de la comunicación remota con los servidores de la SUNAT, mediante el registro transaccional de un evento de salida (*Transactional Outbox*) en PostgreSQL en la misma transacción ACID de la venta.
> * **Criterios de Aceptación (Verificables)**:
>   1. La venta en caja debe quedar confirmada localmente con su comprobante firmado en un tiempo $\le 1.5$ segundos ($P_{95}$), sin depender de la latencia o disponibilidad del Web Service de SUNAT.
>   2. Un proceso demonio asíncrono debe despachar los eventos pendientes de la tabla Outbox hacia SUNAT utilizando una política de reintentos exponenciales con límite de 5 intentos y registro de causas de fallo.
>   3. El sistema debe garantizar la idempotencia de envío, evitando transmisiones duplicadas del mismo comprobante fiscal.

---

### Corrección 3: `RF-MIG-003` - Precisión en la Persistencia de Datos Históricos

> #### `RF-MIG-003` - Repositorio Histórico de Datos Legados en Esquema de Solo Lectura
> * **Clasificación**: `[Explícito]`
> * **Descripción**: El sistema debe almacenar las transacciones históricas de ventas, compras y movimientos de inventario procedentes del sistema legado dentro de un esquema de base de datos segregado (`legacy_archive`) en PostgreSQL, asegurando su inmutabilidad y disponibilidad exclusiva para consulta y auditoría.
> * **Criterios de Aceptación (Verificables)**:
>   1. Los usuarios y roles del sistema tendrán únicamente permisos de lectura (`SELECT`) sobre el esquema `legacy_archive`.
>   2. El sistema debe proveer endpoints de consulta paginada que permitan buscar comprobantes y movimientos anteriores por rango de fechas, RUC de cliente/proveedor y número de comprobante legado.
>   3. Los datos del esquema legado no participarán en los cálculos activos de Kardex ni en la contabilidad del nuevo ejercicio fiscal.

---

### Corrección 4: `RF-ALM-005` - Manejo de Discrepancias en Traslados Interalmacén

> #### `RF-ALM-005` - Transferencias Interalmacén y Conciliación de Recepción
> * **Clasificación**: `[Inferencia Razonable]`
> * **Descripción**: El sistema debe gestionar el traslado de mercancías entre almacenes físicos mediante un flujo en dos fases: despacho en tránsito (origen) y confirmación de recepción (destino), soportando el registro de discrepancias físicas por mermas o roturas en transporte.
> * **Criterios de Aceptación (Verificables)**:
>   1. Al confirmar el despacho en el almacén de origen, el stock se deduce del almacén emisor y se traslada a la cuenta "Stock en Tránsito", no estando disponible para venta en ninguno de los dos locales.
>   2. Al arribar la mercancía al almacén de destino, el operador debe registrar la cantidad efectivamente recibida.
>   3. Si la cantidad recibida es inferior a la despachada ($Q_{recibida} < Q_{despachada}$), el sistema debe registrar automáticamente la diferencia como merma en tránsito (`AJUSTE_MERMA_TRANSPORTE`), requiriendo justificación y aprobación del Jefe de Almacén (`ACT-02`).

---

## 4. Preguntas Pendientes para el Responsable del Sistema (Stakeholder)

Como evaluador técnico, se certifica que el analista **no debe inventar** reglas de negocio ni decisiones estratégicas que competen exclusivamente al cliente o al docente responsable de la cátedra. Deben formularse formalmente las siguientes preguntas:

1. **Sobre la Valuación de Inventarios (Kardex)**:
   * *Pregunta*: ¿La empresa adoptará formalmente el método de **Costo Promedio Ponderado** o **PEPS (Primeras Entradas, Primeras Salidas)** para la presentación de su Libro de Inventarios y Balances ante la SUNAT?
2. **Sobre el Tratamiento Tributario de Pérdidas y Roturas (Mermas vs. Desmedros)**:
   * *Pregunta*: Dado que el giro comercial es abarrotes y productos perecibles, ¿se requerirá clasificar las pérdidas en *Mermas* (pérdida cuantitativa inherente al producto) y *Desmedros* (deterioro cualitativo que exige destrucción ante Notario Público según el Art. 37 de la Ley del Impuesto a la Renta)?
3. **Sobre la Operativa Fiscal (Modalidad SUNAT)**:
   * *Pregunta*: ¿La empresa emitirá directamente contra los Web Services de SUNAT (**SEE-Del Contribuyente**) o utilizará un Operador de Servicios Electrónicos autorizado (**OSE**)?
4. **Sobre la Contingencia en Puntos de Venta sin Internet**:
   * *Pregunta*: Ante una interrupción prolongada del servicio de internet en una tienda física minorista, ¿el cajero continuará despachando mediante **Comprobantes Físicos de Contingencia preimpresos autorizados por SUNAT**, o el punto de venta operará en modo offline con sincronización local diferida?
5. **Sobre el Alcance Temporal de la Migración**:
   * *Pregunta*: ¿La migración desde el sistema anterior abarcará únicamente saldos de apertura a la fecha de corte y maestros (catálogo, clientes, proveedores), o es mandatorio migrar el detalle transaccional de los últimos 2 a 5 ejercicios fiscales cerrados?

---

## 5. Evaluación Final y Dictamen Técnico

### 5.1. Cuadro de Calificación por Criterio

| Dimensión de Calidad | Calificación (Escala 1 - 5) | Estado | Comentario del Evaluador |
| :--- | :---: | :---: | :--- |
| **Completitud** | 4.2 / 5.0 | Aceptable | Alcance amplio y bien delimitado; requiere detallar discrepancias en traslados y pagos mixtos. |
| **Consistencia** | 3.8 / 5.0 | Requiere Ajuste | Se debe subsanar de inmediato la contradicción entre `RF-ALM-004` (sobregiro) y `RN-05` (Kardex no negativo). |
| **No Ambigüedad** | 4.3 / 5.0 | Bueno | Especificaciones claras en un 90%; corregir términos cualitativos en POS y migración. |
| **Trazabilidad** | 4.0 / 5.0 | Aceptable | Incorporar formalmente el identificador omitido `RF-SUN-005` en el cuerpo del texto. |
| **Verificabilidad** | 4.5 / 5.0 | Muy Bueno | Criterios de aceptación estructurados con límites numéricos, códigos y estados precisos. |
| **Factibilidad** | 5.0 / 5.0 | Sobresaliente | Perfectamente viable sobre la arquitectura hexagonal y la base de datos PostgreSQL 15+. |
| **Separación de Certeza** | 5.0 / 5.0 | Sobresaliente | Rigor ejemplar al etiquetar explícitos, inferencias y propuestas de diseño. |
| **Promedio Ponderado** | **4.4 / 5.0** | **APROBADO** | **Cumple satisfactoriamente los estándares de evaluación.** |

### 5.2. Dictamen Oficial del Evaluador

> **DICTAMEN: APROBADO CON OBSERVACIONES MENORES**
> 
> El documento `01_Analisis_Requisitos/analisis_requisitos.md` presenta un nivel de madurez técnica, profundidad analítica y apego a la normativa tributaria peruana (UBL 2.1 / SUNAT) sobresaliente para un examen de Arquitectura de Software.
> 
> Las deficiencias identificadas (inconsistencia en la política de sobregiro de stock, omisión tipográfica de `RF-SUN-005` y ambigüedades menores en la migración de datos) **no invalidan la arquitectura conceptual ni bloquean el avance hacia el modelado de dominio**, siempre y cuando las correcciones y preguntas aquí planteadas se tomen como directrices vinculantes para la fase de diseño táctico.
> 
> **Recomendación**: Se autoriza formalmente la adopción de las formulaciones corregidas presentadas en este informe y se da luz verde conceptual para mantener alineados los entregables posteriores.
