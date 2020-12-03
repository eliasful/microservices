package com.elias.produtos.api;

import com.elias.produtos.model.Produto;
import com.elias.produtos.repository.ProdutoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class ProdutoApi {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping
    public List<Produto> all() {
        return produtoRepository.findAll();
    }

    @GetMapping("{id}")
    public Optional<Produto> one(@PathVariable("id") Long id) {
        return produtoRepository.findById(id);
    }

    @PostMapping
    public Produto insert(@RequestBody Produto produto) throws JsonProcessingException {
        produtoRepository.save(produto);

        var produtoJson = objectMapper.writeValueAsString(produto);
        jmsTemplate.convertAndSend("queue.produto.insert", produtoJson);

        return produto;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Long id) {
        produtoRepository.deleteById(id);
    }
}
