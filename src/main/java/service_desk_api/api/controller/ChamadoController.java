package service_desk_api.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import service_desk_api.api.service.ChamadoService;
import service_desk_api.api.dto.ApiResponse;
import service_desk_api.api.dto.ChamadoRequest;
import service_desk_api.api.dto.ChamadoResponse;
import service_desk_api.api.exception.ResourceNotFoundException;
import service_desk_api.api.mapper.ChamadoMapper;
import service_desk_api.api.model.Chamado;

import java.util.List;


@RestController
@RequestMapping("/chamados")
public class ChamadoController {
	
	private final ChamadoService service;
	
	public ChamadoController(ChamadoService service) {
		this.service = service;
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<ChamadoResponse>>> listar() {
		List<ChamadoResponse> chamados = service.listarTodos()
				.stream()
				.map(ChamadoMapper::paraResponse)
				.toList();
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse.success("Lista de chamados", chamados, HttpStatus.OK.value()));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ChamadoResponse>> buscarPorId(@PathVariable Long id) {
		Chamado chamadoEncontrado = service.buscarPorIdOuFalhar(id);
		ChamadoResponse chamado = ChamadoMapper.paraResponse(chamadoEncontrado);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse.success("Chamado encontrado.", chamado, HttpStatus.OK.value()));
	}

	@PostMapping
	@Transactional
	public ResponseEntity<ApiResponse<ChamadoResponse>> criar(@RequestBody @Valid ChamadoRequest request) {
		Chamado chamado = ChamadoMapper.paraEntidade(request);
		Chamado chamadoCriado = service.criar(chamado);
		ChamadoResponse response = ChamadoMapper.paraResponse(chamadoCriado);
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Chamado criado com sucesso", response, HttpStatus.CREATED.value()));
	}
	
	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<ApiResponse<ChamadoResponse>> atualizar(
			@PathVariable Long id, 
			@RequestBody @Valid ChamadoRequest request) {
		Chamado novoChamado = ChamadoMapper.paraEntidade(request);
		Chamado novoChamadoAtualizado = service.atualizar(id, novoChamado);
		ChamadoResponse response = ChamadoMapper.paraResponse(novoChamadoAtualizado);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse.success("Chamado atualizado com sucesso.", response , HttpStatus.OK.value()));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<ApiResponse<Void>> deletar(@PathVariable Long id) {
		service.deletar(id);
		
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse.success("Chamado deletado com sucesso.", null, HttpStatus.OK.value()));
	}
	
	
}
