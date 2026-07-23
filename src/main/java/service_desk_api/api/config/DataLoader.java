package service_desk_api.api.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import service_desk_api.api.model.Chamado;
import service_desk_api.api.model.Status;
import service_desk_api.api.model.Usuario;
import service_desk_api.api.repository.ChamadoRepository;
import service_desk_api.api.repository.UsuarioRepository;

@Configuration
public class DataLoader {
	
	@Bean
	CommandLineRunner initDatabase(UsuarioRepository repository, ChamadoRepository chamadoRepository, PasswordEncoder encoder) {
		return args -> {
			if(repository.findByEmail("admin@email.com").isEmpty()) {
				Usuario admin = new Usuario(
						"Fernando",
						"admin@email.com", 
						encoder.encode("123456"),
						"ADMIN"
						);
				repository.save(admin);
			}
			if(repository.findByEmail("user@email.com").isEmpty()) {
				Usuario user = new Usuario(
						"Jorge",
						"user@email.com",
						encoder.encode("654321"),
						"USER"
						);
				repository.save(user);
			}
			if(chamadoRepository.findAll().isEmpty()) {
				Chamado chamado = Chamado.builder()
					.titulo("Acesso negado ao servidor remoto")
					.descricao("O usuário Rodolfo não consegue acessar o servidor da LightyearX. Conceder acesso ao funcionário.")
					.status(Status.ABERTO)
					.criadoEm(LocalDateTime.now())
					.atualizadoEm(LocalDateTime.now())
					.build();
				chamadoRepository.save(chamado);
			}
		};
		
	}

}
