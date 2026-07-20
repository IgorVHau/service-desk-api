package service_desk_api.api.service;

import org.springframework.stereotype.Service;

import service_desk_api.api.model.Usuario;
import service_desk_api.api.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	private final UsuarioRepository repository;
	
	public UsuarioService(UsuarioRepository repository) {
		this.repository = repository;
	}
	
	public Usuario save(Usuario usuario) {
		return repository.save(usuario);
	}

}
