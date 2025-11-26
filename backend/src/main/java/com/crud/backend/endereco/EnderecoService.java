package com.crud.backend.endereco;

import com.crud.backend.viaCep.ViaCepDTO;
import com.crud.backend.viaCep.ViaCepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ViaCepService viaCepService;

    /**
     * Cria um novo endereço usando os dados do CEP via ViaCEP.
     */
    @Transactional
    public EnderecoResponse criarEndereco(String cep, Integer numero, String complemento) {

        // Consulta ViaCEP
        ViaCepDTO viaCep = viaCepService.buscarCep(cep);

        // Monta entidade
        EnderecoEntity endereco = new EnderecoEntity();
        endereco.setCep(cep);
        endereco.setNumero(numero);
        endereco.setComplemento(complemento);

        // Salva no banco
        enderecoRepository.save(endereco);

        // Monta resposta unificada
        return new EnderecoResponse(endereco, viaCep);
    }

    /**
     * Busca endereço por ID com os dados do ViaCEP.
     */
    @Transactional(readOnly = true)
    public EnderecoResponse buscarPorId(Long id) {

        EnderecoEntity endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado: " + id));

        ViaCepDTO viaCep = viaCepService.buscarCep(endereco.getCep());

        return new EnderecoResponse(endereco, viaCep);
    }

    /**
     * Atualiza endereço existente.
     */
    @Transactional
    public EnderecoResponse atualizarEndereco(Long id, String cep, Integer numero, String complemento) {
        EnderecoEntity endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado: " + id));

        // Atualiza campos
        endereco.setCep(cep);
        endereco.setNumero(numero);
        endereco.setComplemento(complemento);

        // Consulta ViaCEP atualizado
        ViaCepDTO viaCep = viaCepService.buscarCep(cep);

        // Salva
        enderecoRepository.save(endereco);

        return new EnderecoResponse(endereco, viaCep);
    }

    /**
     * Exclui endereço.
     */
    @Transactional
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new RuntimeException("Endereço não encontrado para excluir: " + id);
        }
        enderecoRepository.deleteById(id);
    }
}
