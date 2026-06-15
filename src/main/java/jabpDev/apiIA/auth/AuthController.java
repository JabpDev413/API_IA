package jabpDev.apiIA.auth;


import jabpDev.apiIA.dto.request.auth.LoginRequestDTO;
import jabpDev.apiIA.dto.response.auth.UsuarioResponseDTO;
import jabpDev.apiIA.exception.ErrorException;
import jabpDev.apiIA.service.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;


    @PostMapping
    public UsuarioResponseDTO login(@RequestBody LoginRequestDTO body) throws Exception{
        if (body.usuario().isEmpty()){
            throw new ErrorException("Informe seu usuário do redmine");
        }
        if (body.senha().isEmpty()){
            throw new ErrorException("Informe sua senha do redmine");
        }
        return authService.login(body.usuario(),body.senha());
    }
}
