package com.massapontogrupo.massaponto.controller;

import com.massapontogrupo.massaponto.model.Categoria;
import com.massapontogrupo.massaponto.model.Produto;
import com.massapontogrupo.massaponto.repository.ProdutoDAO;
import com.massapontogrupo.massaponto.service.ImagemService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
public class ProdutoAdminController {

    private final ProdutoDAO produtoDAO;
    private final ImagemService imagemService;

    public ProdutoAdminController(ProdutoDAO produtoDAO, ImagemService imagemService) {
        this.produtoDAO = produtoDAO;
        this.imagemService = imagemService;
    }

    // ========= LISTAGEM DE PRODUTOS =========
    @GetMapping("/admin/produtos")
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoDAO.listarTodosAtivos());
        return "admin-produtos";
    }

    // ========= FORMULÁRIO NOVO PRODUTO =========
    @GetMapping("/admin/produtos/novo")
    public String formNovo(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", Categoria.values());
        return "admin-produto-form";
    }

    // ========= CADASTRAR PRODUTO =========
    @PostMapping("/admin/produtos")
    public String cadastrar(@ModelAttribute Produto produto,
                            @RequestParam("imagem") MultipartFile imagem,
                            @RequestParam("preco") String precoStr,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        // Validações
        String erro = validarProduto(produto, precoStr, model);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("categorias", Categoria.values());
            return "admin-produto-form";
        }

        // Converter preço para centavos
        int precoCentavos = converterPrecoParaCentavos(precoStr);
        produto.setPrecoCentavos(precoCentavos);

        // Upload da imagem (pode ser null)
        try {
            String imagemPath = imagemService.salvar(imagem);
            produto.setImagemUrl(imagemPath); // null se não enviou
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("categorias", Categoria.values());
            return "admin-produto-form";
        } catch (IllegalStateException e) {
            model.addAttribute("erro", "Erro ao salvar a imagem: " + e.getMessage());
            model.addAttribute("categorias", Categoria.values());
            return "admin-produto-form";
        }

        // Salvar no banco
        try {
            produtoDAO.inserirProduto(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
            return "redirect:/admin/produtos";
        } catch (DataIntegrityViolationException e) {
            // Nome duplicado (inclui inativos porque a coluna é UNIQUE)
            if (produto.getImagemUrl() != null) {
                imagemService.excluirSeExiste(produto.getImagemUrl());
            }
            model.addAttribute("erro", "Já existe um produto com este nome.");
            model.addAttribute("categorias", Categoria.values());
            return "admin-produto-form";
        }
    }

    // ========= FORMULÁRIO EDITAR =========
    @GetMapping("/admin/produtos/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        Produto produto = produtoDAO.buscarPorId(id);
        if (produto == null) {
            return "redirect:/admin/produtos";
        }
        // Converter preço de centavos para string "49,90" para o input
        model.addAttribute("produto", produto);
        model.addAttribute("precoFormatado", formatarPrecoParaInput(produto.getPrecoCentavos()));
        model.addAttribute("categorias", Categoria.values());
        return "admin-produto-form";
    }

    // ========= ATUALIZAR PRODUTO =========
    @PostMapping("/admin/produtos/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute Produto produto,
                            @RequestParam("imagem") MultipartFile imagem,
                            @RequestParam("preco") String precoStr,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        // Validações
        String erro = validarProduto(produto, precoStr, model);
        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("categorias", Categoria.values());
            return "admin-produto-form";
        }

        // Buscar produto existente para pegar a imagem atual
        Produto existente = produtoDAO.buscarPorId(id);
        if (existente == null) {
            return "redirect:/admin/produtos";
        }

        // Converter preço para centavos
        int precoCentavos = converterPrecoParaCentavos(precoStr);
        produto.setPrecoCentavos(precoCentavos);
        produto.setId(id);

        // Upload da imagem (opcional)
        String imagemAnterior = existente.getImagemUrl();
        String novaImagemPath = null;
        try {
            novaImagemPath = imagemService.salvar(imagem);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("categorias", Categoria.values());
            model.addAttribute("precoFormatado", formatarPrecoParaInput(precoCentavos));
            return "admin-produto-form";
        } catch (IllegalStateException e) {
            model.addAttribute("erro", "Erro ao salvar a imagem: " + e.getMessage());
            model.addAttribute("categorias", Categoria.values());
            model.addAttribute("precoFormatado", formatarPrecoParaInput(precoCentavos));
            return "admin-produto-form";
        }

        if (novaImagemPath != null) {
            produto.setImagemUrl(novaImagemPath);
        } else {
            produto.setImagemUrl(imagemAnterior);
        }

        // Atualizar no banco
        try {
            produtoDAO.atualizarProduto(produto);
            // Se subiu nova imagem, excluir a antiga
            if (novaImagemPath != null && imagemAnterior != null) {
                imagemService.excluirSeExiste(imagemAnterior);
            }
            redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso!");
            return "redirect:/admin/produtos";
        } catch (DataIntegrityViolationException e) {
            // Nome duplicado - se subiu nova imagem, limpar
            if (novaImagemPath != null) {
                imagemService.excluirSeExiste(novaImagemPath);
            }
            model.addAttribute("erro", "Já existe um produto com este nome.");
            model.addAttribute("categorias", Categoria.values());
            model.addAttribute("precoFormatado", formatarPrecoParaInput(precoCentavos));
            return "admin-produto-form";
        }
    }

    // ========= CONFIRMAÇÃO DE EXCLUSÃO (GET) =========
    @GetMapping("/admin/produtos/{id}/excluir")
    public String confirmarExcluir(@PathVariable Long id, Model model) {
        Produto produto = produtoDAO.buscarPorId(id);
        if (produto == null) {
            return "redirect:/admin/produtos";
        }
        model.addAttribute("produto", produto);
        return "admin-produto-excluir";
    }

    // ========= EXCLUIR (POST) - exclusão lógica =========
    @PostMapping("/admin/produtos/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        produtoDAO.excluirLogico(id);
        redirectAttributes.addFlashAttribute("sucesso", "Produto desativado com sucesso!");
        return "redirect:/admin/produtos";
    }

    // ========= VALIDAÇÕES COMUNS =========
    private String validarProduto(Produto produto, String precoStr, Model model) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            return "Nome é obrigatório.";
        }
        if (produto.getNome().length() > 100) {
            return "Nome deve ter no máximo 100 caracteres.";
        }
        if (produto.getCategoria() == null) {
            return "Categoria é obrigatória.";
        }
        if (produto.getDescricao() == null || produto.getDescricao().isBlank()) {
            return "Descrição é obrigatória.";
        }
        if (precoStr == null || precoStr.isBlank()) {
            return "Preço é obrigatório.";
        }
        // Validar formato do preço (número positivo)
        try {
            BigDecimal preco = new BigDecimal(precoStr.replace(",", "."));
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                return "Preço deve ser maior que zero.";
            }
            if (preco.compareTo(new BigDecimal("9999.99")) > 0) {
                return "Preço muito alto (máximo R$ 9.999,99).";
            }
        } catch (NumberFormatException e) {
            return "Preço inválido. Use formato como 49,90 ou 49.90.";
        }
        return null;
    }

    private int converterPrecoParaCentavos(String precoStr) {
        BigDecimal preco = new BigDecimal(precoStr.replace(",", "."));
        // Multiplicar por 100 e arredondar para inteiro
        return preco.multiply(new BigDecimal("100"))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
    }

    private String formatarPrecoParaInput(Integer precoCentavos) {
        if (precoCentavos == null) {
            return "";
        }
        long reais = precoCentavos / 100;
        long centavos = precoCentavos % 100;
        return String.format("%d,%02d", reais, centavos);
    }
}