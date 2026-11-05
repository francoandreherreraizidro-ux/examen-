# Análisis de requisitos — Sistema comercial

| Campo | Valor |
|---|---|
| Contexto | Examen de Arquitectura de Software |
| Negocio | Empresa mediana que comercializa abarrotes y mercadería al por mayor y menor |
| Fase | 1 — Análisis de requisitos |
| Estado | Borrador para revisión; sujeto a confirmación del responsable del examen |
| Alcance de este documento | Análisis inicial; no define todavía el modelo de dominio, los DTOs, los endpoints ni la solución de implementación |

## Convenciones de certeza

- **[Expreso]**: aparece de forma directa en el enunciado del examen.
- **[Inferencia]**: se desprende razonablemente de una capacidad solicitada, pero requiere validación antes de considerarse requisito aprobado.
- **[Propuesta]**: alternativa o recomendación de diseño; no es una exigencia del enunciado.
- Las referencias a SUNAT y UBL en este análisis identifican el alcance indicado por el examen. No certifican cumplimiento normativo. Los catálogos, reglas, formatos, plazos y mecanismos aplicables deberán verificarse contra fuentes oficiales vigentes antes de diseñar los contratos de facturación.

## 1. Resumen del problema empresarial

La empresa necesita un sistema que apoye su operación de comercialización mayorista y minorista. El alcance descrito combina la administración de productos y almacén, el registro histórico de movimientos de inventario, la gestión de clientes, proveedores y ventas, y la emisión de comprobantes electrónicos con integración a SUNAT.

También se requiere migrar datos históricos desde el sistema anterior, validar los datos y conservar su trazabilidad. El examen solicita definir primero los contratos de datos y APIs, y luego diseñar una solución modular que articule MVC con principios de arquitectura hexagonal, use PostgreSQL y considere UBL Invoice 2.1 para comprobantes electrónicos.

El enunciado no describe los procesos actuales, los volúmenes, las políticas comerciales, los roles internos, el sistema legado ni el mecanismo concreto de envío a SUNAT. Por ello, este documento separa el alcance expresamente indicado de las decisiones que siguen abiertas.

## 2. Objetivo general

Definir y documentar los requisitos iniciales y las decisiones pendientes para diseñar un sistema comercial modular para la empresa, cubriendo inventario, gestión comercial, comprobantes electrónicos, integración con SUNAT y migración histórica. El diseño posterior deberá respetar las restricciones expresas de PostgreSQL, MVC, arquitectura hexagonal, UBL Invoice 2.1 y enfoque Spec-First, sin iniciar implementación antes de acordar los contratos.

## 3. Actores

El enunciado no asigna nombres de usuario, cargos ni permisos. Por esa razón, los actores humanos concretos se clasifican como propuestos, no confirmados.

### 3.1 Sistemas externos confirmados por el alcance

| ID | Actor | Tipo | Fundamento y límites |
|---|---|---|---|
| ACT-01 | Servicios de SUNAT | Sistema externo | **[Expreso]** Se solicita integración con servicios de SUNAT. No se especifica modalidad, interfaz, operación ni ambiente. |
| ACT-02 | Sistema anterior o fuente de datos histórica | Sistema externo | **[Expreso]** Se solicita migrar datos históricos desde el sistema anterior; se desconocen su tecnología, estructura y disponibilidad. |

### 3.2 Actores propuestos, sujetos a confirmación

| ID | Actor propuesto | Motivo de consideración |
|---|---|---|
| ACT-P01 | Personal/usuarios de la empresa | Es razonable que haya usuarios internos para operar el sistema, pero el enunciado no los nombra ni define sus responsabilidades. |
| ACT-P02 | Administrador del sistema | Podría mantener configuración y usuarios; el enunciado no solicita explícitamente administración de accesos. |
| ACT-P03 | Encargado y operador de almacén | Podrían ejecutar y revisar movimientos; los cargos y la separación de responsabilidades están por definir. |
| ACT-P04 | Vendedor/cajero | Podría registrar ventas mayoristas o minoristas; no se describe el flujo operativo ni el punto de venta. |
| ACT-P05 | Responsable contable/tributario | Podría revisar comprobantes, respuestas de SUNAT y trazabilidad; su participación no está especificada. |
| ACT-P06 | Cliente o proveedor como usuario directo | La gestión de sus datos no implica que accedan directamente al sistema; portales o autoservicio no forman parte del alcance confirmado. |

