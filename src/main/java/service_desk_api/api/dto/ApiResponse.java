package service_desk_api.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
	
	private LocalDateTime timestamp;
	private int status;
	private String message;
	private T data;
	
	public static <T> ApiResponse<T> success(String message, T data, int statusCode) {
		return ApiResponse.<T>builder()
				.timestamp(LocalDateTime.now())
				.status(statusCode)
				.message(message)
				.data(data)
				.build();
	}
	
	public static <T> ApiResponse<T> error(String message, int statusCode) {
		return ApiResponse.<T>builder()
				.timestamp(LocalDateTime.now())
				.status(statusCode)
				.message(message)
				.build();
	}

}
