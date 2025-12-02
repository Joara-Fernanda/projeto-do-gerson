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
// ================= VALIDAÇÕES =================
private boolean validarEmail(String email) {
    return email != null && email.endsWith("@aluno.ifsp.edu.br");
}

private boolean validarSenha(String senha) {
    if (senha == null || senha.length() < 8) {
        return false;
    }
    
    boolean temMaiuscula = false;
    boolean temMinuscula = false;
    boolean temNumero = false;
    boolean temSimbolo = false;
    
    for (char c : senha.toCharArray()) {
        if (Character.isUpperCase(c)) temMaiuscula = true;
        else if (Character.isLowerCase(c)) temMinuscula = true;
        else if (Character.isDigit(c)) temNumero = true;
        else if (!Character.isLetterOrDigit(c)) temSimbolo = true;
    }
    
    return temMaiuscula && temMinuscula && temNumero && temSimbolo;
}

private boolean validarTamanhoCampos(String nome, String email, String senha) {
    return nome != null && nome.getBytes().length <= 255 &&
           email != null && email.getBytes().length <= 150 &&
           senha != null && senha.getBytes().length <= 255;
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
    
    // Validações
    if (!validarEmail(email)) {
        model.addAttribute("erro", "E-mail deve ser do domínio @aluno.ifsp.edu.br");
        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        return "cadastro";
    }
    
    if (!validarSenha(senha)) {
        model.addAttribute("erro", "Senha deve ter pelo menos 8 caracteres, incluindo maiúscula, minúscula, número e símbolo");
        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        return "cadastro";
    }
    
    if (!validarTamanhoCampos(nome, email, senha)) {
        model.addAttribute("erro", "Campos excedem o tamanho máximo permitido");
        model.addAttribute("nome", nome);
        model.addAttribute("email", email);
        return "cadastro";
    }
    
    Usuario existente = usuarioRepository.findByEmail(email);
    if (existente != null) {
        model.addAttribute("erro", "E-mail já cadastrado!");
        model.addAttribute("nome", nome);
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
                            @RequestParam String turma,
                            Model model) throws IOException {

    // Validação de campos obrigatórios
    if (nome == null || nome.trim().isEmpty() ||
        descricao == null || descricao.trim().isEmpty() ||
        categoria == null || categoria.trim().isEmpty()) {
        model.addAttribute("erro", "Nome, descrição e categoria são obrigatórios");
        return "redirect:/doador?email=" + emailDoador + "&erro=campos_obrigatorios";
    }

    // Validação de tamanho
    if (descricao.getBytes().length > 255) {
        model.addAttribute("erro", "Descrição muito longa (máximo 255 caracteres)");
        return "redirect:/doador?email=" + emailDoador + "&erro=descricao_longa";
    }

    // Validação de imagem
    if (!imagem.isEmpty()) {
        String contentType = imagem.getContentType();
        if (contentType == null || 
            (!contentType.equals("image/jpeg") && 
             !contentType.equals("image/png") && 
             !contentType.equals("image/gif"))) {
            model.addAttribute("erro", "Apenas imagens JPEG, PNG ou GIF são permitidas");
            return "redirect:/doador?email=" + emailDoador + "&erro=formato_imagem";
        }
        
        if (imagem.getSize() > 5 * 1024 * 1024) { // 5MB
            model.addAttribute("erro", "Imagem muito grande (máximo 5MB)");
            return "redirect:/doador?email=" + emailDoador + "&erro=imagem_grande";
        }
    }

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
        p.setRemovido(true);
        produtoRepository.save(p);
    }
    return "redirect:/doador?email=" + emailDoador;
}

// ================= RANKING =================
@GetMapping("/ranking")
public String exibirRanking(Model model) {
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
    List<Produto> produtosDoado = produtoRepository.findAll().stream()
            .filter(Produto::isDoado)
            .toList();

    produtoRepository.deleteAll(produtosDoado);
    return "redirect:/ranking";
}
}