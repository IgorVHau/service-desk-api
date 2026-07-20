package service_desk_api.api.service;

import org.springframework.stereotype.Service;

import service_desk_api.api.exception.BusinessException;
import service_desk_api.api.exception.ResourceNotFoundException;
import service_desk_api.api.model.Chamado;
import service_desk_api.api.model.Status;
import service_desk_api.api.repository.ChamadoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ChamadoService {
	
	private final ChamadoRepository repository;
	
	public ChamadoService(ChamadoRepository repository) {
		this.repository = repository;
	}
	
	public List<Chamado> listarTodos() {
		return repository.findAll();
	}
	
	public Optional<Chamado> buscarPorId(Long id) {
		return repository.findById(id);
	}
	
	public Chamado buscarPorIdOuFalhar(Long id) {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado."));
	}
	
	public Chamado criar(Chamado chamado) {
		return repository.save(chamado);
	}
	
	public Chamado atualizar(Long id, Chamado novoChamado) {
		var chamadoAtual = buscarPorIdOuFalhar(id);
		
		validarAtualizacao(chamadoAtual);
		
		chamadoAtual.setTitulo(novoChamado.getTitulo());
		chamadoAtual.setDescricao(novoChamado.getDescricao());
		chamadoAtual.setStatus(novoChamado.getStatus());
		var chamadoAtualizado = repository.save(chamadoAtual);
		return chamadoAtualizado;
	}
	
	public void validarAtualizacao(Chamado chamado) {
		if(chamado.getStatus() == Status.CONCLUIDO) {
			throw new BusinessException("Chamado concluído não pode ser alterado.");
		}
	}
	
	public void deletar(Long id) {
		repository.deleteById(id);
	}

}
