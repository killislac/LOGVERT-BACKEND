package com.logistica.doisv.util.validacao;

import com.logistica.doisv.entities.enums.CategoriaArquivoPermitida;
import com.logistica.doisv.services.exceptions.TipoArquivoInvalidoException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Component
public class ArquivoValidador {

    private final Tika tika = new Tika();

    public void validarObrigatorio(MultipartFile arquivo, Set<CategoriaArquivoPermitida> categoriasPermitidas) {
        if(arquivo == null || arquivo.isEmpty()){
            throw new TipoArquivoInvalidoException("Arquivo obrigatório não informado.");
        }

        validarTipo(arquivo, categoriasPermitidas);
    }

    public void validarOpcional(MultipartFile arquivo, Set<CategoriaArquivoPermitida> categoriasPermitidas){
        if (arquivo == null || arquivo.isEmpty()) {
            return;
        }

        validarTipo(arquivo, categoriasPermitidas);
    }

    public void validarEstruturaCsv(String linha){
        if(linha == null || !linha.contains(";") || linha.split(";").length > 3){
            throw new TipoArquivoInvalidoException("Erro ao validar a estrutura do CSV: " + linha);
        }
    }

    private void validarTipo(MultipartFile arquivo, Set<CategoriaArquivoPermitida> categoriasPermitidas){
        String tipoDetectado = detectarTipoReal(arquivo);

        if(!tipoPermitido(tipoDetectado, categoriasPermitidas)){
            throw new TipoArquivoInvalidoException(
                    String.format("Tipo de arquivo não permitido: %s", tipoDetectado));
        }
    }

    private String detectarTipoReal(MultipartFile arquivo){
        try {
            return tika.detect(arquivo.getInputStream());
        }catch (IOException e){
            throw new TipoArquivoInvalidoException("Erro ao detectar permissoes do arquivo.");
        }
    }

    private boolean tipoPermitido(String tipoDetectado, Set<CategoriaArquivoPermitida> categoriasPermitidas){
        if (isSvg(tipoDetectado)) {
            return false;
        }

        if (tipoDetectado.startsWith("image/") && categoriasPermitidas.contains(CategoriaArquivoPermitida.IMAGEM)) {
            return true;
        }

        if (tipoDetectado.startsWith("video/") && categoriasPermitidas.contains(CategoriaArquivoPermitida.VIDEO)) {
            return true;
        }

        if (isCsv(tipoDetectado) && categoriasPermitidas.contains(CategoriaArquivoPermitida.CSV)) {
            return true;
        }
        return false;
    }

    private boolean isCsv(String tipoDetectado){
        return tipoDetectado.equals("text/csv")
                || tipoDetectado.equals("text/plain")
                || tipoDetectado.equals("application/csv")
                || tipoDetectado.equals("application/vnd.ms-excel");
    }

    private boolean isSvg(String tipoDetectado) {
        return tipoDetectado.equals("image/svg+xml");
    }
}
