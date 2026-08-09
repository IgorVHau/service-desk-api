package service_desk_api.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import service_desk_api.api.exception.BusinessException;
import service_desk_api.api.exception.ResourceNotFoundException;
import service_desk_api.api.model.Categoria;
import service_desk_api.api.model.Chamado;
import service_desk_api.api.model.Prioridade;
import service_desk_api.api.model.Status;
import service_desk_api.api.service.ChamadoService;

@WebMvcTest(
		controllers = ChamadoController.class,
		excludeAutoConfiguration = SecurityAutoConfiguration.class
		)
@Import(TestSecurityConfig.class)
class ChamadoControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ChamadoService chamadoService;
	
	@DisplayName(value = "Deve retornar 200 ao listar chamados paginados")
	@Test
	void deveRetornar200AoListarChamadosPaginados() throws Exception {
		
		Chamado chamado1 = Chamado.builder()
				.id(1L)
				.titulo("Problema de acesso")
				.descricao("Usuário sem acesso ao sistema")
		        .status(Status.ABERTO)
		        .prioridade(Prioridade.ALTA)
		        .categoria(Categoria.ACESSO)
		        .build();
		
		Chamado chamado2 = Chamado.builder()
				.id(2L)
		        .titulo("Falha de rede")
		        .descricao("Instabilidade de conexão")
		        .status(Status.EM_ANDAMENTO)
		        .prioridade(Prioridade.MEDIA)
		        .categoria(Categoria.REDE)
		        .build();
		
		Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
		
		Page<Chamado> pagina = new PageImpl<>(
				List.of(chamado1,chamado2),
				pageable,
				5
			);
		
		when(chamadoService.listarTodos(any(Pageable.class)))
			.thenReturn(pagina);
		
		mockMvc.perform(get("/chamados")
				.param("page", "0")
				.param("size", "2")
				.param("sort", "id,desc"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data.content").isArray())
		.andExpect(jsonPath("$.data.content.length()").value(2))
		.andExpect(jsonPath("$.data.number").value(0))
		.andExpect(jsonPath("$.data.size").value(2))
		.andExpect(jsonPath("$.data.totalElements").value(5))
		.andExpect(jsonPath("$.data.totalPages").value(3))
		.andExpect(jsonPath("$.data.numberOfElements").value(2));
		
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(chamadoService).listarTodos(pageableCaptor.capture());
		Pageable pageableCapturado = pageableCaptor.getValue();
	}
	
	@DisplayName(value = "Deve retornar 400 quando houver campo desconhecido")
	@Test
	void deveRetornar400QuandoHouverCampoDesconhecido() throws Exception {
		
		String body = """
			{
				"titulo": "Teste na API",
				"bescricao": "Nome do campo escrito incorretamente",
				"status": "ABERTO",
				"categoria": "SOFTWARE",
				"prioridade": "ALTA"
			}
				""";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Requisição inválida"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.detail").value(containsString("Campo desconhecido")))
		.andExpect(jsonPath("$.instance").value("/chamados"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 400 quando título estiver ausente")
	@Test
	void deveRetornar400QuandoTituloEstiverAusente() throws Exception {
		
		String body = """
			{
				"descricao": "Título ausente na resposta",
				"status": "ABERTO",
				"categoria": "SOFTWARE",
				"prioridade": "ALTA"
			}
				""";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Requisição inválida"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.detail").value(containsString("Erro de validação")))
		.andExpect(jsonPath("$.instance").value("/chamados"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 400 quando status for inválido")
	@Test
	void deveRetornar400QuandoStatusForInvalido() throws Exception {
		
		String body = """
		{
			"titulo": "Teste na API",
			"descricao": "Valor do status inválido",
			"status": "ABERTA",
			"categoria": "SOFTWARE",
			"prioridade": "ALTA"
		}
			""";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Requisição inválida"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.detail").value(containsString("ABERTO, EM_ANDAMENTO, CONCLUIDO")))
		.andExpect(jsonPath("$.instance").value("/chamados"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 400 quando JSON estiver malformado")
	@Test
	void deveRetornar400QuandoJsonEstiverMalformado() throws Exception {
		
		String body = """
				{
					"titulo": "Teste na API",
					"descricao": "JSON de resposta malformado"
					"status": "ABERTO",
					"categoria": "SOFTWARE",
					"prioridade": "ALTA"
				}
					""";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Requisição inválida"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.detail").value(containsString("JSON malformado")))
		.andExpect(jsonPath("$.instance").value("/chamados"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 404 quando chamado não existir")
	@Test
	void deveRetornar404QuandoBuscarPorIdInexistente() throws Exception {
		
		when(chamadoService.buscarPorIdOuFalhar(99L)).thenThrow(
				new ResourceNotFoundException("Chamado não encontrado."));
		
		mockMvc.perform(get("/chamados/99"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.type").value("about:blank"))
			.andExpect(jsonPath("$.title").value("Não encontrado"))
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.detail").value("Chamado não encontrado."))
			.andExpect(jsonPath("$.instance").value("/chamados/99"))
			.andExpect(content()
					.contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}
	
	@DisplayName(value = "Deve retornar 404 quando recurso não existir")
	@Test
	void deveRetornar404QuandoRecursoNaoExistir() throws Exception {
		
		mockMvc.perform(put("/chamado")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"titulo": "Teste na API",
							"descricao": "Recurso inexistente",
							"status": "ABERTO",
							"categoria": "SOFTWARE",
							"prioridade": "ALTA"
						}
						"""))
		.andExpect(status().isNotFound())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Não encontrado"))
		.andExpect(jsonPath("$.status").value(404))
		.andExpect(jsonPath("$.detail").value("O recurso solicitado não foi encontrado."))
		.andExpect(jsonPath("$.instance").value("/chamado"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 405 ao usar POST em um chamado específico")
	@Test
	void deveRetornar405AoUsarPostEmChamadoEspecifico() throws Exception {
		
		mockMvc.perform(post("/chamados/2")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"titulo": "Teste na API",
							"descricao": "Teste de método não permitido",
							"status": "EM_ANDAMENTO",
							"categoria": "REDE",
							"prioridade": "MEDIA"
						}
						"""))
		.andExpect(status().isMethodNotAllowed())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Método não permitido"))
		.andExpect(jsonPath("$.status").value(405))
		.andExpect(jsonPath("$.detail").value("O método HTTP utilizado não é permitido para este recurso."))
		.andExpect(jsonPath("$.instance").value("/chamados/2"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 405 ao usar PUT sem especificar um id")
	@Test
	void deveRetornar405AoUsarPutSemEspecificarId() throws Exception {
		
		mockMvc.perform(put("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						"""
						{
							"titulo": "Teste na API",
							"descricao": "Teste de método não permitido",
							"status": "EM_ANDAMENTO",
							"categoria": "REDE",
							"prioridade": "MEDIA"
						}
						"""
						))
		.andExpect(status().isMethodNotAllowed())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Método não permitido"))
		.andExpect(jsonPath("$.status").value(405))
		.andExpect(jsonPath("$.detail").value("O método HTTP utilizado não é permitido para este recurso."))
		.andExpect(jsonPath("$.instance").value("/chamados"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 422 ao tentar atualizar chamado concluído")
	@Test
	void deveRetornar422QuandoAtualizarChamadoConcluido() throws Exception {
		
		when(chamadoService.atualizar(anyLong(), any())).thenThrow(
				new BusinessException("Chamado concluído não pode ser alterado."));
		
		mockMvc.perform(put("/chamados/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"titulo": "Teste",
							"descricao": "Teste",
							"status": "CONCLUIDO",
							"categoria": "SOFTWARE",
							"prioridade": "ALTA"
						}
						"""
				))
		.andExpect(status().isUnprocessableEntity())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Conteúdo não processável"))
		.andExpect(jsonPath("$.status").value(422))
		.andExpect(jsonPath("$.detail").value("Chamado concluído não pode ser alterado."))
		.andExpect(jsonPath("$.instance").value("/chamados/1"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}
	
	@DisplayName(value = "Deve retornar 500 quando ocorrer algum erro interno")
	@Test
	void deveRetornar500QuandoOcorrerAlgumErroInterno() throws Exception {
		
		when(chamadoService.atualizar(anyLong(), any()))
			.thenThrow(new RuntimeException("Falha simulada"));
		
		mockMvc.perform(put("/chamados/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"titulo": "Teste",
							"descricao": "Teste",
							"status": "ABERTO",
							"categoria": "SOFTWARE",
							"prioridade": "ALTA"
						}
						"""
					))
		.andExpect(status().isInternalServerError())
		.andExpect(jsonPath("$.type").value("about:blank"))
		.andExpect(jsonPath("$.title").value("Erro interno do servidor"))
		.andExpect(jsonPath("$.status").value(500))
		.andExpect(jsonPath("$.detail").value("Ocorreu um erro interno ao processar a solicitação."))
		.andExpect(jsonPath("$.instance").value("/chamados/1"))
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}
	
	@DisplayName(value = "Deve retornar 200 ao atualizar chamado existente com status diferente de concluído")
	@Test
	void deveRetornar200AoAtualizarChamadoExistenteNaoConcluido() throws Exception {
			
		Chamado chamadoAtualizado = Chamado.builder()
				.id(1L)
				.titulo("Teste")
				.descricao("Teste")
				.status(Status.ABERTO)
				.categoria(Categoria.SOFTWARE)
				.prioridade(Prioridade.ALTA)
				.build();
		
		when(chamadoService.atualizar(eq(1L), any(Chamado.class))).thenReturn(chamadoAtualizado);
		
		mockMvc.perform(put("/chamados/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"titulo": "Teste",
							"descricao": "Teste",
							"status": "ABERTO",
							"categoria": "SOFTWARE",
							"prioridade": "ALTA"
						}
						"""
						))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.status").value(200))
		.andExpect(jsonPath("$.message").value("Chamado atualizado com sucesso."))
		.andExpect(jsonPath("$.data").exists());
	}

}
