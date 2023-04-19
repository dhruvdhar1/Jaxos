package api;

import java.io.Serializable;

public class KeyValueResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private String operation;
  private String value;
  private boolean success;
  private String errorMsg;

  public KeyValueResponse() {
  }

  public KeyValueResponse(String operation, String value, boolean success, String errorMsg) {
    this.operation = operation;
    this.value = value;
    this.success = success;
    this.errorMsg = errorMsg;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  @Override
  public String toString() {
    return "KeyValueResponse{" +
            "operation='" + operation + '\'' +
            ", value='" + value + '\'' +
            ", success=" + success +
            ", errorMsg='" + errorMsg + '\'' +
            '}';
  }
}
