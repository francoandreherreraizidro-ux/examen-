# ADR-0002: Adopción de Arquitectura Hexagonal con MVC como Adaptador Primario

---

## 📋 Metadata

| Attribute | Details |
|---|---|
| **ADR Number** | ADR-0002 |
| **Status** | `ACCEPTED` <!-- Options: PROPOSED | ACCEPTED | REJECTED | DEPRECATED | SUPERSEDED --> |
| **Deciders** | Senior Software Architect & Evaluador Docente de Arquitectura |
| **Date** | 2026-10-22 |
| **Related Specs** | [SPEC-002](../../features/002-sistema-comercial-facturacion.md), [04 Arquitectura Hexagonal](../../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md) |
| **Superseded By** | N/A |

---

## 1. Context and Problem Statement

El sistema bajo diseño debe soportar operaciones comerciales diferenciadas (ventas rápidas en mostrador minorista, pedidos mayoristas corporativos, ingesta ETL de migración y sincronización asíncrona de comprobantes con la SUNAT). 

Tradicionalmente, las aplicaciones basadas en el patrón **Modelo-Vista-Controlador (MVC)** acoplan la lógica de negocio a controladores web o modelos vinculados directamente al motor de base de datos (ActiveRecord o entidades ORM directas). Esto genera graves problemas:
* Imposibilidad de probar reglas de negocio complejas (liquidación de IGV, invariante de Kardex no negativo, validación de RUC Módulo 11) sin inicializar servidores HTTP o bases de datos activas.
* Alto acoplamiento a los esquemas externos de la SUNAT (SOAP WSDL / OASIS UBL 2.1).
* Dificultad para incorporar múltiples puntos de entrada (interfaz gráfica POS, CLI de migración, cron jobs de segundo plano).

---

## 2. Decision Drivers (Key Requirements)

- **Aislamiento del Dominio**: El núcleo de reglas comerciales e inventarios debe ser agnóstico a frameworks web y tecnologías de persistencia.
- **Testabilidad Unitaria Exhaustiva**: Capacidad de validar el 100% de las invariantes y reglas de negocio mediante pruebas unitarias puras sin dependencias externas.
- **Soporte de Múltiples Puntos de Entrada**: Permitir que controladores REST (POS), controladores de Backoffice, scripts CLI de migración y workers de fondo interactúen con los mismos casos de uso sin duplicar código.
- **Flexibilidad de Adaptadores**: Facilidad para cambiar o actualizar librerías criptográficas (XMLDSig) o clientes de servicios web (SUNAT) sin alterar la lógica de negocio.

---

## 3. Considered Options

1. **Opción 1: Arquitectura Tradicional MVC en Tres Capas (Controlador -> Servicio -> DAO/ORM)**.
2. **Opción 2: Arquitectura Hexagonal Pura (Ports & Adapters) con MVC como Adaptador Primario de Entrada**.
3. **Opción 3: Arquitectura Dirigida por Microservicios desde el inicio**.

---

## 4. Decision & Rationale

Elegimos la **Opción 2: Arquitectura Hexagonal (Puertos y Adaptadores) articulando MVC en la capa exterior como Adaptador Primario (Driving Adapter)**.

### Fundamentación Técnica:
1. **El Patrón MVC se confina a la frontera exterior**:
   * **Controlador (Controller)**: Actúa como *Driving Adapter*. Recibe la solicitud HTTP, valida el JSON Schema del DTO de entrada y delega la ejecución a un *Inbound Port* (Caso de Uso).
   * **Vista / Presentador (View / Presenter)**: Formatea las respuestas del caso de uso en DTOs JSON para la API o genera plantillas PDF de tickets de venta con código QR.
   * **Modelo (Model)**: No es una tabla de base de datos; representa la totalidad del **Núcleo Hexagonal** (Servicios de Aplicación, Agregados DDD y Entidades Puras).
2. **Puertos Secundarios (Driven Ports) para Infraestructura**:
   * La persistencia en PostgreSQL y la comunicación con los Web Services de SUNAT se definen como interfaces (*Ports*) dentro de la capa de aplicación. Los adaptadores concretos (*Repositories*, *Gateways*) implementan dichas interfaces en la capa exterior de infraestructura, cumpliendo estrictamente con el **Principio de Inversión de Dependencias (DIP)**.

---

## 5. Pros and Cons of the Options

### Opción 1: MVC Tradicional Acoplado
- **Bueno**, porque es más simple de entender en desarrollos iniciales pequeños.
- **Malo**, porque mezcla responsabilidades tributarias con controladores web, dificulta el testeo aislado y genera fuga de lógica de dominio hacia la capa de persistencia.

### Opción 2: Arquitectura Hexagonal con MVC Exterior (Seleccionada)
- **Bueno**, porque aísla el núcleo de dominio de cambios en la API de SUNAT o en la base de datos PostgreSQL.
- **Bueno**, porque permite pruebas unitarias de casos de uso sustituyendo repositorios y clientes SOAP por *Mocks* o implementaciones en memoria.
- **Bueno**, porque permite que el CLI de migración (`RF-MIG-001`) reutilice exactamente los mismos casos de uso que la API web.
- **Malo**, porque introduce una mayor cantidad de clases, interfaces intermedias y objetos de transferencia (DTOs / Mappers).

---

## 6. Consequences & Follow-up Actions

### Positive Consequences
- El núcleo de negocio de abarrotes, Kardex y facturación puede probarse al 100% antes de implementar la base de datos o controladores HTTP.
- Claridad meridiana en la asignación de responsabilidades: controladores validan sintaxis; servicios de aplicación orquestan transacciones; entidades protegen invariantes; adaptadores manejan I/O técnico.

### Negative Consequences / Trade-offs
- Se requiere mantener mapeadores de datos explícitos entre Entidades del Dominio, DTOs de API y modelos relacionales de PostgreSQL.

### Action Items
- [x] Documentar diagramas de capas y paquetes en [04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md](../../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md).
- [x] Definir interfaces de puertos primarios y secundarios en el catálogo de especificaciones.
- [ ] Implementar el scaffolding de pruebas unitarias verificando la pureza de las entidades sin dependencias externas.
