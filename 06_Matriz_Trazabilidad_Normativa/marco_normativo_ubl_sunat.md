# Marco normativo y técnico para UBL 2.1 y SUNAT

| Metadato | Valor |
|---|---|
| Entregable | Matriz de trazabilidad normativa SUNAT y UBL 2.1 |
| Estado | `DRAFT` — investigación de diseño; no certifica cumplimiento tributario |
| Contrato API relacionado | `API-SISTEMA-COMERCIAL` v0.1.0, `DRAFT` |
| Requisitos base | Análisis de requisitos vigente |
| Fecha de consulta documental | 2026-11-04 |

## 1. Propósito y límites

Este documento organiza las reglas legales y técnicas que pueden vincularse con el alcance actualmente documentado, y las brechas entre esas reglas y los contratos preliminares. La [matriz CSV](./matriz_trazabilidad_normativa.csv) registra cada regla, su fuente/ubicación, entidad, DTO, endpoint, elemento XML, validación y prueba propuesta.

El análisis no es una opinión legal ni una certificación. La existencia de un XML que valide contra el XSD genérico de OASIS UBL 2.1 no demuestra que:

- se haya aplicado el perfil o los anexos técnicos SUNAT vigentes;
- los datos fiscales, catálogos, cálculos, fechas o reglas de emisión sean correctos;
- el documento se haya transmitido dentro del plazo aplicable;
- SUNAT u OSE haya recibido, validado o aceptado el documento.

Se separan cuatro clases de evidencia:

1. **Obligación normativa SUNAT:** texto de una resolución oficial consultada. Su aplicabilidad depende del tipo de comprobante, sistema de emisión y vigencia consolidada.
2. **Restricción XSD OASIS:** cardinalidad/tipo estructural del esquema publicado; no es una obligación legal peruana por sí sola.
3. **Regla de negocio o mapeo pendiente:** requisito que el esquema o las fuentes consultadas no resuelven.
4. **Decisión interna de arquitectura:** propuesta del contrato, como idempotencia o el flujo de confirmación, que no debe atribuirse a SUNAT.

## 2. Alcance real y elementos trazables

El análisis vigente contiene:

- `RF-CPE-001`: contempla emisión electrónica y UBL Invoice 2.1, pero deja por confirmar los tipos de comprobantes.
- `RF-SUN-001`: solicita integración con servicios SUNAT, sin especificar modalidad, interfaz, autenticación, ambiente ni operación.
- `RF-VEN-002`: relación venta-documento inferida, pendiente de confirmación.
- `RF-VAL-001` y `RF-TRZ-001`: validación y trazabilidad generales, sin criterios fiscales concretos.
- `RNF-DOC-001`: considerar UBL Invoice 2.1, sin declarar conformidad normativa.

La API actual sólo propone `POST /api/v1/comprobantes` y `GET /api/v1/comprobantes/{comprobanteId}`. El DTO de solicitud (`SolicitarEmisionComprobanteDto`) sólo tiene `ventaId` y `tipoDocumento`; el DTO de respuesta (`ComprobanteDto`) contiene estado local y algunos identificadores opcionales. No existen un DTO fiscal de factura, un DTO de partes UBL, un DTO de línea de factura, un artefacto XML ni una operación de transmisión/consulta remota definida.

Por ello, la matriz puede trazar la intención de emisión y las estructuras genéricas de Invoice, pero no cerrar un mapeo campo-a-campo de datos SUNAT. En el modelo conceptual se relacionan `ComprobanteElectronico`, `Venta`, `DetalleVenta`, `Producto` y `Cliente`; no existe todavía una entidad/configuración de emisor tributario.

### Alcance documental provisional

- Se considera **UBL `Invoice`** porque así lo nombra el requisito `RNF-DOC-001`. No se da por aprobado si el documento emitido será factura, boleta, ambos u otro tipo.
- No se incorporan notas de crédito/débito, guías, recibos, resúmenes ni documentos distintos de Invoice como si fueran parte confirmada del examen.
- Las normas y anexos consultados contienen reglas para diversos sistemas y documentos. Cada fila condicional identifica su ámbito; no se extrapola una regla de SEE-SOL a SEE del contribuyente u OSE.

