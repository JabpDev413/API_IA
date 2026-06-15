package jabpDev.apiIA.config;

import io.jsonwebtoken.Claims;
import jabpDev.apiIA.utils.UtilsServices;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@AllArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private UtilsServices validacoesToken;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            response.setStatus(HttpServletResponse.SC_OK);
            return; // Retorna OK sem pedir token para o navegador liberar a conexão
        }

        String path = request.getServletPath();
        String header = request.getHeader("Authorization");

        if ( path.equals("/auth") ||
                path.startsWith("/messages")
//                path.startsWith("/chat")
//                || path.startsWith("/arquivo")
//                || path.startsWith("/agente")
//                || path.startsWith("/agente/analizar")
//                || path.startsWith("/agente/parser")
        ){
            filterChain.doFilter(request,response);
            return;
        }
        System.out.println("path: " + path);
        System.out.println("header: " + header);
        if (header == null || !header.startsWith("Bearer ")){
            throw new RuntimeException("Precisa realizar login");
        }

        try {
            String token = header.substring(7);
            Claims claims = validacoesToken.validarToken(token);
            if (claims.isEmpty()){
                throw new RuntimeException("Precisa realizar login");
            }
            String usuario = claims.get("usuario", String.class);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(usuario,null, List.of());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request,response);
        }catch (Exception e){
            System.err.println(e.getMessage());
            throw new RuntimeException("Você não esta logado");
        }
    }

}
