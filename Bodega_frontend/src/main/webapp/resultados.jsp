<%@page contentType="text/html" pageEncoding="UTF-8" %> 
<!-- Importación de la librería de etiquetas del servidor de Jakarta --> 
<%@taglib prefix="c" uri="jakarta.tags.core" %> 
<%@taglib prefix="fn" uri="jakarta.tags.functions" %> 

<!DOCTYPE html> 
<html>     
    <head>         
        <meta charset="UTF-8">         
        <title>Bodega HDP</title>     
    </head>     
    <body>         
        <h1>Bodega Hielo, Dilema y Pasión</h1>         
        <h2>Catálogo de Productos</h2>                 
        <!-- Evaluamos de forma condicional el tamaño de la lista de mapas recibida -->         
        <c:choose>             
            <%-- CASO A: Si la lista contiene elementos, generamos el HTML de la tabla --%>             
            <c:when test="${not empty listaParaMostrar}">                 
                <table border="1" cellpadding="8">                     
                    <thead>                         
                        <tr>                             
                            <th>Categoría</th>                             
                            <th>Marca del Licor</th>                             
                            <th>Presentación Visual</th>                         
                        </tr>                     
                    </thead> 
                    <tbody>                         
                        <%-- El bucle itera sobre los mapas. La variable 'licor' actúa como el mapa actual --%>                         
                        <c:forEach var="itemTexto" items="${listaParaMostrar}">                             
                            <%-- 1. Trocemos la línea usando la barra vertical como criterio --%>                             
                            <c:set var="datos" value="${fn:split(itemTexto, '|')}" /> 
                            <tr>                                 
                                <%-- 2. Accedemos de forma estricta a cada posición del arreglo --%>                                 
                                <td class="text-capitalize fw-bold">${datos[0]}</td>                                 
                                <td class="text-start ps-4">${datos[1]}</td>                                 
                                <td>                                     
                                    <img src="img/${datos[2]}" alt="${datos[1]}" class="img-thumbnail" width="100">                                 
                                </td>                             
                            </tr>                         
                        </c:forEach>                     
                    </tbody>                 
                </table>             
            </c:when> 
            <%-- CASO B: Si la lista de mapas llegó vacía desde el controlador --%>             
            <c:otherwise>                 
                <div style="color: darkred; font-family: sans-serif; padding: 15px;">                     
                    <p>No existen productos disponibles para la categoría seleccionada.</p>                     
                    <img src="img/soldout.jpg" alt="Sin inventario disponible" width="150">                 
                </div>             
            </c:otherwise>         
        </c:choose> 
        <br><br>         
        <!-- Enlace de regreso limpio hacia el inicio de la navegación -->         
        <a href="index.jsp"><button type="button">Regresar a la Página Principal</button></a>     
    </body> 
</html>