## 3. Fuentes verificadas y reglas identificadas

Las URLs, título, versión/fecha, ubicación y límites de lectura se detallan en el [registro de fuentes oficiales](./registro_fuentes_oficiales.md).

### 3.1 SUNAT: versión UBL, modalidad y anexos

La Resolución de Superintendencia N.° 117-2017/SUNAT define en su art. 1.18 el formato digital como XML bajo UBL para los documentos allí enumerados y describe la transición de versiones hasta UBL 2.1. Su art. 39.1 literal j identifica el Anexo N.° 10 como estándar UBL 2.1 y expresa su vigencia desde el 1 de julio de 2017. El art. 39.1 también indica que ciertos anexos de la RS 097-2012/SUNAT se aplican al SEE-OSE.

Esto fundamenta que `UBL 2.1` sea una referencia de diseño relevante, pero no basta para decidir qué sistema de emisión corresponde al negocio ni sustituye la consulta del texto consolidado y del anexo nacional vigente. El esquema OASIS genérico tampoco establece todos los elementos/perfiles que exige SUNAT.

### 3.2 SUNAT: remisión y resultado de comprobación

La RS 193-2020/SUNAT modifica reglas distintas según el SEE:

- **SEE del contribuyente:** su art. 3.1 modifica la RS 097-2012/SUNAT, art. 12.1 literal b, estableciendo para factura electrónica y notas vinculadas el envío a SUNAT en la fecha consignada o hasta el día calendario siguiente.
- **SEE-OSE:** su art. 5.1 modifica la RS 117-2017/SUNAT, art. 15.1 literal a, con el plazo de fecha de emisión o hasta el día calendario siguiente para factura y nota vinculada, y señala el efecto del vencimiento en la comprobación material OSE.
- **SEE-SOL:** su art. 2.1 modifica la RS 188-2010/SUNAT, art. 9.1 literal s, respecto de la forma de pago contado/crédito y, para crédito, el monto neto pendiente y los vencimientos/cuotas según corresponda. Esa regla se registra como condicional al SEE-SOL y no se atribuye automáticamente a otro canal.

La RS 117-2017/SUNAT, arts. 7.4 y 16.1–16.2, describe para el OSE la comprobación informática y la emisión de una CDR o comunicación de inconsistencias según las condiciones aplicables. La matriz conserva estos resultados como distintos del estado local de emisión y del resultado XSD. No se asignan códigos de respuesta ni se presume que un `202` de la API signifique aceptación externa.

Las modificaciones descritas se localizaron en el texto oficial de RS 193-2020 consultado; la resolución indica vigencia desde el 1 de abril de 2021. No se verificó un texto consolidado hasta la fecha de consulta ni que no existan modificaciones posteriores. Por ello, los plazos y su aplicación quedan condicionales hasta una revisión normativa actualizada.

### 3.3 OASIS: reglas estructurales de UBL Invoice 2.1

El estándar OASIS UBL 2.1 y el archivo `UBL-Invoice-2.1.xsd` consultados identifican el elemento global `Invoice` como raíz y definen, entre otros, estos mínimos del tipo `InvoiceType`:

- `cbc:ID`: exactamente uno;
- `cbc:IssueDate`: exactamente uno;
- `cac:AccountingSupplierParty`: exactamente uno;
- `cac:AccountingCustomerParty`: exactamente uno;
- `cac:InvoiceLine`: una o más;
- `cac:LegalMonetaryTotal`: exactamente uno.

En el XSD genérico, `cbc:UBLVersionID`, `cbc:InvoiceTypeCode` y `cbc:DocumentCurrencyCode` son opcionales; `cac:TaxTotal`, `cac:PaymentMeans` y `cac:PaymentTerms` también aparecen con cardinalidad opcional/repetible. Un perfil SUNAT puede imponer requisitos adicionales o restricciones de valor. No se interpreta la opcionalidad del XSD base como permiso para omitir campos que el perfil nacional exija.

