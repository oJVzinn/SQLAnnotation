# SQLAnnotation - Lightweight Java SQL ORM SDK

![Java](https://img.shields.io/badge/Java-8%2B-blue?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-red?style=for-the-badge&logo=apache-maven)
![HikariCP](https://img.shields.io/badge/HikariCP-4.0.3-purple?style=for-the-badge&logo=databricks)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

## 📌 Sumário

1. [Sobre o Projeto](#sobre-o-projeto)
2. [Arquitetura e Funcionamento](#arquitetura-e-funcionamento)
3. [Requisitos do Sistema](#requisitos-do-sistema)
4. [Instalação e Configuração](#instalação-e-configuração)
5. [Mapeamento de Entidades e Anotações](#mapeamento-de-entidades-e-anotações)
6. [Repositórios Dinâmicos e Consultas](#repositórios-dinâmicos-e-consultas)
7. [Relacionamentos entre Tabelas (@Join)](#relacionamentos-entre-tabelas-join)
8. [Gerenciamento de Ciclo de Vida](#gerenciamento-de-ciclo-de-vida)
9. [Plano Diretor de Melhorias, Refatorações e Correções](#plano-diretor-de-melhorias-refatorações-e-correções)
10. [Catálogo de Issues do GitHub (Rastreabilidade)](#catálogo-de-issues-do-github-rastreabilidade)
11. [Diretrizes de Contribuição e Git Flow](#diretrizes-de-contribuição-e-git-flow)

---

## 1. Sobre o Projeto

O **SQLAnnotation** é um SDK ORM (Object-Relational Mapping) de alto desempenho desenvolvido para Java 8+, projetado para simplificar a comunicação e persistência com bancos de dados relacionais.

Ele abstrai a criação de tabelas, execução de queries DDL/DML, resolução de relacionamentos e execução de consultas dinâmicas através de `java.lang.reflect.Proxy`, minimizando boilerplate de código JDBC enquanto mantém pool de conexões otimizado com **HikariCP**.

---

## 2. Arquitetura e Funcionamento

O sistema opera sobre 4 pilares principais:

1. **Camada de Modelos e Anotações**: Define tabelas e colunas através de `@Entity`, `@Column`, `@PrimaryKey`, `@Varchar` e `@Join`.
2. **Dynamic Repository Proxies**: A interface `Repository<T>` é interceptada em tempo de execução pelo `RepositoryProcessor`, mapeando métodos para operações de persistência e busca sem necessidade de implementação manual.
3. **Módulos Especializados de Execução**:
   - `CreateModule`: Geração de DDL (`CREATE TABLE`, validação de colunas).
   - `InsertModule`: Inserção de registros com atribuição automática de chaves geradas (`AUTO_INCREMENT`).
   - `SelectModule`: Construção e execução de `SELECT` com suporte a `JOIN`, `ORDER BY` e `LIMIT`.
   - `UpdateModule`: Atualização de registros existentes vinculados à chave primária.
   - `DeleteModule`: Exclusão por chave primária, condições, truncate e drop de tabelas.
4. **Pool de Conexões HikariCP**: Gerenciamento de conexões reutilizáveis, transações e validação de conectividade.

---

## 3. Requisitos do Sistema

- **Linguagem**: Java JDK 8 ou superior.
- **Gerenciador de Dependências**: Apache Maven 3.6+.
- **Bancos de Dados Compatíveis**:
  - **MySQL**: 5.7+ / 8.0+ / MariaDB.
  - **SQLite**: 3.x (suporte completo planejado na Issue #6).
  - **PostgreSQL**: 12+ (suporte planejado na Issue #7).
- **Permissões de Banco de Dados**:
  - `CREATE`, `ALTER`, `DROP` (para auto-DDL via `scanEntity`, `drop`, `truncate`).
  - `SELECT`, `INSERT`, `UPDATE`, `DELETE` (para operações de repositório).

---

## 4. Instalação e Configuração

### 4.1. Inicialização Programática

```java
import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.model.MySQLModel;
import com.github.ojvzinn.sqlannotation.model.SQLConfigModel;

public class DatabaseManager {

    public static void initialize() {
        MySQLModel mySQL = new MySQLModel("localhost", 3306, "app_database", "root", "secret");
        SQLConfigModel config = new SQLConfigModel(mySQL);
        config.setLog(true); // Habilita log de queries executadas

        SQLAnnotation.init(config);
    }
}
```

---

## 5. Mapeamento de Entidades e Anotações

### 5.1. Anotações Disponíveis

| Anotação | Alvo | Parâmetros | Descrição |
|---|---|---|---|
| `@Entity` | Classe | `name` (String) | Nome da tabela no banco de dados. |
| `@Column` | Campo | `notNull` (boolean), `unique` (boolean) | Define uma coluna mapeada no banco. |
| `@PrimaryKey` | Campo | `autoIncrement` (boolean) | Identifica a chave primária da entidade. |
| `@Varchar` | Campo | `length` (int, padrão 255) | Define o tamanho do campo VARCHAR. |
| `@Join` | Campo | `column` (String) | Estabelece relação com chave estrangeira de outra entidade. |

### 5.2. Exemplo de Entidade

```java
package com.example.models;

import com.github.ojvzinn.sqlannotation.annotations.*;
import lombok.*;

@Entity(name = "tb_users")
@Getter
@Setter
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
    @NonNull
    private Role role;
}
```

### 5.3. Escaneamento e Criação de Tabelas

```java
SQLAnnotation.scanEntity(User.class);
SQLAnnotation.scanEntity(Role.class);
```

---

## 6. Repositórios Dinâmicos e Consultas

### 6.1. Definindo uma Interface de Repositório

```java
package com.example.repositories;

import com.example.models.User;
import com.github.ojvzinn.sqlannotation.interfaces.Repository;
import com.github.ojvzinn.sqlannotation.model.LimitModel;
import org.json.JSONArray;

public interface UserRepository extends Repository<User> {

    User findByName(String name);

    User findByEmail(String email);

    JSONArray findAllByConditionalsAgeAndGender(Integer age, String gender);

    JSONArray findAll(LimitModel limit);

    void deleteByEmail(String email);

    void deleteAllByConditionalsAgeAndGender(Integer age, String gender);
}
```

### 6.2. Carregando e Utilizando o Repositório

```java
UserRepository userRepository = SQLAnnotation.loadRepository(UserRepository.class);

// Salvar / Atualizar
User user = new User("Alice", 28, "alice@example.com", "F", roleAdmin);
userRepository.save(user);

// Buscar por Chave Primária
User found = userRepository.findByKey(user.getId());

// Buscar por campo dinâmico
User byEmail = userRepository.findByEmail("alice@example.com");

// Listar todos com limite
JSONArray users = userRepository.findAll(new LimitModel(10));

// Deletar
userRepository.deleteByEmail("alice@example.com");
```

---

## 7. Relacionamentos entre Tabelas (@Join)

Quando uma entidade possui outra entidade anotada com `@Entity` em um campo com `@Join(column = "referenced_column")`, o `SelectModule` gera automaticamente queries SQL com `JOIN` e popula os objetos aninhados:

```java
User user = userRepository.findByKey(1L);
System.out.println("Cargo: " + user.getRole().getName());
```

---

## 8. Gerenciamento de Ciclo de Vida

```java
// Limpar todos os registros de uma tabela (TRUNCATE)
SQLAnnotation.truncate(User.class);

// Excluir tabela do banco (DROP)
SQLAnnotation.drop(User.class);

// Finalizar pools de conexão
SQLAnnotation.destroy();
```

---

## 9. Plano Diretor de Melhorias, Refatorações e Correções

O planejamento aprofundado de evolução técnica do **SQLAnnotation** abrange:

### 9.1. O que Corrigir (Bugs & Segurança)
- **SQL Injection no `UpdateModule`**: Substituir concatenação de strings por placeholders `?` indexados no `PreparedStatement`.
- **Sintaxe DDL em `MySQLModel`**: Corrigir duplicação de `UNIQUE` na geração de colunas no `ALTER TABLE`.
- **Log incondicional e prints de depuração**: Fazer o logger obedecer a flag `isLog()` e remover `System.out.println` residual em `SelectModule`.

### 9.2. O que Refatorar (Clean Code & Performance)
- **Substituir `org.json` por Generics**: Retornar `List<T>` fortemente tipado em vez de `JSONArray` no `Repository<T>` e `SelectModule`.
- **Cache de Metadados de Reflexão**: Armazenar em cache (`ConcurrentHashMap`) classes, campos e anotações inspecionadas durante o `scanEntity`.
- **Padronização de Nomes**: Corrigir typos em métodos (`appendAppendOrder` -> `appendOrder`) e pacotes (`produt` -> `product`).
- **Exceções de Domínio**: Substituir `RuntimeException` genéricas por `SQLAnnotationException`, `EntityMappingException` e `QueryExecutionException`.

### 9.3. O que Adicionar (Novas Features)
- **Suporte a Tipos Primitivos e Ricos**: Mapeamento para tipos primitivos (`int`, `long`, `boolean`), `UUID`, `BigDecimal`, `LocalDateTime`, `LocalDate`, `Instant`, `byte[]` (BLOB).
- **Dialeto SQLite Completo**: Conexão, ciclo de vida e DDLs específicos para SQLite.
- **Dialeto PostgreSQL**: Driver, DDLs (`SERIAL`, escape de aspas duplas) e suporte a PostgreSQL.
- **Operadores de Busca Avançados & Paginação**: Suporte a `>`, `<`, `>=`, `<=`, `LIKE`, `IN`, `BETWEEN`, `IS NULL` e paginação com `Page<T>` / `PageRequest(page, size)`.
- **Gerenciamento de Transações & Batching**: Transações atômicas com `runInTransaction(callback)` e operações em lote (`saveAll`, `deleteAll`).

### 9.4. O que Retirar (Débito Técnico)
- Stubs sem funcionalidade que causem comportamento inesperado.
- Dependências acopladas desnecessárias no core (`org.json`).
- Mensagens de exceção personalizadas com nomes de autores no código-fonte.

---

## 10. Catálogo de Issues do GitHub (Rastreabilidade)

Todas as issues prontas para abertura no GitHub estão documentadas detalhadamente em arquivos markdown na pasta [`issues/`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues):

| ID | Arquivo de Especificação da Issue | Padrão do Commit |
|---|---|---|
| #1 | [`01-fix-sql-injection-in-update-module.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/01-fix-sql-injection-in-update-module.md) | `fix(update - Fixes #1): eliminate SQL injection and use parameter binding in UpdateModule` |
| #2 | [`02-fix-mysql-check-column-duplicate-unique-syntax.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/02-fix-mysql-check-column-duplicate-unique-syntax.md) | `fix(mysql - Fixes #2): fix duplicated unique keyword in makeSQLCheckColumn` |
| #3 | [`03-fix-logger-respect-config-flag-and-cleanup-sout.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/03-fix-logger-respect-config-flag-and-cleanup-sout.md) | `fix(logger - Fixes #3): respect log configuration flag and remove debug stdout` |
| #4 | [`04-refactor-replace-json-with-generic-types-and-collections.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/04-refactor-replace-json-with-generic-types-and-collections.md) | `refactor(core - Fixes #4): replace org.json dependency with type-safe generic collections` |
| #5 | [`05-feat-support-primitive-types-and-rich-data-types.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/05-feat-support-primitive-types-and-rich-data-types.md) | `feat(types - Fixes #5): support primitive types, boolean, UUID, temporal types in ClassType` |
| #6 | [`06-feat-complete-sqlite-database-support.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/06-feat-complete-sqlite-database-support.md) | `feat(sqlite - Fixes #6): implement SQLite dialect, connection management and DDLs` |
| #7 | [`07-feat-postgresql-database-dialect-support.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/07-feat-postgresql-database-dialect-support.md) | `feat(postgres - Fixes #7): add PostgreSQL dialect, connection provider and DDL generator` |
| #8 | [`08-refactor-fix-typos-and-clean-code-nomenclatures.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/08-refactor-fix-typos-and-clean-code-nomenclatures.md) | `refactor(cleanup - Fixes #8): fix method typos, package names and clean exception messages` |
| #9 | [`09-feat-advanced-query-operators-and-pagination.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/09-feat-advanced-query-operators-and-pagination.md) | `feat(query - Fixes #9): add rich query operators, comparison methods and pagination` |
| #10 | [`10-feat-transaction-management-and-batch-operations.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/10-feat-transaction-management-and-batch-operations.md) | `feat(tx - Fixes #10): implement transaction management and batch execution` |
| #11 | [`11-refactor-custom-domain-exceptions.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/11-refactor-custom-domain-exceptions.md) | `refactor(exceptions - Fixes #11): introduce structured domain-specific exception hierarchy` |
| #12 | [`12-feat-caching-and-reflection-metadata-cache.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/issues/12-feat-caching-and-reflection-metadata-cache.md) | `perf(reflection - Fixes #12): implement metadata reflection cache and optimize hydration` |

---

## 11. Diretrizes de Contribuição e Git Flow

1. **Branch Obrigatória**: Todo o fluxo de desenvolvimento deve ocorrer estritamente na branch `dev-1.0.0`.
2. **Padrão de Commits**:
   - Formato: `tipo(escopo - Fixes/Refs #Issue): descrição detalhada em Inglês`
   - Exemplos:
     - `fix(update - Fixes #1): eliminate SQL injection and use parameter binding in UpdateModule`
     - `feat(types - Fixes #5): support primitive types and temporal types in ClassType`
3. **Clean Code & Padrões Sênior**:
   - Zero comentários no código-fonte (código autoexplicativo por boas nomenclaturas).
   - Sem handles privados desnecessários ou classes com acoplamento indevido.
   - Atualização contínua do [`README.md`](file:///c:/Users/newre/Documents/GitHub/SQLAnnotation/README.md) a cada nova funcionalidade implementada.
