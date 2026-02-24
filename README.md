# muta-conversion-system 🔄

**Muta Conversion System** es un motor de cálculo avanzado diseñado para gestionar la conversión bidireccional de medidas de materiales. El sistema garantiza la integridad de los datos financieros y logísticos al normalizar diversas unidades de entrada (Masa y Volumen) hacia una unidad base estandarizada en la base de datos (**Kilogramos**).

## 🚀 Propósito del Sistema

En la gestión de residuos y materiales, los registros pueden ocurrir en múltiples unidades. Este sistema:
1.  **Normaliza** las entradas (App -> BD): Convierte cualquier unidad a KG para almacenamiento.
2.  **Traduce** las salidas (BD -> App): Convierte de KG a la unidad preferida del usuario para visualización.
3.  **Protege** la precisión financiera mediante el uso estricto de `BigDecimal`.

---

## 🏗️ Arquitectura de Conversión

El sistema implementa el patrón **Strategy**, permitiendo que cada unidad de medida gestione su propia lógica matemática.

### Unidades Soportadas y Factores

| Unidad | Clase | Tipo | Factor / Base |
| :--- | :--- | :--- | :--- |
| **KG** | `KgStrategy` | Masa | **Unidad Base (1:1)** |
| **LB** | `LbStrategy` | Masa | 0.45359237 |
| **TON** | `TonKgStrategy` | Masa | 1000.0 |
| **L** | `LStrategy` | Volumen | Basado en Densidad |
| **GAL US** | `GalUsStrategy` | Volumen | 3.78541 L + Densidad |
| **GAL UK** | `GalUkStrategy` | Volumen | 4.54609 L + Densidad |
| **UNI** | `UnitStrategy` | Conteo | Piezas (Sin conversión) |

---

## ⚠️ Regla de Oro: Contingencia para Medidas Inexistentes

El sistema es tolerante a errores de configuración. Si un usuario o licencia intenta utilizar una unidad de medida que no existe en nuestro sistema (ejemplo: "LT" en lugar de "L", "Bolsas", "Cajas"), el `ConversionContextService` aplicará la siguiente lógica:

> **Si la unidad es desconocida o nula, se trabajará automáticamente como KG.**

Esto evita que la aplicación se rompa y asegura que el valor se procese bajo la unidad de masa estándar por defecto, garantizando que el subtotal y el peso neto sigan siendo coherentes.

---

## 📦 Casos de Uso y Ejemplos de Estrategias

El comportamiento del sistema varía drásticamente si el material es un **Contenedor** o un **Material a Granel**.

### Caso 1: Material Estándar (`isContainer = false`)
Se convierte **todo**: cantidad, peso neto y precio unitario.

* **Ejemplo (Salida de BD a App - Conversión a Libras):**
    * **BD**: 100 KG | Precio: $2.00/KG | Subtotal: $200.00
    * **App (LB)**: 220.46 LB | Precio: $0.91/LB | Subtotal: $200.00
    * *Resultado: El valor monetario se mantiene, pero se traduce a la escala de libras.*

* **Ejemplo (Entrada de App a BD - Conversión de Litros a KG con Densidad 0.8):**
    * **App**: 100 L | Precio: $1.50/L | Subtotal: $150.00
    * **BD (KG)**: 80.00 KG | Precio: $1.875/KG | Subtotal: $150.00

### Caso 2: Contenedor Logístico (`isContainer = true`)
Se protege la unidad comercial (Quantity y Price) y solo se convierte la masa (Net Quantity).

* **Ejemplo (Salida de BD a App - Conversión a Galones con Densidad 1.1):**
    * **BD**: 10 Tambores | Peso Neto: 1000 KG | Precio: $50.00/Tambor | Subtotal: $500.00
    * **App (GAL)**: 10 Tambores | Peso Neto: 239.89 GAL | Precio: $50.00/Tambor | Subtotal: $500.00
    * *Resultado: El usuario sigue viendo "10 Tambores", pero el peso del contenido se muestra en la unidad de volumen deseada.*

---

## 🛠️ Detalles Técnicos

### 1. Manejo de Densidad
Para las estrategias de volumen (`L`, `GAL`), el sistema extrae la densidad directamente del material asociado:
```java
BigDecimal density = material.getDensity();
