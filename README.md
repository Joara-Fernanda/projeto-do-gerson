SolidarIf

Um sistema web desenvolvido para facilitar e organizar o processo de doação de itens entre alunos do Instituto Federal de São Paulo (IFSP).
A plataforma permite que usuários se cadastrem como doadores ou beneficiados, tornando o processo de oferta e solicitação de itens simples, rápido e acessível.

Descrição do Projeto

Este projeto consiste em um sistema web de gerenciamento de doações voltado ao ambiente estudantil do IFSP.
A aplicação oferece um ambiente onde os estudantes podem cadastrar itens para doação, visualizar produtos disponíveis, filtrar resultados e interagir com um fluxo organizado de doações.

O sistema inclui funcionalidades de autenticação, cadastro de produtos, gerenciamento de itens, controle de status e busca avançada, promovendo reutilização de materiais e reduzindo o desperdício.
O objetivo principal é apoiar a comunidade acadêmica, estimulando a solidariedade e a sustentabilidade.

Funcionalidades Principais
1. Autenticação e Registro

Criação de contas com definição do tipo: Doador ou Beneficiado.

Login com validação de credenciais.

Controle de sessão e acesso seguro às funcionalidades internas.

2. Funcionalidades do Módulo do Doador

Cadastro de itens contendo: nome, descrição, categoria e imagem.

Exclusão de itens cadastrados.

Marcação do item como "doado", indicando entrega ou retirada concluída.

3. Funcionalidades do Módulo do Beneficiado

Visualização de todos os produtos disponíveis.

Sistema de busca com filtros por nome, categoria e descrição.

Página de detalhes completa para cada item disponível.

Objetivo do Sistema

O objetivo deste projeto é fornecer uma plataforma eficiente para o compartilhamento de materiais entre estudantes do IFSP, incentivando práticas de reutilização, solidariedade e integração estudantil.
O sistema facilita tanto o ato de doar quanto o de receber, tornando o processo mais organizado, acessível e sustentável.

Tecnologias Utilizadas

Java 17+

Spring Boot (Web, Data JPA)

Maven

H2 Database

HTML5, CSS3, JavaScript

Thymeleaf

Requisitos para Execução
Recurso	 Versão Recomendada
Java     JDK	17 ou superior
Maven    3.8+
H2 Database	já incluído no projeto
Git	opcional

Como Instalar e Executar o Projeto
1. Clonar o Repositório
git clone https://github.com/joara-fernanda/projeto-do-gerson.git
cd repositorio

2. Compilar o Projeto
mvn clean install

3. Executar a Aplicação
mvn spring-boot:run

4. Acessar a Aplicação no Navegador

Acesse:

http://localhost:8080/cadastro

Configuração do H2 Database

O projeto utiliza um banco H2 em memória, com console habilitado.

Console do H2:
http://localhost:8080/h2-console

Credenciais padrão:

JDBC URL: jdbc:h2:file:./data/testdb;AUTO_SERVER=TRUE;IFEXISTS=FALSE

Usuário: sa

Senha: (vazia)

Estrutura de Diretórios do Projeto
src/
 ├── main/
    ├── java/com/ifsp/projeto
    │   ├── controller/        # Controladores Web
    │   ├── repository/        # Interfaces de acesso ao banco
    │     
    └── resources/
        ├── templates/         # Páginas HTML (Thymeleaf)
        ├── static/            # Arquivos CSS, JS e imagens
        └── application.properties
 
Autor(a):
Joara Fernanda
