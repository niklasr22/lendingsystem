package exceptions;

public class LoadSaveException extends Exception {

    public LoadSaveException (String message, Throwable cause) {
        super (message, cause);
    }

    public String getDetails () {
        if (getCause () == null)
            return "no details";
        else
            return getCause().getClass() + ", " + getCause().getMessage();
    }
}
