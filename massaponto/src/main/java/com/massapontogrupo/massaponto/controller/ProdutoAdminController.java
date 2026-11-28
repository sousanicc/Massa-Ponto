package com.massapontogrupo.massaponto.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.massapontogrupo.massaponto.model.Categoria;
import com.massapontogrupo.massaponto.model.Produto;
import com.massapontogrupo.massaponto.repository.ProdutoDAO;

@Controller
public class ProdutoAdminController {

    // Pasta onde os uploads serão salvos
    private static final String UPLOAD_DIR = "./uploads";

    @Autowired
    ApplicationContext context;

    // ========= LISTAGEM DE PRODUTOS =========
    @GetMapping("/admin/produtos")
    public String listarProdutos(Model model) {
        ProdutoDAO dao = context.getBean(ProdutoDAO.class);
        model.addAttribute("produtos", dao.listarTodosAtivos());
        return "listaProdutos";
    }

    // ========= CADASTRO DE PRODUTOS =========
    @GetMapping("/admin/produto/cadastro")
    public String formProduto(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("categorias", Categoria.values());
        return "formProduto";
    }

    @PostMapping("/admin/produto/cadastro")
    public String cadastrarProduto(
            @ModelAttribute Produto produto,
            @RequestParam("imagem") MultipartFile imagem,
            Model model) throws IOException {

        try {
            // Salva a imagem e guarda o nome no objeto produto
            String nomeImagem = salvarArquivo(imagem);
            produto.setImagemUrl(nomeImagem);

            ProdutoDAO dao = context.getBean(ProdutoDAO.class);
            dao.inserirProduto(produto);

            return "redirect:/admin/produtos";

        } catch (DataIntegrityViolationException e) {
            model.addAttribute("erro", "ATENÇÃO: Não foi possível cadastrar, pois já existe um produto com este nome!");
            model.addAttribute("categorias", Categoria.values());
            model.addAttribute("produto", produto);
            return "formProduto";
        }
    }

    // ========= EDIÇÃO DE PRODUTOS =========
    @GetMapping("/admin/produto/{id}/editar")
    public String formEditar(@PathVariable Long id, Model model) {
        ProdutoDAO dao = context.getBean(ProdutoDAO.class);
        Produto produto = dao.buscarPorId(id);
        if (produto == null) {
            return "redirect:/admin/produtos";
        }
        model.addAttribute("produto", produto);
        model.addAttribute("categorias", Categoria.values());
        return "formProduto";
    }

    @PostMapping("/admin/produto/{id}/editar")
    public String editarProduto(
            @PathVariable Long id,
            @ModelAttribute Produto produto,
            @RequestParam("imagem") MultipartFile imagem,
            Model model) throws IOException {

        try {
            ProdutoDAO dao = context.getBean(ProdutoDAO.class);

            // Se enviou nova imagem, salva; senão, mantém a atual
            if (!imagem.isEmpty()) {
                String nomeImagem = salvarArquivo(imagem);
                produto.setImagemUrl(nomeImagem);
            } else {
                Produto existente = dao.buscarPorId(id);
                produto.setImagemUrl(existente.getImagemUrl());
            }

            produto.setId(id);
            dao.atualizarProduto(produto);

            return "redirect:/admin/produtos";

        } catch (DataIntegrityViolationException e) {
            model.addAttribute("erro", "ATENÇÃO: Não foi possível salvar, pois já existe um produto com este nome!");
            model.addAttribute("categorias", Categoria.values());
            model.addAttribute("produto", produto);
            return "formProduto";
        }
    }

    // ========= EXCLUSÃO LÓGICA =========
    @PostMapping("/admin/produto/{id}/deletar")
    public String deletarProduto(@PathVariable Long id) {
        ProdutoDAO dao = context.getBean(ProdutoDAO.class);
        dao.excluirLogico(id);
        return "redirect:/admin/produtos";
    }

    // ========= SERVIR ARQUIVOS UPLOADADOS =========
    @GetMapping("/uploads/{nomeArquivo}")
    public ResponseEntity<Resource> servirArquivo(@PathVariable String nomeArquivo) throws IOException {
        Path caminhoArquivo = Paths.get(UPLOAD_DIR).resolve(nomeArquivo);
        if (Files.exists(caminhoArquivo)) {
            Resource resource = new FileSystemResource(caminhoArquivo);
            String contentType = Files.probeContentType(caminhoArquivo);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        }
        // Fallback para imagens no classpath (imagens padrão)
        Resource resource = new ClassPathResource("static/images/" + nomeArquivo);
        if (resource.exists()) {
            return ResponseEntity.ok().body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    /*
    Método auxiliar: salva um MultipartFile na pasta ./uploads/
    e retorna o nome único gerado para o arquivo.

    Por que UUID + nome original?
    - UUID evita colisão (dois usuários subindo "pizza.jpg" não se sobrescrevem)
    - Manter o nome original facilita identificar o arquivo
    */
    private String salvarArquivo(MultipartFile arquivo) throws IOException {
        // Garante que a pasta de uploads exista
        Path pastaUpload = Paths.get(UPLOAD_DIR);
        if (!Files.exists(pastaUpload)) {
            Files.createDirectories(pastaUpload);
        }
        // Gera nome único: UUID + nome original
        String nomeOriginal = arquivo.getOriginalFilename();
        String nomeUnico = UUID.randomUUID().toString() + "_" + nomeOriginal;
        // Copia o arquivo para a pasta de uploads
        Path caminhoDestino = pastaUpload.resolve(nomeUnico);
        Files.copy(arquivo.getInputStream(), caminhoDestino);
        return nomeUnico;
    }
}
