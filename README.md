# Capifunding 💵

Capifunding é uma plataforma online que conecta criadores de projetos com indivíduos interessados em apoiar ideias
inovadoras e iniciativas por meio de financiamento coletivo. Seja você um empreendedor, artista ou organizador
comunitário em busca de fundos para um novo empreendimento, projeto artístico ou iniciativa comunitária, esta plataforma
oferece uma maneira conveniente e eficaz de arrecadar fundos e dar vida à sua visão.

## Como Funciona:

- Criação do Usuário: Comece criando sua conta na plataforma e ativando sua conta com o email que é enviado para você.
- Criação de Projeto: Crie uma página de projeto convincente, detalhando sua ideia, metas de financiamento e as etapas
  de execução do seu projeto.
- Lançamento da Campanha: Assim que seu projeto estiver no ar, promova-o para sua rede e além para atrair colaboradores.
- Coleta de Contribuições: Veja como os apoiadores contribuem com fundos para o seu projeto, ajudando-o a alcançar sua
  meta de financiamento.
- Conclusão do Projeto: Uma vez financiado, use os fundos arrecadados para dar vida ao seu projeto e mantenha os
  colaboradores atualizados sobre o progresso.

## Requisitos:

- Java 17 ou superior ☕
- Maven 🛠️
- PostgreSQL v14 🐘

## Tecnologias utilizadas:

- Spring Boot 3.2 🍃
- Spring Security 🔒
- Spring Data JPA 🗄️
- Spring Validation ✅
- Lombok ⏩
- Flyway 🐦
- JavaMailSender 📧
- Java JWT 🔑
- Thumbnailator 🖼️
- PostgreSQL 🐘

## Configurando as variáveis de ambiente:

- CAPIFUNDING_DATABASE (url do banco, ex: 'localhost:5432/capifunding')
- CAPIFUNDING_DATABASE_USERNAME (nome do usuário no banco)
- CAPIFUNDING_DATABASE_PASSWORD (senha do usuário no banco)
- CAPIFUNDING_EMAIL (o email que será usado no envio dos emails)
- CAPIFUNDING_EMAIL_PASSWORD (a senha de APP do email, não é a senha padrão)
- CAPIFUNDING_TOKEN (chave secreta que será usada na criação dos tokens)
