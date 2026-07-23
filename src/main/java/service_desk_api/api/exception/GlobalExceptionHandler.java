package service_desk_api.api.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
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
	
	private String buildReadableMessage(String cause, HttpMessageNotReadableException ex) {
		if (cause.contains("Status")) {
			return "O campo status não foi preenchido com o valor correto. Por favor, escolha entre as opções: "
	                + Status.ABERTO + ", " + Status.EM_ANDAMENTO + ", " + Status.CONCLUIDO;
		}
		
		if (cause.contains("Unrecognized field")) {
			return "Campo desconhecido inserido.";
		}
		
		if (cause.contains("Unexpected end") 
				|| cause.contains("Unexpected character")
				|| cause.contains("com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of")) {
			return "JSON malformado.";
		}
		return ex.getMostSpecificCause().toString();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
		String errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(" "));
		
		ProblemDetail problem = buildProblem(
				URI.create("about:blank"),
				"Requisição inválida",
				HttpStatus.BAD_REQUEST,
				"Erro de validação: " + errors,
				request);
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(problem);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ProblemDetail> handleMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
		var specificCause = ex.getMostSpecificCause().toString();
		final String message = buildReadableMessage(specificCause, ex);
		var title = "Requisição inválida";
		
		ProblemDetail problem = buildProblem(
				URI.create("about:blank"),
				title,
				HttpStatus.BAD_REQUEST,
				message,
				request
				);
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(problem);
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
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ProblemDetail> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
		
		ProblemDetail problem = buildProblem(
				URI.create("about:blank"),
				"Não encontrado",
				HttpStatus.NOT_FOUND,
				"O recurso solicitado não foi encontrado.",
				request
				);
		
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(problem);
	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex, HttpServletRequest request) {
		ProblemDetail problem = buildProblem(
				URI.create("about:blank"),
				"Conteúdo não processável",
				HttpStatus.UNPROCESSABLE_ENTITY,
				ex.getMessage(),
				request);
		
		return ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(problem);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, HttpServletRequest request) {
		ProblemDetail problem = buildProblem(
				URI.create("about:blank"),
				"Erro interno do servidor",
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Ocorreu um erro interno ao processar a solicitação.",
				request
				);
		
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(problem);
	}

}
