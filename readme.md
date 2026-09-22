# 🍷 Bodega HDP (Hielo, Dilema y Pasión)

Proyecto final desarrollado para la materia Programación II. Implementa un sistema de gestión de catálogo de licores aplicando una arquitectura cliente-servidor basada en el patrón MVC, separando claramente la interfaz de usuario de la lógica de negocio y persistencia de datos.

## 🚀 Tecnologías Utilizadas

* **Lenguaje:** Java 17+
* **Backend:** Jakarta EE 10 (Servlets), JDBC para la comunicación con el SGBD.
* **Frontend:** JSP, HTML, CSS, y JSTL (Jakarta Standard Tag Library).
* **Base de Datos:** MySQL.
* **Servidor Web:** Apache Tomcat 10+.
* **Gestor de Dependencias:** Maven.

## 📁 Arquitectura del Proyecto (Monorepo)

El repositorio contiene dos proyectos independientes que se comunican entre sí mediante peticiones HTTP:

* 🧠 **`Bodega_backend` (Capa del Servidor):** Funciona como el núcleo del sistema. Se encarga de procesar la lógica de negocio, conectarse de forma segura a MySQL a través de *Connector/J*, y exponer un endpoint que devuelve los datos estructurados en formato JSON.
* 🖥️ **`Bodega_frontend` (Capa del Cliente):** Es la interfaz visual interactiva. Inicia un cliente HTTP nativo en Java para consumir la API del backend y utiliza etiquetas `<c:forEach>` y `<c:if>` de JSTL para renderizar dinámicamente el catálogo HTML.

## 🗄️ Configuración de la Base de Datos

Para ejecutar este proyecto localmente, es necesario contar con un servidor MySQL activo.

1. Abre **MySQL Workbench** o tu cliente SQL preferido e inicia sesión con tu usuario local.
2. Ejecuta el siguiente script para crear la base de datos, la estructura relacional y poblar los datos de prueba:

```sql
CREATE DATABASE bodega_licores; 
USE bodega_licores; 

CREATE TABLE licores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    foto VARCHAR(50) NOT NULL 
); 

INSERT INTO licores (tipo, marca, foto) VALUES    
    ('ron', 'Flor de Caña 12 Años', 'flor_de_cana.png'),    
    ('ron', 'Barceló Imperial', 'barcelo.png'),    
    ('whisky', 'Johnnie Walker Black Label', 'black_label.png'),    
    ('whisky', 'Chivas Regal', 'chivas_regal.png'),    
    ('whisky', 'Ballantines', 'ballantines.png'),    
    ('cerveza', 'Corona', 'corona.png'),    
    ('cerveza', 'Cuzqueña', 'cuzquena.png'),    
    ('cerveza', 'Patagonia', 'patagonia.png'),    
    ('vino', 'La Linda', 'linda.png'),    
    ('vino', 'Rutini', 'rutini.png'); 
```

## ⚙️ Instalación y Despliegue

1. **Clonar el repositorio:**
   Clona este proyecto en tu máquina local utilizando Git.

2. **Configuración de Seguridad (Credenciales):**
   Por motivos de seguridad, las credenciales de la base de datos están excluidas del control de versiones. Debes crear un archivo llamado `config.properties` en la ruta `Bodega_backend/src/main/resources/` con la siguiente estructura:
   ```properties
   db.url=jdbc:mysql://localhost:3306/bodega_licores
   db.user=tu_usuario_de_mysql
   db.password=tu_contraseña_secreta
   ```

3. **Compilación y Arranque del Backend:**
   * Abre los proyectos en Apache NetBeans.
   * Haz clic derecho sobre el proyecto **`Bodega_backend`** y selecciona **Clean and Build**. Maven descargará el driver JDBC de MySQL automáticamente.
   * Haz clic derecho nuevamente y selecciona **Run** para levantar el servidor en Tomcat. (Es normal ver un error 404 en el navegador en este paso, ya que el backend solo responde a rutas específicas).

4. **Compilación y Arranque del Frontend:**
   * Haz clic derecho sobre el proyecto **`Bodega_frontend`** y selecciona **Clean and Build**.
   * Haz clic derecho y selecciona **Run**.
   * El navegador se abrirá automáticamente en `http://localhost:8080/Bodega_frontend/` con la interfaz lista para usar.