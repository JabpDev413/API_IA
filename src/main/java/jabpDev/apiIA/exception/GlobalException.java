package jabpDev.apiIA.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@AllArgsConstructor
public class GlobalException {

    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<?> handleErrorException(ErrorException ex, HttpServletRequest request){
        return processException(ex, HttpStatus.NOT_FOUND, "Recurso não encontrado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request){
        return processException(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
    }

    // Método privado para evitar repetição de código (DRY)
    private ResponseEntity<?> processException(Exception ex, HttpStatus status, String defaultMessage) {
        // 1. Procurar o erro real dentro das suas classes (jabp_dev)
        StackTraceElement relevantTrack = Arrays.stream(ex.getStackTrace())
                .filter(s -> s.getClassName().contains("jabp_dev"))
                .findFirst()
                .orElse(ex.getStackTrace().length > 0 ? ex.getStackTrace()[0] : null);

        String fileName = relevantTrack != null ? relevantTrack.getFileName() : "Unknown";
        Integer lineNumber = relevantTrack != null ? relevantTrack.getLineNumber() : 0;
        String methodName = relevantTrack != null ? relevantTrack.getMethodName() : "Unknown";

        // 2. Pegar a mensagem da CAUSA real (ex: erro no SQL ou NullPointer específico)
        String realErrorMessage = (ex.getCause() != null)
                ? ex.getMessage() + " | Causa: " + ex.getCause().getMessage()
                : ex.getMessage();

        // 4. Print no Console do Render (ajuda muito no debug rápido)
        System.err.println("🚨 ERRO DETECTADO: " + realErrorMessage);
        System.err.println("📍 LOCAL: " + fileName + " -> Linha: " + lineNumber);

        Map<String, Object> bodyException = new LinkedHashMap<>();
        bodyException.put("message", realErrorMessage);
        bodyException.put("details", ex.getMessage()); // Opcional: tirar em produção

        return new ResponseEntity<>(bodyException, status);
    }
}
