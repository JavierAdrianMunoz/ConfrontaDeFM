# Revisión de Cartografía en Inmuebles

## Descripción

Este proyecto es un programa en Java diseñado para la revisión de cartografía en inmuebles. El programa procesa datos en formato JSON y genera informes detallados de cualquier error encontrado en la cartografía. Además, puede trabajar con archivos comprimidos y proporciona una estructura clara para el análisis de los datos.

## Requisitos

- Java JDK 8 o superior
- Un editor de texto o un IDE como IntelliJ IDEA, Eclipse, VSCode

## Instalación

1. **Clona el repositorio:**

   ```bash
   git clone https://github.com/JavierAdrianMunoz/ConfrontaDeFM.git
   cd revision-cartografia-inmuebles
   ```


## Uso

### Datos de Prueba

El programa incluye JSON de prueba ubicados en la carpeta `raiz/resources/INSUMOS/MINI CENSO 2020`. Puedes seleccionar esta carpeta para usar los archivos comprimidos o JSON directamente.

### Ejecución del Programa

1. **Selecciona la carpeta de insumos:**

   Selecciona la carpeta `raiz/resources/INSUMOS/MINI CENSO 2020`. El programa identificará automáticamente los archivos a descomprimir o procesar.

### Generación de Informes

El programa genera varios informes y logs:

1. **Log de errores:**

   En la carpeta `raiz/OUTPUT/{fecha-en-la-que-se-realiza}`, se generan tres archivos `.txt` llamados `manzana`, `frentes`, y `localidad` que detallan todos los errores encontrados en la cartografía.

2. **Archivo Excel:**

   Se genera un archivo Excel con la información concatenada de los archivos `.txt`, dividida por pestañas.

3. **Visualización de Errores:**

   Si se cargan los JSON individualmente o se realiza la confronta de manera individual, se mostrarán 2 o 3 tablas (localidades es opcional):

   - **Manzanas con Frentes:**
     - Las manzanas con frentes se denotan por su CVEGEO o clave geostadística. Si una manzana tiene frentes en `frentes.json`, se pinta de color verde. Si no, se pinta de rojo indicando error.
   - **Revisión de Polígonos:**
     - Se verifica que los polígonos sean de tipo multipolígono. Si se encuentra un "polygon", el texto se pinta de rojo indicando "Error".
   - **Revisión en frentes.json y localidades.json:**
     - Se verifica que contengan multipoint o multipolígono, según corresponda. Los errores se indican en rojo y los correctos en verde.

4. **Selección de Carpeta:**

   Al seleccionar la opción "Seleccionar carpeta" y elegir `raiz/INSUMOS/MINI CENSO 2020/`, el programa identificará automáticamente los archivos a descomprimir o procesar, incluso si contiene subcarpetas.

5. **Log Detallado:**

   Se mostrará un log detallado del archivo a procesar (`mza.json`, `frentes.json`, `locpunt.json`). Al finalizar, se abrirán 3 ventanas con gráficos detallando los errores y su cantidad. Además, se generará un log en `raiz/OUTPUT/Log/{fecha-del-dia-que-se-genera}` con 4 archivos: un Excel con el resumen de los errores y archivos individuales en `.txt`.

### Tiempo de Procesamiento

Dependiendo de la cantidad de archivos JSON o ZIP, el procesamiento puede tomar varios minutos. Este ejemplo tiene precargados varios cientos de archivos similares entre sí, por lo que puede tomar de 5 a 10 minutos en procesarse.
