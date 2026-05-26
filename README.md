# TheForgeFRMWRK

Framework/prototipo de juego 2D basado en [libGDX](https://libgdx.com/) con generación procedural.
El proyecto está organizado como multi-módulo Gradle para compartir la lógica en `core` y ejecutar en escritorio (`lwjgl3`) y Android (`android`).

## Tabla de contenido

- [Visión general](#visión-general)
- [Stack y versiones](#stack-y-versiones)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Requisitos](#requisitos)
- [Inicio rápido (Windows PowerShell)](#inicio-rápido-windows-powershell)
- [Tareas Gradle útiles](#tareas-gradle-útiles)
- [Empaquetado escritorio](#empaquetado-escritorio)
- [Android](#android)
- [Solución de problemas](#solución-de-problemas)
- [Flujo recomendado de desarrollo](#flujo-recomendado-de-desarrollo)

## Visión general

- `core` contiene la lógica principal del framework y demos de generación procedural.
- `lwjgl3` inicia la app de escritorio (`com.siondream.superjumper.lwjgl3.Lwjgl3Launcher`).
- `android` inicia la app Android (`com.siondream.superjumper.android.AndroidLauncher`).
- `assets` contiene recursos compartidos entre plataformas.

La clase base usada actualmente por los launchers es `com.siondream.superjumper.TheForge`.

## Stack y versiones

- **Gradle Wrapper:** `8.14.3` (definido en `gradle/wrapper/gradle-wrapper.properties`).
- **Toolchain JVM del daemon:** Java `17` (en `gradle/gradle-daemon-jvm.properties`).
- **Compatibilidad de código Java:** nivel `8` en módulos Java (`core`/`lwjgl3`) y Android.
- **libGDX:** `1.14.0` (en `gradle.properties`).
- **Módulos incluidos:** `lwjgl3`, `android`, `core` (en `settings.gradle`).

## Estructura del repositorio

```text
TheForge/
|- assets/                  # recursos compartidos (texturas, datos, etc.)
|- core/                    # lógica de juego/framework compartida
|- lwjgl3/                  # launcher y empaquetado de escritorio (LWJGL3)
|- android/                 # launcher Android y configuración APK
|- build.gradle             # configuración raíz de Gradle
|- settings.gradle          # módulos del proyecto
|- gradle.properties        # versiones y flags del build
|- gradlew / gradlew.bat    # wrapper de Gradle
```

## Requisitos

### Para escritorio

- JDK 17 instalado y disponible en `JAVA_HOME` (recomendado para consistencia con toolchain).

### Para Android

- Android SDK instalado.
- `local.properties` en la raíz con `sdk.dir=...` o `ANDROID_SDK_ROOT` definido.
- Dispositivo/emulador para ejecutar e instalar.

## Inicio rápido (Windows PowerShell)

Desde la raíz del proyecto:

```powershell
cd D:\Documentos\IdeaProjects\TheForge
```

Ejecutar escritorio (LWJGL3):

```powershell
.\gradlew.bat :lwjgl3:run
```

Compilar todo el proyecto:

```powershell
.\gradlew.bat clean build
```

Ver tareas disponibles:

```powershell
.\gradlew.bat tasks --all
```

## Tareas Gradle útiles

- `:lwjgl3:run` - Ejecuta la app en escritorio.
- `:lwjgl3:jar` - Genera JAR ejecutable en `lwjgl3/build/libs`.
- `:lwjgl3:dist` - Alias de compatibilidad para `jar`.
- `:android:assembleDebug` - Genera APK debug.
- `:android:installDebug` - Instala APK debug en dispositivo conectado.
- `:android:run` - Lanza la Activity con `adb` (si la app ya está instalada).
- `:android:lint` - Análisis estático Android.
- `clean` / `build` - Limpia y compila todos los módulos.

Flags útiles:

- `--stacktrace` para diagnóstico detallado.
- `--refresh-dependencies` para forzar resolución de dependencias.
- `--offline` para usar caché local.

## Empaquetado escritorio

JAR universal:

```powershell
.\gradlew.bat :lwjgl3:jar
```

JAR específico por plataforma:

```powershell
.\gradlew.bat :lwjgl3:jarWin
.\gradlew.bat :lwjgl3:jarLinux
.\gradlew.bat :lwjgl3:jarMac
```

También existe configuración con `construo` en `lwjgl3/build.gradle` para empaquetado nativo multiplataforma.

## Android

Compilar e instalar debug:

```powershell
.\gradlew.bat :android:assembleDebug
.\gradlew.bat :android:installDebug
```

Abrir la app instalada con ADB (task custom):

```powershell
.\gradlew.bat :android:run
```

Notas:

- `minSdkVersion 21`, `targetSdkVersion 35`, `compileSdk 35`.
- Se copian librerías nativas (`*.so`) automáticamente con la tarea `copyAndroidNatives`.

## Solución de problemas

### Error Gradle: `metadata.bin (El sistema no puede encontrar el archivo especificado)`

Suele deberse a caché local corrupta en `%USERPROFILE%\.gradle\caches`.

```powershell
cd D:\Documentos\IdeaProjects\TheForge
.\gradlew.bat --stop
Remove-Item -Recurse -Force "C:\Users\$env:USERNAME\.gradle\caches\8.14.3\groovy-dsl" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "C:\Users\$env:USERNAME\.gradle\caches\8.14.3\scripts" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "C:\Users\$env:USERNAME\.gradle\caches\8.14.3\kotlin-dsl" -ErrorAction SilentlyContinue
.\gradlew.bat clean build --refresh-dependencies --stacktrace
```

Si persiste:

```powershell
.\gradlew.bat --stop
Remove-Item -Recurse -Force "C:\Users\$env:USERNAME\.gradle\caches" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "C:\Users\$env:USERNAME\.gradle\daemon" -ErrorAction SilentlyContinue
.\gradlew.bat clean build --stacktrace
```

### Dependencias no descargan

- Verifica conexión/red corporativa/proxy.
- Reintenta con `--refresh-dependencies`.
- Si usas mirror local, revisa `mavenLocal()` y repositorios en `build.gradle`.

### Android SDK no encontrado

- Define `sdk.dir` en `local.properties` o `ANDROID_SDK_ROOT` en el entorno.

## Flujo recomendado de desarrollo

1. Implementa lógica compartida en `core`.
2. Prueba rápido con `:lwjgl3:run`.
3. Valida Android con `:android:assembleDebug`.
4. Genera artefactos (`:lwjgl3:jar` o tareas por plataforma) al cerrar iteración.
