package ch.unisg.ics.interactions.hmas.core.io;

public class InvalidResourceProfileException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public InvalidResourceProfileException(String errorMessage) {
    super(errorMessage);
  }

  public InvalidResourceProfileException(String errorMessage, Exception e) {
    super(errorMessage, e);
  }

}
