# muta-conversion-system 🔄

**Muta Conversion System** es un motor de cálculo robusto diseñado para gestionar la conversión bidireccional de medidas de materiales. El sistema garantiza la integridad de los datos financieros y logísticos al normalizar diversas unidades de entrada (Masa y Volumen) hacia una unidad base estandarizada en la base de datos (**Kilogramos**).

## 🚀 Propósito del Sistema

En la gestión de residuos y materiales, los registros pueden ocurrir en múltiples unidades según el país o el tipo de cliente. Este sistema:
1.  **Normaliza** las entradas de la App hacia la Base de Datos (App -> BD).
2.  **Traduce** los datos de la Base de Datos para visualización personalizada (BD -> App).
3.  **Protege** la precisión decimal mediante el uso exclusivo de `BigDecimal`.

---

## 🏗️ Arquitectura de Conversión

El sistema implementa el patrón **Strategy**, permitiendo que cada unidad de medida gestione su propia lógica matemática y reglas de redondeo.

### Estrategias de Conversión Soportadas

| Unidad | Clase | Tipo | Comportamiento |
| :--- | :--- | :--- | :--- |
| **KG** | `KgStrategy` | Masa | Unidad base (Identidad 1:1) |
| **LB** | `LbStrategy` | Masa | Conversión mediante factor 0.45359237 |
| **TON** | `TonKgStrategy` | Masa | Conversión mediante factor 1000.0 |
| **L** | `LStrategy` | Volumen | Conversión basada en **Densidad** del material |
| **GAL US** | `GalUsStrategy` | Volumen | Galón Americano (3.78541 L) + Densidad |
| **GAL UK** | `GalUkStrategy` | Volumen | Galón Británico (4.54609 L) + Densidad |
| **UNI** | `UnitStrategy` | Conteo | Manejo de piezas o unidades físicas |

---

## 📦 Lógica de Contenedores vs. Materiales

Una de las características críticas de este sistema es la discriminación basada en el tipo de carga (`isContainer`):

### Caso A: Material a Granel (`isContainer = false`)
Se realiza una conversión integral. Si el usuario cambia de KG a Libras:
* La **Cantidad** aumenta.
* El **Peso Neto** aumenta.
* El **Precio Unitario** disminuye proporcionalmente.
* El **Subtotal** se recalcula para mantener la consistencia.

### Caso B: Contenedor Logístico (`isContainer = true`)
Diseñado para registros tipo "Barriles", "Cajas" o "IBCs":
* **Quantity Collected**: No se convierte (se mantiene el conteo de envases).
* **Unit Price**: No se convierte (el precio es por envase, no por peso).
* **Net Quantity**: **Sí se convierte** (se requiere saber el peso real del contenido en la unidad destino).
* **Subtotal**: Se calcula directamente como `Quantity * UnitPrice`.



---

## 🛠️ Componentes Técnicos

### 1. ConversionContextService
El orquestador del sistema. Se encarga de:
* Identificar la estrategia correcta mediante el `targetUnit`.
* Aplicar **Fallbacks**: Si una unidad no existe (ej. "LT"), el sistema redirige automáticamente a **KG** para evitar fallos en la transacción.
* Priorizar la unidad **UNI** sobre configuraciones globales si el material lo requiere.

### 2. Manejo de Densidad
Para las estrategias de volumen (`L`, `GAL`), el sistema inyecta dinámicamente la densidad del material:
```java
BigDecimal density = material.getDensity(); // Ejemplo: 0.9 para aceites
