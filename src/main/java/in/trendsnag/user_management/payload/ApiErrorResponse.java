package in.trendsnag.user_management.payload;

import java.time.LocalDateTime;

public class ApiErrorResponse {

	private LocalDateTime timestamp;
	private String  message;
	private String error;
	private int status;
	private String path;
	
	public ApiErrorResponse(int status, String message, String error, String path) {
		this.timestamp = LocalDateTime.now();
		this.error = error;
		this.message = message;
		this.status = status;
		this.path = path;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getError() {
		return error;
	}

	public int getStatus() {
		return status;
	}

	public String getPath() {
		return path;
	}
	
}
