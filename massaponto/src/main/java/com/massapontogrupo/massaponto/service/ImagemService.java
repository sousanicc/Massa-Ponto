package com.massapontogrupo.massaponto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImagemService {

    private final Path pastaUploads;

    public ImagemService(@Value("${app.uploads.dir:uploads/produtos}") String uploadsDir) {
        this.pastaUploads = Paths.get(uploadsDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.pastaUploads);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar a pasta de uploads: " + this.pastaUploads, e);
        }
    }

    /**
     * Salva a imagem validando tipo e tamanho.
     * Gera nome UUID + extensão derivada do Content-Type.
     * Retorna o caminho público "/uploads/produtos/<arquivo>".
     */
    public String salvar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        // Validar tipo MIME
        String contentType = arquivo.getContentType();
        String extensao = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalArgumentException("Formato de imagem não suportado. Use JPEG, PNG ou WebP.");
        };

        // Validar tamanho (2MB = 2 * 1024 * 1024 bytes)
        long maxBytes = 2L * 1024 * 1024;
        if (arquivo.getSize() > maxBytes) {
            throw new IllegalArgumentException("A imagem deve ter no máximo 2 MB.");
        }

        // Gerar nome único: UUID + extensão
        String nomeArquivo = UUID.randomUUID().toString() + extensao;
        Path destino = this.pastaUploads.resolve(nomeArquivo).normalize();

        // Garantir que o destino está dentro da pasta de uploads (proteção path traversal)
        if (!destino.startsWith(this.pastaUploads)) {
            throw new SecurityException("Tentativa de acesso fora da pasta de uploads.");
        }

        try {
            Files.copy(arquivo.getInputStream(), destino);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao salvar a imagem.", e);
        }

        // Retornar caminho público
        return "/uploads/produtos/" + nomeArquivo;
    }

    /**
     * Exclui o arquivo físico se estiver dentro da pasta de uploads.
     * Recebe o caminho público (ex: "/uploads/produtos/uuid.jpg").
     */
    public void excluirSeExiste(String caminhoPublico) {
        if (caminhoPublico == null || caminhoPublico.isBlank()) {
            return;
        }
        String prefixo = "/uploads/produtos/";
        if (!caminhoPublico.startsWith(prefixo)) {
            return; // não é um arquivo nosso (ex: URL externa)
        }
        String nomeArquivo = caminhoPublico.substring(prefixo.length());
        Path arquivo = this.pastaUploads.resolve(nomeArquivo).normalize();
        if (!arquivo.startsWith(this.pastaUploads)) {
            return; // proteção path traversal
        }
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException ignored) {
            // Se não conseguir excluir, apenas ignora (não quebra o fluxo)
        }
    }
}