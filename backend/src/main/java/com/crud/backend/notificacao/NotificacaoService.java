package com.crud.backend.notificacao;

import com.crud.backend.usuario.UsuarioEntity;
import com.crud.backend.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoEntity criarParaUsuario(Long usuarioId, NotificacaoType tipo, String titulo, String mensagem) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));
        NotificacaoEntity n = NotificacaoEntity.of(usuario, tipo, titulo, mensagem);
        return notificacaoRepository.save(n);
    }

    public List<NotificacaoEntity> listarPorUsuario(Long usuarioId) {
        return notificacaoRepository.findAllByUsuarioIdOrderByCriadaEmDesc(usuarioId);
    }

    public List<NotificacaoEntity> listarNaoLidasPorUsuario(Long usuarioId) {
        return notificacaoRepository.findAllByUsuarioIdAndLidaFalseOrderByCriadaEmDesc(usuarioId);
    }

    public void marcarComoLida(Long notificacaoId) {
        notificacaoRepository.findById(notificacaoId).ifPresent(n -> {
            if (!n.isLida()) {
                n.setLida(true);
                notificacaoRepository.save(n);
            }
        });
    }

    public void remover(Long notificacaoId) {
        notificacaoRepository.deleteById(notificacaoId);
    }
}
