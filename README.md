# Mini Web Application — Networking Lab Part 2

Servidor HTTP hecho con sockets de Java que sirve archivos estáticos y tiene unos servicios JSON hardcodeados. Se despliega en una instancia EC2 de AWS.

---

## ¿Qué hace?

Es una aplicación web pequeña que corre en un solo hilo. El servidor atiende una conexión a la vez: recibe la petición, responde, y ahí sí atiende la siguiente. No hay concurrencia, eso es intencional para entender el comportamiento base antes de agregar threads.

---

## Arquitectura

El servidor funciona como una ventanilla única: cada petición espera su turno, se atiende completa, y solo entonces entra la siguiente.

```
Browser
  │
  │  HTTP GET /index.html, /api/greeting?name=X, ...
  ▼
[ EC2 Security Group ]  →  solo puertos 35000 y 22
  │
  ▼
[ WebApp.java — loop secuencial con ServerSocket ]
  ├── Archivos estáticos  →  src/main/resources/static/
  │     index.html, app.js, photo1.jpg, photo2.png
  └── Servicios
        /api/health     →  {"status":"UP"}
        /api/time       →  {"time":"..."}
        /api/greeting   →  {"greeting":"Hello, <name>!"}
        /api/square     →  {"input":n,"square":n²}
```

- El browser carga la página y desde `app.js` hace fetch a los servicios sin recargar
- `WebApp.java` atiende todo: archivos y servicios, uno a la vez
- Las rutas están en un `switch` explícito, sin frameworks

---

## Decisiones de diseño

- **Secuencial a propósito**: para ver dónde está el límite antes de agregar concurrencia
- **Rutas hardcodeadas**: el `switch` hace visible qué path hace qué. Un framework lo ocultaría
- **Archivos como bytes**: tanto texto como imágenes se leen como bytes, así no hay corrupción de encoding
- **Protección path traversal**: se normaliza el path y se verifica que esté dentro del directorio estático
- **Cliente asíncrono**: `fetch` no bloquea el browser, pero eso no hace al servidor concurrente

---

## Estructura

```
src/
├── main/
│   ├── java/networking/
│   │   ├── WebApp.java          # servidor principal (Lab Part 2)
│   │   ├── URLExample.java      # ejercicios 1 y 2: métodos URL + mini-browser
│   │   ├── TCPServer.java       # echo server TCP
│   │   ├── TCPClient.java       # echo client TCP
│   │   ├── SquareServer.java    # ejercicio 4.3.1
│   │   ├── FunctionServer.java  # ejercicio 4.3.2: sin/cos/tan
│   │   ├── HttpServer.java      # sección 4.4: una sola petición
│   │   ├── MultiHttpServer.java # ejercicio 4.5.1: múltiples peticiones
│   │   ├── UDPServer.java
│   │   ├── UDPClient.java
│   │   ├── UDPTimeServer.java
│   │   └── UDPTimeClient.java
│   └── resources/static/
│       ├── index.html
│       ├── app.js
│       └── images/
│           ├── photo1.jpg
│           └── photo2.png
└── test/java/networking/
    └── WebAppTest.java
pom.xml
```

---

## Requisitos

- Java 17+
- Maven 3.8+

---

## Build

```bash
git clone <repo-url>
cd taller_tdse_nicolas_parrado
mvn package
```

Genera `target/webserver.jar` y corre los tests.

---

## Correr local

```bash
java -jar target/webserver.jar
# puerto personalizado:
java -jar target/webserver.jar 8080
```

Abrir `http://localhost:35000` en el browser.

---

## Servicios disponibles

| URL | Parámetro | Respuesta |
|---|---|---|
| `/api/health` | — | `{"status":"UP"}` |
| `/api/time` | — | `{"time":"..."}` |
| `/api/greeting?name=X` | name | `{"greeting":"Hello, X!"}` |
| `/api/square?value=N` | value | `{"input":N,"square":N²}` |

Errores:
- Parámetro faltante o inválido → 400
- Archivo no encontrado → 404
- Método no soportado → 405
- Path traversal → 400

---

## Tests

```bash
mvn test
```

Cubren: escape de JSON, parseo de query params, normalización de paths.

---

## Despliegue en EC2

```bash
# empaquetar
mvn package

# subir a la instancia
scp -i <key.pem> target/webserver.jar ec2-user@<EC2-IP>:~/
scp -i <key.pem> -r src/main/resources/static ec2-user@<EC2-IP>:~/

# instalar Java en Amazon Linux 2023
sudo dnf install java-17-amazon-corretto -y

# correr
java -jar webserver.jar 35000
```

Para que quede corriendo después de cerrar sesión, configurar como servicio systemd:

```ini
[Unit]
Description=Mini Web Server

[Service]
ExecStart=/usr/bin/java -jar /home/ec2-user/webserver.jar 35000
WorkingDirectory=/home/ec2-user
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable webserver
sudo systemctl start webserver
```

Security group: puerto 22 solo desde tu IP, puerto 35000 abierto para el browser.

---

## Limitación secuencial

Si dos browsers hacen peticiones al mismo tiempo, la segunda espera hasta que la primera termine. El `fetch` en JavaScript no bloquea el browser, pero el servidor Java sigue siendo de una petición a la vez.

---

## Limitaciones conocidas

- Una petición a la vez, sin threads
- Solo GET
- Rutas hardcodeadas
- Sin HTTPS ni autenticación

---

## Autor

Nicolas Parrado — Escuela Colombiana de Ingeniería
