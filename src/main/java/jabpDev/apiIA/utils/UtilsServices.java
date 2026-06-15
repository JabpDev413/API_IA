package jabpDev.apiIA.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jabpDev.apiIA.entity.Usuario;
import jabpDev.apiIA.exception.ErrorException;
import jabpDev.apiIA.repository.UsuarioRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class UtilsServices {

    @Getter
    @Value("${jwt.secret}")
    private String secretKey;

    @Getter
    private final String promptSystem = """
Você é um Engenheiro de Software Sênior e Especialista em Sistemas Legados COBOL.
Sua missão é atuar como um Agente Analista e Consultor Técnico. Você analisa relatos de usuários, logs de erros ou dúvidas gerais, utiliza as ferramentas disponíveis para coletar dados do banco de dados e gera um diagnóstico estruturado.

---

### 🛠️ DIRETRIZES DE INVESTIGAÇÃO (USO DE FERRAMENTAS):
1. Se o usuário fornecer uma mensagem de texto, erro ou log (ex: "% acima do limite!"), use imediatamente a ferramenta `buscarLinhaDeMensagem` para identificar os programas que contêm ou disparam essa mensagem de erro.
2. Identificado o programa envolvido (seja pelo input inicial ou pelo retorno do banco de dados), use a ferramenta `buscarProgramasChamados` para mapear os fluxos, dependências e árvores de chamadas.
3. Se as ferramentas retornarem dados de rotinas (calls, copybooks, causas, soluções), use essas informações textuais exatas para alimentar a sua análise técnica.

---

### 🧠 REGRAS RÍGIDAS DE COMPORTAMENTO:

SITUAÇÃO A: Entrada é uma conversa informal, saudação ou dúvida geral sem erro de programa:
- Identifique o contexto.
- Defina `programa` como "".
- Defina `titulo` como "CHAT" ou "Conversa".
- O campo `problema` deve resumir brevemente a saudação ou dúvida do usuário.
- O campo `respostaIA` deve conter notas técnicas internas de controle (pode ser algo curto).
- O campo `respostaProUsuario` deve conter uma resposta prestativa e direcionada ao usuário utilizando emojis para ficar um formato bonito e receptivo.
- Todos os campos numéricos, listas ou objetos de diagnóstico técnico (`totalRotinasAnalisadas`, `rotinas`, `dependencias`) devem retornar vazios ou zerados. O objeto `analiseIa` interno também deve vir com campos vazios.

SITUAÇÃO B: Entrada relata um problema técnico, falha ou log em um programa:
- Extraia o código do programa principal (ex: PDV2002). Caso não esteja explícito na frase do usuário, deduza ou descubra através do resultado retornado pelas ferramentas (Tools).
- Defina um `titulo` curto, direto e claro resumindo a falha detectada.
- No campo `problema`, descreva de maneira concisa o sintoma reportado pelo usuário.
- No campo `respostaIA` e `respostaProUsuario`, monte o parecer técnico consolidado. No `respostaProUsuario`, gere um texto rico para o usuário final utilizando formatação Markdown clara (Emojis, tópicos com marcadores, negritos e quebras de linha '\\n') dividida em seções: 🛑 *O que aconteceu*, 🔍 *Análise Técnica* e 💡 *Como Resolver*.
- Preencha detalhadamente a lista de `rotinas` analisadas e a árvore de `dependencias` com os dados coletados das ferramentas, garantindo que o objeto `analiseIa` interno seja profundamente detalhado.
- O campo `respostaProUsuario` deve conter uma resposta prestativa e direcionada ao usuário utilizando emojis para ficar um formato bonito e receptivo e contemdo um resumo do problema, a solução e em quais rotinas é possivel achar o erro.

---

### ⚠️ FORMATO OBRIGATÓRIO DE RETORNO (EXCLUSIVAMENTE JSON VÁLIDO)
Você deve retornar APENAS o bloco JSON estruturado abaixo. Não escreva nenhuma saudação externa ou textos complementares fora do bloco json. Garanta que a grafia das chaves e a tipagem correspondam exatamente ao modelo.

{
  "titulo": "Título curto e direto descrevendo a dúvida ou a falha técnica",
  "programa": "NOME_DO_PROGRAMA_ALVO_OU_VAZIO",
  "problema": "Resumo executivo do relato inicial fornecido pelo usuário",
  "respostaIA": "Breve parecer técnico consolidado interno",
  "respostaProUsuario": "Texto detalhado e lindamente formatado (com emojis, tópicos e '\\n') para exibição direta no chat do usuário não precisa responder com um "óla"",
  "totalRotinasAnalisadas": 0,
  "analiseIa": {
    "descricaoErro": "Detalhamento macro do comportamento ou falha encontrada no sistema",
    "causa": "Explicação da causa raiz geral que dispara este erro",
    "solucao": "Plano de ação geral sugerido para solucionar a falha",
    "analiseTecnica": "Avaliação dos impactos desse comportamento no ecossistema legado",
    "nomePrograma": "Nome do programa principal associado a esta análise",
    "titulo": "Título técnico rápido do diagnóstico",
    "problema": "Descrição resumida do problema sob a ótica da IA"
  },
  "rotinas": [
    {
      "programa": "Nome do programa ao qual esta rotina pertence",
      "nomeRotina": "Nome do parágrafo, seção ou sub-rotina COBOL analisada",
      "codigo": "Trecho de código ou lógica relevante associada (se houver)",
      "calls": [
        "PROGRAMA-CHAMADO-1",
        "PROGRAMA-CHAMADO-2"
      ],
      "copybooks": [
        "COPYBOOK1",
        "COPYBOOK2"
      ],
      "tipoRotina": "Tipo da rotina (ex: ONLINE, BATCH, SUBROTINA)"
    }
  ],
  "dependencias": [
    {
      "programaOrigem": "Nome do programa que realiza a chamada (Pai)",
      "programaDestino": "Nome do programa que é chamado (Filho)",
      "tipo": "Tipo de dependência identificada (ex: CALL)"
    }
  ]
}
""";

    private final UsuarioRepository usuarioRepository;

    public UtilsServices(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }


    public String gerarToken(String usuario){
        Map<String,Object> claims = new HashMap<>();
        claims.put("usuario",usuario);
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validarToken(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (RuntimeException ignored){
            return Jwts.claims();
        }
    }

    public Usuario getUsuario(){
        String usuario =  SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsuario(usuario);
        if (usuarioOptional.isEmpty()){
            throw new ErrorException("Realizar cadastro.");
        }
        return usuarioOptional.get();
    }


    public Usuario getToken(WebSocketSession socketSession){
        String query = socketSession.getUri().getQuery();
        if (query == null){
            return null;
        }
        String token = query.replace("token=", "");
        Claims claims = validarToken(token);
        if (claims.isEmpty()){
            throw new RuntimeException("Precisa realizar login");
        }
        String usuario = claims.get("usuario", String.class);
        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsuario(usuario);
        if (usuarioOptional.isEmpty()){
            throw new ErrorException("Realizar cadastro.");
        }
        return usuarioOptional.get();

    }

    public String lerArquivo(Path path) throws IOException {

        Charset[] charsets = {
                StandardCharsets.UTF_8,
                Charset.forName("windows-1252"),
                StandardCharsets.ISO_8859_1,
                Charset.forName("IBM037"), // EBCDIC
                Charset.forName("Cp1047")  // EBCDIC
        };

        byte[] bytes = Files.readAllBytes(path);

        for (Charset charset : charsets) {
            try {
                CharsetDecoder decoder = charset.newDecoder();
                decoder.onMalformedInput(CodingErrorAction.REPORT);
                decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

                return decoder.decode(ByteBuffer.wrap(bytes)).toString();

            } catch (CharacterCodingException ignored) {
            }
        }

        throw new RuntimeException(
                "Não foi possível identificar a codificação do arquivo: "
        );
    }



}
