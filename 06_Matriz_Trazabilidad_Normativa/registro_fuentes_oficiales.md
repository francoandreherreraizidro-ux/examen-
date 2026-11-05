# Registro de fuentes oficiales y documentales

| Metadato | Valor |
|---|---|
| Entregable | Registro de fuentes normativas y técnicas |
| Estado | `DRAFT` — fuentes primarias consultadas; vigencia consolidada pendiente |
| Fecha de consulta | 2026-11-04 |

Este registro separa documentos oficiales primarios de los documentos internos del proyecto. Las resoluciones consultadas son originales/modificatorias alojadas por SUNAT; no se obtuvo un texto único consolidado al 2026-11-04. La existencia de una URL oficial no prueba que una regla aislada continúe vigente sin modificaciones.

## Fuentes normativas SUNAT

### SUN-01 — Resolución de Superintendencia N.° 117-2017/SUNAT

- **Título:** Resolución de Superintendencia que aprueba el nuevo Sistema de Emisión Electrónica Operador de Servicios Electrónicos (SEE-OSE).
- **Emisor:** SUNAT.
- **Fecha del documento:** 9 de mayo de 2017.
- **Versión:** texto original de RS 117-2017/SUNAT; no es una compilación consolidada.
- **URL oficial:** https://www.sunat.gob.pe/legislacion/superin/2017/117-2017.pdf
- **Ubicaciones utilizadas:**
  - Art. 1.18: formato XML/UBL y transición de versiones.
  - Art. 7.4: obligación del OSE de comprobar informáticamente condiciones y emitir la respuesta correspondiente.
  - Art. 16.1–16.2: CDR/comunicación de inconsistencias y referencia a la información técnica aplicable.
  - Art. 39.1 literales a–j: anexos de RS 097-2012 referidos por el régimen OSE y Anexo 10 como estándar UBL 2.1.
- **Acceso/lectura:** PDF descargado desde el dominio oficial y extraído con `pdftotext -layout`; se revisaron las ubicaciones citadas.
- **Límite:** no se revisaron todas las modificaciones posteriores de la resolución. Los artículos sobre SEE-OSE no se generalizan a SEE-SOL u otro SEE.

### SUN-02 — Resolución de Superintendencia N.° 193-2020/SUNAT

- **Título:** Modifican la normativa sobre emisión electrónica en relación con la factura electrónica, el recibo por honorarios electrónico y la nota de crédito electrónica.
- **Emisor:** SUNAT.
- **Fecha del documento:** 5 de noviembre de 2020.
- **Vigencia indicada en el texto consultado:** 1 de abril de 2021.
- **Versión:** texto modificatorio; no es una compilación consolidada de las resoluciones modificadas.
- **URL oficial:** https://www.sunat.gob.pe/legislacion/superin/2020/193-2020.pdf
- **Ubicaciones utilizadas:**
  - Art. 2.1: modifica RS 188-2010/SUNAT, art. 9.1 literal s, para forma de pago de factura en el sistema al que esa resolución se refiere.
  - Art. 3.1: modifica RS 097-2012/SUNAT, art. 12.1 literal b, sobre remisión de factura y nota vinculada en el SEE del contribuyente.
  - Art. 5.1: modifica RS 117-2017/SUNAT, art. 15.1 literales a–b, sobre remisión al OSE y plazo correspondiente.
  - Disposición Complementaria Final Única: vigencia.
- **Acceso/lectura:** PDF descargado desde SUNAT y extraído con `pdftotext -layout`; se revisó el texto de las modificaciones citadas.
- **Límite:** se registran reglas según el texto modificatorio y su sistema de emisión. La regla de SEE-SOL no se extrapola a otros sistemas. No se comprobó la vigencia consolidada ni las modificaciones posteriores.

### SUN-03 — Resolución de Superintendencia N.° 097-2012/SUNAT

- **Título de referencia:** Sistema de emisión electrónica desarrollado desde los sistemas del contribuyente (SEE del contribuyente).
- **Emisor:** SUNAT.
- **Fecha:** 2012 (la fecha exacta de publicación no se usa en la matriz).
- **Versión:** original y modificada por normas posteriores; el archivo enlazado no representa texto consolidado.
- **URL oficial:** https://www.sunat.gob.pe/legislacion/superin/2012/097-2012.pdf
- **Ubicación relacionada:** art. 12.1 literal b, citado y modificado por RS 193-2020/SUNAT, art. 3.1; anexos nacionales citados por RS 117-2017/SUNAT, art. 39.1.
- **Acceso/lectura:** la descarga oficial respondió correctamente, pero el PDF consultado está escaneado y la extracción de texto local no produjo texto legible.
- **Límite:** no se atribuye a este archivo ninguna regla que no haya sido verificada en otra fuente textual. Se requiere conseguir el anexo vigente y el texto consolidado antes de cerrar las filas de cumplimiento de SEE del contribuyente.