## 4. Requisitos funcionales

Los requisitos siguientes expresan el alcance inicial, no una aprobación de reglas operativas o tributarias aún no descritas. Los criterios de aceptación detallados deberán acordarse en las especificaciones de cada capacidad.

### 4.1 Productos y almacén

| Código | Requisito | Certeza | Criterio inicial de verificación / límite |
|---|---|---|---|
| RF-PRO-001 | El sistema permitirá gestionar la información de productos comercializados por la empresa. | **[Expreso]** Gestión de productos | Confirmar atributos, identificadores, unidades, categorías y operaciones permitidas antes de definir el contrato. |
| RF-INV-001 | El sistema permitirá gestionar la información necesaria de almacenes. | **[Expreso]** Gestión de almacén | Confirmar si existen uno o varios almacenes, ubicaciones internas y responsables. |
| RF-INV-002 | El sistema registrará históricamente los ingresos, salidas y demás movimientos de inventario. | **[Expreso]** Registro histórico indicado en el enunciado | Verificar que cada movimiento pueda consultarse con su origen, fecha y datos de auditoría acordados. No se presupone método de valorización ni política de stock negativo. |

### 4.2 Clientes, proveedores y ventas

| Código | Requisito | Certeza | Criterio inicial de verificación / límite |
|---|---|---|---|
| RF-COM-001 | El sistema permitirá gestionar clientes y proveedores. | **[Expreso]** Gestión de clientes y proveedores | Confirmar datos requeridos, identificadores, estados y reglas de duplicidad. |
| RF-VEN-001 | El sistema permitirá registrar y consultar ventas de la empresa. | **[Expreso]** Gestión de ventas | Confirmar ciclo de venta, canales, estados, pagos, devoluciones y relación con el inventario antes de cerrar el alcance. |
| RF-VEN-002 | El sistema relacionará la información de una venta con los datos necesarios para emitir su comprobante, cuando corresponda. | **[Inferencia]** Conexión entre ventas y emisión de comprobantes solicitada | Definir los documentos admitidos y el momento de emisión; no se presupone un flujo de caja, crédito ni despacho. |

### 4.3 Comprobantes electrónicos e integración

| Código | Requisito | Certeza | Criterio inicial de verificación / límite |
|---|---|---|---|
| RF-CPE-001 | El sistema contemplará la emisión de comprobantes electrónicos conforme al alcance que se defina para la empresa y al formato UBL Invoice 2.1 indicado por el examen. | **[Expreso]** Emisión electrónica y UBL Invoice 2.1 | Validar la estructura y reglas aplicables contra documentación oficial vigente y confirmar tipos de comprobantes incluidos. UBL 2.1, por sí solo, no acredita aceptación de SUNAT. |
| RF-SUN-001 | El sistema se integrará con los servicios de SUNAT dentro del flujo de comprobantes electrónicos. | **[Expreso]** Integración solicitada | Precisar modalidad de emisión, servicios, autenticación, ambientes, respuestas, reintentos y tratamiento de indisponibilidad. |

### 4.4 Migración, validación y trazabilidad

| Código | Requisito | Certeza | Criterio inicial de verificación / límite |
|---|---|---|---|
| RF-MIG-001 | El sistema contemplará la migración de datos históricos desde el sistema anterior. | **[Expreso]** Migración histórica | Definir datos, periodo, formatos, reglas de transformación, conciliación, errores y aprobación de resultados. |
| RF-VAL-001 | El sistema validará los datos tratados por los procesos incluidos en el alcance. | **[Expreso]** Validación de datos | Acordar reglas por entidad y proceso, momento de validación y forma de reportar errores; no se presuponen reglas tributarias concretas. |
| RF-TRZ-001 | El sistema conservará trazabilidad de los datos y operaciones que se acuerden como relevantes. | **[Expreso]** Conservación de trazabilidad | Determinar eventos, relaciones, datos de auditoría, retención y permisos de consulta antes de diseñar la solución. |

