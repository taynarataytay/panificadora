package com.exemplo.panificadora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class PanificadoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(PanificadoraApplication.class, args);
    }

    @Entity
    public static class Produto {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull
        private String nome;

        @NotNull
        private int quantidade;

        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    }

    @Entity
    public static class Pedido {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotNull
        private String cliente;

        @NotNull
        private String produto;

        @NotNull
        private int quantidade;

        private String status = "pendente";

        // Getters e Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCliente() { return cliente; }
        public void setCliente(String cliente) { this.cliente = cliente; }
        public String getProduto() { return produto; }
        public void setProduto(String produto) { this.produto = produto; }
        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    }

    @Repository
    public interface ProdutoRepository extends JpaRepository<Produto, Long> {}

    @Repository
    public interface PedidoRepository extends JpaRepository<Pedido, Long> {
        List<Pedido> findByStatus(String status);
    }

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public PanificadoraApplication(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping("/produtos")
    public Produto adicionarProduto(@RequestBody Produto produto) {
        return produtoRepository.save(produto);
    }

    @PostMapping("/pedidos")
    public Pedido registrarPedido(@RequestBody Pedido pedido) {
        Produto produto = produtoRepository.findById(pedido.getId()).orElse(null);
        if (produto != null && produto.getQuantidade() >= pedido.getQuantidade()) {
            produto.setQuantidade(produto.getQuantidade() - pedido.getQuantidade());
            produtoRepository.save(produto);
            return pedidoRepository.save(pedido);
        }
        return null;
    }

    @GetMapping("/produtos")
    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    @GetMapping("/pedidos/pendentes")
    public List<Pedido> listarPedidosPendentes() {
        return pedidoRepository.findByStatus("pendente");
    }
}
