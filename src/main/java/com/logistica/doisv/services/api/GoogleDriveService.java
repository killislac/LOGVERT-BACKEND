package com.logistica.doisv.services.api;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.logistica.doisv.configuration.GoogleDriveConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleDriveService {

    private static String appDistribuicao;

    @Value("${app.distribuicao.ativa}")
    public void setAppDistribuicao(String distribuicao){
        GoogleDriveService.appDistribuicao = distribuicao;
    }
   
    public static String salvarArquivoDrive(MultipartFile arquivo, String idItem, String nomePasta) throws IOException, GeneralSecurityException {
        Drive driveService = GoogleDriveConfig.getDriveService();

        //Obtém o ID da pasta desejada a cada chamada, em vez de usar uma variável estática.
        String idPasta = obterOuCriarPasta(driveService, nomePasta);

        //Busca o arquivo passando o ID da pasta correta como parâmetro.
        String arquivoExistenteId = buscarArquivoPorNome(driveService, idItem, idPasta);

        // Metadados do arquivo
        File metadadosArquivo = new File();

        ByteArrayInputStream inputStream  = new ByteArrayInputStream(arquivo.getBytes());

        File arquivoSalvo;
        if (arquivoExistenteId != null) {
            // Atualiza arquivo existente
            arquivoSalvo = driveService.files().update(arquivoExistenteId, metadadosArquivo, new com.google.api.client.http.InputStreamContent(
                            arquivo.getContentType(), inputStream))
                    .setFields("id, webViewLink")
                    .execute();
        } else {
            // Cria novo arquivo
            metadadosArquivo.setName(idItem.toString());
            metadadosArquivo.setParents(Collections.singletonList(idPasta));
            arquivoSalvo = driveService.files().create(metadadosArquivo, new com.google.api.client.http.InputStreamContent(
                            arquivo.getContentType(),
                            inputStream
                    ))
                    .setFields("id, webViewLink")
                    .execute();
        }

        // Retorna URL do arquivo salvo
        return arquivoSalvo.getWebViewLink();
    }

    private static String obterOuCriarPasta(Drive driveService, String nomePastaFilha) throws IOException {
        FileList resultado = driveService.files().list()
                .setQ("name='"+ nomePastaFilha +"' and '"+ appDistribuicao +"' in parents and mimeType='application/vnd.google-apps.folder' and trashed=false")
                .setSpaces("drive")
                .execute();

        // Se já existe, retorna o ID
        if (!resultado.getFiles().isEmpty()) {
            return resultado.getFiles().get(0).getId();
        }

        // Se não existe, cria a pasta
        File pasta = new File();
        pasta.setName(nomePastaFilha);
        pasta.setMimeType("application/vnd.google-apps.folder");

        pasta.setParents(Collections.singletonList(appDistribuicao));

        File pastaCriada = driveService.files().create(pasta)
                .setFields("id")
                .execute();

        return pastaCriada.getId();
    }

    private static String buscarArquivoPorNome(Drive driveService, String nomeArquivo, String idDaPastaCorreta) throws IOException {
        String query = String.format("name='%s' and '%s' in parents and trashed=false", nomeArquivo, idDaPastaCorreta);

        FileList resultado = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        return resultado.getFiles().isEmpty() ? null : resultado.getFiles().get(0).getId();
    }

    public static void excluirArquivoDrive(String idItem, String nomePasta) throws GeneralSecurityException, IOException {
        Drive driveService = GoogleDriveConfig.getDriveService();

        //Obtém o ID da pasta desejada a cada chamada, em vez de usar uma variável estática.
        String idPasta = obterOuCriarPasta(driveService, nomePasta);

        //Busca o arquivo passando o ID da pasta correta como parâmetro.
        String arquivoExistenteId = buscarArquivoPorNome(driveService, idItem, idPasta);

        if(arquivoExistenteId != null) {
            driveService.files().delete(arquivoExistenteId).execute();
        }
    }
}
