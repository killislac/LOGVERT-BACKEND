package com.logistica.doisv.services;

import com.logistica.doisv.entities.Licenca;
import com.logistica.doisv.entities.Loja;
import com.logistica.doisv.entities.enums.Status;
import com.logistica.doisv.repositories.LicencaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LicencaService {

    @Autowired
    private LicencaRepository licencaRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    protected void desativarLicencasExpiradas(){
        List<Licenca> licencas = licencaRepository.findAllByStatus(Status.ATIVO);

        licencas.stream()
                .filter(l -> l.getValidade().isBefore(LocalDate.now()))
                .forEach(l -> l.setStatus(Status.INATIVO));
    }

    @Transactional
    public void cadastrarLicenca(Loja loja, Integer periodoValidade){
        Licenca licenca = new Licenca();
        licenca.setValidade(LocalDate.now().plusDays(periodoValidade));
        licenca.setLoja(loja);

        licencaRepository.save(licenca);
    }
}
