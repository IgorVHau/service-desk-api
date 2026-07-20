package service_desk_api.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import service_desk_api.api.exception.BusinessException;
import service_desk_api.api.exception.ResourceNotFoundException;
import service_desk_api.api.model.Chamado;
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
	
	@DisplayName(value = "Deve retornar 400 quando houver campo desconhecido")
	@Test
	void deveRetornar400QuandoHouverCampoDesconhecido() throws Exception {
		
		String body = """
			{
				"titulo": "Teste na API",
				"bescricao": "Nome do campo escrito incorretamente",
				"status": "ABERTO"
			}
				""";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.message").value(containsString("Unrecognized field")))
		.andExpect(jsonPath("$.data").doesNotExist());
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 400 quando título estiver ausente")
	@Test
	void deveRetornar400QuandoTituloEstiverAusente() throws Exception {
		
		String body = """
			{
				"descricao": "Título ausente na resposta",
				"status": "ABERTO"
			}
				""";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.message").value("Erro de validação: O título é obrigatório."))
		.andExpect(jsonPath("$.data").doesNotExist());
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 400 quando status for inválido")
	@Test
	void deveRetornar400QuandoStatusForInvalido() throws Exception {
		
		String body = """
		{
			"titulo": "Teste na API",
			"descricao": "Valor do status inválido",
			"status": "ABERTA"
		}
			""";
		
		String expectedMessage = "O campo status não foi preenchido com o valor correto. Por favor, escolha entre as opções: ABERTO, EM_ANDAMENTO, CONCLUIDO";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.message").value(expectedMessage))
		.andExpect(jsonPath("$.data").doesNotExist());
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 400 quando JSON estiver malformado")
	@Test
	void deveRetornar400QuandoJsonEstiverMalformado() throws Exception {
		
		String body = """
				{
					"titulo": "Teste na API",
					"descricao": "JSON de resposta malformado"
					"status": "ABERTO"
				}
					""";
		
		mockMvc.perform(post("/chamados")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.message").isNotEmpty())
		.andExpect(jsonPath("$.data").doesNotExist());
		
		verifyNoInteractions(chamadoService);
	}
	
	@DisplayName(value = "Deve retornar 404 quando chamado não existir")
	@Test
	void deveRetornar404QuandoBuscarPorIdInexistente() throws Exception {
		
		when(chamadoService.buscarPorIdOuFalhar(99L)).thenThrow(new ResourceNotFoundException("Chamado não encontrado."));
		
		mockMvc.perform(get("/chamados/99"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.timestamp").isNotEmpty())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message").value("Chamado não encontrado."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}
	
	@DisplayName(value = "Deve retornar 422 ao tentar atualizar chamado concluído")
	@Test
	void deveRetornar422QuandoAtualizarChamadoConcluido() throws Exception {
		
		when(chamadoService.atualizar(anyLong(), any())).thenThrow(new BusinessException("Chamado concluído não pode ser alterado."));
		
		mockMvc.perform(put("/chamados/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"titulo": "Teste",
							"descricao": "Teste",
							"status": "CONCLUIDO"
						}
						"""
				))
		.andExpect(status().isUnprocessableEntity())
		.andExpect(jsonPath("$.status").value(422))
		.andExpect(jsonPath("$.message").value("Chamado concluído não pode ser alterado."))
		.andExpect(jsonPath("$.data").doesNotExist());
	}
	
	@DisplayName(value = "Deve retornar 200 ao atualizar chamado existente com status diferente de concluído")
	@Test
	void deveRetornar200AoAtualizarChamadoExistenteNaoConcluido() throws Exception {
			
		Chamado chamadoAtualizado = Chamado.builder()
				.id(1L)
				.titulo("Teste")
				.descricao("Teste")
				.status(Status.ABERTO)
				.build();
		
		when(chamadoService.atualizar(eq(1L), any(Chamado.class))).thenReturn(chamadoAtualizado);
		
		//Act
		mockMvc.perform(put("/chamados/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"titulo": "Teste",
							"descricao": "Teste",
							"status": "ABERTO"
						}
						"""
						))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.status").value(200))
		.andExpect(jsonPath("$.message").value("Chamado atualizado com sucesso."))
		.andExpect(jsonPath("$.data").exists());
	}

}
