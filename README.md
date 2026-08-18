# SQLAnnotation - SDK para Sistemas SQL

![Java](https://img.shields.io/badge/Java-8-blue?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-4.0.0-red?style=for-the-badge&logo=apache-maven)
![HikariCP](https://img.shields.io/badge/HikariCP-4.0.3-purple?style=for-the-badge&logo=databricks)

---

# Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Requisitos e Permissões](#requisitos-e-permissões)
- [Instalação e Configuração](#instalação-e-configuração)
- [Exemplo de Uso](#exemplo-de-uso)
  - [Configuração e Inicialização](#-configuração-e-inicialização)
  - [Entidades e Anotações](#-entidades-e-anotações)
  - [Repositórios Dinâmicos](#-repositórios-dinâmicos)
  - [Relacionamentos com @Join](#-relacionamentos-com-join)
- [Ciclo de Vida e Operações Administrativas](#ciclo-de-vida-e-operações-administrativas)
- [Compilando o Projeto](#compilando-o-projeto)

---

## Sobre o Projeto

**SQLAnnotation** é um SDK em **Java** desenvolvido para simplificar o gerenciamento de consultas SQL em diferentes bancos de dados.  

O foco principal é oferecer **abstrações simples**, **funções utilitárias** e **gerenciamento de conexões eficiente** através do **HikariCP**, permitindo que desenvolvedores criem aplicações escaláveis e de alto desempenho com menos esforço.  

Atualmente o SDK possui suporte a **MySQL**, com estrutura planejada para **PostgreSQL** e **SQLite**.

---

## Funcionalidades

- **Abstrações de Queries**: Gerenciamento de requisições SQL por meio de **interfaces de repositório dinâmicas** (`Repository<T>`).  
- **Auto DDL**: Criação automática de tabelas e validação de colunas baseada em anotações (`@Entity`, `@Column`, `@PrimaryKey`, `@Varchar`, `@Join`).  
- **Connection Pooling**: Integração nativa com **HikariCP** para alta performance e estabilidade de conexões.  
- **Suporte a Relacionamentos**: Mapeamento e consultas relacionais automáticas via `@Join`.  
- **Prepared Statements Seguros**: Execução parametrizada de operações CRUD prevenindo injeções de SQL.  

---

## Requisitos e Permissões

- **Java**: Versão 8 ou superior.  
- **Maven**: 4.0.0 ou superior.  
- **Banco de Dados**: Servidor **MySQL** acessível.  
- **Permissões SQL Necessárias**:
  - `CREATE TABLE`, `ALTER TABLE` (para `SQLAnnotation.scanEntity`).
  - `SELECT`, `INSERT`, `UPDATE`, `DELETE` (para operações de repositório).
  - `DROP TABLE`, `TRUNCATE` (para `SQLAnnotation.drop` e `SQLAnnotation.truncate`).

---

## Instalação e Configuração

1. **Clone o repositório**:  
   ```sh
   git clone https://github.com/oJVzinn/SQLAnnotation.git
   ```
2. **Navegue até o diretório**:  
   ```sh
   cd SQLAnnotation
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
import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.model.MySQLModel;
import com.github.ojvzinn.sqlannotation.model.SQLConfigModel;

public class DatabaseSetup {

    public static void init() {
        MySQLModel mySQL = new MySQLModel("localhost", 3306, "meubanco", "root", "senha");
        SQLConfigModel config = new SQLConfigModel(mySQL);
        config.setLog(true);

        SQLAnnotation.init(config);
        SQLAnnotation.scanEntity(User.class);
    }
}
```

---

### 📌 Entidades e Anotações

```java
import com.github.ojvzinn.sqlannotation.annotations.*;
import lombok.*;

@Entity(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    @Column
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @Column(notNull = true)
    @NonNull
    private String name;

    @Column(notNull = true)
    @NonNull
    private Integer age;

    @Column(notNull = true, unique = true)
    @NonNull
    private String email;

    @Varchar(length = 1)
    @Column(notNull = true)
    @NonNull
    private String gender;

    @Column
    @Join(column = "id")
    private Role role;
}
```

---

### 📌 Repositórios Dinâmicos

```java
import com.github.ojvzinn.sqlannotation.interfaces.Repository;
import com.github.ojvzinn.sqlannotation.model.LimitModel;
import java.util.List;

public interface UserRepository extends Repository<User> {

    User findByName(String name);

    User findByEmail(String email);

    List<User> findAllByConditionalsAgeAndName(Integer age, String name);

    List<User> findAll(LimitModel limit);

    void deleteAllByConditionalsAgeAndEmail(Integer age, String email);

    void deleteByAge(Integer age);
}
```

```java
UserRepository userRepository = SQLAnnotation.loadRepository(UserRepository.class);

// Salvar / Inserir ou Atualizar
userRepository.save(new User("Carlos", 25, "carlos@gmail.com", "M"));

// Buscar por chave primária
User user = userRepository.findByKey(1L);

// Buscar por método derivado
User userByName = userRepository.findByName("Carlos");
```

---

### 📌 Relacionamentos com @Join

```java
User user = userRepository.findByKey(1L);
if (user != null && user.getRole() != null) {
    System.out.println("Cargo: " + user.getRole().getName());
}
```

---

## Ciclo de Vida e Operações Administrativas

```java
// Limpar todos os registros de uma tabela
SQLAnnotation.truncate(User.class);

// Dropar tabela do banco
SQLAnnotation.drop(User.class);

// Encerrar conexões e pool HikariCP
SQLAnnotation.destroy();
```

---

## Compilando o Projeto

Caso queira modificar ou compilar manualmente:  

1. Clone o repositório:
   ```sh
   git clone https://github.com/oJVzinn/SQLAnnotation.git
   ```
2. Entre no diretório:
   ```sh
   cd SQLAnnotation
   ```
3. Compile com Maven:
   ```sh
   mvn clean package
   ```
4. O `.jar` final estará em `target/`.  

---

Desenvolvido com ❤️ por [oJVzinn](https://github.com/oJVzinn)