## 5. Requisitos no funcionales

El enunciado fija principalmente restricciones técnicas y arquitectónicas; no establece objetivos cuantitativos de calidad (por ejemplo, rendimiento, disponibilidad o volumen). Se distinguen las condiciones expresas de propuestas para completar posteriormente.

| Código | Requisito / atributo | Certeza | Observación de verificación |
|---|---|---|---|
| RNF-TEC-001 | PostgreSQL será la base de datos del sistema diseñado. | **[Expreso]** | La versión, topología, alojamiento y requisitos operativos están por determinar. |
| RNF-ARQ-001 | La solución tendrá una arquitectura modular basada en MVC y considerará principios de arquitectura hexagonal. | **[Expreso]** | Los límites de módulos, puertos, adaptadores y responsabilidades MVC se definirán en la fase de arquitectura. |
| RNF-SDD-001 | Los contratos de datos y APIs se definirán antes de implementar código. | **[Expreso]** | La revisión de contratos deberá preceder cualquier implementación de las interfaces correspondientes. |
| RNF-DOC-001 | El diseño de comprobantes considerará UBL Invoice 2.1. | **[Expreso]** | Su perfilado para SUNAT y las reglas normativas asociadas deben verificarse; no se afirma cumplimiento en esta etapa. |
| RNF-SEG-001 | Definir requisitos de seguridad, privacidad y control de acceso para datos comerciales y tributarios. | **[Propuesta]** | El enunciado no especifica controles, niveles de acceso, retención ni tratamiento de credenciales. |
| RNF-REL-001 | Definir objetivos de disponibilidad y comportamiento ante fallos de PostgreSQL, conectividad o servicios externos. | **[Propuesta]** | No se especifican SLA, contingencia, colas ni operación offline. |
| RNF-PER-001 | Definir objetivos de rendimiento y capacidad para las operaciones comerciales e inventario. | **[Propuesta]** | No se conocen usuarios concurrentes, transacciones, productos ni tiempos objetivo. |
| RNF-MIG-001 | Definir criterios verificables de integridad, conciliación y recuperación para la migración. | **[Propuesta]** | Depende de la fuente, volumen, calidad de datos y ventana de migración. |

## 6. Restricciones técnicas y arquitectónicas

### 6.1 Expresas

1. **No implementar en esta fase.** El alcance actual es análisis; no se debe programar, crear una aplicación web, instalar dependencias ni generar una base de datos.
2. **PostgreSQL.** La base de datos prevista para el diseño es PostgreSQL.
3. **MVC y arquitectura hexagonal.** La solución debe ser modular y combinar el patrón MVC con principios hexagonales. La forma concreta de articularlos sigue pendiente de diseño.
4. **Spec-First.** Los contratos de datos y APIs deben definirse antes de implementar código.
5. **UBL Invoice 2.1.** El diseño de comprobantes debe considerar este formato.
6. **SUNAT.** El alcance incluye integración con sus servicios, pero no se establece la modalidad técnica ni la normativa aplicable en detalle.

### 6.2 No establecidas por el enunciado

No se fijan lenguaje de programación, framework MVC, estilo de API (REST u otro), mecanismo de autenticación, proveedor de certificados, arquitectura de despliegue, versión de PostgreSQL, infraestructura cloud/on-premise, patrón de mensajería, estrategia de alta disponibilidad ni periodo de retención. Cualquier selección de estos elementos será una decisión de diseño por justificar, no una exigencia del examen.

## 7. Reglas de negocio

### 7.1 Reglas expresas

