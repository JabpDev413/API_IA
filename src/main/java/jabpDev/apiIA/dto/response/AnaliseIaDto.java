package jabpDev.apiIA.dto.response;

public record AnaliseIaDto(
        String descricaoErro,
        String causa,
        String solucao,
        String analiseTecnica,
        String nomePrograma,
        String titulo,
        String problema
) {
}