El sistema debe validar en capas separadas: estructura XML, conformidad con el perfil SUNAT/SEE aplicable, consistencia de dominio/impuestos y respuesta real del servicio. Los mapeos a tributos, catálogo de tipo documental, moneda, datos de las partes y pagos siguen pendientes de reglas nacionales verificadas y requisitos de negocio.

## 4. Lectura de la matriz

La matriz contiene 14 reglas trazables, agrupadas como:

- `SUN-*`: disposiciones de emisión o respuesta SUNAT consultadas, con aplicabilidad condicional al SEE;
- `UBL-*`: restricciones del XSD base de OASIS;
- `INT-*`: decisiones internas del contrato, expresamente no normativas.

Cada fila tiene una validación técnica y una prueba propuesta. Las pruebas no son casos de cumplimiento aprobados: deben recibir valores/oráculos oficiales después de definir el tipo documental, la modalidad de emisión y el perfil tributario. Los campos ausentes de los DTOs se marcan como brecha y no se ocultan asignándoles automáticamente un elemento XML.

## 5. Capas de validación recomendadas (propuesta)

1. **Validación de entrada/API:** tipo y forma del mensaje según DTO y reglas de negocio expresamente acordadas.
2. **Mapeo y generación XML:** conversión determinista de los datos comerciales al perfil UBL/SUNAT seleccionado.
3. **Validación XSD OASIS 2.1:** namespaces, secuencia, tipos y cardinalidades del esquema genérico y sus imports.
4. **Validación del perfil nacional y catálogos:** restricciones de anexos y códigos SUNAT vigentes para modalidad y documento, aún por obtener/confirmar.
5. **Validación semántica y fiscal:** consistencia entre líneas, bases, tributos, moneda y totales, conforme a reglas verificadas; no definida todavía.
6. **Intercambio y respuesta externa:** transmisión, marcas de tiempo, respuesta, CDR/inconsistencias según el SEE elegido; no implementado ni especificado.

Una prueba XSD correcta sólo acredita la capa 3 sobre el esquema concreto usado; no certifica las capas 4–6.

## 6. Revisión de documentos y conflicto arquitectónico

La versión vigente de [diagramas_arquitectura.md](../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md) fue reconciliada con el análisis: ahora está en `DRAFT`, conserva MVC, principios hexagonales y PostgreSQL como restricciones expresas, y no fija protocolos, flujo ni reglas operativas pendientes. Las antiguas propuestas específicas se retiraron de la arquitectura actual; la [matriz regulatoria histórica](../06_Matriz_Trazabilidad_Regulatoria/matriz_trazabilidad_regulatoria.md) se marcó como supersedida y no debe usarse como fuente normativa.

Las ADRs 0002/0003 conservan decisiones de diseño cuyo estado `ACCEPTED` no amplía por sí mismo el alcance aprobado por los requisitos. La ADR-0003 incluye reglas de costo promedio, stock no negativo, montos/precisiones y referencias normativas que el análisis actual no declara aprobadas. Esta matriz no convierte esas afirmaciones en obligaciones; su vigencia y aplicabilidad siguen pendientes de revisión. Toda regla tributaria debe verificarse contra la fuente oficial vigente y el SEE finalmente seleccionado.

## 7. Recomendación

Mantener los contratos y este entregable en `DRAFT`. Antes de aprobar la matriz o diseñar el DTO fiscal:

1. confirmar los tipos de comprobante del alcance (factura, boleta u otros);
2. seleccionar SEE y modalidad (SEE del contribuyente, SEE-OSE, SEE-SOL u otra aplicable);
3. obtener y revisar el texto consolidado y los anexos técnicos vigentes para esa combinación;
4. definir los datos de emisor/adquirente, líneas, identificación, moneda, pago, tributos, importes, numeración y estados;
5. aprobar el mapeo a `Invoice` y las reglas de validación con responsables funcionales/tributarios;
6. especificar pruebas positivas, negativas y de frontera con datos de referencia oficiales;
7. realizar validación integral contra XSD, reglas nacionales y ambiente oficial de prueba cuando corresponda, sin equiparar esos resultados a una certificación general.

No se ha implementado código, no se han generado XML de producción ni se ha cambiado el estado de los contratos API.
