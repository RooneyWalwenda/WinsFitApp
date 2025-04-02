package appointment;

public class ResponseModel {
	Object data;
	String messageCode;
	String message;
	public ResponseModel() {
		
	}
	
public ResponseModel(String message, String messageCode) {
	this.message=message;
	this.messageCode=messageCode;
		
	}
public ResponseModel(String message, String messageCode, Object data) {
	this.message=message;
	this.messageCode=messageCode;
	this.data=data;
		
	}

public Object getData() {
	return data;
}

public void setData(Object data) {
	this.data = data;
}

public String getMessageCode() {
	return messageCode;
}

public void setMessageCode(String messageCode) {
	this.messageCode = messageCode;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

}
