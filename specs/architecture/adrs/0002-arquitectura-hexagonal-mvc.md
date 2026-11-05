# ADR-0002: Articulación propuesta de MVC y arquitectura hexagonal

---

## Metadatos

| Campo | Valor |
|---|---|
| Número | ADR-0002 |
| Estado | `PROPOSED` — pendiente de revisión y autorización |
| Decisores | Responsable del proyecto/examen, pendiente de confirmación |
| Fecha del borrador original | 2026-10-22 |
| Fecha de revisión | 2026-11-04 |
| Requisitos relacionados | `RNF-ARQ-001`, `RNF-SDD-001`; capacidades aplicables `RF-PRO-001`, `RF-INV-001/002`, `RF-COM-001`, `RF-VEN-001/002`, `RF-CPE-001`, `RF-SUN-001`, `RF-MIG-001` |
| Documentos relacionados | [Análisis de requisitos](../../../01_Analisis_Requisitos/analisis_requisitos.md), [SPEC-002](../../features/002-sistema-comercial-facturacion.md), [arquitectura](../../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md) |
| Reemplaza | No aplica |

## 1. Contexto y problema

El requisito `RNF-ARQ-001` solicita una arquitectura modular basada en MVC y principios de arquitectura hexagonal. `RNF-SDD-001` exige definir contratos antes de implementar. Los requisitos no fijan cómo se articulan MVC y los puertos/adaptadores, cuáles son los módulos, ni el framework, transporte o estructura de código.

La versión anterior de este ADR describía como hechos POS, crédito mayorista, reglas de inventario/tributación y sincronización asíncrona SUNAT. Esos supuestos no tienen respaldo en el alcance vigente y se excluyen de la decisión propuesta.

## 2. Problema a resolver

Definir una separación conceptual que satisfaga MVC y principios hexagonales sin acoplar la lógica del negocio a interfaces de usuario, transporte, persistencia o servicios externos. La especificación de límites y módulos debe preceder a la implementación, pero el detalle de los casos de uso depende de decisiones pendientes de dominio.

## 3. Requisitos y certeza

| Código | Relación con la decisión | Clasificación |
|---|---|---|
| `RNF-ARQ-001` | Exige modularidad, MVC y principios de arquitectura hexagonal. | Expreso |
| `RNF-SDD-001` | Requiere acordar contratos antes de implementar código. | Expreso |
| `RF-MIG-001` | Requiere contemplar migración; no exige CLI, ETL o un adaptador concreto. | Expreso para la capacidad; medio técnico pendiente |
| `RF-SUN-001` | Requiere integración SUNAT; no exige SOAP, OSE, worker ni procesamiento asíncrono. | Expreso para la integración; modalidad pendiente |
| MVC como adaptador primario exterior | Una manera posible de articular MVC y puertos/adaptadores. | Propuesta |
| Controladores REST, CLI, presentadores PDF, módulos y nombres de puertos | Detalles de solución no especificados. | Propuesta/pendiente |

## 4. Alternativas consideradas

1. **MVC modular con límites hexagonales:** ubicar controladores/presentadores en el borde y expresar la interacción del núcleo mediante puertos. Es la alternativa propuesta para desarrollar el diseño.
2. **MVC modular con otra distribución explícita de puertos/adaptadores:** conservar el aislamiento hexagonal, pero decidir los límites de MVC y módulos de otra manera. Requiere diagramar y comparar antes de aprobar.
3. **MVC tradicional con acceso directo a persistencia desde controladores/modelos:** no se propone porque aumenta el acoplamiento, aunque el análisis por sí solo no prohíbe esa implementación.
4. **Microservicios:** no se justifica con los requisitos y datos disponibles; no se propone como punto de partida.

## 5. Decisión propuesta y fundamento

**Se propone**, sin aprobación todavía, expresar MVC como adaptador de entrada/presentación en el límite del sistema y mantener el núcleo de aplicación/dominio independiente mediante puertos y adaptadores. Los adaptadores para PostgreSQL, SUNAT y la fuente histórica se definirían contra interfaces del núcleo.

La propuesta es compatible con los patrones expresamente solicitados y facilita aislar las dependencias externas. No se elige framework, estilo HTTP, protocolo SUNAT, mecanismo de migración, formato de presentación ni patrón de ejecución asíncrona. La arquitectura publicada en el entregable 04 refleja esta propuesta y permanece en `DRAFT`.

## 6. Consecuencias previstas

### Dominio

- Los conceptos de producto, almacén, movimiento, terceros, venta y comprobante pueden modelarse sin importar frameworks ni estructuras de persistencia.
- Las invariantes y relaciones concretas siguen sujetas a la definición del modelo de dominio y las reglas de negocio.
- No se presupone que cualquier regla tributaria o de inventario de borradores previos forme parte del dominio aprobado.

### Arquitectura

- Los límites entre adaptadores, aplicación y dominio son una guía conceptual, no una estructura de paquetes/clases autorizada.
- La integración SUNAT y la migración se representan como dependencias externas intercambiables; sus interfaces y protocolos siguen pendientes.
- No se establece una topología de despliegue, colas, Outbox ni estrategia transaccional.

### Contratos API/DTO

- Los controladores deberían traducir contratos externos a solicitudes de aplicación y presentar resultados mediante DTOs; no deberían exponer entidades de persistencia.
- La API actual propone HTTP/REST y sigue en `DRAFT`; el estilo de transporte no es requisito expreso.
- El diseño de contratos no debe introducir roles, estados, operaciones de SUNAT o semánticas de confirmación no aprobadas.

## 7. Contradicciones corregidas respecto de documentos anteriores

- La arquitectura vigente ya no describe POS, pedidos a crédito, SOAP, XMLDSig, OSE, Outbox, reglas tributarias ni estrategias de stock como decisiones confirmadas.
- Se retiran las afirmaciones de cobertura de pruebas del 100% y las acciones marcadas como completadas sobre interfaces/paquetes no aprobados.
- `ACCEPTED` se cambia a `PROPOSED` porque el análisis respalda el patrón general, pero no la articulación precisa ni una aprobación del responsable del proyecto.

## 8. Pendientes de autorización

Antes de cambiar este ADR a `ACCEPTED`, el responsable debe confirmar:

1. si aprueba MVC como adaptador primario exterior;
2. límites modulares y responsabilidades MVC;
3. qué transportes y adaptadores de entrada se requieren;
4. interfaces de salida y transacciones para PostgreSQL, SUNAT y migración;
5. si hacen falta requisitos medibles de pruebas, seguridad, confiabilidad y operación.

## 9. Acciones

- [x] Reconciliar el contenido con el análisis de requisitos vigente.
- [ ] Revisar y aprobar o modificar la propuesta.
- [ ] Actualizar arquitectura y contratos sólo después de autorización de decisiones que los afecten.
- [ ] Cambiar estado a `ACCEPTED` únicamente con aprobación expresa documentada.
