package com.ifsp.projeto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProjetoController {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public ProjetoController(UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }
    // ================= LOGIN =================
    @GetMapping("/login")
    public String exibirLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@RequestParam("email") String email,
                                 @RequestParam("senha") String senha,
                                 Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            if ("doador".equalsIgnoreCase(usuario.getTipo())) {
                return "redirect:/doador?email=" + email;
            } else if ("beneficiado".equalsIgnoreCase(usuario.getTipo())) {
                return "redirect:/beneficiado?email=" + email;
            }
        }
        model.addAttribute("erro", "E-mail ou senha inválidos");
        model.addAttribute("email", email);
        return "login";
    }

    // ================= CADASTRO =================
    @GetMapping("/cadastro")
    public String exibirCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processarCadastro(@RequestParam("nome") String nome,
                                    @RequestParam("email") String email,
                                    @RequestParam("senha") String senha,
                                    @RequestParam("tipo") String tipo,
                                    Model model) {
        Usuario existente = usuarioRepository.findByEmail(email);
        if (existente != null) {
            model.addAttribute("erro", "E-mail já cadastrado!");
            model.addAttribute("email", email);
            return "cadastro";
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipo(tipo);
        usuarioRepository.save(usuario);

        if (tipo.equalsIgnoreCase("doador")) {
            return "redirect:/doador?email=" + email;
        } else {
            return "redirect:/beneficiado?email=" + email;
        }
    }

    // ================= BENEFICIADO =================
    @GetMapping("/beneficiado")
    public String exibirPaginaBeneficiado(@RequestParam(value = "email", required = false) String email,
                                          @RequestParam(value = "q", required = false) String q,
                                          @RequestParam(value = "categoria", required = false) String categoria,
                                          Model model) {

        if (categoria != null) categoria = categoria.trim().toLowerCase();
        if (q != null) q = q.trim();

        List<Produto> produtos;

        if (q != null && !q.isEmpty() && categoria != null && !categoria.isEmpty()) {
            produtos = produtoRepository.findByCategoriaIgnoreCaseAndNomeContainingIgnoreCase(categoria, q)
                    .stream().filter(p -> !p.isRemovido()).toList();
        } else if (q != null && !q.isEmpty()) {
            produtos = produtoRepository.findByNomeContainingIgnoreCase(q)
                    .stream().filter(p -> !p.isRemovido()).toList();
        } else if (categoria != null && !categoria.isEmpty()) {
            produtos = produtoRepository.findByCategoriaIgnoreCase(categoria)
                    .stream().filter(p -> !p.isRemovido()).toList();
        } else {
            produtos = produtoRepository.findAll()
                    .stream().filter(p -> !p.isRemovido()).toList();
        }

        model.addAttribute("produtos", produtos);

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            model.addAttribute("nome", usuario.getNome());
            model.addAttribute("email", email);
            model.addAttribute("tipo", usuario.getTipo());
            model.addAttribute("iniciais", email.substring(0, 1).toUpperCase());
        } else {
            model.addAttribute("iniciais", "U");
        }

        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("categoria", categoria == null ? "" : categoria);

        return "beneficiado";
    }

    // ================= DOADOR =================
    @GetMapping("/doador")
    public String exibirPaginaDoador(@RequestParam String email, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            model.addAttribute("nome", usuario.getNome());
            model.addAttribute("email", email);
            model.addAttribute("tipo", usuario.getTipo());
            model.addAttribute("iniciais", email.substring(0, 1).toUpperCase());
            model.addAttribute("produtos", produtoRepository.findByEmailDoadorAndRemovidoFalse(email));
        }
        return "doador";
    }

    // ================= POSTAR PRODUTO =================
    @PostMapping("/doador/postar")
    public String postarProduto(@RequestParam String nome,
                                @RequestParam String descricao,
                                @RequestParam String categoria,
                                @RequestParam("imagem") MultipartFile imagem,
                                @RequestParam String emailDoador,
                                @RequestParam String telefoneDoador,
                                @RequestParam String turma) throws IOException {

        Produto p = new Produto();
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setEmailDoador(emailDoador);
        p.setTelefoneDoador(telefoneDoador);
        p.setCategoria(categoria.trim().toLowerCase());
        p.setTurma(turma);
        p.setDoado(false);
        p.setRemovido(false);

        if (!imagem.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(imagem.getBytes());
            p.setImagem(base64);
        }

        produtoRepository.save(p);
        return "redirect:/doador?email=" + emailDoador;
    }

    // ================= MARCAR COMO DOADO =================
    @PostMapping("/doador/doado")
    public String marcarComoDoado(@RequestParam int produtoId,
                                  @RequestParam String emailDoador) {
        Optional<Produto> optionalProduto = produtoRepository.findById(produtoId);
        if (optionalProduto.isPresent()) {
            Produto p = optionalProduto.get();
            p.setDoado(true);
            produtoRepository.save(p);
        }
        return "redirect:/doador?email=" + emailDoador;
    }

    // ================= APAGAR PRODUTO =================
    @PostMapping("/doador/apagar")
    public String apagarProduto(@RequestParam int produtoId, @RequestParam String emailDoador) {
        Optional<Produto> optionalProduto = produtoRepository.findById(produtoId);
        if (optionalProduto.isPresent()) {
            Produto p = optionalProduto.get();
            p.setRemovido(true); // marca como removido, mas permanece no ranking
            produtoRepository.save(p);
        }
        return "redirect:/doador?email=" + emailDoador;
    }

    // ================= RANKING =================
    @GetMapping("/ranking")
    public String exibirRanking(Model model) {
        // Todos os produtos doados, mesmo removidos
        List<Produto> produtosDoado = produtoRepository.findAll().stream()
                .filter(Produto::isDoado)
                .toList();

        Set<String> cursosValidos = Set.of("Edificações", "Eletromecânica", "Informática", "Mecânica");

        Map<String, Long> doacoesPorTurma = produtosDoado.stream()
                .filter(p -> cursosValidos.contains(p.getTurma()))
                .collect(Collectors.groupingBy(
                        Produto::getTurma,
                        Collectors.counting()
                ));

        List<RankingDTO> ranking = doacoesPorTurma.entrySet().stream()
                .map(e -> new RankingDTO(e.getKey(), e.getValue()))
                .sorted((a, b) -> Long.compare(b.getTotalDoacoes(), a.getTotalDoacoes()))
                .toList();

        model.addAttribute("ranking", ranking);
        return "ranking";
    }

   // ================= RESETAR RANKING =================
@PostMapping("/ranking/resetar")
public String resetarRanking() {
    // Busca todos os produtos que foram doados
    List<Produto> produtosDoado = produtoRepository.findAll().stream()
            .filter(Produto::isDoado)
            .toList();

    // Apaga esses produtos do banco
    produtoRepository.deleteAll(produtosDoado);

    // Redireciona de volta para o ranking
    return "redirect:/ranking";
}
}