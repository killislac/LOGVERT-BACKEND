package com.logistica.doisv.util.conversao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PaginacaoUtil {

    public static <T> Page<T> paraPagina(Pageable paginacao, List<T> todos) {
        int inicio = (int) paginacao.getOffset();
        int fim = Math.min(inicio + paginacao.getPageSize(), todos.size());
        List<T> conteudo = (inicio >= todos.size()) ? List.of() : todos.subList(inicio, fim);
        return new PageImpl<>(conteudo, paginacao, todos.size());
    }

}
