-- LOJAS
INSERT IGNORE INTO tb_Loja (id_loja, nome, cnpj, segmento, logo, email, status, id_publico) VALUES
(1, 'Loja CenterTech', 'RKO5GZPX000178', 'Tecnologia', '1qHS3CjGqHF0C3rTDLuMU_dG8dCzYgXrv', 'contato@centertech.com', 'ATIVO', UUID()),
(2, 'Loja Estilo&Cia', '35420723000124', 'Moda', '1qQist6b6FVov1d5LbBVddDY3U6IAD264', 'contato@estilocia.com', 'ATIVO', UUID()),
(3, 'Loja MundoLivros', 'NWFO17KR000186', 'Livros', '1ua9emAVgEZIraJIHVhgpcMO0XL6qn8e5', 'contato@mundolivros.com', 'ATIVO', UUID());

-- ADMIN (admin = true)
INSERT IGNORE INTO tb_Lojista (id_lojista, nome, cpf, email, senha, status, admin, id_loja) VALUES
(1, 'Usuário Administrador', '03032792061', 'admin@logvert.com', '$2a$10$s/w9pVrBTGP9MWhfF1c68eorYnBB4wGK.eZ2iHGZGW01X71qkcKxq', 'ATIVO', true, 1);

-- LOJISTAS (todos com admin = false)
INSERT IGNORE INTO tb_Lojista (id_lojista, nome, cpf, email, senha, status, admin, id_loja) VALUES
(2, 'Carla Souza', '76196945017', 'carla.souza@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 2),
(3, 'Pedro Lima', '35749998010', 'pedro.lima@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', true, 3),
(4, 'Juliana Martins', '08233781010', 'juliana.martins@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 1),
(5, 'Bruno Rocha', '50543505006', 'bruno.rocha@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 2),
(6, 'Renata Alves', '51340979098', 'renata.alves@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 3),
(7, 'Luciano Mendes', '53828563031', 'luciano.mendes@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 1),
(8, 'Patrícia Freitas', '68835673054', 'patricia.freitas@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', true, 2),
(9, 'Eduardo Silva', '79567695075', 'eduardo.silva@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 3),
(10, 'Fernanda Oliveira', '93549921020', 'fernanda.oliveira@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 1),
(11, 'João Vieira', '03882210036', 'joao.vieira@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 2),
(12, 'Aline Costa', '95154875010', 'aline.costa@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 3),
(13, 'Rafael Teixeira', '97495750081', 'rafael.teixeira@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 1),
(14, 'Camila Duarte', '29724618005', 'camila.duarte@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 2),
(15, 'Diego Ramos', '50282985050', 'diego.ramos@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 3),
(16, 'Marina Lopes', '82764720017', 'marina.lopes@email.com', '$2a$10$GVrZrk.vF0EA9dcblX.Q8.PBtsQEMrWUzrGq6YBn5IrRJ2h5eC0zu', 'ATIVO', false, 1);

-- PRODUTOS
INSERT IGNORE INTO tb_Produto (id_produto, descricao, unidade_medida, preco, status, id_loja, imagem) VALUES
(1, 'Mouse sem fio Logitech', 'UN', 79.90, 'ATIVO', 1, '1h4EitgpXr3VSVNOGXh3BAOS4MsyUomEu'),
(2, 'Notebook Lenovo i5', 'UN', 3299.00, 'ATIVO', 1, '1ai3ayKbMx-aNXkklsKgudedY5mYhRzSs'),
(3, 'Cabo USB-C 1m', 'UN', 19.90, 'ATIVO', 1, '1-34lGTLrMpYY9X0trByV827gGhEKfRB0'),
(4, 'Camisa Polo Masculina', 'UN', 49.90, 'ATIVO', 2, '1jtxvTQXXh9n8JXX10iybZeNiSJp1bF9m'),
(5, 'Tênis Casual Feminino', 'UN', 139.90, 'ATIVO', 2, '1ZvK1F2CHQQCxJBlNjBtkEbxFgE3b2hAd'),
(6, 'Calça Jeans Skinny', 'UN', 99.90, 'ATIVO', 2, '16vHTW43tZcYi7lPWkBh7GOT6Ds5OzQJD'),
(7, 'Livro - O Pequeno Príncipe', 'UN', 29.90, 'ATIVO', 3, '1-ho3hIqUE8O0r1V3hEFhmC_jn2kSuYbS'),
(8, 'Livro - 1984', 'UN', 39.90, 'ATIVO', 3, '1WsFWmIFbyhazUCpnIQ4DVo6Y0c1kPQMc'),
(9, 'Livro - Dom Casmurro', 'UN', 34.90, 'ATIVO', 3, '18Gce-cQQ9q0KHTbFQObhQThKPyTmcLiQ'),
(10, 'Fone Bluetooth JBL', 'UN', 199.90, 'ATIVO', 1, '1F2uodXRjD6tGjNObhcMpdvy0mmnzWUGO'),
(11, 'Blusa de Tricô Feminina', 'UN', 69.90, 'ATIVO', 2, '1EhLiqIFxRZrMycDoCNacCBYwQiBEvRJd'),
(12, 'Relógio Smartwatch', 'UN', 249.00, 'ATIVO', 1, '16Ub2GOOmRGpgNdpzZU8M8CkJWeUHTaCe'),
(13, 'Saia Jeans', 'UN', 59.90, 'ATIVO', 2, '1L51eYYJFEDMGg4M1NgzWXXBZaROxAUQ'),
(14, 'Livro - Código Limpo', 'UN', 89.90, 'ATIVO', 3, '1TYACsKaTmTxGmC7x9FtEGh96knnwmjxs'),
(15, 'Carregador Portátil 10000mAh', 'UN', 89.00, 'ATIVO', 1, '15lABDs-liGTrqcznHOX8HTRefN9tB0MO'),
(16, 'Livro - A Revolução dos Bichos', 'UN', 33.00, 'ATIVO', 3, '1xrp5UJkIHxN8-FPzmLmfDD_cAEB9zUxK'),
(17, 'Camiseta Básica', 'UN', 29.90, 'ATIVO', 2, '1yMEWRxmVlzReE1cjWVPU7sSdgLS3FyfC'),
(18, 'Monitor 24" Samsung', 'UN', 899.00, 'ATIVO', 1, '1b2Za1CyaWisZMSykFzWTKL3scPvmDCLY'),
(19, 'Vestido Longo', 'UN', 129.00, 'ATIVO', 2, '1Vlt-ExQwlaOwGqvRlpg_M5nELhpw7q7W'),
(20, 'Livro - O Hobbit', 'UN', 44.90, 'ATIVO', 3, '1m24_93PGaHu8_9jwsTatGfa3cNMlQrWq'),
(21, 'Teclado Mecânico Redragon', 'UN', 299.90, 'ATIVO', 1, '16JOslYBNXrd_R8WeNxQ6wFDFrhlNybFH'),
(22, 'Blazer Feminino', 'UN', 159.00, 'ATIVO', 2, '17stsfzWSzBouUboMHFLCigetayqtPjkd'),
(23, 'Livro - Mindset', 'UN', 54.90, 'ATIVO', 3, '1wn9qbM7WBMIz45gdwQlN3udkL1nLed3m'),
(24, 'Webcam Full HD', 'UN', 189.00, 'ATIVO', 1, '1zaVLBofRdv1SYF9LvjBsTuIqyMT4NI-l'),
(25, 'Jaqueta Jeans', 'UN', 139.90, 'ATIVO', 2, '11xZaSMrCpXlJazwYy8WMxOwwAou9FcjD'),
(26, 'Livro - O Poder do Hábito', 'UN', 49.90, 'ATIVO', 3, '1Q8KV-4oUJic5rm0aFkAM93UTARmExpEI'),
(27, 'Cadeira Gamer', 'UN', 1199.00, 'ATIVO', 1, '1s5PDPUDd1KfBZBiBqre64Di2jXkGrdnA'),
(28, 'Saia Midi', 'UN', 74.90, 'ATIVO', 2, '1nvtIeHEkU8N4HOzPE7dRq1r4ZEk1zvbs'),
(29, 'Livro - A Arte da Guerra', 'UN', 27.90, 'ATIVO', 3, '1FY2sYRwJd4XwPneDuTR504KW0Z_a_yhT'),
(30, 'Pen Drive 64GB', 'UN', 49.00, 'ATIVO', 1, '1mdL_BkPU19n06j0KeF53UjNXD1GJLh7n');

-- CONSUMIDORES
INSERT IGNORE INTO tb_Consumidor (id_consumidor, nome, cpf_cnpj, email, celular, telefone, endereco, status, id_loja) VALUES
(1, 'Carlos Henrique', '82771935058', 'carlos.henrique@email.com', '11987654321', '1134567890', 'Rua das Flores, 123 - São Paulo/SP', 'ATIVO', 1),
(2, 'Juliana Mendes', '73466938090', 'juliana.mendes@email.com', '21998765432', NULL, 'Av. Brasil, 456 - Rio de Janeiro/RJ', 'ATIVO', 2),
(3, 'Fernando Silva', '71831158019', 'fernando.silva@email.com', '31912345678', '3132221111', 'Rua Minas Gerais, 789 - Belo Horizonte/MG', 'ATIVO', 3),
(4, 'Ana Beatriz', '02546517040', 'ana.beatriz@email.com', '51987001122', NULL, 'Rua João Goulart, 321 - Porto Alegre/RS', 'ATIVO', 1),
(5, 'Rafael Gomes', '21101893044', 'rafael.gomes@email.com', '41998887766', '4130302020', 'Rua XV de Novembro, 654 - Curitiba/PR', 'ATIVO', 2),
(6, 'Camila Rocha', '28912505084', 'camila.rocha@email.com', '61919191919', NULL, 'SQS 305 Bloco E - Brasília/DF', 'ATIVO', 3),
(7, 'Marcelo William', '85515937066', 'marcelo.sousa@uscsonline.com.br', '85987871212', '8532223333', 'Av. Beira Mar, 800 - Fortaleza/CE', 'ATIVO', 1),
(8, 'Patrícia Carvalho', '08448589009', 'patricia.carvalho@email.com', '71923456789', NULL, 'Rua do Porto, 200 - Salvador/BA', 'ATIVO', 2),
(9, 'Lucas Almeida', '26360658062', 'lucas.almeida@email.com', '91999990000', NULL, 'Travessa Tucuruí, 55 - Belém/PA', 'ATIVO', 3),
(10, 'Renata Figueiredo', '00415751055', 'renata.figueiredo@email.com', '62988881234', '6240028922', 'Av. Goiás, 1000 - Goiânia/GO', 'ATIVO', 1);

--VENDAS
INSERT IGNORE INTO tb_Venda
(id, data_criacao, data_entrega, desconto, forma_pagamento, prazo_devolucao, prazo_troca, preco_total, senha, serial_venda, status, status_pedido, id_consumidor, id_loja)
VALUES
(1, '2025-06-15 10:15:00', '2025-06-20', 0.00, 'CREDITO', 7, 30, 3378.90, '$2a$10$QoIVIO3H/GjERAZhd8l2pOTK7pDrsNOi0wiNeMY1lUtlsHesJQSpS', '8f2e3d4a5c', 'ATIVO', 'ENTREGUE', 1, 1),
(2, '2026-01-15 14:30:00', NULL, 10.00, 'PIX', 7, 30, 289.80, NULL, NULL, 'ATIVO', 'EM_ANDAMENTO', 2, 2),
(3, '2025-12-20 09:00:00', NULL, 5.00, 'BOLETO', 7, 30, 64.80, NULL, NULL, 'ATIVO', 'EM_ANDAMENTO', 3, 3),
(4, '2026-01-28 18:45:00', NULL, 0.00, 'PIX', 7, 30, 19.90, NULL, NULL, 'ATIVO', 'EM_ANDAMENTO', 4, 1),
(5, '2025-09-12 11:20:00', '2025-09-15', 0.00, 'DEBITO', 7, 30, 139.90, '$2a$10$a1sX37tLUnP5XLJgEo8Nsu5O7oFmKaa1l0ChDbEtSEbodVC6DPS7m', 'b1c2d3e4f5', 'ATIVO', 'ENTREGUE', 5, 2),
(6, '2025-07-05 16:10:00', NULL, 0.00, 'CREDITO', 7, 30, 34.90, NULL, NULL, 'INATIVO', 'CANCELADA', 6, 3),
(7, '2025-08-20 13:00:00', '2025-08-25', 50.00, 'CREDITO', 7, 30, 1199.00, '$2a$10$MYXUFMXTkS/eJdxOphkKEudSTUCiNBbXmey2OpUy90BVIYrzmJHy2', '7a8b9c0d1e', 'ATIVO', 'ENTREGUE', 7, 1),
(8, '2026-01-10 08:30:00', NULL, 0.00, 'PIX', 7, 30, 159.00, NULL, NULL, 'ATIVO', 'EM_ANDAMENTO', 8, 2),
(9, '2025-03-15 09:45:00', '2025-03-18', 10.00, 'DEBITO', 7, 30, 82.80, '$2a$10$2MIi1J7AsF.OFN/u939a9eDSgl95980Wenk0RbLlG.evdjvX56RrO', '4f5e6d7c8b', 'ATIVO', 'ENTREGUE', 9, 3),
(10, '2026-01-30 15:55:00', NULL, 0.00, 'CREDITO', 7, 30, 249.00, NULL, NULL, 'ATIVO', 'EM_ANDAMENTO', 10, 1);

--ITENS VENDA
INSERT IGNORE INTO tb_Item_Venda
(id, detalhes, percentual_variacao, preco_original, preco_vendido, quantidade, status, id_produto, id_venda)
VALUES
-- Venda 1 (Total: 3378.90 | Loja Tech) -- Notebook Lenovo (3299.00) + Mouse (79.90) = 3378.90
(1, 'Notebook para trabalho', 1.00, 3299.00, 3299.00, 1.0, 'ATIVO', 2, 1),
(2, 'Acessório incluso', 1.00, 79.90, 79.90, 1.0, 'ATIVO', 1, 1),
-- Venda 2 (Total: 289.80 | Loja Moda) -- 2 Tênis com leve ajuste de preço (144.90 * 2 = 289.80) | Orig: 139.90
(3, 'Par de tênis promocional', 1.04, 139.90, 144.90, 2.0, 'ATIVO', 5, 2),
-- Venda 3 (Total: 64.80 | Loja Livros) -- Dom Casmurro (34.90) + Pequeno Príncipe (29.90) = 64.80
(4, 'Clássico Brasileiro', 1.00, 34.90, 34.90, 1.0, 'ATIVO', 9, 3),
(5, 'Literatura Infantil', 1.00, 29.90, 29.90, 1.0, 'ATIVO', 7, 3),
-- Venda 4 (Total: 19.90 | Loja Tech) -- Cabo USB (19.90)
(6, 'Reposição de cabo', 1.00, 19.90, 19.90, 1.0, 'ATIVO', 3, 4),
-- Venda 5 (Total: 139.90 | Loja Moda) -- Tênis Feminino (139.90)
(7, 'Presente dia das mães', 1.00, 139.90, 139.90, 1.0, 'ATIVO', 5, 5),
-- Venda 6 (Total: 34.90 | Loja Livros) -- Dom Casmurro (34.90)
(8, 'Leitura escolar', 1.00, 34.90, 34.90, 1.0, 'ATIVO', 9, 6),
-- Venda 7 (Total: 1199.00 | Loja Tech) -- Cadeira Gamer (1199.00)
(9, 'Setup Home Office', 1.00, 1199.00, 1199.00, 1.0, 'ATIVO', 27, 7),
-- Venda 8 (Total: 159.00 | Loja Moda) -- Blazer Feminino (159.00)
(10, 'Roupa social', 1.00, 159.00, 159.00, 1.0, 'ATIVO', 22, 8),
-- Venda 9 (Total: 82.80 | Loja Livros) -- 2 Livros O Hobbit com desconto (Orig: 44.90 -> Vendido: 41.40 * 2 = 82.80)
(11, 'Combo Fantasia', 0.92, 44.90, 41.40, 2.0, 'ATIVO', 20, 9),
-- Venda 10 (Total: 249.00 | Loja Tech) -- Smartwatch (249.00)
(12, 'Relógio Inteligente', 1.00, 249.00, 249.00, 1.0, 'ATIVO', 12, 10);

-- SOLICITACAO
INSERT IGNORE INTO tb_Solicitacao
(id, data_atualizacao, data_solicitacao, motivo, quantidade, status, status_solicitacao, tipo_solicitacao, id_consumidor, id_item_venda, id_venda)
VALUES
-- 1. Venda 1 (Junho/2025) - Troca Concluída
(1, '2025-06-25 10:00:00', '2025-06-21 09:30:00', 'Tela apresentando pixels mortos', 1.0, 'ATIVO', 'CONCLUIDA', 'TROCA', 1, 1, 1),
-- 2. Venda 1 (Junho/2025) - Devolução Concluída
(2, '2025-06-26 14:20:00', '2025-06-22 11:00:00', 'Mouse muito pequeno para minha mão', 1.0, 'ATIVO', 'CONCLUIDA', 'DEVOLUCAO', 1, 2, 1),
-- 3. Venda 5 (Setembro/2025) - Troca Aprovada/Concluída
(3, '2025-09-20 16:00:00', '2025-09-16 08:45:00', 'Ficou apertado, solicito troca pelo 37', 1.0, 'ATIVO', 'CONCLUIDA', 'TROCA', 5, 7, 5),
-- 4. Venda 5 (Setembro/2025) - Solicitação duplicada/errada Cancelada
(4, '2025-09-16 08:50:00', '2025-09-16 08:40:00', 'Cliquei errado, desconsiderar', 1.0, 'INATIVO', 'CANCELADA', 'TROCA', 5, 7, 5),
-- 5. Venda 7 (Agosto/2025) - Devolução Rejeitada
(5, '2025-09-01 10:00:00', '2025-08-30 15:00:00', 'A cor não combinou com o quarto', 1.0, 'INATIVO', 'REJEITADA', 'DEVOLUCAO', 7, 9, 7),
-- 6. Venda 7 (Agosto/2025) - Troca Concluída
(6, '2025-09-05 11:30:00', '2025-09-02 09:00:00', 'Pistão de gás descendo sozinho', 1.0, 'ATIVO', 'CONCLUIDA', 'TROCA', 7, 9, 7),
-- 7. Venda 9 (Março/2025) - Troca Concluída
(7, '2025-03-20 13:00:00', '2025-03-19 10:00:00', 'Chegou com a capa rasgada', 1.0, 'ATIVO', 'CONCLUIDA', 'TROCA', 9, 11, 9),
-- 8. Venda 9 (Março/2025) - Troca Rejeitada
(8, '2025-06-15 08:00:00', '2025-06-14 20:00:00', 'Folhas amarelando muito rápido', 1.0, 'INATIVO', 'REJEITADA', 'TROCA', 9, 11, 9);

-- HISTORICO SOLICITACAO
INSERT IGNORE INTO tb_Historico_Solicitacao
(id, data_atualizacao, observacao, status_anterior, status_atual, id_solicitacao)
VALUES
-- SOLICITAÇÃO 1
(1, '2025-06-22 14:00:00', 'Solicitação de troca aprovada! Aguardando envio.', 'PENDENTE', 'APROVADA', 1),
(2, '2025-06-23 09:30:00', 'Produto postado pelo cliente. Código de rastreio recebido.', 'APROVADA', 'EM_TRANSITO', 1),
(3, '2025-06-25 10:00:00', 'Produto recebido e verificado. Troca autorizada e finalizada.', 'EM_TRANSITO', 'CONCLUIDA', 1),
-- SOLICITAÇÃO 2
(4, '2025-06-22 15:00:00', 'Solicitação de devolução aprovada!', 'PENDENTE', 'APROVADA', 2),
(5, '2025-06-26 14:20:00', 'Produto recebido em perfeito estado. Estorno solicitado.', 'APROVADA', 'CONCLUIDA', 2),
-- SOLICITAÇÃO 3
(6, '2025-09-16 10:00:00', 'Solicitação de troca aprovada!', 'PENDENTE', 'APROVADA', 3),
(7, '2025-09-20 16:00:00', 'Troca de numeração efetuada com sucesso.', 'APROVADA', 'CONCLUIDA', 3),
-- SOLICITAÇÃO 4
(8, '2025-09-16 08:50:00', 'Solicitação cancelada - 16/09/2025 08:50', 'PENDENTE', 'CANCELADA', 4),
-- SOLICITAÇÃO 5
(9, '2025-09-01 10:00:00', 'Solicitação de devolução reprovada! Motivo: Fora do prazo legal de 7 dias e sem defeito técnico.', 'PENDENTE', 'REJEITADA', 5),
-- SOLICITAÇÃO 6
(10, '2025-09-02 10:00:00', 'Solicitação de troca aprovada! Agendando coleta.', 'PENDENTE', 'APROVADA', 6),
(11, '2025-09-05 11:30:00', 'Troca realizada. Pistão substituído.', 'APROVADA', 'CONCLUIDA', 6),
-- SOLICITAÇÃO 7
(12, '2025-03-19 14:00:00', 'Solicitação de troca aprovada!', 'PENDENTE', 'APROVADA', 7),
(13, '2025-03-20 13:00:00', 'Novo exemplar enviado ao cliente.', 'APROVADA', 'CONCLUIDA', 7),
-- SOLICITAÇÃO 8
(14, '2025-06-15 08:00:00', 'Solicitação de troca reprovada! Produto adquirido há mais de 3 meses.', 'PENDENTE', 'REJEITADA', 8);

-- FEEDBACKS
INSERT IGNORE INTO tb_feedback
(id_feedback, comentario, data_feedback, nota, status, tipo_feedback, id_consumidor, id_loja, id_solicitacao)
VALUES
-- SOLICITAÇÃO 1 (Venda da Loja 1 - CenterTech)
(1, 'Processo de troca foi muito rápido e transparente. O novo notebook está perfeito.', '2025-06-26', 5, 'ATIVO', 'SOLICITACAO', 1, 1, 1),
(2, 'Sempre compro na CenterTech e nunca decepcionam, mesmo quando dá problema.', '2025-06-26', 5, 'ATIVO', 'LOJA', 1, 1, 1),
-- SOLICITAÇÃO 2 (Venda da Loja 1 - CenterTech)
(3, 'A devolução funcionou, mas o estorno demorou um pouco para cair no cartão.', '2025-06-28', 3, 'ATIVO', 'SOLICITACAO', 1, 1, 2),
-- SOLICITAÇÃO 3 (Venda da Loja 2 - Estilo&Cia)
(4, 'A Estilo&Cia tem as melhores roupas. O atendimento da Carla foi excelente.', '2025-09-21', 5, 'ATIVO', 'LOJA', 5, 2, 3),
-- SOLICITAÇÃO 6 (Venda da Loja 1 - CenterTech)
(5, 'Tive que insistir muito no chat para conseguirem aprovar a troca.', '2025-09-06', 2, 'ATIVO', 'LOJA', 7, 1, 6),
(6, 'Pelo menos a cadeira nova veio sem defeito no pistão. Resolveram o problema.', '2025-09-06', 4, 'ATIVO', 'SOLICITACAO', 7, 1, 6),
-- SOLICITAÇÃO 7 (Venda da Loja 3 - MundoLivros)
(7, 'Enviam livros sem proteção adequada. É a segunda vez que chega amassado.', '2025-03-21', 1, 'ATIVO', 'LOJA', 9, 3, 7),
(8, 'Sistema de troca confuso, pede muitas fotos e demora para validar.', '2025-03-21', 2, 'ATIVO', 'SOLICITACAO', 9, 3, 7);

-- LICENCAS
INSERT IGNORE INTO tb_licenca (id_licenca, validade, status, id_loja) VALUES
('31323635-3133-3064-2d31-3464622d3131', '2026-12-31', 'ATIVO', 1),
('31323635-3163-3634-2d31-3464622d3131', '2026-10-15', 'ATIVO', 2),
('31323635-3165-3330-2d31-3464622d3131', '2026-08-20', 'ATIVO', 3);