El enunciado describe capacidades y restricciones de solución, pero no define reglas transaccionales detalladas de ventas, inventario o tributación. Por tanto, en esta fase no hay reglas operativas específicas que puedan declararse confirmadas (por ejemplo, umbrales de identificación, límites de crédito, prohibición de stock negativo, método de costeo o plazos de envío).

Las siguientes son exigencias de alcance confirmadas, no reglas de negocio transaccionales:

- **ALC-01:** Se debe conservar un registro histórico de ingresos, salidas y movimientos de inventario.
- **ALC-02:** Se debe considerar la emisión de comprobantes electrónicos y la integración con SUNAT.
- **ALC-03:** Se deben migrar datos históricos, validar datos y conservar trazabilidad.

Estas declaraciones no definen por sí mismas cómo se ejecutan las transacciones ni garantizan cumplimiento normativo.

### 7.2 Reglas inferidas por validar

- **RN-INF-001:** Los movimientos deben poder asociarse con el producto y almacén correspondientes para consultar el historial. **[Inferencia]**
- **RN-INF-002:** La emisión de un comprobante necesitará datos de emisor, adquirente, conceptos e importes compatibles con el contrato que finalmente se acuerde. **[Inferencia]**
- **RN-INF-003:** Los registros migrados deberán distinguirse de los creados en la operación nueva si se requiere auditar su procedencia. **[Inferencia]**

### 7.3 Decisiones de negocio pendientes

No se deben adoptar como reglas hasta que el responsable las confirme:

- Método de valoración de inventario y tratamiento de ajustes.
- Si se permiten reservas, ventas contra stock insuficiente, stock negativo o ventas sin conexión.
- Estados y autorizaciones para ingresos, salidas, transferencias, devoluciones y ajustes.
- Unidades de medida, fraccionamiento, lotes, vencimientos y productos sujetos a control especial.
- Tipos de comprobantes, series, políticas de emisión, anulaciones, notas y representación al cliente.
- Tratamiento tributario por producto y operación, redondeo, descuentos, medios de pago y ventas a crédito.
- Modalidad de integración con SUNAT u otro proveedor autorizado, autenticación y ambiente de pruebas/producción.
- Alcance temporal y granularidad de la migración, conciliación, correcciones y aceptación de resultados.
- Eventos sujetos a auditoría, datos que se registrarán, retención y permisos de acceso.

## 8. Dudas y ambigüedades del enunciado

1. **Usuarios y responsabilidades:** ¿Qué perfiles humanos usarán el sistema y qué permisos tendrá cada uno?
2. **Productos:** ¿Qué atributos son obligatorios? ¿Se manejarán códigos de barras, varias unidades, presentaciones, lotes o vencimientos?
3. **Almacén:** ¿Cuántos almacenes existen? ¿Se requieren ubicaciones, transferencias, reservas, conteos o ajustes?
4. **Inventario:** ¿Qué método de valoración se utilizará? ¿Cuál es la política ante existencias insuficientes y diferencias físicas?
5. **Ventas:** ¿Qué canales y etapas incluye el proceso? ¿Se requiere POS, cotizaciones, pedidos, crédito, pagos mixtos, devoluciones o despacho?
6. **Clientes y proveedores:** ¿Qué datos e identificadores se registrarán? ¿Se consultarán fuentes externas para verificar o completar información?
7. **Comprobantes:** ¿Qué documentos se deben emitir además de los citados de manera genérica? ¿Cuál es el alcance exacto de UBL Invoice 2.1 y qué perfil nacional se debe aplicar?
8. **SUNAT:** ¿Se utilizará emisión directa, un OSE u otra modalidad permitida? ¿Qué operaciones, credenciales, ambientes, respuestas y contingencias forman parte del alcance?
9. **Normativa:** ¿Qué versión vigente de las especificaciones, catálogos y reglas de validación debe usar el examen? ¿Quién revisará y aprobará la matriz normativa?
10. **Migración:** ¿Qué fuentes y formatos existen? ¿Qué entidades y periodos se migrarán? ¿Se migran documentos y movimientos detallados o solo maestros y saldos? ¿Cómo se concilian y aprueban?
11. **Trazabilidad:** ¿Qué significa en el contexto del examen (historial de cambios, auditoría de usuario, relación entre venta/inventario/comprobante u otros)? ¿Qué retención y controles se requieren?
12. **Calidad del servicio:** ¿Qué volúmenes, usuarios concurrentes, disponibilidad, tiempos de respuesta, copias de seguridad y objetivos de recuperación se esperan?
13. **Contratos:** ¿Qué formatos (por ejemplo, OpenAPI o JSON Schema) exige el curso y quién debe aprobarlos antes de iniciar la implementación?
14. **Arquitectura:** ¿Qué responsabilidad corresponde a MVC y cuál al núcleo hexagonal en la tecnología que se elija? ¿Se espera una aplicación monolítica modular u otro despliegue?

