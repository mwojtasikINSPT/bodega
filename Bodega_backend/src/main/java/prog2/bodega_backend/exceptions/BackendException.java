package prog2.bodega_backend.exceptions;

public class BackendException extends Exception {

    public BackendException(String mensaje) {
        super(mensaje);
    }

    public BackendException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
