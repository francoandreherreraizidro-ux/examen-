# Especificación de Endpoints y Servicios mediante Contratos de API (Spec-First)

| Metadato | Detalle |
| :--- | :--- |
| **Proyecto / Asignatura** | Examen de Arquitectura de Software |
| **Entregable N° 3** | Especificación Formal de Endpoints y Servicios vía Contratos de API |
| **Dominio** | Gestión Comercial, Control de Inventarios y Facturación Electrónica UBL 2.1 |
| **Enfoque** | Spec-First / Contract-Driven Development (OpenAPI 3.1 & JSON Schema) |
| **Estándar de Errores** | RFC 7807 / RFC 9457 (Problem Details for HTTP APIs) |
| **Protocolo de Seguridad** | OAuth 2.0 / JWT Bearer Tokens con RBAC |
| **Estado** | `APPROVED SPECIFICATION` (Contratos Congelados para Implementación y Tests) |
| **Versión** | 1.0.0 |
| **Base URL** | `https://api.empresa-abarrotes.pe/api/v1` |

---

## Índice de Contenidos

1. [Políticas Globales de la API](#1-políticas-globales-de-la-api)
   - 1.1. Cabeceras Comunes (Headers)
   - 1.2. Idempotencia en Operaciones Financieras y de Inventario
   - 1.3. Estructura Estándar de Respuesta (Success Envelope)
   - 1.4. Manejo de Errores Estándar (RFC 7807 Problem Details)
2. [Módulo 1: Catálogo de Productos y Almacenes](#2-módulo-1-catálogo-de-productos-y-almacenes)
   - 2.1. `POST /api/v1/catalog/products` - Creación de Producto con Precios Duales
   - 2.2. `GET /api/v1/catalog/products` - Consulta Paginada de Catálogo
   - 2.3. `GET /api/v1/inventory/warehouses` - Listado de Almacenes y Locales
3. [Módulo 2: Control de Inventarios y Kardex](#3-módulo-2-control-de-inventarios-y-kardex)
   - 3.1. `POST /api/v1/inventory/movements/inflows` - Registro de Entrada (Compra / Devolución)
   - 3.2. `POST /api/v1/inventory/movements/transfers` - Traslado Interalmacén
   - 3.3. `POST /api/v1/inventory/movements/adjustments` - Ajuste de Inventario Físico
   - 3.4. `GET /api/v1/inventory/kardex` - Consulta de Kardex Permanente Valorizado
4. [Módulo 3: Clientes, Proveedores y Líneas de Crédito](#4-módulo-3-clientes-proveedores-y-líneas-de-crédito)
   - 4.1. `POST /api/v1/directory/customers` - Registro de Cliente (Validación Catálogo 06 SUNAT)
   - 4.2. `GET /api/v1/directory/customers/{id}/credit-status` - Verificación de Línea de Crédito
5. [Módulo 4: Operaciones de Venta (Retail POS y Mayorista)](#5-módulo-4-operaciones-de-venta)
   - 5.1. `POST /api/v1/sales/pos` - Venta Rápida Minorista (Checkout en Mostrador)
   - 5.2. `POST /api/v1/sales/wholesale/orders` - Creación de Pedido Mayorista con Reserva
   - 5.3. `POST /api/v1/sales/wholesale/orders/{id}/confirm` - Confirmación y Aprobación Comercial
6. [Módulo 5: Facturación Electrónica UBL 2.1 e Integración SUNAT](#6-módulo-5-facturación-electrónica-ubl-21)
   - 6.1. `POST /api/v1/billing/invoices` - Emisión Directa de Comprobante (Factura / Boleta)
   - 6.2. `POST /api/v1/billing/credit-notes` - Emisión de Nota de Crédito (Tipo 07)
   - 6.3. `GET /api/v1/billing/documents/{id}/xml` - Descarga del XML UBL 2.1 Firmado
   - 6.4. `GET /api/v1/billing/documents/{id}/cdr` - Consulta y Descarga de la Constancia CDR
   - 6.5. `POST /api/v1/billing/outbox/process` - Disparo Manual / Worker de Lote a SUNAT
7. [Módulo 6: Migración de Datos Legados (ETL Spec)](#7-módulo-6-migración-de-datos-legados)
   - 7.1. `POST /api/v1/migration/jobs/stock-balances` - Carga de Saldos Iniciales de Stock
   - 7.2. `GET /api/v1/migration/jobs/{jobId}` - Consulta de Estado y Reporte de Conciliación

---

## 1. Políticas Globales de la API

### 1.1. Cabeceras Comunes (Headers)

#### Cabeceras de Solicitud (Request Headers)
| Cabecera | Tipo | Obligatorio | Descripción |
| :--- | :--- | :--- | :--- |
| `Authorization` | String | Sí | Token JWT Bearer: `Bearer <token_jwt>`. |
| `Content-Type` | String | Sí | `application/json` (o `multipart/form-data` para archivos de migración). |
| `Accept` | String | Sí | `application/json`, `application/xml` (para UBL) o `application/pdf`. |
| `X-Correlation-ID` | UUID | Sí | Identificador único de trazabilidad distribuida generado por el cliente. |
| `Idempotency-Key` | String | Condicional | Obligatorio en operaciones de mutación crítica (`POST /sales/*`, `POST /billing/*`). |

#### Cabeceras de Respuesta (Response Headers)
| Cabecera | Tipo | Descripción |
| :--- | :--- | :--- |
| `X-Correlation-ID` | UUID | Eco del ID de trazabilidad recibido para auditoría cruzada. |
| `X-RateLimit-Limit` | Integer | Límite máximo de peticiones por minuto. |
| `X-RateLimit-Remaining`| Integer | Peticiones remanentes en la ventana de tiempo. |

---

### 1.2. Idempotencia en Operaciones Críticas

Para evitar dobles cobros, duplicidad de comprobantes fiscales ante la SUNAT o deducciones dobles de inventario ante reintentos de red:
* Las rutas de creación de ventas, emisión de comprobantes y ajustes de inventario exigen la cabecera `Idempotency-Key: <UUID>`.
* El servidor registra la clave en PostgreSQL junto con el hash del payload.
* Si el cliente reintenta con la misma `Idempotency-Key` en un lapso de 24 horas, el servidor retorna la respuesta almacenada previamente con código HTTP `200 OK` y cabecera `X-Cache-Lookup: HIT`, sin volver a ejecutar la transacción en base de datos ni emitir comprobantes duplicados.

---

### 1.3. Estructura Estándar de Respuesta (Success Envelope)

Toda respuesta exitosa sigue una envoltura homogénea:

```json
{
  "success": true,
  "data": {},
  "metadata": {
    "correlationId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2026-10-22T10:15:30.123Z"
  }
}
```

---

### 1.4. Manejo de Errores Estándar (RFC 7807 / RFC 9457)

Los errores se retornan bajo el estándar formal `application/problem+json`:

```json
{
  "type": "https://api.empresa-abarrotes.pe/errors/insufficient-stock",
  "title": "Stock Insuficiente en Almacén",
  "status": 422,
  "detail": "El producto con SKU 'ABAR-ARROZ-COSTE-50K' en el Almacén Central cuenta con stock disponible de 12.000 unidades, pero se solicitaron 25.000 unidades.",
  "instance": "/api/v1/sales/pos",
  "code": "ERR_INSUFFICIENT_STOCK",
  "invalidParams": [
    {
      "name": "items[0].quantity",
      "reason": "Requested quantity exceeds available stock"
    }
  ],
  "timestamp": "2026-10-22T10:15:30.125Z"
}
```

---

## 2. Módulo 1: Catálogo de Productos y Almacenes

---

### 2.1. `POST /api/v1/catalog/products` - Creación de Producto

#### Descripción
Registra un nuevo producto en el catálogo general, definiendo sus unidades de medida normalizadas conforme al Catálogo N° 03 de la SUNAT, afectación al IGV por defecto y su esquema dual de precios (minorista y mayorista).

#### Permisos Requeridos (RBAC)
* Rol: `ROLE_ADMIN`, `ROLE_CATALOG_MANAGER`.

#### Request Body
```json
{
  "sku": "ABAR-ARROZ-COSTE-50K",
  "barcode": "7750123456789",
  "name": "Arroz Costeño Extra Saco 50 Kg",
  "description": "Arroz grano largo seleccionado de primera calidad",
  "category": "ABARROTES",
  "unitOfMeasure": "KGM",
  "sunatProductCode": "50111515",
  "taxAffectionDefault": "10",
  "isPerishable": true,
  "priceTiers": [
    {
      "tierType": "RETAIL_PRICE",
      "minQuantity": 1.0,
      "unitPrice": 185.00,
      "currency": "PEN",
      "validFrom": "2026-10-01"
    },
    {
      "tierType": "WHOLESALE_TIER_1",
      "minQuantity": 10.0,
      "unitPrice": 172.50,
      "currency": "PEN",
      "validFrom": "2026-10-01"
    }
  ]
}
```

#### Tabla de Esquema de Campos
| Campo | Tipo | Obligatorio | Restricciones | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `sku` | String | Sí | 3 a 50 caracteres, alfanumérico único. | Identificador interno SKU. |
| `barcode` | String | No | Formato EAN-13, EAN-8 o UPC válido. | Código de barras comercial. |
| `name` | String | Sí | Máximo 255 caracteres. | Denominación del producto. |
| `category` | String | Sí | Máximo 100 caracteres. | Categoría de mercadería. |
| `unitOfMeasure` | String | Sí | Valores válidos: `NIU`, `KGM`, `BX`, `PK`. | Código según Catálogo 03 SUNAT. |
| `sunatProductCode` | String | No | 8 dígitos numéricos. | Código UNSPSC Catálogo 25. |
| `taxAffectionDefault` | String | Sí | Enum: `10` (Gravado), `20` (Exon), `30` (Inaf). | Afectación tributaria Catálogo 07. |
| `isPerishable` | Boolean | Sí | Default `false`. | Exige control de lote y caducidad. |
| `priceTiers` | Array | Sí | Mínimo 1 elemento (minorista). | Escalas de precio con IGV incluido. |

#### Respuesta Exitosa: `201 Created`
```json
{
  "success": true,
  "data": {
    "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "sku": "ABAR-ARROZ-COSTE-50K",
    "name": "Arroz Costeño Extra Saco 50 Kg",
    "isActive": true,
    "createdAt": "2026-10-22T10:15:30.000Z"
  },
  "metadata": {
    "correlationId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2026-10-22T10:15:30.123Z"
  }
}
```

---

### 2.2. `GET /api/v1/catalog/products` - Consulta Paginada de Catálogo

#### Query Parameters
* `page` (Integer, opcional, default: `1`): Número de página.
* `limit` (Integer, opcional, default: `20`, máx: `100`): Tamaño de página.
* `search` (String, opcional): Búsqueda por SKU, código de barras o nombre.
* `category` (String, opcional): Filtro por categoría.
* `isActive` (Boolean, opcional, default: `true`).

#### Respuesta Exitosa: `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "sku": "ABAR-ARROZ-COSTE-50K",
      "barcode": "7750123456789",
      "name": "Arroz Costeño Extra Saco 50 Kg",
      "unitOfMeasure": "KGM",
      "retailPrice": 185.00,
      "wholesaleMinPrice": 172.50,
      "isPerishable": true,
      "isActive": true
    }
  ],
  "pagination": {
    "currentPage": 1,
    "pageSize": 20,
    "totalRecords": 1450,
    "totalPages": 73
  }
}
```

---

## 3. Módulo 2: Control de Inventarios y Kardex

---

### 3.1. `POST /api/v1/inventory/movements/inflows` - Registro de Entrada (Compra)

#### Descripción
Registra el ingreso físico de mercadería a un almacén determinado, sustentado en una factura de proveedor o guía de remisión, y recalcula automáticamente el **Costo Promedio Ponderado** del producto actualizando el Kardex inmutable.

#### Request Body
```json
{
  "warehouseId": "d3b07384-d113-40a2-9b26-8094a45a3287",
  "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "batchCode": "LOTE-2026-X45",
  "expirationDate": "2027-10-15",
  "sunatOperationType": "02",
  "quantity": 100.0,
  "unitCostWithoutTax": 142.50,
  "documentReference": {
    "documentTypeCode": "01",
    "series": "F001",
    "number": "00045612",
    "supplierRuc": "20551234567"
  },
  "notes": "Ingreso según OC-2026-0089 Proveedor Molinera del Centro"
}
```

#### Respuesta Exitosa: `201 Created`
```json
{
  "success": true,
  "data": {
    "kardexEntryId": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
    "movementTimestamp": "2026-10-22T10:20:00Z",
    "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "warehouseId": "d3b07384-d113-40a2-9b26-8094a45a3287",
    "operationType": "02",
    "inflow": {
      "quantity": 100.0,
      "unitCost": 142.500000,
      "totalCost": 14250.00
    },
    "balance": {
      "accumulatedPhysicalQuantity": 150.0,
      "newAverageUnitCost": 141.666667,
      "newTotalCostBalance": 21250.00
    }
  }
}
```

---

### 3.2. `POST /api/v1/inventory/movements/transfers` - Traslado Interalmacén

#### Descripción
Inicia o completa el movimiento físico de mercancía entre dos almacenes de la empresa (ej. de Almacén Central hacia Almacén Tienda 01).

#### Request Body
```json
{
  "sourceWarehouseId": "d3b07384-d113-40a2-9b26-8094a45a3287",
  "targetWarehouseId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "quantity": 25.0,
  "dispatchNoteSeries": "T001",
  "dispatchNoteNumber": "00000145"
}
```

#### Respuesta Exitosa: `200 OK`
```json
{
  "success": true,
  "data": {
    "transferId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "status": "COMPLETED",
    "sourceWarehouseRemaining": 125.0,
    "targetWarehouseNewBalance": 55.0,
    "transferTimestamp": "2026-10-22T10:25:00Z"
  }
}
```

---

### 3.3. `GET /api/v1/inventory/kardex` - Consulta de Kardex Permanente

#### Query Parameters
* `warehouseId` (UUID, obligatorio): Identificador del almacén.
* `productId` (UUID, obligatorio): Identificador del producto.
* `startDate` (Date, obligatorio, formato `YYYY-MM-DD`): Fecha de inicio.
* `endDate` (Date, obligatorio, formato `YYYY-MM-DD`): Fecha de fin.

#### Respuesta Exitosa: `200 OK`
```json
{
  "success": true,
  "data": {
    "warehouse": {
      "warehouseId": "d3b07384-d113-40a2-9b26-8094a45a3287",
      "code": "ALM-CENTRAL",
      "name": "Almacén Central Mayorista"
    },
    "product": {
      "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "sku": "ABAR-ARROZ-COSTE-50K",
      "name": "Arroz Costeño Extra Saco 50 Kg",
      "unitOfMeasure": "KGM"
    },
    "period": {
      "startDate": "2026-10-01",
      "endDate": "2026-10-22"
    },
    "entries": [
      {
        "timestamp": "2026-10-01T08:00:00Z",
        "operationCode": "16",
        "operationDescription": "Saldo Inicial",
        "document": "INV-0001",
        "inflow": { "quantity": 50.0, "unitCost": 140.00, "total": 7000.00 },
        "outflow": { "quantity": 0.0, "unitCost": 0.00, "total": 0.00 },
        "balance": { "quantity": 50.0, "unitCost": 140.00, "total": 7000.00 }
      },
      {
        "timestamp": "2026-10-22T10:20:00Z",
        "operationCode": "02",
        "operationDescription": "Compra Nacional",
        "document": "F001-00045612",
        "inflow": { "quantity": 100.0, "unitCost": 142.50, "total": 14250.00 },
        "outflow": { "quantity": 0.0, "unitCost": 0.00, "total": 0.00 },
        "balance": { "quantity": 150.0, "unitCost": 141.666667, "total": 21250.00 }
      }
    ]
  }
}
```

---

## 4. Módulo 3: Clientes, Proveedores y Líneas de Crédito

---

### 4.1. `POST /api/v1/directory/customers` - Registro de Cliente

#### Descripción
Registra un nuevo cliente para venta minorista o mayorista. Valida que el documento de identidad cumpla estrictamente con el **Catálogo N° 06 de la SUNAT** (DNI de 8 dígitos numéricos o RUC de 11 dígitos con dígito verificador).

#### Request Body
```json
{
  "identityDocumentType": "6",
  "identityDocumentNumber": "20601234567",
  "legalName": "DISTRIBUIDORA Y COMERCIALIZADORA DEL NORTE S.A.C.",
  "tradeName": "DISNORTE",
  "customerType": "WHOLESALE",
  "email": "facturacion@disnorte.pe",
  "phone": "+51999888777",
  "fiscalAddress": {
    "ubigeo": "150101",
    "department": "LIMA",
    "province": "LIMA",
    "district": "LIMA",
    "addressLine": "Av. Materiales Nro. 2450 Int. 3B"
  },
  "creditProfile": {
    "creditLimit": 50000.00,
    "creditTermDays": 30
  }
}
```

#### Respuesta Exitosa: `201 Created`
```json
{
  "success": true,
  "data": {
    "customerId": "8f3b6c20-639a-4c96-857e-f4a43a0d1e55",
    "identityDocumentType": "6",
    "identityDocumentNumber": "20601234567",
    "legalName": "DISTRIBUIDORA Y COMERCIALIZADORA DEL NORTE S.A.C.",
    "customerType": "WHOLESALE",
    "creditLimit": 50000.00,
    "creditTermDays": 30,
    "isCreditBlocked": false
  }
}
```

---

## 5. Módulo 4: Operaciones de Venta (Retail POS y Mayorista)

---

### 5.1. `POST /api/v1/sales/pos` - Venta Rápida Minorista (Checkout)

#### Descripción
Endpoint de alto rendimiento para el mostrador de ventas minoristas. En una sola transacción ACID en PostgreSQL: deduce stock disponible, genera la venta comercial, registra el comprobante fiscal localmente y programa el evento en la tabla Outbox.

#### Headers Requeridos
* `Idempotency-Key`: UUID (Obligatorio).

#### Request Body
```json
{
  "dispatchWarehouseId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "customerId": "8f3b6c20-639a-4c96-857e-f4a43a0d1e55",
  "paymentCondition": "CASH",
  "currency": "PEN",
  "documentType": "03",
  "series": "B001",
  "items": [
    {
      "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "quantity": 2.0,
      "unitPrice": 185.00
    }
  ],
  "cashReceivedAmount": 400.00
}
```

#### Respuesta Exitosa: `201 Created`
```json
{
  "success": true,
  "data": {
    "saleOrderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "document": {
      "documentId": "c8a7d519-8685-48fa-bb4b-7a3d24269d01",
      "documentType": "03",
      "fullNumber": "B001-00012450",
      "issueDate": "2026-10-22",
      "issueTime": "10:35:12",
      "status": "XML_SIGNED_OUTBOX_PENDING",
      "digestValue": "9Zf23xKl0O71eYh3aB8/4rTxL9E="
    },
    "totals": {
      "taxableAmount": 313.56,
      "igvAmount": 56.44,
      "totalAmount": 370.00
    },
    "cashChangeDue": 30.00,
    "ticketPrintUrl": "/api/v1/billing/documents/c8a7d519-8685-48fa-bb4b-7a3d24269d01/pdf?format=ticket80mm"
  }
}
```

---

### 5.2. `POST /api/v1/sales/wholesale/orders` - Creación de Pedido Mayorista

#### Descripción
Genera una orden de venta corporativa con reserva de stock en almacén central y asignación de condiciones de crédito comercial.

#### Request Body
```json
{
  "customerId": "8f3b6c20-639a-4c96-857e-f4a43a0d1e55",
  "dispatchWarehouseId": "d3b07384-d113-40a2-9b26-8094a45a3287",
  "paymentCondition": "CREDIT_30D",
  "currency": "PEN",
  "items": [
    {
      "productId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "quantity": 50.0,
      "unitPrice": 172.50
    }
  ]
}
```

#### Respuesta Exitosa: `201 Created`
```json
{
  "success": true,
  "data": {
    "orderId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
    "orderStatus": "CONFIRMED",
    "customerId": "8f3b6c20-639a-4c96-857e-f4a43a0d1e55",
    "totalPayable": 8625.00,
    "reservedStockExpiresAt": "2026-10-23T18:00:00Z",
    "creditEvaluation": {
      "isApproved": true,
      "creditLimit": 50000.00,
      "currentDebt": 12000.00,
      "availableCreditAfterOrder": 29375.00
    }
  }
}
```

---

## 6. Módulo 5: Facturación Electrónica UBL 2.1 e Integración SUNAT

---

### 6.1. `POST /api/v1/billing/invoices` - Emisión Directa de Comprobante

#### Descripción
Convierte un pedido mayorista o venta aprobada en un Comprobante de Pago Electrónico formal bajo el estándar **OASIS UBL 2.1**, incorporando los requisitos de la **Resolución de Superintendencia N° 193-2020/SUNAT** para operaciones a crédito (desglose de cuotas y fechas de vencimiento).

#### Request Body
```json
{
  "saleOrderId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
  "documentTypeCode": "01",
  "series": "F001",
  "issueDate": "2026-10-22",
  "paymentTerms": {
    "type": "Credito",
    "totalPendingAmount": 8625.00,
    "installments": [
      {
        "installmentNumber": 1,
        "amount": 4312.50,
        "dueDate": "2026-11-06"
      },
      {
        "installmentNumber": 2,
        "amount": 4312.50,
        "dueDate": "2026-11-21"
      }
    ]
  }
}
```

#### Respuesta Exitosa: `201 Created`
```json
{
  "success": true,
  "data": {
    "documentId": "e3b0c442-98fc-1c14-9afbf4c8996fb924",
    "documentType": "01",
    "series": "F001",
    "correlative": 8540,
    "fullNumber": "F001-00008540",
    "issueDate": "2026-10-22",
    "documentStatus": "XML_SIGNED_OUTBOX_PENDING",
    "digestValue": "G8b12Nlk3P4QW0rS9/1uVxY7Z2M=",
    "xmlDownloadUrl": "/api/v1/billing/documents/e3b0c442-98fc-1c14-9afbf4c8996fb924/xml",
    "pdfDownloadUrl": "/api/v1/billing/documents/e3b0c442-98fc-1c14-9afbf4c8996fb924/pdf"
  }
}
```

---

### 6.2. `GET /api/v1/billing/documents/{id}/cdr` - Consulta de CDR de SUNAT

#### Descripción
Retorna la información deserializada y oficial de la Constancia de Recepción (CDR) emitida por la SUNAT tras la transmisión del comprobante electrónico.

#### Respuesta Exitosa: `200 OK`
```json
{
  "success": true,
  "data": {
    "documentId": "e3b0c442-98fc-1c14-9afbf4c8996fb924",
    "fullNumber": "F001-00008540",
    "cdrStatus": "ACCEPTED",
    "sunatResponseCode": "0",
    "sunatDescription": "La Factura numero F001-00008540, ha sido aceptada",
    "receiptTimestamp": "2026-10-22T10:40:15Z",
    "cdrHash": "K9v21Lm02X31eYh3aB8/4rTxL9E=",
    "observations": [],
    "cdrZipDownloadUrl": "/api/v1/billing/documents/e3b0c442-98fc-1c14-9afbf4c8996fb924/cdr/download"
  }
}
```

---

## 7. Módulo 6: Migración de Datos Legados (ETL Spec)

---

### 7.1. `POST /api/v1/migration/jobs/stock-balances` - Carga de Saldos Iniciales

#### Descripción
Endpoint de uso restringido para el Administrador de TI (`ACT-01`). Permite cargar en bloque el inventario inicial valorizado desde el sistema legado, garantizando la reconciliación previa a la apertura operativa.

#### Request Body
```json
{
  "cutoffDate": "2026-09-30",
  "warehouseCode": "ALM-CENTRAL",
  "reconciliationSignature": "SIG-ADM-99482-OK",
  "records": [
    {
      "sku": "ABAR-ARROZ-COSTE-50K",
      "quantity": 150.0,
      "unitCostWithoutTax": 140.00,
      "batchCode": "LOTE-LEGADO-001",
      "expirationDate": "2027-08-31"
    },
    {
      "sku": "ABAR-AZUCAR-PARAM-50K",
      "quantity": 80.0,
      "unitCostWithoutTax": 128.50
    }
  ]
}
```

#### Respuesta Exitosa: `202 Accepted`
```json
{
  "success": true,
  "data": {
    "jobId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "status": "PROCESSING",
    "totalRecordsSubmitted": 2,
    "submittedAt": "2026-10-22T10:45:00Z",
    "statusTrackingUrl": "/api/v1/migration/jobs/a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

---

### 7.2. `GET /api/v1/migration/jobs/{jobId}` - Reporte de Conciliación

#### Respuesta Exitosa: `200 OK`
```json
{
  "success": true,
  "data": {
    "jobId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "target": "INITIAL_STOCK_BALANCES",
    "status": "RECONCILED",
    "totalRecords": 2,
    "successfulRecords": 2,
    "failedRecords": 0,
    "auditSummary": {
      "totalUnitsLoaded": 230.0,
      "totalInventoryValuePEN": 31280.00,
      "kardexEntriesGenerated": 2
    },
    "errors": []
  }
}
```