### SUN-04 — Resolución de Superintendencia N.° 007-99/SUNAT

- **Título:** Reglamento de Comprobantes de Pago.
- **Emisor:** SUNAT.
- **Publicación indicada en el documento:** 24 de enero de 1999; entrada en vigencia indicada: 1 de febrero de 1999.
- **Versión:** reglamento original alojado por SUNAT; no es el texto consolidado.
- **URL oficial:** https://www.sunat.gob.pe/legislacion/superin/1999/007.pdf
- **Acceso/lectura:** el archivo se descargó, pero el texto extraído solo permitió revisar la carátula/índice general en esta inspección.
- **Uso en este entregable:** referencia legal general para revisión posterior; no se usó para afirmar requisitos de campos ni plazos en la matriz.
- **Pendiente:** revisar texto consolidado, artículos pertinentes y modificaciones antes de definir condiciones de emisión específicas.

## Fuente técnica OASIS

### UBL-01 — OASIS Universal Business Language Version 2.1

- **Título:** Universal Business Language Version 2.1.
- **Organización:** OASIS.
- **Versión/estado:** UBL 2.1, OASIS Standard.
- **Fecha de publicación del estándar:** 4 de noviembre de 2013.
- **Especificación:** https://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html
- **XSD del documento Invoice:** https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/maindoc/UBL-Invoice-2.1.xsd
- **XSD de componentes básicos (referencia complementaria):** https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/common/UBL-CommonBasicComponents-2.1.xsd
- **Ubicaciones utilizadas:** XSD Invoice 2.1, elemento global `Invoice`, tipo `InvoiceType` y declaraciones de `cbc:ID`, `cbc:IssueDate`, `cac:AccountingSupplierParty`, `cac:AccountingCustomerParty`, `cac:InvoiceLine`, `cac:TaxTotal` y `cac:LegalMonetaryTotal`.
- **Acceso/lectura:** página de especificación y XSD consultados directamente; el XSD Invoice se descargó desde OASIS para revisar declaraciones y cardinalidades.
- **Límite:** el esquema es genérico y no implementa por sí solo el perfil peruano, reglas tributarias, catálogos SUNAT, firma, transporte o aceptación.

## Documentos internos contrastados

Estos archivos son evidencia del alcance/decisiones del proyecto, no fuentes normativas:

- [Análisis de requisitos](../01_Analisis_Requisitos/analisis_requisitos.md): RF-CPE-001, RF-SUN-001, RF-VEN-002, RF-VAL-001, RF-TRZ-001 y RNF-DOC-001.
- [Revisión final de contratos API](../05_Contratos_API/revision_final_contratos_api.md): estado DRAFT y brechas API/DTO.
- [Especificación de contratos API](../05_Contratos_API/especificacion_contratos_api.md) y [OpenAPI](../specs/api/sistema-comercial.openapi.yaml): rutas/operaciones y estados actuales.
- [Especificación DTO](../03_DTOs_Schemas/dtos_schemas.md), [JSON Schema](../03_DTOs_Schemas/dto-contracts.schema.json) y [mapeo dominio-contratos](../03_DTOs_Schemas/modelo_dominio_contratos.md): campos existentes y ausentes.
- [Modelo de dominio](../02_Modelo_Dominio/modelo_dominio.md): entidades y asociaciones conceptuales.
- [Diagramas de arquitectura](../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md): versión reconciliada con el análisis vigente y marcada `DRAFT`; las modalidades y reglas no confirmadas se conservan como pendientes. [ADR-0002](../specs/architecture/adrs/0002-arquitectura-hexagonal-mvc.md) y [ADR-0003](../specs/architecture/adrs/0003-persistencia-postgresql-kardex.md) fueron revisadas y están `PROPOSED`; el [informe de revisión](../specs/architecture/revision_ADR_0002_0003.md) detalla lo expreso y lo pendiente.
