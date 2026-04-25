package com.logistica.doisv.services;

import com.logistica.doisv.entities.Lojista;
import com.logistica.doisv.entities.Venda;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviarEmailAcessoConsumidor(Venda venda, String senha){
        try {
            if (venda == null || venda.getConsumidor() == null || venda.getLoja() == null) {
                return;
            }
            String corpoHtmlEmail = """
                    <!DOCTYPE html>
                    <html lang="pt-br">
                    <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Acesso para Troca ou Devolução - %s</title>
                    <style>
                        /* Estilos Gerais */
                        body {
                            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                            margin: 0;
                            padding: 0;
                            background-color: #f1f4f8;
                        }
                        .container {
                            width: 100%%;
                            max-width: 600px;
                            margin: 20px auto;
                            background-color: #ffffff;
                            border-radius: 12px;
                            overflow: hidden;
                            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                            border: 1px solid #dee2e6;
                        }
                    
                        /* Cabeçalho */
                        .header {
                            padding: 25px;
                            text-align: center;
                            background-color: #f8f9fa;
                            border-bottom: 1px solid #dee2e6;
                        }
                        .header img {
                            max-width: 120px;
                            height: auto;
                            margin-bottom: 10px;
                        }
                        .header h2 {
                            margin: 0;
                            color: #343a40;
                            font-size: 22px;
                            font-weight: 600;
                        }
                    
                        /* Conteúdo */
                        .content {
                            padding: 35px;
                            color: #000000;
                            line-height: 1.7;
                            font-size: 16px;
                        }
                        .content h1 {
                            color: #212529;
                            font-size: 26px;
                            font-weight: 700;
                            margin-bottom: 20px;
                        }
                        .content p {
                            margin: 15px 0;
                        }
                    
                        /* Caixa de Credenciais (Tons de Azul) */
                        .credentials-box {
                            background-color: #5583e7;
                            border-left: 5px solid #3c6adb;
                            padding: 20px;
                            margin: 30px 0;
                            text-align: center;
                        }
                        .credentials-box p {
                            margin: 12px 0;
                            font-size: 15px;
                            color: #ffffff;
                        }
                        .credentials-box span {
                            font-weight: bold;
                            font-size: 14px;
                            color: #5583e7;
                            background-color: #ffffff;
                            padding: 8px 15px;
                            border-radius: 6px;
                            font-family: 'Courier New', Courier, monospace;
                            display: inline-block;
                            margin-left: 8px;
                            border: 1px solid #e0e0e0;
                        }
                    
                        /* Botão (Gradiente Azul) */
                        .button {
                            display: inline-block;
                            background-color: #5583e7;
                            color: #ffffff;
                            padding: 15px 35px;
                            text-align: center;
                            text-decoration: none;
                            border-radius: 8px;
                            margin-top: 20px;
                            font-size: 16px;
                            font-weight: bold;
                            transition: transform 0.2s, box-shadow 0.2s;
                        }
                        .button:hover {
                            transform: scale(1.05);
                            box-shadow: 0 4px 10px rgba(77, 208, 225, 0.4); /* Sombra sutil no hover */
                        }
                    
                        /* Rodapé */
                        .footer {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            font-size: 13px;
                            color: #6c757d;
                            border-top: 1px solid #dee2e6;
                        }
                        .footer p {
                            margin: 5px 0;
                        }
                        .disclaimer {
                            font-size: 11px;
                            color: #adb5bd;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <img src="https://lh3.googleusercontent.com/d/%s" alt="Logo de %s">
                                <h2>%s</h2>
                            </div>
                            <div class="content">
                                <h1>Olá, %s,</h1>
                                <p>Agradecemos por comprar na loja %s! Você pode realizar a troca ou devolução de sua compra utilizando o código e a senha exclusivos em nosso portal.</p>
                                <p>Por favor, utilize as credenciais abaixo para acessar a área dedicada e seguir com a sua solicitação:</p>
                                <div class="credentials-box">
                                    <p><strong>Serial de Acesso:</strong><span>%s</span></p>
                                    <p><strong>Senha:</strong><span>%s</span></p>
                                </div>
                                <p>Clique no botão abaixo para ser direcionado ao portal e prosseguir:</p>
                                <a href="%s" class="button">Acessar Portal de Trocas</a>
                                <p style="margin-top: 30px;">Atenciosamente,<br>Equipe %s</p>
                            </div>
                            <div class="footer">
                                <p>© %d %s. Todos os direitos reservados.</p>
                                <p class="disclaimer">Este é um e-mail automático. Por favor, não o responda se não precisar de suporte.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """;

            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setTo(venda.getConsumidor().getEmail());
            helper.setSubject("Acesso para Troca ou Devolução - " + venda.getLoja().getNome());

            String corpoEmail = String.format(corpoHtmlEmail,
                    venda.getLoja().getNome(),
                    venda.getLoja().getLogo(),
                    venda.getLoja().getNome(),
                    venda.getLoja().getNome(),
                    venda.getConsumidor().getNome(),
                    venda.getLoja().getNome(),
                    venda.getSerialVenda(),
                    senha,
                    "google.com",
                    "Logvert",
                    LocalDate.now().getYear(),
                    "Logvert"
            );

            helper.setText(corpoEmail, true);
            mailSender.send(mensagem);
        }catch (Exception e) {
            System.err.println("Erro ao enviar email da venda ID: " + venda.getId());
            e.printStackTrace();
        }
    }

    @Async
    public void enviarEmailRecuperacaoSenha(Lojista lojista, String codigoRecuperacao) throws MessagingException{
        String corpoHtmlRecuperacaoSenha = """
                <!DOCTYPE html>
                <html lang="pt-br">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recuperação de Senha - %s</title>
                <style>
                    /* Estilos Gerais */
                    body {
                        font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: #f1f4f8;
                    }
                    .container {
                        width: 100%%;
                        max-width: 600px;
                        margin: 20px auto;
                        background-color: #ffffff;
                        border-radius: 12px;
                        overflow: hidden;
                        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                        border: 1px solid #dee2e6;
                    }
                
                    /* Cabeçalho */
                    .header {
                        padding: 25px;
                        text-align: center;
                        background-color: #f8f9fa;
                        border-bottom: 1px solid #dee2e6;
                    }
                    .header img {
                        max-width: 120px;
                        height: auto;
                        margin-bottom: 10px;
                    }
                    .header h2 {
                        margin: 0;
                        color: #343a40;
                        font-size: 22px;
                        font-weight: 600;
                    }
                
                    /* Conteúdo */
                    .content {
                        padding: 35px;
                        color: #000000;
                        line-height: 1.7;
                        font-size: 16px;
                    }
                    .content h1 {
                        color: #212529;
                        font-size: 26px;
                        font-weight: 700;
                        margin-bottom: 20px;
                    }
                    .content p {
                        margin: 15px 0;
                    }
                
                    /* Caixa de Código */
                    .code-box {
                        background-color: #5583e7;
                        border-left: 5px solid #3c6adb;
                        padding: 30px 20px;
                        margin: 30px 0;
                        text-align: center;
                    }
                    .code-box p {
                        margin: 8px 0;
                        font-size: 15px;
                        color: #ffffff;
                        font-weight: 600;
                    }
                    .code-box span {
                        font-weight: bold;
                        font-size: 32px;
                        color: #5583e7;
                        background-color: #ffffff;
                        padding: 15px 40px;
                        border-radius: 8px;
                        font-family: 'Courier New', Courier, monospace;
                        display: inline-block;
                        margin-top: 12px;
                        letter-spacing: 8px;
                        border: 1px solid #e0e0e0;
                    }
                    
                    /* Aviso de Segurança */
                    .security-warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 25px 0;
                        border-radius: 6px;
                    }
                    .security-warning p {
                        margin: 5px 0;
                        color: #856404;
                        font-size: 14px;
                    }
                
                    /* Rodapé */
                    .footer {
                        background-color: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 13px;
                        color: #6c757d;
                        border-top: 1px solid #dee2e6;
                    }
                    .footer p {
                        margin: 5px 0;
                    }
                    .disclaimer {
                        font-size: 11px;
                        color: #adb5bd;
                    }
                </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <img src="https://lh3.googleusercontent.com/d/%s" alt="Logo de %s">
                            <h2>%s</h2>
                        </div>
                        <div class="content">
                            <h1>Olá, %s,</h1>
                            <p>Recebemos uma solicitação de recuperação de senha para sua conta. Utilize o código de verificação abaixo para redefinir sua senha:</p>
                            <div class="code-box">
                                <p>CÓDIGO DE VERIFICAÇÃO</p>
                                <span>%s</span>
                            </div>
                            <div class="security-warning">
                                <p><strong>⚠️ Importante:</strong></p>
                                <p>• Este código é válido por 30 minutos</p>
                                <p>• Não compartilhe este código com ninguém</p>
                                <p>• Se você não solicitou esta recuperação, ignore este e-mail</p>
                            </div>
                            <p>Após inserir o código, você poderá criar uma nova senha de acesso ao sistema.</p>
                            <p style="margin-top: 30px;">Atenciosamente,<br>Equipe %s</p>
                        </div>
                        <div class="footer">
                            <p>© %d %s. Todos os direitos reservados.</p>
                            <p class="disclaimer">Este é um e-mail automático. Por favor, não o responda se não precisar de suporte.</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

        helper.setTo(lojista.getEmail());
        helper.setSubject("Recuperação de Senha");

        String corpoEmail = String.format(corpoHtmlRecuperacaoSenha,
                lojista.getLoja().getNome(),
                lojista.getLoja().getLogo(),
                lojista.getLoja().getNome(),
                lojista.getLoja().getNome(),
                lojista.getNome(),
                codigoRecuperacao,
                lojista.getLoja().getNome(),
                LocalDate.now().getYear(),
                lojista.getLoja().getNome()
        );

        helper.setText(corpoEmail, true);
        mailSender.send(mensagem);
    }

    @Async
    public void enviarEmailCadastroLoja(Lojista lojistaAdmin, String senha) throws MessagingException {
        String corpoHtmlBoasVindas = """
            <!DOCTYPE html>
            <html lang="pt-br">
            <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Bem-vindo ao %s - Seu Primeiro Acesso</title>
            <style>
                /* Estilos Gerais */
                body {
                    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                    margin: 0;
                    padding: 0;
                    background-color: #f1f4f8;
                }
                .container {
                    width: 100%%;
                    max-width: 600px;
                    margin: 20px auto;
                    background-color: #ffffff;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                    border: 1px solid #dee2e6;
                }
            
                /* Cabeçalho */
                .header {
                    padding: 25px;
                    text-align: center;
                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    border-bottom: 1px solid #dee2e6;
                }
                .header img {
                    max-width: 120px;
                    height: auto;
                    margin-bottom: 10px;
                    background-color: white;
                    border-radius: 60px;
                    padding: 10px;
                }
                .header h2 {
                    margin: 0;
                    color: #ffffff;
                    font-size: 22px;
                    font-weight: 600;
                }
            
                /* Conteúdo */
                .content {
                    padding: 35px;
                    color: #000000;
                    line-height: 1.7;
                    font-size: 16px;
                }
                .content h1 {
                    color: #212529;
                    font-size: 26px;
                    font-weight: 700;
                    margin-bottom: 20px;
                }
                .content p {
                    margin: 15px 0;
                }
            
                /* Caixa de Credenciais */
                .credentials-box {
                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    border-radius: 12px;
                    padding: 30px 20px;
                    margin: 30px 0;
                    text-align: center;
                    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                }
                .credentials-box p {
                    margin: 8px 0;
                    font-size: 15px;
                    color: #ffffff;
                    font-weight: 600;
                    text-transform: uppercase;
                    letter-spacing: 1px;
                }
                .credentials-box .credential-item {
                    margin: 20px 0;
                }
                .credentials-box .label {
                    font-size: 14px;
                    color: rgba(255, 255, 255, 0.8);
                    margin-bottom: 5px;
                }
                .credentials-box .value {
                    font-weight: bold;
                    font-size: 24px;
                    color: #ffffff;
                    background-color: rgba(255, 255, 255, 0.15);
                    padding: 15px 30px;
                    border-radius: 50px;
                    font-family: 'Courier New', Courier, monospace;
                    display: inline-block;
                    margin-top: 5px;
                    letter-spacing: 2px;
                    border: 2px solid rgba(255, 255, 255, 0.3);
                    backdrop-filter: blur(10px);
                    max-width: 90%%;
                    word-wrap: break-word;
                    overflow-wrap: break-word;
                }
                
                .credentials-box .value a {
                        color: #ffffff !important;
                        text-decoration: none !important;
                    }
                
                /* Cartões de Orientação */
                .guidance-card {
                    background-color: #f8f9fa;
                    border-radius: 10px;
                    padding: 20px;
                    margin: 25px 0;
                    border-left: 4px solid #ffc107;
                }
                .guidance-card h3 {
                    color: #212529;
                    font-size: 18px;
                    margin-top: 0;
                    margin-bottom: 15px;
                    font-weight: 600;
                }
                .guidance-card ul {
                    margin: 10px 0;
                    padding-left: 20px;
                }
                .guidance-card li {
                    margin: 10px 0;
                    color: #495057;
                }
                
                /* Avisos de Segurança */
                .security-box {
                    background-color: #fff3cd;
                    border: 1px solid #ffc107;
                    border-radius: 8px;
                    padding: 20px;
                    margin: 25px 0;
                }
                .security-box h4 {
                    color: #856404;
                    margin-top: 0;
                    margin-bottom: 15px;
                    font-size: 16px;
                    font-weight: 600;
                }
                .security-box p {
                    margin: 10px 0;
                    color: #856404;
                    font-size: 14px;
                }
                .security-box .warning-icon {
                    font-size: 20px;
                    margin-right: 10px;
                }
                
                /* Botão de Ação */
                .action-button {
                    text-align: center;
                    margin: 35px 0;
                }
                .action-button a {
                    background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                    color: #ffffff;
                    padding: 15px 40px;
                    border-radius: 50px;
                    text-decoration: none;
                    font-weight: 600;
                    font-size: 16px;
                    display: inline-block;
                    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                    border: none;
                    transition: transform 0.3s ease;
                }
                .action-button a:hover {
                    transform: translateY(-2px);
                }
            
                /* Rodapé */
                .footer {
                    background-color: #f8f9fa;
                    padding: 20px;
                    text-align: center;
                    font-size: 13px;
                    color: #6c757d;
                    border-top: 1px solid #dee2e6;
                }
                .footer p {
                    margin: 5px 0;
                }
                .disclaimer {
                    font-size: 11px;
                    color: #adb5bd;
                }
            </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <img src="https://lh3.googleusercontent.com/d/%s" alt="Logo %s">
                        <h2>%s</h2>
                    </div>
                    <div class="content">
                        <h1>Olá, %s! 🎉</h1>
                        <p>Seja muito bem-vindo ao <strong>%s</strong>! Estamos muito felizes em ter sua loja conosco. Sua conta foi criada com sucesso e preparamos um acesso especial para você começar a gerenciar seu negócio.</p>
                        
                        <div class="credentials-box">
                            <p>🔐 SUAS CREDENCIAIS DE ACESSO</p>
                            <div class="credential-item">
                                <div class="label">USUÁRIO (E-MAIL)</div>
                                <div class="value">%s</div>
                            </div>
                            <div class="credential-item">
                                <div class="label">SENHA PROVISÓRIA</div>
                                <div class="value">%s</div>
                            </div>
                        </div>
                        
                        <div class="guidance-card">
                            <h3>📋 PRIMEIROS PASSOS OBRIGATÓRIOS:</h3>
                            <ul>
                                <li><strong>1. ALTERE SUA SENHA AGORA MESMO:</strong> Por segurança, você deve alterar esta senha provisória no seu primeiro acesso ao sistema.</li>
                                <li><strong>2. ACESSO ADMINISTRATIVO:</strong> Este usuário possui privilégios de administrador. Utilize-o apenas para configurações gerais e gerenciamento de outros usuários.</li>
                                <li><strong>3. CRIE SEU USUÁRIO DIÁRIO:</strong> Para operações do dia a dia, crie um usuário comum com permissões limitadas.</li>
                            </ul>
                        </div>
                        
                        <div class="security-box">
                            <h4><span class="warning-icon">⚠️</span> RECOMENDAÇÕES IMPORTANTES DE SEGURANÇA:</h4>
                            <p>• <strong>NÃO COMPARTILHE ESTE ACESSO:</strong> Este usuário admin é pessoal e intransferível. Cada administrador deve ter seu próprio acesso.</p>
                            <p>• <strong>SENHA FORTE:</strong> Ao alterar sua senha, utilize uma combinação forte com letras maiúsculas, minúsculas, números e caracteres especiais.</p>
                            <p>• <strong>USUÁRIO DO DIA A DIA:</strong> Crie um usuário comum para atividades rotineiras como vendas, consultas e emissão de relatórios básicos.</p>
                            <p>• <strong>RESERVE O ADMIN:</strong> Mantenha este acesso para situações que realmente exijam permissões elevadas como cadastro de novos usuários, configurações do sistema e exclusão de dados.</p>
                        </div>
                        
                        <div class="action-button">
                            <a href="%s" target="_blank">🔑 ACESSAR O SISTEMA AGORA</a>
                        </div>
                        
                        <p style="margin-top: 30px;">Precisa de ajuda? Nossa equipe de suporte está pronta para auxiliá-lo no que for preciso.</p>
                        <p style="margin-top: 30px;">Atenciosamente,<br>Equipe <strong>%s</strong></p>
                    </div>
                    <div class="footer">
                        <p>© %d %s. Todos os direitos reservados.</p>
                        <p class="disclaimer">Este é um e-mail automático. Por favor, não responda se não precisar de suporte.</p>
                    </div>
                </div>
            </body>
            </html>
            """;

        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

        helper.setTo(lojistaAdmin.getEmail());
        helper.setSubject(String.format("Bem-vindo ao %s - Seu acesso foi criado!",
                lojistaAdmin.getLoja().getNome()));

        String urlAcesso = "https://app.logistica.com.br/login"; // URL do seu sistema
        String logoId = "1OAZrlZgYhXO-UzJLx9SZy6JgdLs6W4v2";

        String corpoEmail = String.format(corpoHtmlBoasVindas,
                lojistaAdmin.getLoja().getNome(), // Título
                logoId, // Logo
                lojistaAdmin.getLoja().getNome(), // Nome da loja no header
                "2V Logística", // Nome da loja repetido
                lojistaAdmin.getNome(), // Nome do lojista
                lojistaAdmin.getLoja().getNome(), // Nome do sistema
                lojistaAdmin.getEmail(), // Email do usuário
                senha, // Senha provisória gerada
                urlAcesso, // URL de acesso
                "2V", // Nome da loja no rodapé
                LocalDate.now().getYear(), // Ano atual
                "2V Logística" // Nome da loja no copyright
        );

        helper.setText(corpoEmail, true);
        mailSender.send(mensagem);
    }

    @Async
    public void enviarEmailUsuarioMasterSuporte(String emailUsuarioMaster, String senha, String nomeLoja) throws MessagingException {
        String corpoHtmlMasterSuporte = """
        <!DOCTYPE html>
        <html lang="pt-br">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Usuário Master Criado - %s</title>
        <style>
            body {
                font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f1f4f8;
            }
            .container {
                width: 100%%;
                max-width: 600px;
                margin: 20px auto;
                background-color: #ffffff;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                border: 1px solid #dee2e6;
            }

            /* Cabeçalho */
            .header {
                padding: 25px;
                text-align: center;
                background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                border-bottom: 1px solid #dee2e6;
            }
            .header img {
                max-width: 120px;
                height: auto;
                margin-bottom: 10px;
                background-color: white;
                border-radius: 60px;
                padding: 10px;
            }
            .header h2 {
                margin: 0;
                color: #ffffff;
                font-size: 22px;
                font-weight: 600;
            }

            /* Conteúdo */
            .content {
                padding: 35px;
                color: #000000;
                line-height: 1.7;
                font-size: 16px;
            }
            .content h1 {
                color: #212529;
                font-size: 26px;
                font-weight: 700;
                margin-bottom: 20px;
            }
            .content p {
                margin: 15px 0;
            }

            /* Caixa de Credenciais */
            .credentials-box {
                background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                border-radius: 12px;
                padding: 30px 20px;
                margin: 30px 0;
                text-align: center;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
            }
            .credentials-box p {
                margin: 8px 0;
                font-size: 15px;
                color: #ffffff;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 1px;
            }
            .credentials-box .credential-item {
                margin: 20px 0;
            }
            .credentials-box .label {
                font-size: 14px;
                color: rgba(255, 255, 255, 0.8);
                margin-bottom: 5px;
            }
            .credentials-box .value {
                font-weight: bold;
                font-size: 24px;
                color: #ffffff;
                background-color: rgba(255, 255, 255, 0.15);
                padding: 15px 30px;
                border-radius: 50px;
                font-family: 'Courier New', Courier, monospace;
                display: inline-block;
                margin-top: 5px;
                letter-spacing: 2px;
                border: 2px solid rgba(255, 255, 255, 0.3);
                backdrop-filter: blur(10px);
                max-width: 90%%;
                word-wrap: break-word;
                overflow-wrap: break-word;
            }

            .credentials-box .value a {
                color: #ffffff !important;
                text-decoration: none !important;
            }

            /* Cartões de Orientação */
            .guidance-card {
                background-color: #f8f9fa;
                border-radius: 10px;
                padding: 20px;
                margin: 25px 0;
                border-left: 4px solid #ffc107;
            }
            .guidance-card h3 {
                color: #212529;
                font-size: 18px;
                margin-top: 0;
                margin-bottom: 15px;
                font-weight: 600;
            }
            .guidance-card ul {
                margin: 10px 0;
                padding-left: 20px;
            }
            .guidance-card li {
                margin: 10px 0;
                color: #495057;
            }

            /* Avisos de Segurança */
            .security-box {
                background-color: #fff3cd;
                border: 1px solid #ffc107;
                border-radius: 8px;
                padding: 20px;
                margin: 25px 0;
            }
            .security-box h4 {
                color: #856404;
                margin-top: 0;
                margin-bottom: 15px;
                font-size: 16px;
                font-weight: 600;
            }
            .security-box p {
                margin: 10px 0;
                color: #856404;
                font-size: 14px;
            }
            .security-box .warning-icon {
                font-size: 20px;
                margin-right: 10px;
            }

            /* Botão de Ação */
            .action-button {
                text-align: center;
                margin: 35px 0;
            }
            .action-button a {
                background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                color: #ffffff;
                padding: 15px 40px;
                border-radius: 50px;
                text-decoration: none;
                font-weight: 600;
                font-size: 16px;
                display: inline-block;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                border: none;
                transition: transform 0.3s ease;
            }
            .action-button a:hover {
                transform: translateY(-2px);
            }

            /* Rodapé */
            .footer {
                background-color: #f8f9fa;
                padding: 20px;
                text-align: center;
                font-size: 13px;
                color: #6c757d;
                border-top: 1px solid #dee2e6;
            }
            .footer p {
                margin: 5px 0;
            }
            .disclaimer {
                font-size: 11px;
                color: #adb5bd;
            }
        </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <img src="https://lh3.googleusercontent.com/d/%s" alt="Logo 2V Logística">
                    <h2>2V Logística — Uso Interno</h2>
                </div>
                <div class="content">
                    <h1>🛡️ Novo Usuário Master Criado</h1>
                    <p>Uma nova loja foi cadastrada no sistema e um <strong>usuário master de suporte</strong> foi gerado automaticamente. Este acesso é destinado exclusivamente à equipe de suporte para realização de ações administrativas e correções quando necessário.</p>

                    <div class="guidance-card">
                        <h3>🏪 Informações da Loja</h3>
                        <ul>
                            <li><strong>Nome da Loja:</strong> %s</li>
                            <li><strong>Data de Cadastro:</strong> %s</li>
                        </ul>
                    </div>

                    <div class="credentials-box">
                        <p>🔐 CREDENCIAIS DO USUÁRIO MASTER</p>
                        <div class="credential-item">
                            <div class="label">USUÁRIO (E-MAIL)</div>
                            <div class="value">%s</div>
                        </div>
                        <div class="credential-item">
                            <div class="label">SENHA MASTER</div>
                            <div class="value">%s</div>
                        </div>
                    </div>

                    <div class="security-box">
                        <h4><span class="warning-icon">⚠️</span> ATENÇÃO — USO RESTRITO AO SUPORTE:</h4>
                        <p>• <strong>ACESSO INTERNO:</strong> Estas credenciais são de uso exclusivo da equipe de suporte técnico. Não devem ser compartilhadas com o lojista.</p>
                        <p>• <strong>USO EMERGENCIAL:</strong> Utilize este acesso somente para correções, ajustes técnicos ou ações que não possam ser realizadas pelo próprio lojista.</p>
                        <p>• <strong>GUARDE COM SEGURANÇA:</strong> Armazene estas credenciais em local seguro e de acesso controlado pela equipe.</p>
                        <p>• <strong>RASTREABILIDADE:</strong> Toda ação realizada com este usuário ficará registrada nos logs do sistema. Utilize-o com responsabilidade.</p>
                    </div>

                    <div class="action-button">
                        <a href="%s" target="_blank">🔑 ACESSAR O SISTEMA</a>
                    </div>

                    <p style="margin-top: 30px;">Este e-mail foi gerado automaticamente no momento do cadastro da loja. Em caso de dúvidas, entre em contato com a equipe de desenvolvimento.</p>
                    <p style="margin-top: 30px;">Atenciosamente,<br>Sistema <strong>2V Logística</strong></p>
                </div>
                <div class="footer">
                    <p>© %d 2V Logística. Todos os direitos reservados.</p>
                    <p class="disclaimer">Este é um e-mail automático de uso interno. Não responda este e-mail.</p>
                </div>
            </div>
        </body>
        </html>
        """;

        MimeMessage mensagem = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

        helper.setTo("kamilylacerda1@gmail.com");
        helper.setSubject(String.format("[SUPORTE] Usuário Master criado — Loja: %s", nomeLoja));

        String urlAcesso = "https://app.logistica.com.br/login";
        String logoId = "1OAZrlZgYhXO-UzJLx9SZy6JgdLs6W4v2";

        String corpoEmail = String.format(corpoHtmlMasterSuporte,
                nomeLoja,                                   // Título da página
                logoId,                                     // ID do logo
                nomeLoja,                                   // Nome da loja no guidance-card
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), // Data de cadastro
                emailUsuarioMaster,                         // Email do usuário master
                senha,                                      // Senha master gerada
                urlAcesso,                                  // URL de acesso
                LocalDate.now().getYear()                   // Ano no rodapé
        );

        helper.setText(corpoEmail, true);
        mailSender.send(mensagem);
    }
}
 