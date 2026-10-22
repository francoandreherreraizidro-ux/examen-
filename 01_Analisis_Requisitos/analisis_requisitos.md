# Documento de Análisis de Requisitos del Sistema

| Parámetro | Detalle |
| :--- | :--- |
| **Proyecto / Asignatura** | Examen de Arquitectura de Software |
| **Dominio** | Sistema Comercial, Control de Inventarios y Facturación Electrónica UBL 2.1 |
| **Giro de Negocio** | Comercializadora mediana de abarrotes y mercadería general (Mayorista y Minorista) |
| **Metodología** | Spec-Driven Development (Spec-First) / Enfoque Hexagonal + MVC |
| **Base de Datos** | PostgreSQL |
| **Normativa Fiscal** | SUNAT (Perú) - Estándar OASIS UBL 2.1 |
| **Fase Actual** | **Fase 1: Análisis de Requisitos y Definición del Alcance** |
| **Estado del Documento** | `DRAFT - PENDIENTE DE REVISIÓN Y APROBACIÓN` |
| **Fecha de Elaboración** | Octubre 2026 |

---

## Índice de Contenidos

1. [Resumen del Problema de Negocio](#1-resumen-del-problema-de-negocio)
2. [Objetivo General y Objetivos Específicos del Sistema](#2-objetivo-general-y-objetivos-específicos-del-sistema)
3. [Actores del Sistema (Confirmados vs. Propuestos)](#3-actores-del-sistema)
4. [Requerimientos Funcionales (RF)](#4-requerimientos-funcionales-rf)
   - 4.1. Módulo 1: Catálogo y Productos (CAT)
   - 4.2. Módulo 2: Almacenes e Inventarios (ALM)
   - 4.3. Módulo 3: Clientes y Proveedores (DIR)
   - 4.4. Módulo 4: Ventas y Operación Comercial (VEN)
   - 4.5. Módulo 5: Facturación Electrónica y Estándar UBL 2.1 (CPE)
   - 4.6. Módulo 6: Integración con Servicios SUNAT (SUN)
   - 4.7. Módulo 7: Migración de Datos Históricos (MIG)
   - 4.8. Módulo 8: Validación, Auditoría y Trazabilidad (TRZ)
5. [Requerimientos No Funcionales (RNF)](#5-requerimientos-no-funcionales-rnf)
6. [Restricciones Técnicas y Arquitectónicas](#6-restricciones-técnicas-y-arquitectónicas)
7. [Reglas de Negocio (Confirmadas y Por Confirmar)](#7-reglas-de-negocio)
8. [Incertidumbres y Ambigüedades en el Planteamiento](#8-incertidumbres-y-ambigüedades-en-el-planteamiento)
9. [Matriz de Trazabilidad Inicial (Necesidades vs. Requerimientos)](#9-matriz-de-trazabilidad-inicial)

---

## 1. Resumen del Problema de Negocio

La empresa bajo análisis es una organización mediana dedicada a la comercialización y distribución de abarrotes y mercadería general en el mercado peruano. Su modelo operativo integra dos canales de comercialización con dinámicas drásticamente diferenciadas:

1. **Canal Mayorista**: Operaciones de alto volumen por pedido, acuerdos comerciales con precios escalonados, concesión de líneas de crédito a plazos determinados, despachos consolidados y emisión obligatoria de Facturas Electrónicas (RUC de empresas o personas naturales con negocio).
2. **Canal Minorista (Retail / Mostrador)**: Ventas de alta rotación y velocidad en punto de atención, cobro inmediato (efectivo, transferencias o tarjetas), emisión masiva de Boletas de Venta Electrónicas (con identificación obligatoria mediante DNI para montos superiores a S/ 700.00 o sin identificación para montos menores) y despacho en mostrador.

### Problemática Actual Identificada

* **Descontrol de Inventarios y Quiebres de Stock**: La falta de un registro histórico y valorizado (Kardex) unificado provoca discrepancias entre el stock físico y el stock lógico, compras inoportunas, pérdidas por productos perecibles vencidos o mermas sin trazabilidad, y descoordinación entre múltiples almacenes y puntos de venta.
* **Riesgo Fiscal y Tributario ante la SUNAT**: La emisión de comprobantes de pago presenta fricciones operativas. Los errores en la estructuración de documentos bajo el estándar UBL 2.1, el desfase en la recepción de la Constancia de Recepción (CDR), o el incumplimiento de los plazos legales de envío (máximo 3 días calendario para facturas según normativa vigente de la SUNAT) exponen a la empresa a graves sanciones tributarias y comprobantes declarados no válidos.
* **Fragmentación del Ciclo Comercial**: No existe sincronización en tiempo real entre la aprobación de una venta (al contado o a crédito) y la reserva/despacho de mercancías en almacén, lo que causa retrasos en la entrega y sobreventas de productos con inventario agotado.
* **Obsolescencia del Sistema Legado**: Los datos históricos residen en un sistema heredado desarticulado. Se requiere un plan de migración riguroso que consolide maestros de clientes, proveedores, catálogo y saldos iniciales de inventario sin interrumpir la operación ni comprometer la trazabilidad fiscal de periodos precedentes.
* **Carencia de una Arquitectura Robusta**: El sistema actual adolece de alto acoplamiento, lo que impide actualizar reglas tributarias, conectar nuevos servicios (como APIs de consulta de RUC/DNI o pasarelas de pago) o mantener pruebas automatizadas fiables.

---

## 2. Objetivo General y Objetivos Específicos del Sistema

### 2.1. Objetivo General

Diseñar la arquitectura de software y las especificaciones técnicas completas para un sistema integral de gestión comercial, control de inventarios multialmacén y facturación electrónica, empleando los patrones **MVC** y **Arquitectura Hexagonal (Ports & Adapters)** con base de datos **PostgreSQL**, garantizando el cumplimiento estricto del estándar **OASIS UBL 2.1**, la integración asíncrona y segura con los servicios de la **SUNAT**, la migración íntegra y auditada de los datos del sistema legado, y la definición formal de contratos de datos y APIs bajo un enfoque metodológico **Spec-First**.

### 2.2. Objetivos Específicos

1. **Gestión Integral de Inventarios**: Implementar un modelo de control multialmacén con registro inmutable de movimientos (entradas, salidas, transferencias y ajustes) garantizando la generación automática de Kardex físico y valorizado.
2. **Unificación de Procesos Comerciales**: Soportar de manera coherente las operaciones mayoristas (cotización, pedidos, condiciones de crédito, facturación) y minoristas (ventas rápidas en caja, boletas masivas).
3. **Cumplimiento Tributario Electrónico**: Asegurar la generación sintáctica y semánticamente válida de archivos XML conforme al esquema OASIS UBL 2.1 (Facturas, Boletas, Notas de Crédito, Notas de Débito), su firma digital mediante certificados X.509 y el intercambio asíncrono con los Web Services de la SUNAT, procesando integralmente los CDR.
4. **Migración Segura y Verificable**: Definir los procesos de extracción, transformación y carga (ETL) para la incorporación de datos maestros y saldos de apertura de inventario desde el sistema legado, con mecanismos formales de conciliación.
5. **Excelencia Arquitectónica y Desacoplamiento**: Separar las reglas del núcleo del dominio de los mecanismos de persistencia e infraestructura externa (PostgreSQL, Web Services de SUNAT, interfaces de usuario), garantizando la testabilidad unitaria y de contratos sin dependencias externas.

---

## 3. Actores del Sistema

Para preservar el rigor metodológico, los actores se categorizan formalmente en **Confirmados** (explícitamente exigidos o deducibles de manera directa del escenario) y **Propuestos** (recomendados como buena práctica de arquitectura para un diseño integral):

### 3.1. Actores Confirmados [CONFIRMADO]

| ID Actor | Nombre del Actor | Tipo | Descripción y Responsabilidad |
| :--- | :--- | :--- | :--- |
| **ACT-01** | **Administrador del Sistema (TI)** | Humano | Responsable de la configuración global, gestión de parámetros de la empresa, control de usuarios y perfiles (RBAC), ejecución y validación de procesos de migración de datos legados. |
| **ACT-02** | **Jefe / Encargado de Almacén** | Humano | Responsable de la administración de almacenes, definición de ubicaciones, control y revisión del Kardex, autorización de transferencias entre almacenes y aprobación de ajustes de inventario. |
| **ACT-03** | **Operador de Almacén** | Humano | Encargado del registro operativo de recepción de mercadería de proveedores, preparación de pedidos (picking/packing) y ejecución física de salidas o traslados. |
| **ACT-04** | **Cajero / Vendedor Minorista** | Humano | Opera el punto de venta (POS) minorista. Realiza cobros rápidos, aplica listas de precios al por menor y emite Boletas de Venta o Facturas de mostrador. |
| **ACT-05** | **Ejecutivo de Ventas Mayoristas** | Humano | Gestiona la cartera de clientes corporativos, elabora cotizaciones, registra pedidos de alto volumen, consulta disponibilidad multialmacén y aplica condiciones de crédito. |
| **ACT-06** | **Contador / Responsable Tributario** | Humano | Supervisa la consistencia contable y tributaria. Controla la emisión de comprobantes electrónicos, revisa el estado de los CDR, gestiona la anulación mediante Notas de Crédito y genera resúmenes. |
| **ACT-07** | **SUNAT (Sistema Externo)** | Sistema Externo | Autoridad Tributaria del Perú. Expone Web Services (SOAP / REST) para recepción de comprobantes (`sendBill`, `sendSummary`), emisión de tickets y entrega de Constancias de Recepción (`CDR`). |
| **ACT-08** | **Sistema Legado / Motor de Migración** | Sistema Externo | Repositorio o fuente de datos anterior desde donde se extraen maestros (productos, clientes, proveedores) y saldos históricos iniciales. |

### 3.2. Actores Propuestos [PROPUESTO]

| ID Actor | Nombre del Actor | Tipo | Justificación de la Propuesta |
| :--- | :--- | :--- | :--- |
| **ACT-PROP-01** | **Cliente Final (Portal de Autoservicio)** | Humano | Permite a clientes mayoristas y minoristas consultar y descargar sus comprobantes electrónicos (XML firmado y representación impresa PDF) conforme a la exigencia de la SUNAT de poner a disposición el comprobante. |
| **ACT-PROP-02** | **Gerente Comercial / Operaciones** | Humano | Requiere acceso a tableros de control consolidados: rotación de stock, márgenes por canal de venta (mayorista vs minorista) y efectividad de cobranzas. |
| **ACT-PROP-03** | **Auditor Interno / Fiscalizador** | Humano | Perfil especializado para inspección de pistas de auditoría inmutables, trazabilidad de comprobantes y verificación de cuadres de Kardex. |

---

## 4. Requerimientos Funcionales (RF)

Cada requerimiento funcional cuenta con un código estructurado, nombre, clasificación de certeza y especificación detallada:
* **[Explícito]**: Demandado formalmente en el planteamiento del examen.
* **[Inferencia Razonable]**: Requerimiento técnico o de dominio estrictamente necesario para cumplir una demanda explícita.
* **[Propuesta de Diseño]**: Elemento de valor agregado sugerido por el arquitecto para robustecer la solución.

---

### 4.1. Módulo 1: Catálogo y Productos (CAT)

#### `RF-CAT-001` - Gestión de Catálogo de Productos y Mercadería General
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe permitir registrar, consultar, actualizar y deshabilitar productos del catálogo de abarrotes y mercadería general, capturando atributos comerciales, código interno único, código de barras (EAN-13/UPC), descripción detallada, marca, categoría y subcategoría.
* **Criterios de Aceptación**: Ningún producto podrá eliminarse físicamente si posee historial de movimientos de inventario o comprobantes de venta asociados (eliminación lógica / borrado suave).

#### `RF-CAT-002` - Esquema de Precios Dual (Mayorista y Minorista)
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe soportar listas de precios diferenciadas por producto para atender a los canales mayorista y minorista.
* **Criterios de Aceptación**: Se debe permitir definir un precio unitario minorista estándar y al menos una escala de precio mayorista (por volumen de compra o por categoría de cliente mayorista). Cada cambio de precio debe registrar fecha, hora y usuario responsable.

#### `RF-CAT-003` - Normalización de Unidades de Medida según Catálogo 03 SUNAT
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: Cada producto debe tener asignada obligatoriamente una unidad de medida estándar conforme al Catálogo N° 03 de la SUNAT (ej. `NIU` para unidades, `KGM` para kilogramos, `BX` para cajas, `PK` para paquetes).
* **Criterios de Aceptación**: El código de unidad de medida debe validarse contra la tabla oficial de la SUNAT para garantizar la compatibilidad con el elemento `cbc:InvoicedQuantity/@unitCode` de UBL 2.1.

#### `RF-CAT-004` - Control de Lotes y Fechas de Vencimiento para Abarrotes Perecibles
* **Clasificación**: `[Propuesta de Diseño]`
* **Descripción**: El sistema debe permitir asociar números de lote y fechas de caducidad a aquellos productos catalogados como perecibles o de consumo humano directo.
* **Criterios de Aceptación**: El sistema debe alertar sobre lotes próximos a vencer e impedir la venta de productos cuya fecha de caducidad haya expirado.

#### `RF-CAT-005` - Clasificación de Productos según Código de Productos y Servicios SUNAT (UNSPSC)
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El sistema debe permitir asignar a cada producto el código estándar del Catálogo N° 25 de la SUNAT (Segmento/Familia/Clase/Commodity basado en UNSPSC).
* **Criterios de Aceptación**: Campo opcional u obligatorio según la obligatoriedad fiscal aplicable al sector comercial, mapeado al campo UBL `cac:Item/cac:CommodityClassification`.

---

### 4.2. Módulo 2: Almacenes e Inventarios (ALM)

#### `RF-ALM-001` - Gestión Multialmacén y Ubicaciones Físicas
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe permitir la configuración y gestión de múltiples almacenes físicos (por ejemplo: Almacén Central, Almacén Mayorista, Tienda/Mostrador Retail 1, Almacén de Merma/Cuarentena), permitiendo definir zonas, pasillos o ubicaciones internas.
* **Criterios de Aceptación**: Cada movimiento de stock debe estar obligatoriamente vinculado a un almacén de origen o destino claramente individualizado.

#### `RF-ALM-002` - Registro Histórico de Movimientos de Inventario (Kardex Permanente)
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe mantener un historial inmutable y cronológico de todas las transacciones que afecten el stock de un producto (entradas, salidas, transferencias y ajustes).
* **Criterios de Aceptación**: El sistema debe calcular el stock disponible y el stock contable/valorizado a una fecha determinada. No se permitirá la modificación ni eliminación de registros históricos del Kardex; cualquier corrección requerirá una transacción de ajuste compensatorio.

#### `RF-ALM-003` - Registro de Entradas de Mercadería (Inflows)
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe registrar el ingreso de productos al inventario derivado de compras a proveedores, devoluciones de clientes, o recepción por transferencias internas, solicitando datos del comprobante de origen (Guía de Remisión o Factura de compra).
* **Criterios de Aceptación**: El ingreso actualizará de inmediato el saldo disponible en el almacén de destino y afectará el costo unitario según el método de valuación configurado.

#### `RF-ALM-004` - Registro de Salidas de Mercadería (Outflows)
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe registrar las salidas de inventario asociadas a ventas comerciales (mayoristas o minoristas), mermas, desmedros o transferencias hacia otros almacenes.
* **Criterios de Aceptación**: Las ventas en estado confirmado deben rebajar automáticamente el stock del almacén asignado. No se permitirá confirmar salidas si la cantidad solicitada excede el stock disponible (a menos que se habilite una política explícita de sobregiro controlado).

#### `RF-ALM-005` - Transferencias de Stock Interalmacén
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El sistema debe permitir el traslado de mercancías entre almacenes de la empresa mediante un flujo de doble confirmación: salida en tránsito desde el almacén emisor y confirmación de recepción en el almacén receptor.
* **Criterios de Aceptación**: Durante el tránsito, el stock debe figurar como "En Transferencia", evitando que esté disponible para venta en ambos almacenes simultáneamente.

#### `RF-ALM-006` - Ajustes de Inventario y Conciliación Física
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El sistema debe registrar tomas de inventario físico y permitir la aplicación de ajustes positivos (sobrantes) o negativos (faltantes/mermas) debidamente justificados.
* **Criterios de Aceptación**: Los ajustes de inventario deben requerir la autorización del Jefe de Almacén (`ACT-02`) y quedarán documentados con código de motivo y usuario responsable.

---

### 4.3. Módulo 3: Clientes y Proveedores (DIR)

#### `RF-DIR-001` - Maestro de Clientes y Validación de Identidad (Catálogo 06 SUNAT)
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe gestionar el registro de clientes categorizándolos en minoristas y mayoristas. Cada cliente debe contar con su tipo de documento de identidad validado según el Catálogo N° 06 de la SUNAT:
  * Tipo 1: DNI (8 dígitos).
  * Tipo 6: RUC (11 dígitos, validación de dígito verificador módulo 11).
  * Tipo 4: Carné de Extranjería / Tipo 7: Pasaporte.
  * Tipo 0: Documento no domiciliado / Sin documento (aplicable para boletas minoristas menores a S/ 700.00).
* **Criterios de Aceptación**: Para la emisión de Facturas Electrónicas, el cliente debe contar obligatoriamente con RUC de 11 dígitos y estado activo/habido.

#### `RF-DIR-002` - Maestro de Proveedores y Condiciones de Abastecimiento
* **Clasificación**: `[Explícito]`
* **Descripción**: Registro y mantenimiento de proveedores de abarrotes y mercadería, registrando RUC, razón social, dirección fiscal, personas de contacto, términos de pago y catálogo de productos suministrados.
* **Criterios de Aceptación**: El sistema validará que el RUC sea válido y no permitirá proveedores duplicados.

#### `RF-DIR-003` - Gestión de Condiciones y Líneas de Crédito para Clientes Mayoristas
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El sistema debe permitir configurar para clientes mayoristas un límite de crédito monetario máximo y plazos de pago (ej. 15, 30, 45 días).
* **Criterios de Aceptación**: Al momento de registrar un pedido mayorista a crédito, el sistema verificará si el monto excede la línea de crédito disponible o si el cliente mantiene facturas vencidas impagas.

#### `RF-DIR-004` - Consulta Automatizada de Padrón RUC y DNI
* **Clasificación**: `[Propuesta de Diseño]`
* **Descripción**: Integrar un servicio de consulta externa a los padrones de SUNAT/RENIEC para autocompletar la razón social, nombre y dirección fiscal al ingresar un número de RUC o DNI.
* **Criterios de Aceptación**: Debe operar con mecanismos de caché local para optimizar el rendimiento y no bloquear la venta si el servicio externo no responde.

---

### 4.4. Módulo 4: Ventas y Operación Comercial (VEN)

#### `RF-VEN-001` - Punto de Venta Minorista (Retail POS)
* **Clasificación**: `[Explícito]`
* **Descripción**: Interfaz de ventas de alta velocidad para mostrador que permita la lectura de códigos de barras, adición rápida de ítems, selección del medio de pago (efectivo, tarjeta, billetera digital) y emisión instantánea del comprobante (Boleta de Venta o Factura).
* **Criterios de Aceptación**: El tiempo total de procesamiento de la transacción en caja (desde el cierre hasta la emisión) no debe superar los 2 segundos en condiciones normales.

#### `RF-VEN-002` - Gestión del Flujo Comercial Mayorista
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe soportar el ciclo comercial mayorista estructurado en fases: Cotización / Proforma -> Pedido de Venta -> Aprobación de Crédito -> Asignación de Despacho -> Facturación.
* **Criterios de Aceptación**: Un pedido confirmado debe comprometer stock en el almacén de despacho para evitar que sea vendido a través de otro canal mientras se prepara el pedido.

#### `RF-VEN-003` - Cálculo Automatizado de Tributos Comerciales Peruanos
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El motor de liquidación de ventas debe calcular con precisión matemática los importes brutos, descuentos globales o por ítem, y la desagregación de tributos conforme a la legislación peruana:
  * Operaciones Gravadas con IGV (18% o tasa vigente, afectación 10).
  * Operaciones Exoneradas (afectación 20 - Apéndice I de la Ley del IGV para ciertos productos agrícolas/abarrotes).
  * Operaciones Inafectas (afectación 30).
  * Impuesto al Consumo de Bolsas de Plástico (ICBPER) si aplica en el canal minorista.
* **Criterios de Aceptación**: El redondeo debe respetar la precisión decimal requerida por el estándar UBL 2.1 (importes monetarios a 2 decimales, precios unitarios hasta 10 decimales).

#### `RF-VEN-004` - Gestión de Descuentos y Promociones
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El sistema debe permitir configurar descuentos por volumen de compra (escalas por docenas, cajones o bultos) y promociones específicas de mercadería.
* **Criterios de Aceptación**: Los descuentos aplicados deben reflejarse explícitamente en el comprobante electrónico cumpliendo las reglas del Catálogo N° 53 de SUNAT (Cargos y Descuentos).

---

### 4.5. Módulo 5: Facturación Electrónica y Estándar UBL 2.1 (CPE)

#### `RF-CPE-001` - Generación de Facturas Electrónicas (Tipo 01) en UBL 2.1
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe construir el documento XML bajo el estándar OASIS UBL 2.1 (`Invoice`), cumpliendo las directrices del Anexo I de la Resolución de Superintendencia N° 097-2012/SUNAT y sus modificatorias (RS 193-2020/SUNAT y vigentes).
* **Criterios de Aceptación**: El XML debe incluir la estructura jerárquica obligatoria: datos del emisor, datos del adquirente (con RUC válido), tipo de operación (Catálogo 51), desglose de tributos (`cac:TaxTotal`), líneas de detalle (`cac:InvoiceLine`), y forma de pago (Contado o Crédito con cuotas y fechas de vencimiento, según RS 193-2020).

#### `RF-CPE-002` - Generación de Boletas de Venta Electrónicas (Tipo 03) en UBL 2.1
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe generar el archivo XML de Boleta de Venta Electrónica bajo la especificación OASIS UBL 2.1 (`Invoice`).
* **Criterios de Aceptación**: Si el importe total es igual o superior a S/ 700.00, el XML debe contener obligatoriamente el número de documento de identidad del adquirente (DNI/CE/Pasaporte).

#### `RF-CPE-003` - Generación de Notas de Crédito (Tipo 07) y Notas de Débito (Tipo 08)
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe generar documentos XML bajo el estándar UBL 2.1 para Notas de Crédito (`CreditNote`) y Notas de Débito (`DebitNote`), vinculándolas al comprobante de pago electrónico de referencia.
* **Criterios de Aceptación**: Se debe registrar el tipo de nota según los Catálogos N° 09 (Notas de Crédito: anulación de la operación, anulación por error en RUC, corrección por error en descripción, descuento global, etc.) o N° 10 (Notas de Débito: intereses por mora, aumento de valor) y el motivo sustentatorio.

#### `RF-CPE-004` - Firma Digital del Documento XML mediante Certificado X.509
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El sistema debe incorporar un módulo criptográfico capaz de aplicar la firma digital estándar XMLDSig (XML Digital Signature) sobre el documento UBL 2.1 antes de su transmisión.
* **Criterios de Aceptación**: La firma debe utilizar un certificado digital tributario válido emitido por una entidad de certificación acreditada por INDECOPI, incorporando el elemento `ds:Signature` y calculando el resumen de firma (DigestValue / Hash SHA-256).

#### `RF-CPE-005` - Generación de la Representación Impresa (PDF) con Código QR
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: El sistema debe renderizar la representación gráfica del comprobante en formato PDF (tamaño ticket para mostrador y A4 para ventas mayoristas).
* **Criterios de Aceptación**: El PDF debe incorporar el código de barras bidimensional QR que contenga los datos exigidos por SUNAT (RUC emisor, tipo de comprobante, serie, número, IGV, total, fecha, tipo doc adquirente, número adquirente, digest value) y el texto legal oficial de autorización fiscal.

#### `RF-CPE-006` - Generación de Resúmenes Diarios de Boletas y Comunicaciones de Baja
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: Soportar la generación de documentos XML de resumen de boletas de venta (`SummaryDocuments`) y comunicaciones de baja (`VoidedDocuments`) conforme al estándar UBL 2.1 para la notificación agrupada o anulación de comprobantes cuando aplique la modalidad de envío por resumen.
* **Criterios de Aceptación**: Los resúmenes deben agruparse con correlativo diario (`RC-YYYYMMDD-NNNNN` y `RA-YYYYMMDD-NNNNN`) y enviarse dentro de los plazos reglamentarios.

#### `RF-CPE-007` - Generación de Guías de Remisión Electrónica Remitente (Tipo 09)
* **Clasificación**: `[Propuesta de Diseño]`
* **Descripción**: Generar la Guía de Remisión Electrónica (GRE Remitente) en formato UBL 2.1 (`DespatchAdvice`) para amparar el traslado de mercaderías entre almacenes propios o hacia clientes mayoristas.
* **Criterios de Aceptación**: Cumplir con la obligatoriedad de emisión de GRE según la RS 123-2022/SUNAT y sus modificatorias.

---

### 4.6. Módulo 6: Integración con Servicios SUNAT (SUN)

#### `RF-SUN-001` - Envío de Comprobantes a Web Services de SUNAT (`sendBill`)
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe empaquetar el archivo XML firmado dentro de un contenedor ZIP (con nomenclatura oficial `RUC-TIPO-SERIE-CORRELATIVO.zip`) y remitirlo mediante protocolo SOAP/WSDL seguro (HTTPS con credenciales SOL de producción/homologación) a los servidores de SUNAT.
* **Criterios de Aceptación**: La invocación debe completarse de forma asíncrona o no bloqueante para no congelar las interfaces de usuario.

#### `RF-SUN-002` - Recepción, Descompresión y Procesamiento del CDR
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe recibir la respuesta del Web Service de SUNAT, consistente en un archivo ZIP con la Constancia de Recepción (CDR con nombre `R-RUC-TIPO-SERIE-CORRELATIVO.zip`), extraer el XML de respuesta, validar la firma de SUNAT y registrar el estado del comprobante.
* **Criterios de Aceptación**: El estado del comprobante debe actualizarse a:
  * `ACEPTADO`: Código de respuesta 0.
  * `ACEPTADO CON OBSERVACIONES`: Código 0 con códigos de advertencia en el CDR.
  * `RECHAZADO`: Código de error retornado por SUNAT (100 a 3999), conservando la descripción exacta del rechazo.

#### `RF-SUN-003` - Consulta de Estado de Tickets (`getStatus`)
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: Para envíos masivos o resúmenes diarios procesados mediante el método `sendSummary`, el sistema debe capturar el número de ticket generado y consultar periódicamente su estado hasta obtener el CDR final.
* **Criterios de Aceptación**: Debe implementarse un mecanismo de sondeo controlado (polling) con límite de reintentos e intervalos de espera de al menos 10 segundos para no saturar los servicios tributarios.

#### `RF-SUN-004` - Manejo de Contingencias y Falla de Conectividad
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: Si el servicio de SUNAT no se encuentra disponible (errores HTTP 5xx, timeouts o código de excepción SOAP), el sistema debe almacenar el comprobante en una cola de envíos pendientes con estado `EMITIDO_PENDIENTE_ENVIO`.
* **Criterios de Aceptación**: Los comprobantes pendientes deben reintentarse automáticamente mediante un patrón de reintentos exponenciales con límite de tiempo, asegurando que se transmitan antes de que venza el plazo máximo de 3 días calendario.

---

### 4.7. Módulo 7: Migración de Datos Históricos (MIG)

#### `RF-MIG-001` - Extracción, Transformación y Carga (ETL) de Maestros
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe proveer scripts o servicios especializados para ingerir datos maestros provenientes del sistema legado (archivos CSV, hojas de cálculo o volcados de base de datos) transformándolos al modelo de dominio unificado.
* **Criterios de Aceptación**: Se debe validar la unicidad de códigos de producto, unicidad y formato de RUC/DNI, y homologación de unidades de medida antes de la inserción definitiva.

#### `RF-MIG-002` - Carga y Conciliación de Saldos Iniciales de Inventario
* **Clasificación**: `[Explícito]`
* **Descripción**: Proceso de migración que permita ingresar los saldos de apertura de stock por producto y por almacén a una fecha de corte determinada.
* **Criterios de Aceptación**: La carga generará la transacción inicial del Kardex (`SALDO_INICIAL`) con su respectivo costo unitario de apertura. Se emitirá un reporte comparativo entre el saldo reportado por el sistema anterior y el saldo cargado en PostgreSQL, requiriendo firma digital o aprobación del Administrador (`ACT-01`).

#### `RF-MIG-003` - Repositorio de Consulta de Histórico de Ventas y Movimientos Legados
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe almacenar o permitir consultar las transacciones de ventas y movimientos de inventario de periodos anteriores para fines de auditoría y análisis histórico.
* **Criterios de Aceptación**: Los datos históricos transaccionales migrados tendrán estado de solo lectura (`READ-ONLY`) para prevenir alteraciones de registros de ejercicios fiscales cerrados.

---

### 4.8. Módulo 8: Validación, Auditoría y Trazabilidad (TRZ)

#### `RF-TRZ-001` - Trazabilidad Integral Venta - Inventario - Comprobante - CDR
* **Clasificación**: `[Explícito]`
* **Descripción**: El sistema debe asegurar que para cualquier comprobante electrónico emitido sea posible rastrear: la venta comercial originaria, el cliente adquirente, los ítems y lotes despachados, el movimiento de Kardex asociado, el archivo XML firmado emitido, el CDR recibido de SUNAT y la huella digital (DigestValue).
* **Criterios de Aceptación**: Toda consulta de auditoría debe permitir la navegación bidireccional desde el comprobante hacia el almacén o desde el movimiento de stock hacia el comprobante fiscal.

#### `RF-TRZ-002` - Validación Sintáctica y Semántica Previa a la Persistencia
* **Clasificación**: `[Explícito]`
* **Descripción**: Antes de persistir cualquier venta o comprobante, el sistema debe ejecutar validaciones de integridad referencial, consistencia aritmética (suma de líneas = total, cálculo de bases imponibles) y correspondencia de tipos de comprobante y series.
* **Criterios de Aceptación**: Si una validación falla, la transacción debe abortarse en su totalidad (rollback transaccional ACID) sin dejar registros huérfanos.

#### `RF-TRZ-003` - Registro Inmutable de Auditoría (Audit Log)
* **Clasificación**: `[Inferencia Razonable]`
* **Descripción**: Registro detallado de eventos críticos del sistema (creación de usuarios, cambios de precios, ajustes de inventario, anulación de pedidos, transmisiones a SUNAT), registrando marca temporal en UTC, dirección IP, usuario ejecutor y estado anterior/posterior del registro.
* **Criterios de Aceptación**: La tabla de logs de auditoría en PostgreSQL debe contar con políticas de seguridad que impidan la actualización (`UPDATE`) o eliminación (`DELETE`) de registros, incluso por parte de usuarios administradores de la aplicación.

---

## 5. Requerimientos No Funcionales (RNF)

| Código | Categoría | Requerimiento No Funcional | Métrica / Criterio Verificable |
| :--- | :--- | :--- | :--- |
| **RNF-PER-001** | Rendimiento (Performance) | Tiempo de respuesta en Punto de Venta Retail | La finalización de una venta en mostrador y generación preliminar del comprobante debe responder en un percentil 95 ($P_{95}$) $\le 1.5$ segundos. |
| **RNF-PER-002** | Rendimiento (Performance) | Procesamiento criptográfico de firma XMLDSig | La generación y firma digital de un archivo XML UBL 2.1 no debe tardar más de 350 milisegundos por documento. |
| **RNF-SEC-001** | Seguridad y Criptografía | Custodia del Certificado Digital | El certificado digital tributario (formato PKCS#12 `.p12`/`.pfx`) y su clave privada deben residir cifrados en almacenamiento seguro con claves administradas fuera del código fuente. |
| **RNF-SEC-002** | Seguridad y Criptografía | Control de Acceso Basado en Roles (RBAC) | Todas las operaciones del sistema deben estar protegidas mediante autenticación robusta y autorización estricta por roles y privilegios granulares. |
| **RNF-SEC-003** | Seguridad y Criptografía | Cifrado en Tránsito y Reposo | Comunicación externa hacia SUNAT vía HTTPS TLS 1.2 o superior. Las conexiones a la base de datos PostgreSQL deben exigir cifrado SSL/TLS. |
| **RNF-REL-001** | Confiabilidad (Reliability) | Resiliencia ante caídas de Web Services externos | El sistema debe implementar el patrón *Transactional Outbox* o colas persistentes para desacoplar la venta del envío a SUNAT, garantizando cero pérdida de comprobantes. |
| **RNF-REL-002** | Confiabilidad (Reliability) | Idempotencia en la Transmisión Fiscal | El reintento de envío de un comprobante a SUNAT debe ser estrictamente idempotente para evitar envíos duplicados o inconsistencias de estado. |
| **RNF-MNT-001** | Mantenibilidad | Desacoplamiento Arquitectónico Hexagonal | El núcleo de lógica de negocio (dominio) debe carecer por completo de dependencias de frameworks web, librerías de persistencia directa o SDKs específicos de SUNAT. |
| **RNF-MNT-002** | Mantenibilidad | Enfoque Spec-First y Contratos Tipados | Todos los servicios de aplicación y adaptadores deben regirse por contratos formales (OpenAPI / JSON Schema / DTOs tipados) antes de su implementación de código. |
| **RNF-CMP-001** | Cumplimiento Normativo | Cumplimiento Estricto OASIS UBL 2.1 | Los documentos XML emitidos deben superar sin errores las validaciones de esquemas XSD oficiales de OASIS y las reglas de validación Schematron publicadas por SUNAT. |
| **RNF-DAT-001** | Integridad de Datos | Propiedades ACID en PostgreSQL | Toda operación de inventario o venta debe ejecutarse bajo niveles de aislamiento transaccional adecuados (`Read Committed` o `Serializable`) garantizando consistencia absoluta de saldos. |
| **RNF-AUD-001** | Auditabilidad | Inmutabilidad de Documentos Tributarios | Una vez generado el XML firmado y obtenido el CDR, ambos artefactos binarios deben almacenarse de forma inmutable, garantizando su integridad mediante hash SHA-256. |

---

## 6. Restricciones Técnicas y Arquitectónicas

1. **Restricción de Fase Inicial (Golden Rule)**: Durante la fase actual de análisis y especificación arquitectónica, queda terminantemente prohibido generar código ejecutable en `src/`, inicializar aplicaciones web, instalar librerías/dependencias o aprovisionar bases de datos en caliente. Todo el avance se fundamenta en especificaciones y contratos formales.
2. **Patrón Arquitectónico Dual (MVC + Hexagonal)**:
   * El sistema debe concebirse articulando el patrón **Modelo-Vista-Controlador (MVC)** con los principios de la **Arquitectura Hexagonal (Ports & Adapters)**.
   * Los Controladores del MVC actuarán como *Adaptadores Primarios (Driving Adapters)* que reciben solicitudes de usuarios o clientes API.
   * La lógica de negocio y las entidades residen en el *Núcleo Hexagonal (Domain & Application Services)*, totalmente aisladas de frameworks.
   * El acceso a base de datos y la comunicación con SUNAT operan como *Adaptadores Secundarios (Driven Adapters)* que implementan *Puertos (Ports)* definidos por el dominio.
3. **Motor de Base de Datos**: Obligatoriamente **PostgreSQL** (versión 15 o superior recomendada), aprovechando sus capacidades de transaccionalidad ACID, tipos de datos estructurados (JSONB para metadatos o esquemas semiestructurados) y funciones de integridad referencial avanzada.
4. **Estándar de Documentos Electrónicos**: Los comprobantes de pago deben construirse bajo la especificación **Universal Business Language (OASIS UBL) versión 2.1**, cumpliendo estrictamente los catálogos y formatos exigidos por la legislación de la SUNAT.
5. **Enfoque Spec-First**: No se admite desarrollo sin especificación aprobada previa (`specs/features/`), acompañada de registros de decisiones arquitectónicas (`specs/architecture/adrs/`) y esquemas formales de API (`specs/api/`).

---

## 7. Reglas de Negocio

### 7.1. Reglas de Negocio Confirmadas (Normativas y Operativas)

* **`RN-01: Inmutabilidad de Comprobantes con CDR Aceptado`**: Un comprobante electrónico (Factura o Boleta) cuyo XML haya sido firmado y cuente con CDR de aceptación por parte de SUNAT no puede ser modificado, recalculado ni eliminado de la base de datos bajo ninguna circunstancia. Toda modificación cuantitativa o cualitativa posterior debe realizarse exclusivamente mediante la emisión de una Nota de Crédito o Nota de Débito electrónica.
* **`RN-02: Correlatividad y Series por Tipo de Comprobante`**: Las series deben respetar estrictamente el formato alfanumérico reglamentario de SUNAT:
  * Facturas: Serie que inicia con `F` (ej. `F001`, `F002`).
  * Boletas de Venta: Serie que inicia con `B` (ej. `B001`, `B002`).
  * Notas de Crédito sobre Facturas: Serie que inicia con `FC` o `F` (ej. `FC01`).
  * Notas de Crédito sobre Boletas: Serie que inicia con `BC` o `B` (ej. `BC01`).
  * La numeración correlativa dentro de cada serie debe ser secuencial, estrictamente ascendente, sin huecos ni duplicados.
* **`RN-03: Validación Obligatoria del Documento del Adquirente`**:
  * Para emitir una **Factura Electrónica (01)**, el cliente adquirente debe identificarse obligatoriamente con **RUC de 11 dígitos** (Catálogo 06, Tipo 6), con estado tributario activo.
  * Para emitir una **Boleta de Venta (03)** por importe total igual o superior a **S/ 700.00**, es obligatorio registrar los nombres completos y el documento de identidad (DNI, CE o Pasaporte) del comprador. Para importes menores, la consignación del documento es facultativa a solicitud del cliente.
* **`RN-04: Plazo Máximo Legal de Envío a SUNAT`**: Las Facturas Electrónicas y sus respectivas notas deben enviarse a la SUNAT o al OSE en un plazo máximo de hasta **3 días calendario** contados desde el día siguiente a la fecha de emisión (conforme a la Resolución de Superintendencia N° 193-2020/SUNAT y RS 000003-2023/SUNAT). Los comprobantes remitidos fuera de dicho plazo carecerán de validez tributaria y serán rechazados por la SUNAT.
* **`RN-05: Restricción de Kardex Negativo`**: En condiciones normales de operación, el sistema debe impedir la confirmación de salidas de inventario que provoquen un saldo negativo en el stock físico de un producto en el almacén de despacho.
* **`RN-06: Identificación de Afectación al IGV por Línea`**: Cada ítem en una venta debe contar con su correspondiente código de afectación al IGV (Catálogo N° 07 de SUNAT). Un mismo comprobante puede mezclar ítems gravados (18%), exonerados (0% por Ley de la Amazonía o Apéndice I) o inafectos, acumulando las bases imponibles en sus respectivos tags tributarios UBL.

### 7.2. Reglas de Negocio Pendientes de Confirmación (Preguntas Abiertas)

* **`RN-PEND-01: Método de Valoración de Inventarios`**:
  * *Interrogante*: ¿Qué método de valuación de existencias exigirá la contabilidad de la empresa: **Costo Promedio Ponderado** o **Primeras Entradas, Primeras Salidas (PEPS / FIFO)**?
  * *Impacto Arquitectónico*: El cálculo del Kardex valorizado difiere sustancialmente en la complejidad de las estructuras de datos y consultas en PostgreSQL si se manejan capas de costos (PEPS) frente a un promedio acumulado.
* **`RN-PEND-02: Alcance Obligatorio del Control de Lotes y Vencimientos`**:
  * *Interrogante*: ¿El control de lotes y fechas de vencimiento será obligatorio para el 100% de los productos, o únicamente para categorías perecibles seleccionadas?
  * *Impacto Arquitectónico*: Determina si las tablas de movimiento de stock (`stock_movement`) deben exigir obligatoriamente una clave foránea hacia la tabla de lotes (`inventory_lot`).
* **`RN-PEND-03: Modalidad de Emisión Electrónica (SEE Directo vs. OSE)`**:
  * *Interrogante*: ¿La empresa transmitirá sus comprobantes directamente a los Web Services de la SUNAT como **SEE-Del Contribuyente**, o está obligada / optará por contratar un **Operador de Servicios Electrónicos (OSE)**?
  * *Impacto Arquitectónico*: Si bien el estándar UBL 2.1 es idéntico, los endpoints WSDL, las políticas de reintentos y los plazos de respuesta difieren entre SUNAT y las plataformas OSE autorizadas.
* **`RN-PEND-04: Modalidad de Envío de Boletas de Venta (Individual vs. Resumen Diario)`**:
  * *Interrogante*: ¿Las Boletas de Venta Electrónicas se transmitirán individualmente en tiempo real (`sendBill`) o se agruparán en un Resumen Diario de Boletas (`sendSummary`) al cierre del día?
  * *Impacto Arquitectónico*: En entornos de retail masivo, el envío por resumen diario ahorra consumo de ancho de banda y reduce la dependencia de la conectividad en caja, pero requiere un proceso batch nocturno programado.
* **`RN-PEND-05: Profundidad y Ventana Temporal de la Migración Histórica`**:
  * *Interrogante*: ¿La migración desde el sistema legado abarcará únicamente catálogos maestros y **saldos iniciales de stock a la fecha de corte**, o se requerirá migrar el detalle transaccional de ventas y compras de los últimos ejercicios fiscales cerrados?
  * *Impacto Arquitectónico*: Afecta el dimensionamiento del almacenamiento en PostgreSQL, la complejidad del pipeline ETL y la necesidad de esquemas de conciliación histórica.
* **`RN-PEND-06: Política de Contingencia ante Caída de Conexión en Puntos de Venta`**:
  * *Interrogante*: ¿En caso de pérdida total de internet en una tienda minorista, el cajero continuará vendiendo mediante comprobantes preimpresos físicos de contingencia autorizados por SUNAT, o el sistema local operará en modo offline con sincronización posterior?
  * *Impacto Arquitectónico*: Determina si los puestos de venta requieren almacenamiento local autónomo (SQLite / PWA / Base local) o si se diseñará como un sistema centralizado web con tolerancia a fallas de comunicación con SUNAT.

---

## 8. Incertidumbres y Ambigüedades en el Planteamiento

Al analizar exhaustivamente el enunciado del examen, se identifican las siguientes ambigüedades técnicas y vacíos conceptuales que deben ser mitigados durante el diseño:

1. **Articulación de MVC con Arquitectura Hexagonal**:
   * *Ambigüedad*: Tradicionalmente, MVC y Arquitectura Hexagonal son paradigmas con límites diferentes. MVC suele asociarse a aplicaciones web monolíticas acopladas a la presentación y la base de datos (ActiveRecord), mientras que la Arquitectura Hexagonal aísla por completo el dominio del framework.
   * *Mitigación Arquitectónica*: Se define que **MVC constituirá el Adaptador de Entrada (Driving Adapter)**. El Controlador MVC recibe el payload HTTP, lo valida a nivel de formato, invoca al Caso de Uso del Núcleo Hexagonal y la Vista/Presentador formatea la respuesta (JSON DTO o vista de interfaz), garantizando que el Modelo no sea una entidad de base de datos directa sino un agregado de dominio.
2. **Alcance de Documentos Electrónicos Adicionales (Guías de Remisión)**:
   * *Ambigüedad*: El enunciado menciona facturas y boletas, pero al tratarse de venta mayorista con despacho y movimiento multialmacén, la normativa peruana exige obligatoriamente la emisión de **Guías de Remisión Electrónica (GRE)**.
   * *Mitigación Arquitectónica*: Se incluye en este análisis la GRE como propuesta de diseño modular para que la arquitectura contemple los puertos de emisión de comprobantes de traslado sin romper el modelo.
3. **Mecanismo de Desacoplamiento de Servicios SUNAT**:
   * *Ambigüedad*: El planteamiento no especifica cómo evitar que los tiempos de respuesta erráticos o caídas frecuentes de los servidores de la SUNAT bloqueen la finalización de una venta comercial en mostrador.
   * *Mitigación Arquitectónica*: Se propone la adopción del patrón **Transactional Outbox**, donde la venta y el documento fiscal se persisten localmente con éxito inmediato y un proceso asíncrono gestiona la firma, compresión y transmisión al Web Service de SUNAT.

---

## 9. Matriz de Trazabilidad Inicial

A continuación se vinculan las necesidades del negocio expuestas en el problema con los requerimientos funcionales, requerimientos no funcionales y su nivel de certeza inicial:

| ID Necesidad | Necesidad del Negocio | Requerimientos Asociados | Tipo | Nivel de Certeza | Método de Verificación |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **NN-01** | Control y visibilidad de existencias en múltiples locales | `RF-ALM-001`, `RF-ALM-002`, `RF-ALM-005` | Funcional | `Confirmado` | Pruebas de integración de saldos por almacén y transferencias. |
| **NN-02** | Historial fidedigno e inmutable de movimientos de stock | `RF-ALM-002`, `RF-ALM-003`, `RF-ALM-004`, `RN-05` | Funcional / Regla | `Confirmado` | Generación y balance de Kardex físico/valorizado. |
| **NN-03** | Diferenciación comercial entre venta mayorista y retail | `RF-CAT-002`, `RF-VEN-001`, `RF-VEN-002`, `RF-DIR-003` | Funcional | `Confirmado` | Escenarios de venta en mostrador vs pedidos con crédito. |
| **NN-04** | Cumplimiento fiscal de emisión electrónica bajo UBL 2.1 | `RF-CPE-001` a `RF-CPE-004`, `RNF-CMP-001` | Funcional / Técnico | `Confirmado` | Validación de XML contra XSD de OASIS y Schematron de SUNAT. |
| **NN-05** | Integración y validación con los servicios de SUNAT | `RF-SUN-001`, `RF-SUN-002`, `RF-SUN-004`, `RN-04` | Funcional / Regla | `Confirmado` | Simulación de intercambio SOAP con Web Services de Homologación SUNAT y recepción de CDR. |
| **NN-06** | Continuidad operativa migrando datos del sistema anterior | `RF-MIG-001`, `RF-MIG-002`, `RF-MIG-003` | Funcional / Técnico | `Confirmado` | Pruebas de ETL y reporte de conciliación de saldos iniciales de inventario. |
| **NN-07** | Trazabilidad cruzada de operaciones y auditoría interna | `RF-TRZ-001`, `RF-TRZ-002`, `RF-TRZ-003`, `RNF-AUD-001` | Funcional / RNF | `Confirmado` | Consulta de pistas de auditoría y enlace comprobante-movimiento. |
| **NN-08** | Desacoplamiento de componentes y facilidad de evolución | `RNF-MNT-001`, `RNF-MNT-002`, Restricción Hexagonal | Arquitectónico | `Confirmado` | Inspección de dependencias en el código y testabilidad aislada sin BD. |
| **NN-09** | Robustez y consistencia transaccional de datos | `RNF-DAT-001`, Restricción PostgreSQL | Arquitectónico | `Confirmado` | Pruebas de concurrencia y transacciones ACID en PostgreSQL. |
| **NN-10** | Alta disponibilidad en ventas ante fallas de SUNAT | `RF-SUN-005`, `RNF-REL-001`, `RNF-REL-002` | Arquitectónico | `Inferencia` | Pruebas de contingencia con corte deliberado de conexión hacia SUNAT. |
| **NN-11** | Trazabilidad sanitaria y control de caducidad en abarrotes | `RF-CAT-004`, `RN-PEND-02` | Negocio | `Propuesta` | Verificación de avisos de vencimiento y bloqueo de despacho por lote caduco. |

---

## 10. Conclusiones y Próximos Pasos

El presente análisis de requisitos establece el marco de referencia formal para el desarrollo del examen de Arquitectura de Software. Se ha delimitado con precisión el alcance, distinguiendo las demandas explícitas de las inferencias operativas y las propuestas arquitectónicas, sustentando cada elemento tributario en la normativa formal de la SUNAT y el estándar OASIS UBL 2.1.

### Próxima Fase (Fase 2 - Pendiente de Autorización):
* **Modelado del Dominio y Contratos de Datos (DTOs/Schemas)**:
  * Definición de Entidades de Dominio, Agregados y Value Objects (DDD).
  * Elaboración de contratos de interfaz (Ports) para persistencia y servicios externos.
  * Definición de DTOs y esquemas de validación de comprobantes y movimientos bajo enfoque Spec-First.
