package jabpDev.apiIA.service.auth;

import jabpDev.apiIA.dto.response.auth.UsuarioResponseDTO;
import jabpDev.apiIA.entity.Usuario;
import jabpDev.apiIA.exception.ErrorException;
import jabpDev.apiIA.repository.UsuarioRepository;
import jabpDev.apiIA.utils.UtilsServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private UsuarioRepository usuarioRepository;
    private UtilsServices validacoesToken;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO login(String usuario, String senha) throws Exception{
        try {
            Optional<Usuario> usuarioOptional = Optional.ofNullable(usuarioRepository.findByUsuario(usuario).orElseThrow(() -> new ErrorException("Usuário não encontrado")));
            if (usuarioOptional.isEmpty()){
                throw new ErrorException("Usuário não encontrado");
            }
            if (passwordEncoder.matches( senha, usuarioOptional.get().getSenha())){
                throw new ErrorException("Senha invalida");
            }
            return new UsuarioResponseDTO(
                    usuarioOptional.get().getNome(),
                    usuarioOptional.get().getRol(),
                    validacoesToken.gerarToken(usuario)
            );
        } catch (RuntimeException e) {
            System.err.println("Erro no login: " + e.getMessage() + " usua: " + usuario + " sen: " + senha);
            throw new ErrorException(e.getMessage());
        }
    }
}
