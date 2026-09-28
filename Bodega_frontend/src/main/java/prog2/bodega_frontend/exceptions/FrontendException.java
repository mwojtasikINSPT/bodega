package prog2.bodega_frontend.exceptions;

public class FrontendException extends Exception {

    public FrontendException(String mensaje) {
        super(mensaje);
    }

    public FrontendException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