## 9. Matriz inicial de trazabilidad

La matriz conecta las necesidades mencionadas en el enunciado con los requisitos de este análisis. Las referencias regulatorias concretas no se incorporan como obligaciones hasta ser verificadas.

| ID necesidad | Necesidad empresarial del enunciado | Requisitos relacionados | Certeza de origen | Evidencia / verificación inicial |
|---|---|---|---|---|
| NE-01 | Administrar productos y almacén | RF-PRO-001, RF-INV-001 | Expresa | Contratos y escenarios de gestión aprobados por el responsable. |
| NE-02 | Consultar el historial de ingresos, salidas y movimientos de inventario | RF-INV-002, ALC-01 | Expresa | Revisar escenarios de registro y consulta del historial acordado. |
| NE-03 | Gestionar clientes, proveedores y ventas | RF-COM-001, RF-VEN-001 | Expresa | Validar alcance y datos mínimos de cada proceso con los interesados. |
| NE-04 | Emitir comprobantes electrónicos considerando UBL Invoice 2.1 | RF-CPE-001, RF-VEN-002, RNF-DOC-001, ALC-02 | Expresa; relación venta-comprobante inferida | Revisar contrato del documento y contrastar perfil/reglas con fuentes oficiales vigentes antes de afirmar conformidad. |
| NE-05 | Integrarse con servicios de SUNAT | RF-SUN-001, RF-CPE-001, ALC-02 | Expresa | Aprobar modalidad, servicios, ambientes y criterios de aceptación; validar documentación oficial vigente. |
| NE-06 | Migrar datos históricos desde el sistema anterior | RF-MIG-001, RNF-MIG-001, ALC-03 | Expresa; criterios de conciliación propuestos | Inventario de fuentes, mapeo, ejecución de prueba y aprobación de conciliación por definir. |
| NE-07 | Validar datos y conservar trazabilidad | RF-VAL-001, RF-TRZ-001, ALC-03 | Expresa | Definir reglas, eventos y evidencia de auditoría con los interesados. |
| NE-08 | Diseñar una solución modular MVC con visión hexagonal y PostgreSQL | RNF-TEC-001, RNF-ARQ-001 | Expresa | Revisar diagramas, límites modulares y decisiones de arquitectura en la fase correspondiente. |
| NE-09 | Definir contratos antes de implementar | RNF-SDD-001 | Expresa | Verificar que los contratos hayan sido revisados antes de autorizar implementación. |

## 10. Criterio de cierre de esta fase

Esta primera fase queda documentada como **borrador de requisitos**, no como especificación aprobada. Antes de avanzar al modelo de dominio, se recomienda confirmar las preguntas relevantes de las secciones 7 y 8, en especial las reglas de inventario, el alcance de ventas y comprobantes, la modalidad SUNAT, la profundidad de migración y el significado de trazabilidad. Las referencias tributarias deberán contrastarse con fuentes oficiales vigentes en una fase posterior.

La siguiente fase —modelo de dominio— queda pendiente de autorización expresa del usuario.
