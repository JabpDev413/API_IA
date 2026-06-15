package jabpDev.apiIA.service.chat;

import jabpDev.apiIA.agente.Agente;
import jabpDev.apiIA.dto.response.AgenteResponseDto;
import jabpDev.apiIA.dto.response.ProgramaDependeciaDto;
import jabpDev.apiIA.dto.response.RespostaProUsuarioResponseDTO;
import jabpDev.apiIA.dto.response.RoutineResponseDto;
import jabpDev.apiIA.dto.response.chat.ConversaResponseDTO;
import jabpDev.apiIA.dto.response.chat.MessageResponseDTO;
import jabpDev.apiIA.dto.response.chat.WebSocketResponseDTO;
import jabpDev.apiIA.entity.Conversa;
import jabpDev.apiIA.entity.Mensagem;
import jabpDev.apiIA.entity.Usuario;
import jabpDev.apiIA.exception.ErrorException;
import jabpDev.apiIA.repository.ConversaRepository;
import jabpDev.apiIA.repository.MensagemRepository;
import jabpDev.apiIA.repository.UsuarioRepository;
import jabpDev.apiIA.service.AgenteService;
import jabpDev.apiIA.utils.UtilsServices;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    private ConversaRepository conversaRepository;
    private MensagemRepository mensagemRepository;
    private UsuarioRepository usuarioRepository;
    private UtilsServices utilsServices;
    private Agente agente;


    @Transactional
    public ConversaResponseDTO novaConversa() throws Exception{
        try {
            Usuario usuario = utilsServices.getUsuario();
            Conversa novaConversa = Conversa.builder()
                    .titulo("")
                    .programa("")
                    .usuario(usuario)
                    .criadoEm(LocalDateTime.now())
                    .build();
            Conversa conversa = conversaRepository.save(novaConversa);
            if (conversa.getId() > 0){
                String mensagemPadrao = """
                                            Olá 👋
                                            Sou o agente de suporte IA da H&S.
                                            Descreva o problema encontrado ou informe:
                                            • mensagem de erro
                                            • rotina/programa
                                            • contexto da operação
                                            
                                            Quanto mais detalhes forem informados, mais precisa será a análise.
                                            """;
                Mensagem novoMensagem = Mensagem.builder()
                        .conversa(conversa)
                        .tipo("IA")
                        .conteudo(mensagemPadrao)
                        .criadoEm(LocalDateTime.now())
                        .build();
                Mensagem mensagem =  mensagemRepository.save(novoMensagem);
                String dataConversa = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String horaConversa = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String dataMessage = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String horaMessage = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                List<MessageResponseDTO> message = new ArrayList<>();
                message.add(new MessageResponseDTO(
                        mensagem.getId(),
                        mensagem.getTipo(),
                        mensagem.getConteudo(),
                        dataMessage,
                        horaMessage
                ));

                return new ConversaResponseDTO(
                        conversa.getId(),
                        usuario.getId(),
                        conversa.getTitulo(),
                        dataConversa,
                        horaConversa,
                        message
                        );
            }else{
                throw new ErrorException("Não foi possível iniciar a conversa");
            }

        } catch (RuntimeException e) {
            System.err.println("Erro na novoConversa: " + e.getMessage());
            throw new ErrorException("Não foi possível iniciar a conversa");
        }
    }


    public ConversaResponseDTO conversarIa(WebSocketResponseDTO socketResponseDTO)throws Exception{
        try {
            Optional<Conversa> conversaOptional = Optional.ofNullable(conversaRepository.findById(socketResponseDTO.conversaId()).orElseThrow(() -> new ErrorException("Conversa não encontrada")));

            if (conversaOptional.isEmpty()){
                throw new ErrorException("Conversa não encontrada");
            }
            Mensagem mensagemUser = Mensagem.builder()
                    .conversa(conversaOptional.get())
                    .tipo("USER")
                    .conteudo(socketResponseDTO.conteudo())
                    .criadoEm(LocalDateTime.now())
                    .build();
            mensagemRepository.save(mensagemUser);

            RespostaProUsuarioResponseDTO responseIa  = agente.analizar(socketResponseDTO.conteudo());

            System.err.println("titulo: " + responseIa.titulo());

            if (conversaOptional.get().getTitulo().isEmpty()){
                conversaOptional.get().setTitulo( responseIa.titulo());
            }
            if (conversaOptional.get().getPrograma().isEmpty()){
                conversaOptional.get().setPrograma(responseIa.programa());
            }
            Conversa conversa = conversaRepository.save(conversaOptional.get());

            System.err.println("Responsta IA: " + responseIa);

            Mensagem mensagem = Mensagem.builder()
                    .conversa(conversa)
                    .tipo("IA")
                    .conteudo(responseIa.respostaProUsuario())
                    .criadoEm(LocalDateTime.now())
                    .build();
            Mensagem mensagemResponse = mensagemRepository.save(mensagem);

            String dataConversa = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String horaConversa = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String dataMessage = mensagemResponse.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String horaMessage = mensagemResponse.getCriadoEm().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            List<MessageResponseDTO> message = new ArrayList<>();
            message.add(new MessageResponseDTO(
                    mensagemResponse.getId(),
                    mensagemResponse.getTipo(),
                    mensagemResponse.getConteudo(),
                    dataMessage,
                    horaMessage
            ));



            return new ConversaResponseDTO(
                    conversa.getId(),
                    socketResponseDTO.usuarioId(),
                    responseIa.titulo(),
                    dataConversa,
                    horaConversa,
                    message
            );
        } catch (Exception e) {
            System.err.println("Erro no chatService: " + e.getMessage());
            e.printStackTrace();
            throw new ErrorException(e.getMessage());
        }
    }


    public List<ConversaResponseDTO> buscarConversas()throws Exception{
        try {

            List<Conversa> conversaList = conversaRepository.findAll();
            return conversaList.stream().map((conversa) ->{
                String dataConversa = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String horaConversa = conversa.getCriadoEm().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                return new ConversaResponseDTO(
                        conversa.getId(),
                        conversa.getUsuario().getId(),
                        conversa.getTitulo(),
                        dataConversa,
                        horaConversa,
                        List.of()
                );

            }).collect(Collectors.toList());
        }catch (Exception e){
            System.err.println("Erro ao buscar conversas: " + e.getMessage());
            throw new ErrorException(e.getMessage());
        }
    }

    public List<MessageResponseDTO> buscarMensagensConversa(Long conversaId) throws Exception{
        try {
            List<Mensagem> mensagemList = mensagemRepository.findByConversaIdOrderByCriadoEmAsc(conversaId);
            return mensagemList.stream().map((mensagem) -> {
                String dataMessage = mensagem.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String horaMessage = mensagem.getCriadoEm().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                return new MessageResponseDTO(
                        mensagem.getId(),
                        mensagem.getTipo(),
                        mensagem.getConteudo(),
                        dataMessage,
                        horaMessage
                );
            }).collect(Collectors.toList());
        }catch (Exception e){
            System.err.println("Erro ao buscar mensagens da conversa: " + e.getMessage());
            throw new ErrorException(e.getMessage());
        }
    }
}
