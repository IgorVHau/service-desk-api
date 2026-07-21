package service_desk_api.api.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import service_desk_api.api.dto.ApiResponse;
import service_desk_api.api.model.Status;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private ProblemDetail buildProblem(
			URI type,
			String title,
			HttpStatus status,
			String detail,
			HttpServletRequest request
			) {
		
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
		
		problem.setType(type);
		problem.setTitle(title);
		problem.setInstance(URI.create(request.getRequestURI()));
		problem.setProperty("timestamp", LocalDateTime.now());
		
		return problem;
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining("; "));
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("Erro de validação: " + errors, 400));
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("Erro interno: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<?>> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
		var message = ex.getMostSpecificCause().toString().contains("Status")
				? "O campo status não foi preenchido com o valor correto. Por favor, escolha entre as opções: "
						+ Status.ABERTO + ", " + Status.EM_ANDAMENTO + ", " + Status.CONCLUIDO
				: ex.getMessage();
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(message, HttpStatus.BAD_REQUEST.value()));
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
		String message = ex.getMessage();
		
		ProblemDetail problem = buildProblem(
				URI.create("about:blank"),
				"Não encontrado",
				HttpStatus.NOT_FOUND,
				message,
				request);
		
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(problem);
	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
		return ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(ApiResponse.error(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<?>> handleGenericException(Exception e) {
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("Erro inesperado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}

}
