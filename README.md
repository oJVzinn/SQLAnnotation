# SQLAnnotation - SDK para Sistemas SQL

![Java](https://img.shields.io/badge/Java-8-blue?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red?style=for-the-badge&logo=apache-maven)
![HikariCP](https://img.shields.io/badge/HikariCP-4.0.3-purple?style=for-the-badge&logo=databricks)

---

# Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Requisitos](#requisitos)
- [Instalação e Configuração](#instalação-e-configuração)
- [Exemplo de Uso](#exemplo-de-uso)
- [Para Desenvolvedores](#compilando-o-projeto)

---

## Sobre o Projeto

**SQLAnnotation** é um SDK em **Java** desenvolvido para simplificar o gerenciamento de consultas SQL em diferentes bancos de dados.  

O foco principal é oferecer **abstrações simples**, **funções utilitárias** e **gerenciamento de conexões eficiente** através do **HikariCP**, permitindo que desenvolvedores criem aplicações escaláveis e de alto desempenho com menos esforço.  

Atualmente o SDK possui suporte a **MySQL**, mas foi projetado para ser **flexível** e futuramente oferecer compatibilidade com **PostgreSQL** e **SQLite**.

---

## Funcionalidades

- **Abstrações de Queries**: Gerenciamento de requisições SQL por meio de **interfaces personalizáveis**.  
- **Compatibilidade**: Suporte aos principais bancos SQL *(atualmente apenas MySQL)*.  
- **Configuração Simples**: Setup rápido via arquivos de configuração ou código.  
- **Connection Pooling**: Integração com **HikariCP** para conexões estáveis e performáticas.  
- **Extensível**: Estrutura flexível para adicionar suporte a novos bancos de dados.  

---

## Requisitos

- **Java**: Versão 8 ou superior.  
- **Maven**: 4.0.0 ou superior.  
- **Banco de Dados**: Servidor **MySQL** acessível (PostgreSQL e SQLite em breve).  

---

## Instalação e Configuração

1. **Clone o repositório**:  
   ```sh
   git clone https://github.com/seu-usuario/sqlannotation.git
   ```
2. **Navegue até o diretório**:  
   ```sh
   cd sqlannotation
   ```
3. **Compile o projeto**:  
   ```sh
   mvn clean package
   ```
4. Adicione o `.jar` gerado na pasta `target/` ao seu projeto.  

---

## Exemplo de Uso

### 📌 Configuração e Inicialização

```java
public class Teste {

    private final UserRepository repository = SQLAnnotation.loadRepository(UserRepository.class);

    @Test
    public void main() {
        MySQLEntity mySQL = new MySQLEntity("localhost", 3306, "server", "root", "");
        SQLConfigEntity config = new SQLConfigEntity(mySQL);
        config.setLog(true);

        // Inicializa o SQLAnnotation
        SQLAnnotation.init(config);

        // Escaneia a entidade User
        SQLAnnotation.scanEntity(User.class);
    }

}
```

---

### 📌 Entidade de Exemplo

```java
@Entity(name = "USERS")
@Getter
@Setter
@ToString
public class User {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer age;

    @Column
    private String email;

    @Varchar(length = 1)
    @Column(notNull = false)
    private String gender;

}
```

---

### 📌 Repository com Queries Customizadas

```java
public interface UserRepository extends Repository<User> {

    User findByName(String name);

    JSONArray findAllByConditionalsAgeAndName(Integer age, String name);

    void deleteAllByConditionalsAgeAndEmail(Integer age, String email);

    void deleteByAge(Integer age);
}
```

---

## Compilando o Projeto (Para Desenvolvedores)

Caso queira modificar ou compilar manualmente:  

1. Clone o repositório:
   ```sh
   git clone https://github.com/seu-usuario/sqlannotation.git
   ```
2. Entre no diretório:
   ```sh
   cd sqlannotation
   ```
3. Compile com Maven:
   ```sh
   mvn clean package
   ```
4. O `.jar` final estará em `target/`.  

---

Desenvolvido com ❤️ por [oJVzinn](https://github.com/oJVzinn) && [oNyell](https://github.com/oNyell)
