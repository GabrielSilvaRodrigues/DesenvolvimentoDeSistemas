package com.crud.backend.notificacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/notificacao")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    // Listar todas as notificações de um usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificacaoEntity>> listarPorUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(notificacaoService.listarPorUsuario(userId));
    }

    // Listar notificações não lidas
    @GetMapping("/user/{userId}/nao-lidas")
    public ResponseEntity<List<NotificacaoEntity>> listarNaoLidas(@PathVariable Long userId) {
        return ResponseEntity.ok(notificacaoService.listarNaoLidasPorUsuario(userId));
    }

    // Criar notificação para usuário (JSON body)
    @PostMapping("/user/{userId}")
    public ResponseEntity<NotificacaoEntity> criarParaUsuario(
            @PathVariable Long userId,
            @Valid @RequestBody NotificacaoRequest req
    ) {
        NotificacaoEntity criado = notificacaoService.criarParaUsuario(userId, req.tipo(), req.titulo(), req.mensagem());
        return ResponseEntity.ok(criado);
    }

    // Marcar uma notificação como lida
    @PostMapping("/{id}/lida")
    public ResponseEntity<Void> marcarLida(@PathVariable Long id) {
        notificacaoService.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }

    // Request DTO
    public static record NotificacaoRequest(
            @NotNull NotificacaoType tipo,
            @NotBlank String titulo,
            @NotBlank String mensagem
    ) {}
}
