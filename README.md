# Techblog - Plataforma de Compartilhamento de Artigos

## 1. Concepção

Este projeto é uma implementação do desafio técnico proposto pela Grão Direto, consistindo em uma plataforma de compartilhamento de artigos entre colaboradores. O objetivo principal é criar um MVP que permita ao usuário:

* **Visualizar** artigos, podendo filtrá-los por tags ou buscando por texto.
* **Ler** o conteúdo do artigo.
* **Comentar** nos artigos.
* **Publicar** novos artigos.
* **Editar** e **Excluir** seus próprios artigos.

## 2. Decisões Técnicas

### 2.1. Tecnologias Utilizadas

* **Backend:**
    * **Linguagem:** Java 24 (Maven)
    * **Framework:** Spring Boot 3.4.11
    * **Banco de Dados:** PostgreSQL na produção, H2 no desenvolvimento
    * **Segurança:** Spring Security e JWT
* **Frontend:**
    * **Framework:** React (que na verdade é uma biblioteca, mas costumeiramente) usada como um framework + React Router
    * **Estilização:** CSS puro

### 2.2. Justificativas

* **Java/Spring Boot:** Escolha robusta, com uma certa aptidão para desenvolvimento de APIs REST, integração com bancos de dados (usando o JPA) e segurança (Spring Security). Além disso, era a linguagem que mais estava fresca na minha memória para ser usada no backend.
* **React:** React é uma biblioteca para construção de interfaces dinâmicas e componentizadas.
* **PostgreSQL:** Banco de dados relacional open-source, adequado para essa aplicação web e alinhado com o requisito de um BD relacional. O H2 foi usado no desenvolvimento pela vantagem de ser leve e para uma exploração desse "database local" que o Java oferece.
* **JWT:** Padrão de autenticação em APIs REST e o Spring Security oferece uma boa integração com o JWT.
* **API REST & DTOs:** O uso de DTOs (Data Transfer Objects; um conjunto de classes que ajuda na transferência de dados na comunicação front -> backend) na camada da API (ex: `ArticleDTO`, `ArticleSummaryDTO`, `CommentDTO` e melhora a segurança já que não expõe dados sensíveis.
* **CSS Puro:** Nesse projeto, o CSS puro foi suficiente para replicar o design e por isso, não houve necessidade de usar bibliotecas (como o Bootstrap).

## 3. Organização do Código

O projeto está dividido em:

* **`techblog-api/` (Backend)**
    * `src/main/java/com/techblog/techblogapi/`
        * `config/`: Classes de configuração.
        * `controller/`: Controladores.
        * `dto/`: Data Transfer Objects.
        * `model/`: Entidades.
        * `repository/`: Interfaces Spring Data.
        * `security/`: Classes relacionadas à segurança.
    * `src/main/resources/`: Arquivos de configuração e dados iniciais.
    * `pom.xml`: Dependências e configuração do Maven.

* **`techblog-ui/` (Frontend)**
    * `src/`
        * `components/`: Componentes React.
        * `pages/`: Componentes que representam páginas completas.
        * `services/`: Lógica de comunicação com a API.
        * `App.jsx`: Componente principal.
        * `main.jsx`: Configuração do React Router.
        * `index.css`: Os estilos das páginas.
    * `package.json`: Dependências e scripts do npm (vem junto da pasta de instalação).
    * `vite.config.js`: Configuração do Vite(vem junto da pasta de instalação).

## 4. Como Rodar o Projeto Localmente

### Requisitos:

* JDK 24 ou superior.
* Eu uso algumas extensões no VS Code: "Debbuger for Java - Microsoft", "ES7+ React - dsznajder", "Extension Pack for Java - Microsoft", "Laguage Support for Java(TM) - Red Hat", "Maven for Java", "Spring Boot Extension Pack - VMware", "Spring Boot Tools - VMware", "Spring Initializr Java Support - Microsoft", "Test Runner for Java - Microsoft", "XML - Red Hat".
* Maven.
* Node.js e npm.
* PostgreSQL instalado e rodando (com um banco de dados `techblog_db` já criado).

### Backend (`techblog-api/`)

1.  **Configure o Banco:** Edite o arquivo `src/main/resources/application-prod.properties` e ajuste `spring.datasource.username` e `spring.datasource.password` com suas credenciais do PostgreSQL — as configuradas na instalação.
2.  **Compile e Rode:** Navegue até o diretório `techblog-api/` no terminal e execute:
    ```bash
    mvn spring-boot:run
    ```
    ou vá até o arquivo "TechblogApiApplication.java", clique com o botão direito na janela do código e selecione "Run Java".
    O backend estará rodando em `http://localhost:8080`. Na primeira execução, ele criará as tabelas e populará o banco com os dados do `data.json`.

### Frontend (`techblog-ui/`)

1.  **Instale as Dependências:** Navegue até o diretório `techblog-ui/` no terminal e execute:
    ```bash
    npm install
    ```
2.  **Rode o Servidor de Desenvolvimento:** No mesmo diretório, execute:
    ```bash
    npm run dev
    ```
    A aplicação frontend estará acessível em `http://localhost:5173`.

### Acesso à Aplicação

Abra seu navegador e acesse `http://localhost:5173`.

* **Usuário de Teste (criado pelo Seeder, mas que eu defini no código como):**
    * **Email:** `admin@techblog.com`
    * **Senha:** `123456`
