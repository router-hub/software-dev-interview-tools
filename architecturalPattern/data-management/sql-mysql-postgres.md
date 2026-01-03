# SQL, MySQL & PostgreSQL Interview Notes - SDE 2

## Table of Contents
- [SQL Fundamentals](#sql-fundamentals)
- [MySQL Specifics](#mysql-specifics)
- [PostgreSQL Specifics](#postgresql-specifics)
- [MySQL vs PostgreSQL](#mysql-vs-postgresql)
- [Java Integration](#java-integration)
- [Spring Boot & JPA](#spring-boot--jpa)
- [Performance Optimization](#performance-optimization)
- [Interview Questions](#interview-questions)

---

## SQL Fundamentals

### Database Basics

**SQL (Structured Query Language)** is a standard language for managing relational databases.

**Command Types:**
- **DDL (Data Definition Language):** Defines structure (CREATE, ALTER, DROP, TRUNCATE)
- **DML (Data Manipulation Language):** Manipulates data (SELECT, INSERT, UPDATE, DELETE)
- **DCL (Data Control Language):** Controls access (GRANT, REVOKE)
- **TCL (Transaction Control Language):** Manages transactions (COMMIT, ROLLBACK, SAVEPOINT)

### Logical Query Processing Order
Unlike the lexical order (SELECT... FROM...), the database engine processes queries in this order:
1. **FROM / JOIN**: Pick tables and join them.
2. **WHERE**: Filter rows.
3. **GROUP BY**: Group rows.
4. **HAVING**: Filter groups.
5. **SELECT**: Select columns (and window functions).
6. **DISTINCT**: Remove duplicates.
7. **ORDER BY**: Sort results.
8. **LIMIT / OFFSET**: Paging.

### Normalization

Database design technique to reduce redundancy and improve integrity.

**Why Normalize?**
- **Insertion Anomaly:** Unable to add data because unrelated data is missing (e.g., can't add a student without a course).
- **Update Anomaly:** Data inconsistency when updating one copy of duplicated data but not others.
- **Deletion Anomaly:** Unintended loss of valid data when deleting other data.

**Normal Forms:**
- **1NF:** Atomic values (no list/set in a column), no repeating groups.
- **2NF:** 1NF + No partial dependencies (non-key attributes must depend on the *entire* primary key).
- **3NF:** 2NF + No transitive dependencies (non-key attributes must depend *only* on the primary key, not other non-key attributes).
- **BCNF:** A stricter version of 3NF where every determinant is a candidate key.

### Constraints

-- Primary Key
CREATE TABLE employees (
id SERIAL PRIMARY KEY,
email VARCHAR(255) UNIQUE NOT NULL,
dept_id INT,
salary DECIMAL CHECK (salary > 0),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (dept_id) REFERENCES departments(id)
);



**Key Constraints:**
- **PRIMARY KEY:** Uniqueness + NOT NULL. Identifies a row.
- **FOREIGN KEY:** Ensures referential integrity between tables.
- **UNIQUE:** Ensures all values in a column are distinct.
- **NOT NULL:** Prevents NULL values.
- **CHECK:** Validates data against a boolean expression.
- **DEFAULT:** Sets a default value if none is provided.

### Joins
```sql
-- INNER JOIN - Only matching rows
SELECT e.name, d.department_name
FROM employees e
INNER JOIN departments d ON e.dept_id = d.id;

-- LEFT JOIN - All from left + matching from right
SELECT e.name, d.department_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.id;

-- RIGHT JOIN - All from right + matching from left
SELECT e.name, d.department_name
FROM employees e
RIGHT JOIN departments d ON e.dept_id = d.id;

-- FULL OUTER JOIN - All rows from both tables
SELECT e.name, d.department_name
FROM employees e
FULL OUTER JOIN departments d ON e.dept_id = d.id;

-- SELF JOIN - Join table to itself
SELECT e1.name AS employee, e2.name AS manager
FROM employees e1
LEFT JOIN employees e2 ON e1.manager_id = e2.id;

-- CROSS JOIN - Cartesian product
SELECT p.product_name, c.color
FROM products p
CROSS JOIN colors c;
```


### Join Algorithms (Internals)
Understanding how a database physically executes a join is critical for debugging slow queries (using EXPLAIN) and optimizing performance.

#### 1. Nested Loop Join (NLJ) - "The Loop inside a Loop"
This is the simplest algorithm. It works exactly like writing two `for` loops in Java.

**The Logic:**
- **Outer Loop:** Iterates through rows of the first table (Driving Table).
- **Inner Loop:** For each outer row, it scans the second table (Inner Table) to find matches.

**Types & Performance:**
- **A. Naive Nested Loop (The Disaster)**
  - **Occurs:** When there is **NO Index** on the join column.
  - **Mechanism:** Full Table Scan on the Inner table for every Outer row.
  - **Complexity:** $O(N \times M)$
  - **The Trap:** Joining two tables of 1 Million rows each = 1 Trillion Operations.
  - **Analogy:** Reading a jumbled phonebook from start to finish for every single person you want to call.

- **B. Index Nested Loop (The Standard)**
  - **Occurs:** When the Inner table **HAS an Index** on the join column.
  - **Mechanism:** Uses a B-Tree lookup for the Inner table.
  - **Complexity:** $O(N \times \log M)$
  - **Best For:** OLTP queries (e.g., "Get User 101 and their Orders"). Small "Outer" datasets.

#### 2. Hash Join - "The In-Memory Map"
The standard algorithm for joining two large, unsorted datasets (e.g., Reporting/Analytics).

**The Logic:**
1.  **Build Phase:** The DB identifies the Smaller table. It reads it entirely into RAM and builds a generic **Hash Map** (Key = Join Column, Value = Row Data).
2.  **Probe Phase:** The DB scans the Larger table one by one. It hashes the join column and checks the Hash Map for matches.

**The "Senior Engineer" Insight:**
- **Complexity:** $O(N + M)$ (Linear time). Much faster than NLJ for big data.
- **The Risk ("Spilling to Disk"):**
    - This algorithm requires memory (`work_mem` in Postgres).
    - If the Hash Map is too big for RAM, the DB writes temporary files to the Hard Disk.
    - **Result:** Performance drops by 100x.
    - **Fix:** Increase memory allocation or filter data before joining.

#### 3. Sort-Merge Join - "The Zipper"
A fallback or specialist algorithm.

**The Logic:**
1.  **Sort:** Both tables must be sorted by the Join Key (if not already).
2.  **Merge:** The DB uses two pointers (one for each table) and zips them together efficiently.

**When is it used?**
- **Already Sorted:** If the data is already sorted (e.g., Joining on a Primary Key or Timestamp), this skips the expensive Hash Map build.
- **Too Big for Hash:** If a Hash Join would spill to disk, Sort-Merge is often preferred because it handles disk I/O more sequentially.
- **Complexity:** $O(N \log N + M \log M)$ (The cost is dominated by the sorting).

#### 4. The Critical "Trap": Indexes & Foreign Keys
This is the #1 cause of slow joins in production.

**The Misconception:**
> "I have a Primary Key on Users and a Foreign Key on Orders. Therefore, my join is indexed." **FALSE.**

**The Reality:**

| Column Type     | Auto-Indexed? | Status                      |
| :-------------- | :------------ | :-------------------------- |
| **Primary Key** | ✅ YES         | Fast Lookups.               |
| **Foreign Key** | ❌ NO          | Full Table Scan (**Slow**). |

**The Scenario:**
- **Query:** `SELECT * FROM Users JOIN Orders ON Users.id = Orders.user_id`
- **Database Strategy:** Pick a User -> Go find matches in Orders.
- **The Lookup:** The DB looks at `Orders.user_id`.
- **The Result:** Since `Orders.user_id` has no index, the DB must scan **every single order** to find matches for that one user.

**The Golden Rule:**
Always manually create an index on your Foreign Key columns.

```sql
-- Fix the trap manually
CREATE INDEX idx_orders_user_id ON Orders(user_id);
```

**Summary Decision Matrix:**

| IF the scenario is...                  | The DB Optimizer picks...                                    |
| :------------------------------------- | :----------------------------------------------------------- |
| **Small Data** (or filtering to 1 row) | Nested Loop (Index)                                          |
| **Large Data** (Reporting/All Rows)    | Hash Join                                                    |
| **Sorted Data** (or Massive Data)      | Sort-Merge Join                                              |
| **Missing Index on Large Data**        | Nested Loop (Naive) $\rightarrow$ **Server Crash / Timeout** |

* Do we configure it? No. The Optimizer automates this.
* Can we force it? Yes, using "Hints" (Oracle) or SET commands (Postgres), but use this only for debugging.
* What if it picks the wrong one? Your statistics are likely old. Run an ANALYZE command to update the row counts so the Optimizer knows the true size of the data.

### Indexes

Improve query performance by enabling faster data retrieval.

**Deep Dive: How B+Tree Indexes Work (The Industry Standard)**
Most relational databases use **B+Trees** (Balanced Trees) for on-disk storage.
- **Structure:**
  - **Root Node:** Entry point.
  - **Internal Nodes:** Routing keys to guide search.
  - **Leaf Nodes:** Contain actual data (or pointers to data) and are **linked** (doubly linked list).
- **Search:** `O(log N)`. Traverse from root to leaf.
- **Range Scans:** key benefit of B+Tree. Once the starting leaf is found, the engine simply scans the linked leaves (Sequential I/O), making it extremely efficient for range queries (`>`, `<`).
- **Page Splits:** When a node is full during insertion, it splits, promoting a key to the parent. This keeps the tree balanced but causes I/O overhead on widely distributed writes (e.g., random UUID inserts).

**Other Index Structures:**
- **Hash Index:** (Memory engines/Postgres) O(1) lookup. No range support.
- **LSM Tree:** (NoSQL/RocksDB) Log-Structured Merge Tree. Optimized for high write throughput (append-only), slightly slower reads (compaction needed).

**Types:**
- **Clustered Index:** Defines the physical order of data in the table (usually Primary Key). Only one per table.
- **Non-clustered Index:** Stored separately from data. Contains pointers to the actual data rows.
- **Composite Index:** Index on multiple columns. Order matters (Leftmost Prefix Rule).
- **Unique Index:** Ensures all values in the index are distinct.
```sql
-- Create indexes
CREATE INDEX idx_employee_email ON employees(email);
CREATE INDEX idx_employee_dept ON employees(dept_id);
CREATE INDEX idx_composite ON employees(dept_id, salary);

-- Unique index
CREATE UNIQUE INDEX idx_unique_email ON employees(email);
```

**When to Use:
- Columns in WHERE clauses
- JOIN columns
- ORDER BY columns
- Avoid on frequently updated columns

### Aggregate Functions

```sql
-- Common aggregates
SELECT
COUNT(*) as total_employees,
COUNT(DISTINCT dept_id) as departments,
AVG(salary) as avg_salary,
SUM(salary) as total_salary,
MAX(salary) as max_salary,
MIN(salary) as min_salary
FROM employees;

-- GROUP BY with HAVING
SELECT dept_id, AVG(salary) as avg_salary
FROM employees
GROUP BY dept_id
HAVING AVG(salary) > 50000;
```

### Window Functions
```sql
-- ROW_NUMBER - Sequential numbering
SELECT
name,
salary,
ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) as rank
FROM employees;

-- RANK and DENSE_RANK
SELECT
name,
salary,
RANK() OVER (ORDER BY salary DESC) as rank,
DENSE_RANK() OVER (ORDER BY salary DESC) as dense_rank
FROM employees;

-- LEAD and LAG
SELECT
name,
salary,
LAG(salary) OVER (ORDER BY salary) as prev_salary,
LEAD(salary) OVER (ORDER BY salary) as next_salary
FROM employees;

-- Running totals
SELECT
date,
amount,
SUM(amount) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as running_total
FROM transactions;
```

### Subqueries
```sql
-- Scalar subquery
SELECT name, salary
FROM employees
WHERE salary > (SELECT AVG(salary) FROM employees);

-- Correlated subquery
SELECT e1.name, e1.salary
FROM employees e1
WHERE salary > (
SELECT AVG(salary)
FROM employees e2
WHERE e1.dept_id = e2.dept_id
);

-- EXISTS
SELECT name
FROM employees e
WHERE EXISTS (
SELECT 1 FROM departments d
WHERE d.id = e.dept_id AND d.location = 'NY'
);

-- IN clause
SELECT name
FROM employees
WHERE dept_id IN (SELECT id FROM departments WHERE budget > 100000);

```

### CTEs (Common Table Expressions)
```sql
-- Simple CTE
WITH high_earners AS (
SELECT * FROM employees WHERE salary > 100000
)
SELECT dept_id, COUNT(*) as count
FROM high_earners
GROUP BY dept_id;

-- Recursive CTE - Organization hierarchy
WITH RECURSIVE employee_hierarchy AS (
-- Base case
SELECT id, name, manager_id, 1 as level
FROM employees
WHERE manager_id IS NULL


UNION ALL

-- Recursive case
SELECT e.id, e.name, e.manager_id, eh.level + 1
FROM employees e
INNER JOIN employee_hierarchy eh ON e.manager_id = eh.id
)
SELECT * FROM employee_hierarchy;
```


### Transactions

-- ACID Properties: Atomicity, Consistency, Isolation, Durability

BEGIN TRANSACTION;
```sql
UPDATE accounts SET balance = balance - 100 WHERE account_id = 1;
UPDATE accounts SET balance = balance + 100 WHERE account_id = 2;
```


-- If all operations succeed
COMMIT;

-- If any operation fails
ROLLBACK;

-- Savepoints
```sql
BEGIN TRANSACTION;
UPDATE accounts SET balance = balance - 100 WHERE account_id = 1;
SAVEPOINT sp1;
UPDATE accounts SET balance = balance + 100 WHERE account_id = 2;
ROLLBACK TO SAVEPOINT sp1;
COMMIT;
```


**Locking Mechanisms (Deep Dive):**
To ensure ACID properties, databases use robust locking strategies.

*   **Lock Granularity:**
    *   **Row Lock:** Locks specific rows (High concurrency, high overhead).
    *   **Table Lock:** Locks entire table (Low concurrency, low overhead).
    *   **Page Lock:** Locks a data page ( Compromise).

*   **Lock Modes:**
    *   **Shared Lock (S):** For reading. Multiple transactions can hold S locks. Blocks X locks. (*"I am reading, please don't change it"*).
    *   **Exclusive Lock (X):** For writing. Only one transaction can hold X. Blocks all other S and X locks. (*"I am changing this, nobody touch it"*).
    *   **Intention Locks (IS, IX):** Optimization. Placed on the *Table* to indicate that a *Row* inside is locked. Prevents another transaction from locking the whole table without scanning every row.
    *   **Gap Locks (InnoDB):** Locks the "gap" between index records. Critical for preventing **Phantom Reads** in Repeatable Read isolation.

**Read Phenomena (Anomalies):**
- **Dirty Read:** Reading uncommitted data from another transaction.
- **Non-Repeatable Read:** Reading the same row twice gives different results (due to updates).
- **Phantom Read:** Range query returns different rows when repeated (due to inserts/deletes).

**Isolation Levels:**
- **READ UNCOMMITTED:** Lowest level. Allows Dirty Reads.
- **READ COMMITTED:** Default in many DBs (Postgres, SQL Server). Blocks Dirty Reads.
- **REPEATABLE READ:** Blocks Dirty, Non-Repeatable Reads. (MySQL default).
- **SERIALIZABLE:** Highest level. Blocks all anomalies. Emulates serial execution.

---

## MySQL Specifics

### Architecture

**MySQL** uses a thread-per-connection model.

### Deep Dive: InnoDB Architecture
InnoDB is the default storage engine, designed for high reliability and performance.

**1. In-Memory Structures:**
*   **Buffer Pool:** The most important memory area. Caches data pages and index pages to minimize disk I/O.
    *   *Tip:* Set to 70-80% of RAM on a dedicated DB server.
*   **Change Buffer:** special data structure that caches changes to *secondary index* pages when those pages are not in the Buffer Pool. Merged later to reduce random I/O.
*   **Adaptive Hash Index:** Automatically created hash indexes in memory for frequently accessed reads.

**2. On-Disk Structures:**
*   **System Tablespace (ibdata1):** Contains Data Dictionary, Doublewrite Buffer, Change Buffer, Undo Logs.
*   **File-Per-Table (.ibd):** Each table's data and indexes stored in separate files.
*   **Redo Log (ib_logfile):** Write-Ahead Log (WAL). Records changes physically *before* writing to data files. Ensures durability in case of crash.
*   **Undo Log:** Stores "before image" of data. Used for **Rollback** and **MVCC** (consistent reads).

**3. Clustered Index (How data is stored):**
*   **Primary Key is the Data:** The B+Tree leaf nodes contain the *entire* row content.
*   **Secondary Indexes:** Leaf nodes contain the *Primary Key* value, NOT the row pointer.
*   **Double Lookup:** Searching via secondary index requires two lookups:
    1.  Find PK from Secondary Index.
    2.  Find Row from Clustered Index using PK.

**Storage Engines Summary:**
- **InnoDB:** Default. ACID. Row-locking. Clustered Index.
- **MyISAM:** Legacy. Table-locking. No Transactions. Dead.

### MySQL Data Types
```sql
-- Numeric
TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT
DECIMAL(10,2), FLOAT, DOUBLE

-- String
CHAR(n), VARCHAR(n), , MEDIUMTEXT, LONGTEXT
ENUM('value1', 'value2')

-- Date/Time
DATE, TIME, DATETIME, TIMESTAMP, YEAR

-- Binary
BLOB, MEDIUMBLOB, LONGBLOB

-- JSON (MySQL 5.7+)
JSON
```


### MySQL Features
```
-- AUTO_INCREMENT
CREATE TABLE users (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100)
);

-- JSON operations (MySQL 5.7+)
CREATE TABLE products (
id INT PRIMARY KEY,
attributes JSON
);

INSERT INTO products VALUES (1, '{"color": "red", "size": "M"}');

SELECT
id,
JSON_EXTRACT(attributes, '$.color') as color,
attributes->'$.size' as size
FROM products;

-- Full- search
CREATE TABLE articles (
id INT PRIMARY KEY,
title VARCHAR(200),
content ,
FULLTEXT(title, content)
);

SELECT * FROM articles
WHERE MATCH(title, content) AGAINST('search term' IN NATURAL LANGUAGE MODE);
```


### MySQL Configuration
```properties
#application.properties for MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

#Connection pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```
---

## PostgreSQL Specifics

### Architecture

**PostgreSQL** uses a process-per-connection model.

### Deep Dive: MVCC (Multi-Version Concurrency Control)
Postgres handles concurrency by keeping multiple versions of a row (tuples) instead of overwriting them.

**1. Hidden Columns:**
Every row has hidden columns to track versions:
*   `xmin`: Transaction ID (XID) that *created* the row.
*   `xmax`: Transaction ID (XID) that *deleted* (or updated) the row.
*   `cmin`/`cmax`: Command identifiers within a transaction.

**2. Visibility Rules (How Snapshot Isolation works):**
When a transaction starts, it takes a snapshot (list of active XIDs).
*   A row is **visible** if:
    *   `xmin` is committed and is *before* the current transaction.
    *   `xmax` is NULL (alive) OR `xmax` is from a future/active transaction (not yet committed).

**3. The Cost of MVCC:**
*   **Bloat:** Updates create new tuples; old tuples (dead rows) remain on disk until cleaned suitable for rollback or other old snapshots.
*   **VACUUM:** The garbage collector process.
    *   Reclaims space from dead tuples.
    *   Updates statistics for the Query Optimizer.
    *   **Auto-vacuum:** Daemon that runs in background. Critical to tune correctly.
*   **Transaction ID Wraparound:** XIDs are 32-bit integers (~4 billion). Postgres must "Freeze" old XIDs to prevent wraparound data loss.

**4. TOAST (The Oversized-Attribute Storage Technique):**
*   Postgres pages are 8KB. Large fields (TEXT, JSONB) are compressed or split into chunks and stored in a separate TOAST table transparently.

**5. Write-Ahead Logging (WAL):**
*   Changes are written to WAL (append-only) before data files. Ensures durability and used for Replication (Streaming WAL).

**Key Features:**
- Advanced data types (arrays, hstore, JSONB, XML)
- Full ACID compliance
- Advanced indexing (GiST, GIN for JSON/Text, BRIN for large data)
- Table inheritance and Partitioning
- Extensibility (Custom types, functions, extensions like PostGIS)

### PostgreSQL Data Types
```sql
-- Numeric
SMALLINT, INTEGER, BIGINT
DECIMAL, NUMERIC, REAL, DOUBLE PRECISION
SERIAL, BIGSERIAL

-- String
CHAR(n), VARCHAR(n), 

-- Date/Time
DATE, TIME, TIMESTAMP, TIMESTAMPTZ, INTERVAL

-- Boolean
BOOLEAN

-- Arrays
INTEGER[], []

-- JSON
JSON, JSONB (binary, indexed)

-- UUID
UUID

-- Network
INET, CIDR, MACADDR

-- Geometric
POINT, LINE, CIRCLE, POLYGON
```


### PostgreSQL Advanced Features
```
-- Arrays
CREATE TABLE posts (
id SERIAL PRIMARY KEY,
tags []
);

INSERT INTO posts VALUES (1, ARRAY['java', 'spring', 'postgres']);

SELECT * FROM posts WHERE 'java' = ANY(tags);
SELECT * FROM posts WHERE tags @> ARRAY['java'];

-- JSONB (indexed JSON)
CREATE TABLE users (
id SERIAL PRIMARY KEY,
profile JSONB
);

CREATE INDEX idx_profile ON users USING GIN(profile);

INSERT INTO users VALUES (1, '{"name": "John", "age": 30, "skills": ["Java", "SQL"]}');

SELECT * FROM users WHERE profile->>'name' = 'John';
SELECT * FROM users WHERE profile @> '{"age": 30}';
SELECT * FROM users WHERE profile->'skills' ? 'Java';

-- Generate Series
SELECT generate_series(1, 10);
SELECT generate_series('2024-01-01'::date, '2024-12-31'::date, '1 month');

-- CTEs with materialization
WITH MATERIALIZED high_value_customers AS (
SELECT * FROM customers WHERE total_spent > 10000
)
SELECT * FROM high_value_customers;

-- UPSERT (INSERT ON CONFLICT)
INSERT INTO users (id, name, email)
VALUES (1, 'John', 'john@example.com')
ON CONFLICT (id)
DO UPDATE SET email = EXCLUDED.email;

-- Table partitioning
CREATE TABLE sales (
id SERIAL,
sale_date DATE,
amount DECIMAL
) PARTITION BY RANGE (sale_date);

CREATE TABLE sales_2024_q1 PARTITION OF sales
FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');
```


### PostgreSQL Configuration
```properties
#application.properties for PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

#JPA properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

```
---

## MySQL vs PostgreSQL

### Key Differences

<details>
<summary>📊 <strong>Show/Hide: Comparison Table (MySQL vs PostgreSQL)</strong></summary>

| Feature              | MySQL                           | PostgreSQL                          |
| -------------------- | ------------------------------- | ----------------------------------- |
| **Architecture**     | Thread-per-connection           | Process-per-connection              |
| **Concurrency**      | Lock-based (mostly)             | MVCC (Robust)                       |
| **ACID Compliance**  | Yes (InnoDB)                    | Yes (Strict)                        |
| **Replication**      | Master-Slave, Group Replication | Streaming, Logical (Strong support) |
| **Indexing**         | B-tree, Hash, Full-text         | B-tree, Hash, GiST, GIN, BRIN       |
| **JSON Support**     | JSON type                       | JSONB (Binary, efficient indexing)  |
| **Arrays**           | No                              | Native Support                      |
| **Window Functions** | Yes (8.0+)                      | Yes (Mature support)                |
| **Complex Queries**  | Good                            | Excellent (Optimizer is stronger)   |

</details>

### When to Choose

**Choose MySQL:**
- Simple CRUD operations
- Read-heavy workloads
- Need maximum speed for simple queries
- Ubiquity and ease of hosting is a priority

**Choose PostgreSQL:**
- Complex queries and analytics
- Data integrity is critical
- Need advanced data types
- Write-heavy workloads
- Require extensibility (e.g. PostGIS)

---

## Java Integration

### JDBC Basics
```java

// Traditional JDBC
public class JdbcExample {
    public List<User> findAllUsers() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/mydb";
        List<User> users = new ArrayList<>();


        try (Connection conn = DriverManager.getConnection(url, "user", "pass");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
        }
        return users;
    }

    // PreparedStatement - prevents SQL injection
    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    // Batch operations
    public void batchInsert(List<User> users) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (User user : users) {
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getEmail());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
        }
    }

    // Transaction management
    public void transferFunds(Long fromId, Long toId, BigDecimal amount)
            throws SQLException {
        Connection conn = getConnection();

        try {
            conn.setAutoCommit(false);

            // Debit from account
            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(debitSql)) {
                pstmt.setBigDecimal(1, amount);
                pstmt.setLong(2, fromId);
                pstmt.executeUpdate();
            }

            // Credit to account
            String creditSql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(creditSql)) {
                pstmt.setBigDecimal(1, amount);
                pstmt.setLong(2, toId);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```


### Connection Pooling
```java
// HikariCP Configuration
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("postgres");
        config.setPassword("password");

        // Pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // Performance
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }
}
```


---

## Spring Boot & JPA

### Entity Mapping
```java
@Entity
@Table(name = "users", indexes = {
@Index(name = "idx_email", columnList = "email"),
@Index(name = "idx_username", columnList = "username")
})
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // One-to-Many
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // Many-to-One
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Many-to-Many
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Embedded
    @Embedded
    private Address address;

    // JSON (PostgreSQL)
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    // Helper methods for bidirectional relationships
    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setUser(null);
    }
}

@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
```


### Repository Pattern
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {


// Query methods
Optional<User> findByEmail(String email);

List<User> findByStatus(UserStatus status);

List<User> findByCreatedAtBetween(Date start, Date end);

// @Query with JPQL
@Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
List<User> findByEmailDomain(@Param("domain") String domain);

// Native query
@Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)
List<User> findRecentUsers(@Param("date") Date date);

// Modifying query
@Modifying
@Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
int updateStatus(@Param("id") Long id, @Param("status") UserStatus status);

// Projection
@Query("SELECT new com.example.dto.UserDTO(u.id, u.username, u.email) FROM User u")
List<UserDTO> findAllDTOs();

// Pagination
Page<User> findByStatus(UserStatus status, Pageable pageable);

// Sorting
List<User> findByStatus(UserStatus status, Sort sort);
}

// Custom repository implementation
public interface CustomUserRepository {
List<User> findByComplexCriteria(UserSearchCriteria criteria);
}

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findByComplexCriteria(UserSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getUsername() != null) {
            predicates.add(cb.like(user.get("username"), "%" + criteria.getUsername() + "%"));
        }

        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(user.get("status"), criteria.getStatus()));
        }

        query.where(predicates.toArray(new Predicate));

        return entityManager.createQuery(query).getResultList();
    }
}
```



### Transaction Management
```java
@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Default transaction - READ_WRITE, REQUIRED propagation
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Read-only optimization
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    // Custom timeout
    @Transactional(timeout = 5)
    public void longRunningOperation() {
// Operation that should complete within 5 seconds
    }

    // Rollback for checked exceptions
    @Transactional(rollbackFor = Exception.class)
    public void operationWithCheckedException() throws Exception {
// Will rollback even for checked exceptions
    }

    // No rollback for specific exceptions
    @Transactional(noRollbackFor = ValidationException.class)
    public void operationWithValidation() {
// Won't rollback for ValidationException
    }

    // Propagation types
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void independentTransaction() {
// Runs in new transaction, parent transaction suspended
    }

    @Transactional(propagation = Propagation.NESTED)
    public void nestedTransaction() {
// Creates savepoint, can rollback to this point
    }

    // Isolation levels
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void serializedOperation() {
// Highest isolation, prevents phantom reads
    }

    // Complex transaction
    @Transactional
    public void transferFunds(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountRepository.findById(fromId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Account to = accountRepository.findById(toId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        // If exception occurs here, entire transaction rolls back
    }
}
```

### Best Practices
```java
// 1. Use DTOs for API responses
@Service
public class UserService {


    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return UserMapper.toDTO(user);
    }
}

// 2. Fetch optimization - avoid N+1 queries
@Repository
public interface UserRepository extends JpaRepository<User, Long> {


@Query("SELECT u FROM User u LEFT JOIN FETCH u.posts WHERE u.id = :id")
Optional<User> findByIdWithPosts(@Param("id") Long id);

@EntityGraph(attributePaths = {"posts", "roles"})
Optional<User> findWithPostsAndRolesById(Long id);
}

// 3. Batch operations
@Service
public class UserService {


    @Transactional
    public void batchInsert(List<User> users) {
        int batchSize = 50;
        for (int i = 0; i < users.size(); i++) {
            userRepository.save(users.get(i));
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}

// 4. Pagination for large datasets
@Service
public class UserService {


    @Transactional(readOnly = true)
    public Page<UserDTO> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable)
                .map(UserMapper::toDTO);
    }
}

// 5. Auditing
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AuditableEntity {


@CreatedBy
@Column(name = "created_by", updatable = false)
private String createdBy;

@CreatedDate
@Column(name = "created_at", updatable = false)
private Instant createdAt;

@LastModifiedBy
@Column(name = "updated_by")
private String updatedBy;

@LastModifiedDate
@Column(name = "updated_at")
private Instant updatedAt;
}

@Configuration
@EnableJpaAuditing
public class JpaConfig {


    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return Optional.ofNullable(auth)
                    .map(Authentication::getName);
        };
    }
}
```



---

## Performance Optimization

### Indexing Strategies

-- B-tree index (default) - good for equality and range queries
CREATE INDEX idx_created_at ON users(created_at);

-- Composite index - leftmost prefix rule
CREATE INDEX idx_status_created ON users(status, created_at);
-- Can be used for:
-- WHERE status = 'ACTIVE'
-- WHERE status = 'ACTIVE' AND created_at > '2024-01-01'
-- Cannot be efficiently used for:
-- WHERE created_at > '2024-01-01' (without status)

-- Partial index (PostgreSQL) - index only subset of data
CREATE INDEX idx_active_users ON users(email) WHERE status = 'ACTIVE';

-- Expression index
CREATE INDEX idx_lower_email ON users(LOWER(email));

-- GIN index for JSONB (PostgreSQL)
CREATE INDEX idx_metadata ON users USING GIN(metadata);

-- Full- search index
CREATE INDEX idx_fulltext ON articles USING GIN(to_tsvector('english', content));

-- Analyze query plans
EXPLAIN ANALYZE SELECT * FROM users WHERE email = 'test@example.com';



### Query Optimization

-- Use EXISTS instead of IN for large subqueries
-- Bad
SELECT * FROM users
WHERE id IN (SELECT user_id FROM orders WHERE amount > 1000);

-- Good
SELECT * FROM users u
WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id AND o.amount > 1000);

-- Use LIMIT for large result sets
SELECT * FROM users ORDER BY created_at DESC LIMIT 100;

-- Avoid SELECT * - specify columns
SELECT id, name, email FROM users;

-- Use covering indexes
CREATE INDEX idx_covering ON users(status, email, created_at);
SELECT email, created_at FROM users WHERE status = 'ACTIVE'; -- Index-only scan

-- Partition large tables (PostgreSQL)
CREATE TABLE orders (
id SERIAL,
order_date DATE,
amount DECIMAL
) PARTITION BY RANGE (order_date);

CREATE TABLE orders_2024 PARTITION OF orders
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');



### Connection Pool Tuning

HikariCP settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=60000

Statement caching
spring.datasource.hikari.data-source-properties.cachePrepStmts=true
spring.datasource.hikari.data-source-properties.prepStmtCacheSize=250
spring.datasource.hikari.data-source-properties.prepStmtCacheSqlLimit=2048



### JPA Performance
```java
// 1. Lazy loading for associations
@ManyToOne(fetch = FetchType.LAZY)
private Department department;

// 2. Batch fetching
@org.hibernate.annotations.BatchSize(size = 10)
@OneToMany(mappedBy = "user")
private List<Post> posts;

// 3. Query hints
@QueryHints({
@QueryHint(name = "org.hibernate.fetchSize", value = "50"),
@QueryHint(name = "org.hibernate.cacheable", value = "true")
})
List<User> findByStatus(UserStatus status);

// 4. Second-level cache
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {
// ...
}
```


---

## Interview Questions

<details>
<summary>💬 <strong>Click to Expand: Interview & FAQ</strong></summary>

### SQL Query Questions

**Q1: Find second highest salary**  
```sql
SELECT MAX(salary)
FROM employees
WHERE salary < (SELECT MAX(salary) FROM employees);
```
Or using window functions:
```sql
SELECT DISTINCT salary
FROM (
  SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) as rank
  FROM employees
) ranked
WHERE rank = 2;
```

---

**Q2: Find duplicate emails**  
```sql
SELECT email, COUNT() as count
FROM users
GROUP BY email
HAVING COUNT() > 1;
```

---

**Q3: Find employees with no manager**  
```sql
SELECT e1.name
FROM employees e1
LEFT JOIN employees e2 ON e1.manager_id = e2.id
WHERE e2.id IS NULL;
```

---

**Q4: Running total**
```sql
SELECT
  date,
  amount,
  SUM(amount) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as running_total
FROM transactions;
```

---

**Q5: Delete duplicates keeping one**
```sql
DELETE FROM users
WHERE id NOT IN (
  SELECT MIN(id)
  FROM users
  GROUP BY email
);
```



### Conceptual Questions

**Q6: Explain ACID properties:**  
- **Atomicity:** All operations in transaction complete or none do  
- **Consistency:** Database moves from one valid state to another  
- **Isolation:** Concurrent transactions don't interfere  
- **Durability:** Committed changes persist even after system failure  


---

**Q7: What is N+1 query problem?**  
One query to fetch parent entities, then N queries for each parent's children.  
**Fix:** Solve with `JOIN FETCH` or `@EntityGraph`.  


---

**Q8: Difference between UNION and UNION ALL:**  
- `UNION` removes duplicates, slower  
- `UNION ALL` keeps duplicates, faster  


---

**Q9: What are database triggers?**  
Stored procedures that automatically execute on `INSERT`, `UPDATE`, or `DELETE` events.  


---

**Q10: Explain database normalization benefits:**  
- Reduces data redundancy  
- Improves data integrity  
- Easier maintenance  
- Better query performance (usually)  


---

**Q11: When to denormalize?**  
- Read-heavy workloads  
- Complex joins affecting performance  
- Data warehouse scenarios  
- Caching layer available  

### Spring Boot Questions

**Q12: How does @Transactional work?**  
Spring creates proxy around method, starts transaction before method execution, commits on success, rolls back on unchecked exceptions.  


---

**Q13: What is transaction propagation?**  
Defines how transactions relate to each other:
- REQUIRED: Use existing or create new
- REQUIRES_NEW: Always create new, suspend existing
- NESTED: Create savepoint
- SUPPORTS: Use existing if available
- NOT_SUPPORTED: Execute non-transactionally  


---

**Q14: How to prevent N+1 queries?**  
- Use `JOIN FETCH` in JPQL  
- Use `@EntityGraph`
- Enable batch fetching
- Use `@BatchSize`  


---

**Q15: What is connection pooling?**  
Reusable database connections maintained in memory to avoid overhead of creating new connections for each request.


---

</details>

---

## Common Mistakes to Avoid

1. **Not using indexes properly** - Index columns used in WHERE, JOIN, ORDER BY
2. **SELECT * in production** - Specify only needed columns
3. **N+1 queries** - Use proper fetch strategies
4. **Missing @Transactional** - Ensure data consistency
5. **Incorrect transaction propagation** - Understand isolation levels
6. **Not using connection pooling** - Configure HikariCP properly
7. **Ignoring query performance** - Use EXPLAIN ANALYZE
8. **Not handling exceptions in transactions** - Specify rollback rules
9. **Lazy initialization exceptions** - Use @Transactional or DTOs
10. **Not using pagination** - Memory issues with large datasets

---

## Resources for Further Study

- Official PostgreSQL Documentation
- MySQL Documentation
- Spring Data JPA Reference
- Hibernate Documentation
- Database Design Best Practices
- SQL Performance Tuning Guides

---
