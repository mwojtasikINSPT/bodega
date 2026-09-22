<%@page contentType="text/html" pageEncoding="UTF-8" %> 
<!DOCTYPE html> 
<html>     
    <head>         
        <meta charset="UTF-8">         
        <title>Bodega HDP</title>     
    </head>     
    <body>         
        <h1>Bodega Hielo, Dilema y Pasión</h1>         
        <h2>Filtro del Inventario</h2>                 
        <!-- El formulario apunta directamente a la URL de nuestro Servlet intermedio -->         
        <form action="buscar" method="GET">             
            <label for="tipo">Seleccione un tipo de licor:</label>             
            <select name="tipo" id="tipo">                 
                <option value="ron">Rones</option>                 
                <option value="whisky">Whiskies</option>                 
                <option value="pisco">Piscos</option>                 
                <option value="cerveza">Cervezas</option>                 
                <option value="vino">Vinos</option>             
            </select>             
            <br><br>             
            <button type="submit">Mostrar</button>         
        </form>     
    </body> 
</html>