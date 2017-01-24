## jOOQ-examples

### jOOQ-academy

#### ActiveRecords

```
AuthorRecord author;

// create
author = dsl.newRecord(AUTHOR);
author.store();

// update
author = dsl.selectFrom(AUTHOR).where(AUTHOR.ID.eq(1)).fetchOne();
author.store();

// read
author = dsl.newRecord(AUTHOR);
author.setId(3);
author.refresh();

// delete
author.delete();

#### Optimistic Locking

        Connection connection = connection();
        DSLContext dsl = DSL.using(connection, new Settings().withExecuteWithOptimisticLocking(true));

        try {
            Tools.title("Applying optimistic locking");

            BookRecord book1 = dsl.selectFrom(BOOK).where(BOOK.ID.eq(1)).fetchOne();
            BookRecord book2 = dsl.selectFrom(BOOK).where(BOOK.ID.eq(1)).fetchOne();

            book1.setTitle("New Title");
            book1.store();

            book2.setTitle("Another Title");
            book2.store();
        }

        catch (DataChangedException expected) {
            expected.printStackTrace();
        }

        // Don't store the changes
        finally {
            connection.rollback();
        }

说明 http://www.jooq.org/javadoc/3.2.0/org/jooq/UpdatableRecord.html

store method

* VERSION and TIMESTAMP columns
jOOQ can auto-generate "version" and "timestamp" values that can be used for optimistic locking. If this is an UpdatableRecord and if this record returns fields for either Table.getRecordVersion() or Table.getRecordTimestamp(), then these values are set onto the INSERT or UPDATE statement being executed. On execution success, the generated values are set to this record. Use the code-generation configuration to specify naming patterns for auto-generated "version" and "timestamp" columns.

Should you want to circumvent jOOQ-generated updates to these columns, you can render an INSERT or UPDATE statement manually using the various DSLContext.insertInto(Table), DSLContext.update(Table) methods.

##### Optimistic locking

If an UPDATE statement is executed and Settings.isExecuteWithOptimisticLocking() is set to true, then this record will first be compared with the latest state in the database. There are two modes of operation for optimistic locking:

* With VERSION and/or TIMESTAMP columns configured
This is the preferred way of using optimistic locking in jOOQ. If this is an UpdatableRecord and if this record returns fields for either Table.getRecordVersion() or Table.getRecordTimestamp(), then these values are compared to the corresponding value in the database in the WHERE clause of the executed DELETE statement.

* Without any specific column configurations
In order to compare this record with the latest state, the database record will be locked pessimistically using a SELECT .. FOR UPDATE statement. Not all databases support the FOR UPDATE clause natively. Namely, the following databases will show slightly different behaviour:

  * SQLDialect.CUBRID and SQLDialect.SQLSERVER: jOOQ will try to lock the database record using JDBC's ResultSet.TYPE_SCROLL_SENSITIVE and ResultSet.CONCUR_UPDATABLE.
  * SQLDialect.SQLITE: No pessimistic locking is possible. Client code must assure that no race-conditions can occur between jOOQ's checking of database record state and the actual UPDATE

See SelectQuery.setForUpdate(boolean) for more details

###### Statement examples

Possible statements are

   INSERT INTO [table] ([modified fields, including keys])
   VALUES ([modified values, including keys])
 
   UPDATE [table]
   SET [modified fields = modified values, excluding keys]
    WHERE [key fields = key values]
   AND [version/timestamp fields = version/timestamp values]


http://www.jooq.org/doc/2.5/manual/sql-execution/crud-with-updatablerecords/optimistic-locking/

##### Optimised optimistic locking using TIMESTAMP fields
If you're using jOOQ's code generator, you can take indicate TIMESTAMP or UPDATE COUNTER fields for every generated table in the code generation configuration. Let's say we have this table:

只要在gen jooq中设定Timestamp，jooq自动生成的代码就会乐观 lock 这个字段。

http://www.jooq.org/doc/2.5/manual/code-generation/codegen-advanced/

  <!-- All table and view columns that are used as "timestamp" fields for
       optimistic locking (several Java regular expressions, separated by comma).
       See UpdatableRecord.store() and UpdatableRecord.delete() for details -->
  <recordTimestampFields>REC_TIMESTAMP</recordTimestampFields/>


#### PreparedStatements

        Tools.title("Distinguishing between static and prepared statements with JDBC");
        // 1% of the time
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SELECT * FROM AUTHOR");
        }

        // 99% of the time
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM AUTHOR")) {
            stmt.execute();
        }

        Tools.title("Distinguishing between static and prepared statements with jOOQ");
        // 1% of the time
        DSL.using(connection, new Settings().withStatementType(StatementType.STATIC_STATEMENT))
           .fetch("SELECT * FROM AUTHOR")

        // 99% of the time
        DSL.using(connection).fetch("SELECT * FROM AUTHOR")


#### ConnectionProvider

        Tools.title("Using jOOQ with a DBCP connection pool");
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(Tools.driver());
        ds.setUrl(Tools.url());
        ds.setUsername(Tools.username());
        ds.setPassword(Tools.password());
        System.out.println(
            DSL.using(ds, SQLDialect.H2)
               .select()
               .from(AUTHOR)
               .fetch()
        );

#### SQLDialect

        Tools.title("Generate SELECT 1 FROM DUAL for all SQL dialect families");
        Arrays.stream(SQLDialect.families())
              .map(dialect -> String.format("%15s : ", dialect) + DSL.using(dialect).render(DSL.selectOne()))
              .forEach(System.out::println);

#### Settings

        Select<?> select =
        DSL.select()
           .from(AUTHOR)
           .where(AUTHOR.ID.eq(3));

        Tools.title("A couple of settings at work - Formatting");
        out.println(using(H2, new Settings().withRenderFormatted(false)).render(select));
        out.println(using(H2, new Settings().withRenderFormatted(true)).render(select));

        Tools.title("A couple of settings at work - Schema");
        out.println(using(H2, new Settings().withRenderSchema(false)).render(select));
        out.println(using(H2, new Settings().withRenderSchema(true)).render(select));

        Tools.title("A couple of settings at work - Name style");
        out.println(using(H2, new Settings().withRenderNameStyle(RenderNameStyle.AS_IS)).render(select));
        out.println(using(H2, new Settings().withRenderNameStyle(RenderNameStyle.UPPER)).render(select));
        out.println(using(H2, new Settings().withRenderNameStyle(RenderNameStyle.LOWER)).render(select));
        out.println(using(H2, new Settings().withRenderNameStyle(RenderNameStyle.QUOTED)).render(select));

        Tools.title("A couple of settings at work - Keyword style");
        out.println(using(H2, new Settings().withRenderKeywordStyle(RenderKeywordStyle.UPPER)).render(select));
        out.println(using(H2, new Settings().withRenderKeywordStyle(RenderKeywordStyle.LOWER)).render(select));

        Tools.title("A couple of settings at work - Mapping");
        out.println(using(H2, new Settings()
            .withRenderMapping(new RenderMapping()
                .withSchemata(new MappedSchema()
                    .withInput("PUBLIC")
                    .withOutput("test")
                    .withTables(new MappedTable()
                        .withInput("AUTHOR")
                        .withOutput("test-author"))
                )
            )).render(select));

#### ExecuteListener

        Tools.title("Displaying execution time using a custom ExecuteListener");
        ExecuteListener listener = new DefaultExecuteListener() {
            @Override
            public void start(ExecuteContext ctx) {
                // Register the start time to the current context
                ctx.data("time", System.nanoTime());
            }

            @Override
            public void end(ExecuteContext ctx) {
                // Extract the start time from the current context
                Long time = (Long) ctx.data("time");
                System.out.println("Execution time : " + ((System.nanoTime() - time) / 1000 / 1000.0) + "ms. Query : " + ctx.sql());
            }
        };

        DSL.using(new DefaultConfiguration()
               .set(SQLDialect.H2)
               .set(new DefaultConnectionProvider(connection()))
               .set(new DefaultExecuteListenerProvider(listener))
           )
           .select(AUTHOR.ID)
           .from(AUTHOR)
           .fetch();















## community

http://www.jooq.org/community

http://aakashjapi.com/caching-with-jooq-and-redis/

http://www.vertabelo.com/blog/technical-articles/how-to-create-a-spark-rest-api-with-jooq

https://blog.jooq.org/2015/12/03/reactive-database-access-part-1-why-async/
https://blog.jooq.org/2016/01/14/reactive-database-access-part-3-using-jooq-with-scala-futures-and-actors/

























## document

#### 3.3. Different use cases for jOOQ
jOOQ has originally been created as a library for complete abstraction of JDBC and all database interaction. Various best practices that are frequently encountered in pre-existing software products are applied to this library. This includes:
* Typesafe database object referencing through generated schema, table, column, record, procedure, type, dao, pojo artefacts (see the chapter about code generation)
* Typesafe SQL construction / SQL building through a complete querying DSL API modelling SQL as a domain specific language in Java (see the chapter about the query DSL API)
* Convenient query execution through an improved API for result fetching (see the chapters about the various types of data fetching)
* SQL dialect abstraction and SQL clause emulation to improve cross-database compatibility and to enable missing features in simpler databases (see the chapter about SQL dialects)
* SQL logging and debugging using jOOQ as an integral part of your development process (see the chapters about logging)

Effectively, jOOQ was originally designed to replace any other database abstraction framework short of the ones handling connection pooling (and more sophisticated transaction management)

* 3.3.1. jOOQ as a SQL builder
* 3.3.2. jOOQ as a SQL builder with code generation
* 3.3.3. jOOQ as a SQL executor
* 3.3.4. jOOQ for CRUD
* 3.3.5. jOOQ for PROs
  * jOOQ's Execute Listeners: jOOQ allows you to hook your custom execute listeners into jOOQ's SQL statement execution lifecycle in order to centrally coordinate any arbitrary operation performed on SQL being executed. Use this for logging, identity generation, SQL tracing, performance measurements, etc.
  * Logging
  * Stored Procedures
  * Batch execution
  * Exporting and Importing: jOOQ ships with an API to easily export/import data in various formats


todo transactionManager
http://www.jooq.org/doc/3.8/manual-single-page/#jooq-with-spring


http://www.jooq.org/doc/3.8/manual-single-page/#jooq-and-scala




#### 4.2. The DSLContext class
http://www.jooq.org/doc/3.8/manual-single-page/#dsl-context

A Configuration can be supplied with these objects:
* org.jooq.SQLDialect : The dialect of your database. This may be any of the currently supported database types (see SQL Dialect for more details)
* org.jooq.conf.Settings : An optional runtime configuration (see Custom Settings for more details)
* org.jooq.ExecuteListenerProvider : An optional reference to a provider class that can provide execute listeners to jOOQ (see ExecuteListeners for more details)
* org.jooq.RecordMapperProvider : An optional reference to a provider class that can provide record mappers to jOOQ (see POJOs with RecordMappers for more details)
* Any of these:
  * java.sql.Connection : An optional JDBC Connection that will be re-used for the whole lifecycle of your Configuration (see Connection vs. DataSource for more details). For simplicity, this is the use-case referenced from this manual, most of the time.
  * java.sql.DataSource : An optional JDBC DataSource that will be re-used for the whole lifecycle of your Configuration. If you prefer using DataSources over Connections, jOOQ will internally fetch new Connections from your DataSource, conveniently closing them again after query execution. This is particularly useful in J2EE or Spring contexts (see Connection vs. DataSource for more details)
  * org.jooq.ConnectionProvider : A custom abstraction that is used by jOOQ to "acquire" and "release" connections. jOOQ will internally "acquire" new Connections from your ConnectionProvider, conveniently "releasing" them again after query execution. (see Connection vs. DataSource for more details)

Wrapping a Configuration object, a DSLContext can construct statements, for later execution. An example is given here:

##### 4.2.4. Custom data

```
public class NoInsertListener extends DefaultExecuteListener {
    @Override
    public void start(ExecuteContext ctx) {
        // This listener is active only, when your custom flag is set to true
        if (Boolean.TRUE.equals(ctx.configuration().data("com.example.my-namespace.no-inserts"))) {
            // If active, fail this execution, if an INSERT statement is being executed
            if (ctx.query() instanceof Insert) {
                throw new DataAccessException("No INSERT statements allowed");
            }
        }
    }
}
```

##### 4.2.5. Custom ExecuteListeners

```
// Create your Configuration
Configuration configuration = new DefaultConfiguration().set(connection).set(dialect);

// Hook your listener providers into the configuration:
configuration.set(
    new DefaultExecuteListenerProvider(new MyFirstListener()),
    new DefaultExecuteListenerProvider(new PerformanceLoggingListener()),
    new DefaultExecuteListenerProvider(new NoInsertListener())
);
```

##### 4.2.6. Custom Settings

##### 4.3.1. jOOQ's DSL and model API

dynamic query

```
DSLContext create = DSL.using(connection, dialect);
SelectQuery<Record> query = create.selectQuery();
query.addFrom(AUTHOR);

// Join books only under certain circumstances
if (join) {
    query.addJoin(BOOK, BOOK.AUTHOR_ID.equal(AUTHOR.ID));
}

Result<?> result = query.fetch();


// ... or ...
DSLContext create = DSL.using(connection, dialect);
SelectFinalStep<?> select = create.select().from(AUTHOR);

// Add the JOIN clause on the internal QueryObject representation
SelectQuery<?> query = select.getQuery();
query.addJoin(BOOK, BOOK.AUTHOR_ID.equal(AUTHOR.ID));
```

注意 Mutability

#### 4.3.3. The SELECT statement

SELECT from single tables

```
public <R extends Record> SelectWhereStep<R> selectFrom(Table<R> table);
```

##### 4.3.3.1. The SELECT clause



##### 4.3.3.10. The LIMIT .. OFFSET clause
##### 4.3.3.11. The SEEK clause

http://use-the-index-luke.com/no-offset
不要使用limit offset做分页

##### 4.3.3.12. The FOR UPDATE clause

 indicate to the database, that a set of cells or records should be locked by a given transaction for subsequent updates

##### 4.3.3.15. Lexical and logical SELECT clause order

Logical SELECT clause order

##### 4.3.4.5. INSERT .. ON DUPLICATE KEY

supported by jOOQ and emulated in other RDBMS, where this is possible (i.e. if they support the SQL standard MERGE statement)

```
create.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.LAST_NAME)
      .values(3, "Koontz")
      .onDuplicateKeyUpdate()
      .set(AUTHOR.LAST_NAME, "Koontz")
      .execute();

// or ignore
create.insertInto(AUTHOR, AUTHOR.ID, AUTHOR.LAST_NAME)
      .values(3, "Koontz")
      .onDuplicateKeyIgnore()
      .execute();
```

##### 4.3.4.6. INSERT .. RETURNING

The Postgres database has native support for an INSERT .. RETURNING clause. This is a very powerful concept that is emulated for all other dialects using JDBC's getGeneratedKeys() method. Take this example:

Some databases have poor support for returning generated keys after INSERTs. In those cases, jOOQ might need to issue another SELECT statement in order to fetch an @@identity value. Be aware, that this can lead to race-conditions in those databases that cannot properly return generated ID values. For more information, please consider the jOOQ Javadoc for the returning() clause.


#### 4.3.5. The UPDATE statement

```
UPDATE AUTHOR
   SET FIRST_NAME = (
         SELECT FIRST_NAME FROM PERSON WHERE PERSON.ID = AUTHOR.ID
       ),
 WHERE ID = 3;
 
UPDATE AUTHOR
  SET (FIRST_NAME, LAST_NAME) = (
        SELECT PERSON.FIRST_NAME, PERSON.LAST_NAME FROM PERSON WHERE PERSON.ID = AUTHOR.ID
      )
 WHERE ID = 3;
```


#### 4.4.5. Generating DDL from objects

```
// SCHEMA is the generated schema that contains a reference to all generated tables
Queries ddl =
DSL.using(configuration)
   .ddl(SCHEMA);
   
for (Query query : ddl.queries()) {
    System.out.println(query);
}
```

### 4.6. Table expressions 

#### 4.6.2. Aliased Tables

#### 4.6.5. Nested SELECTs
A SELECT statement can appear almost anywhere a table expression can. Such a "nested SELECT" is often called a "derived table". Apart from many convenience methods accepting org.jooq.Select objects directly, a SELECT statement can always be transformed into a org.jooq.Table object using the asTable() method.

```
create.select()
      .from(BOOK)
      .where(BOOK.AUTHOR_ID.equal(create
             .select(AUTHOR.ID)
             .from(AUTHOR)
             .where(AUTHOR.LAST_NAME.equal("Orwell"))))
      .fetch();

// or
Table<Record> nested =
    create.select(BOOK.AUTHOR_ID, count().as("books"))
          .from(BOOK)
          .groupBy(BOOK.AUTHOR_ID).asTable("nested");

create.select(nested.fields())
      .from(nested)
      .orderBy(nested.field("books"))
      .fetch();

// or
/*
SELECT LAST_NAME, (
      SELECT COUNT(*)
       FROM BOOK
      WHERE BOOK.AUTHOR_ID = AUTHOR.ID) books
    FROM AUTHOR
ORDER BY books DESC
*/

// The type of books cannot be inferred from the Select<?>
Field<Object> books =
    create.selectCount()
          .from(BOOK)
          .where(BOOK.AUTHOR_ID.equal(AUTHOR.ID))
          .asField("books");

create.select(AUTHOR.ID, books)
      .from(AUTHOR)
      .orderBy(books, AUTHOR.ID))
      .fetch();
```

### 4.7. Column expressions

```
// A regular table column expression
Field<String> field1 = BOOK.TITLE;

// A function created from the DSL using "prefix" notation
Field<String> field2 = trim(BOOK.TITLE);

// The same function created from a pre-existing Field using "postfix" notation
Field<String> field3 = BOOK.TITLE.trim();

// More complex function with advanced DSL syntax
Field<String> field4 = listAgg(BOOK.TITLE)
                          .withinGroupOrderBy(BOOK.ID.asc())
                          .over().partitionBy(AUTHOR.ID);
```

#### 4.7.3. Cast expressions

```
SELECT CAST(AUTHOR.LAST_NAME AS TEXT) FROM DUAL

create.select(TAuthor.LAST_NAME.cast(PostgresDataType.TEXT)).fetch();
```

The complete CAST API in org.jooq.Field consists of these three methods:

```
public interface Field<T> {

    // Cast this field to the type of another field
    <Z> Field<Z> cast(Field<Z> field);
    
    // Cast this field to a given DataType
    <Z> Field<Z> cast(DataType<Z> type);
    
    // Cast this field to the default DataType for a given Class
    <Z> Field<Z> cast(Class<? extends Z> type);
}

// And additional convenience methods in the DSL:
public class DSL {
    <T> Field<T> cast(Object object, Field<T> field);
    <T> Field<T> cast(Object object, DataType<T> type);
    <T> Field<T> cast(Object object, Class<? extends T> type);
    <T> Field<T> castNull(Field<T> field);
    <T> Field<T> castNull(DataType<T> type);
    <T> Field<T> castNull(Class<? extends T> type);
}
```

#### 4.7.4. Datatype coercions


Ordered-set aggregate functions

```
SELECT   LISTAGG(TITLE, ', ')
         WITHIN GROUP (ORDER BY TITLE)
FROM     BOOK
GROUP BY AUTHOR_ID

+---------------------+
| LISTAGG             |
+---------------------+
| 1984, Animal Farm   |
| O Alquimista, Brida |
+---------------------+
```

ROLLUP() explained in SQL

```
-- ROLLUP() with two arguments
SELECT AUTHOR_ID, PUBLISHED_IN, COUNT(*)
FROM BOOK
GROUP BY ROLLUP(AUTHOR_ID, PUBLISHED_IN)

扩展
+-----------+--------------+----------+
| AUTHOR_ID | PUBLISHED_IN | COUNT(*) |
+-----------+--------------+----------+
|         1 |         1945 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
|         1 |         1948 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
|         1 |         NULL |        2 | <- GROUP BY (AUTHOR_ID)
|         2 |         1988 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
|         2 |         1990 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
|         2 |         NULL |        2 | <- GROUP BY (AUTHOR_ID)
|      NULL |         NULL |        4 | <- GROUP BY ()
+-----------+--------------+----------+
```

CUBE() explained in SQL

```
-- CUBE() with two arguments
SELECT AUTHOR_ID, PUBLISHED_IN, COUNT(*)
FROM BOOK
GROUP BY CUBE(AUTHOR_ID, PUBLISHED_IN)

+-----------+--------------+----------+
| AUTHOR_ID | PUBLISHED_IN | COUNT(*) |
+-----------+--------------+----------+
|      NULL |         NULL |        2 | <- GROUP BY ()
|      NULL |         1945 |        1 | <- GROUP BY (PUBLISHED_IN)
|      NULL |         1948 |        1 | <- GROUP BY (PUBLISHED_IN)
|      NULL |         1988 |        1 | <- GROUP BY (PUBLISHED_IN)
|      NULL |         1990 |        1 | <- GROUP BY (PUBLISHED_IN)
|         1 |         NULL |        2 | <- GROUP BY (AUTHOR_ID)
|         1 |         1945 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
|         1 |         1948 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
|         2 |         NULL |        2 | <- GROUP BY (AUTHOR_ID)
|         2 |         1988 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
|         2 |         1990 |        1 | <- GROUP BY (AUTHOR_ID, PUBLISHED_IN)
+-----------+--------------+----------+
```

#### 4.7.19. The CASE expression

```
CASE WHEN AUTHOR.FIRST_NAME = 'Paulo'  THEN 'brazilian'
     WHEN AUTHOR.FIRST_NAME = 'George' THEN 'english'
                                       ELSE 'unknown'
END

-- OR:

CASE AUTHOR.FIRST_NAME WHEN 'Paulo'  THEN 'brazilian'
                       WHEN 'George' THEN 'english'
                                     ELSE 'unknown'
END

DSL
      .when(AUTHOR.FIRST_NAME.equal("Paulo"), "brazilian")
      .when(AUTHOR.FIRST_NAME.equal("George"), "english")
      .otherwise("unknown");

// OR:

DSL.choose(AUTHOR.FIRST_NAME)
   .when("Paulo", "brazilian")
   .when("George", "english")
   .otherwise("unknown");
```

### 4.8. Conditional expressions

```
Condition a = BOOK.TITLE.equal("Animal Farm");
Condition b = BOOK.TITLE.equal("1984");
Condition c = AUTHOR.LAST_NAME.equal("Orwell");

Condition combined1 = a.or(b);             // These OR-connected conditions form a new condition, wrapped in parentheses
Condition combined2 = combined1.andNot(c); // The left-hand side of the AND NOT () operator is already wrapped in parentheses
```

```
-- Using an IN list
(BOOK.ID, BOOK.TITLE) IN ((1, 'A'), (2, 'B'))

-- Using a subselect
(BOOK.ID, BOOK.TITLE) IN (
  SELECT T.ID, T.TITLE
  FROM T 
)
```

### 4.9. Dynamic SQL

```
create.select(
          AUTHOR.FIRST_NAME.concat(AUTHOR.LAST_NAME),
          count()
      .from(AUTHOR)
      .join(BOOK).on(AUTHOR.ID.eq(BOOK.AUTHOR_ID))
      .groupBy(AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
      .orderBy(count().desc())
      .fetch();

// to
SelectField<?>[] select = {
    AUTHOR.FIRST_NAME.concat(AUTHOR.LAST_NAME),
    count()
};
Table<?> from = AUTHOR.join(BOOK).on(AUTHOR.ID.eq(BOOK.AUTHOR_ID));
GroupField[] groupBy = { AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME };
SortField<?>[] orderBy = { count().desc() };

create.select(select)
      .from(from)
      .groupBy(groupBy)
      .orderBy()
      .fetch();
```

Each individual expression, and collection of expressions can be seen as an independent entity that can be
Constructed dynamically
Reused across queries
Dynamic construction is particularly useful in the case of the WHERE clause, for dynamic predicate building. For instance:

### 4.10. Plain SQL

别用

### 4.12. Bind values and parameters

Bind values are used in SQL / JDBC for various reasons. Among the most obvious ones are:

* Protection against SQL injection. Instead of inlining values possibly originating from user input, you bind those values to your prepared statement and let the JDBC driver / database take care of handling security aspects.
* Increased speed. Advanced databases such as Oracle can keep execution plans of similar queries in a dedicated cache to prevent hard-parsing your query again and again. In many cases, the actual value of a bind variable does not influence the execution plan, hence it can be reused. Preparing a statement will thus be faster
8 On a JDBC level, you can also reuse the SQL string and prepared statement object instead of constructing it again, as you can bind new values to the prepared statement. jOOQ currently does not cache prepared statements, internally.

The following sections explain how you can introduce bind values in jOOQ, and how you can control the way they are rendered and bound to SQL.

Extract bind values from a query

```
Select<?> select = create.select().from(BOOK).where(BOOK.ID.equal(5)).and(BOOK.TITLE.equal("Animal Farm"));

// Render the SQL statement:
String sql = select.getSQL();
assertEquals("SELECT * FROM BOOK WHERE ID = ? AND TITLE = ?", sql);

// Get the bind values:
List<Object> values = select.getBindValues();
assertEquals(2, values.size());
assertEquals(5, values.get(0));
assertEquals("Animal Farm", values.get(1));

// extract specific bind values by index from a query
Param<?> param = select.getParam("2");

// You could now modify the Query's underlying bind value:
if ("Animal Farm".equals(param.getValue())) {
    param.setConverted("1984");
}
```

#### 4.12.4. SQL injection

SQL injection happens because a programming language (SQL) is used to dynamically create arbitrary server-side statements based on user input. Programmers must take lots of care not to mix the language parts (SQL) with the user input (bind variables)

```
// This query will use bind values, internally.
create.fetch("SELECT * FROM BOOK WHERE ID = ? AND TITLE = ?", 5, "Animal Farm");

// This query will not use bind values, internally.
create.fetch("SELECT * FROM BOOK WHERE ID = 5 AND TITLE = 'Animal Farm'");
```

All methods in the jOOQ API that allow for plain (unescaped, untreated) SQL contain a warning message in their relevant Javadoc, to remind you of the risk of SQL injection in what is otherwise a SQL-injection-safe API.

https://blog.jooq.org/2013/11/05/using-sql-injection-vulnerabilities-to-dump-your-database/ 

### 4.13. QueryParts
A org.jooq.Query and all its contained objects is a org.jooq.QueryPart. QueryParts essentially provide this functionality:
* they can render SQL using the accept(Context) method
* they can bind variables using the accept(Context) method

An example of rendering SQL

```
-- [...]
FROM AUTHOR
JOIN BOOK ON AUTHOR.ID = BOOK.AUTHOR_ID
-- [...]

This is how jOOQ renders such a condition (simplified example):

@Override
public final void accept(Context<?> context) {
    // The CompareCondition delegates rendering of the Fields to the Fields
    // themselves and connects them using the Condition's comparator operator:
    context.visit(field1)
           .sql(" ")
           .keyword(comparator.toSQL())
           .sql(" ")
           .visit(field2);
}
```

An example of binding values to SQL

TODO document typo in here
"A simple example can be provided by checking out jOOQ's internal representation of a (simplified) CompareCondition. It is used for any org.jooq.Condition comparing two fields as for example the ！！！AUTHOR.ID = BOOK.AUTHOR_ID！！！ condition here:"

```
-- [...]
WHERE AUTHOR.ID = ?
-- [...]

This is how jOOQ binds values on such a condition:

@Override
public final void bind(BindContext context) throws DataAccessException {
    // The CompareCondition itself does not bind any variables.
    // But the two fields involved in the condition might do so...
    context.bind(field1).bind(field2);
}
```

#### 4.13.4. Custom data type bindings

jOOQ supports all the standard SQL data types out of the box, i.e. the types contained in java.sql.Types. But your domain model might be more specific, or you might be using a vendor-specific data type, such as JSON, HSTORE, or some other data structure. If this is the case, this section will be right for you, we'll see how you can create org.jooq.Converter types and org.jooq.Binding types.

```
public interface Converter<T, U> {

    // Your conversion logic goes into these two methods, that can convert
    // between the database type T and the user type U:
    U from(T databaseObject);
    T to(U userObject);

	// You need to provide Class instances for each type, too:
    Class<T> fromType();
    Class<U> toType();
}
```

#### 4.13.5. Custom syntax elements

#### 4.13.6. Plain SQL QueryParts

```
// Plain SQL using bind values. The value 5 is bound to the first variable, "Animal Farm" to the second variable:
create.selectFrom(BOOK).where("BOOK.ID = ? AND TITLE = ?", 5, "Animal Farm").fetch();

// Plain SQL using placeholders (counting from zero).
// The QueryPart "id" is substituted for the placeholder {0}, the QueryPart "title" for {1}
Field<Integer> id   = val(5);
Field<String> title = val("Animal Farm");
create.selectFrom(BOOK).where("BOOK.ID = {0} AND TITLE = {1}", id, title).fetch();
```

#### 4.13.8. Custom SQL transformation
With jOOQ 3.2's org.jooq.VisitListener SPI, it is possible to perform custom SQL transformation to implement things like shared-schema multi-tenancy, or a security layer centrally preventing access to certain data. This SPI is extremely powerful, as you can make ad-hoc decisions at runtime regarding local or global transformation of your SQL statement. The following sections show a couple of simple, yet real-world use-cases.

##### 4.13.8.1. Logging abbreviated bind values


### 4.14. SQL building in Scala

```
select (
  BOOK.ID * BOOK.AUTHOR_ID,
  BOOK.ID + BOOK.AUTHOR_ID * 3 + 4,
  BOOK.TITLE || " abc" || " xy")
from BOOK
leftOuterJoin (
  select (x.ID, x.YEAR_OF_BIRTH)
  from x
  limit 1
  asTable x.getName()
)
on BOOK.AUTHOR_ID === x.ID
where (BOOK.ID <> 2)
or (BOOK.TITLE in ("O Alquimista", "Brida"))
fetch
```

Scala 2.10 Macros

This feature is still being experimented with. With Scala Macros, it might be possible to inline a true SQL dialect into the Scala syntax, backed by the jOOQ API. Stay tuned!


## 5. SQL execution
### 5.1. Comparison between jOOQ and JDBC

### 5.2. Query vs. ResultQuery
With plain SQL, the distinction can be made clear most easily:

```
// Create a Query object and execute it:
Query query = create.query("DELETE FROM BOOK");
query.execute();

// Create a ResultQuery object and execute it, fetching results:
ResultQuery<Record> resultQuery = create.resultQuery("SELECT * FROM BOOK");
Result<Record> result = resultQuery.fetch();
```

TODO typo a the    neglegted

### 5.3. Fetching

Fetching is something that has been completely neglegted by JDBC and also by various other database abstraction libraries. Fetching is much more than just looping or listing records or mapped objects. There are so many ways you may want to fetch data from a database, it should be considered a first-class feature of any database abstraction API. Just to name a few, here are some of jOOQ's fetching modes:
* Untyped vs. typed fetching: Sometimes you care about the returned type of your records, sometimes (with arbitrary projections) you don't.
* Fetching arrays, maps, or lists: Instead of letting you transform your result sets into any more suitable data type, a library should do that work for you.
* Fetching through handler callbacks: This is an entirely different fetching paradigm. With Java 8's lambda expressions, this will become even more powerful.
* Fetching through mapper callbacks: This is an entirely different fetching paradigm. With Java 8's lambda expressions, this will become even more powerful.
* Fetching custom POJOs: This is what made Hibernate and JPA so strong. Automatic mapping of tables to custom POJOs.
* Lazy vs. eager fetching: It should be easy to distinguish these two fetch modes.
* Fetching many results: Some databases allow for returning many result sets from a single query. JDBC can handle this but it's very verbose. A list of results should be returned instead.
* Fetching data asynchronously: Some queries take too long to execute to wait for their results. You should be able to spawn query execution in a separate process.

These modes of fetching are also documented in subsequent sections of the manual

```
// The "standard" fetch
Result<R> fetch();

// The "standard" fetch when you know your query returns only one record. This may return null.
R fetchOne();

// The "standard" fetch when you know your query returns only one record.
Optional<R> fetchOptional();

// The "standard" fetch when you only want to fetch the first record
R fetchAny();

// Create a "lazy" Cursor, that keeps an open underlying JDBC ResultSet
Cursor<R> fetchLazy();
Cursor<R> fetchLazy(int fetchSize);
Stream<R> stream();

// Fetch several results at once
List<Result<Record>> fetchMany();

// Fetch records into a custom callback
<H extends RecordHandler<R>> H fetchInto(H handler);

// Map records using a custom callback
<E> List<E> fetch(RecordMapper<? super R, E> mapper);

// Execute a ResultQuery with jOOQ, but return a JDBC ResultSet, not a jOOQ object
ResultSet fetchResultSet();



// These methods are convenience for fetching only a single field,
// possibly converting results to another type
<T>    List<T> fetch(Field<T> field);
<T>    List<T> fetch(Field<?> field, Class<? extends T> type);
<T, U> List<U> fetch(Field<T> field, Converter<? super T, U> converter);
       List<?> fetch(int fieldIndex);
<T>    List<T> fetch(int fieldIndex, Class<? extends T> type);
<U>    List<U> fetch(int fieldIndex, Converter<?, U> converter);
       List<?> fetch(String fieldName);
<T>    List<T> fetch(String fieldName, Class<? extends T> type);
<U>    List<U> fetch(String fieldName, Converter<?, U> converter);

// These methods are convenience for fetching only a single field, possibly converting results to another type
// Instead of returning lists, these return arrays
<T>    T[]      fetchArray(Field<T> field);
<T>    T[]      fetchArray(Field<?> field, Class<? extends T> type);
<T, U> U[]      fetchArray(Field<T> field, Converter<? super T, U> converter);
       Object[] fetchArray(int fieldIndex);
<T>    T[]      fetchArray(int fieldIndex, Class<? extends T> type);
<U>    U[]      fetchArray(int fieldIndex, Converter<?, U> converter);
       Object[] fetchArray(String fieldName);
<T>    T[]      fetchArray(String fieldName, Class<? extends T> type);
<U>    U[]      fetchArray(String fieldName, Converter<?, U> converter);

// These methods are convenience for fetching only a single field from a single record,
// possibly converting results to another type
<T>    T      fetchOne(Field<T> field);
<T>    T      fetchOne(Field<?> field, Class<? extends T> type);
<T, U> U      fetchOne(Field<T> field, Converter<? super T, U> converter);
       Object fetchOne(int fieldIndex);
<T>    T      fetchOne(int fieldIndex, Class<? extends T> type);
<U>    U      fetchOne(int fieldIndex, Converter<?, U> converter);
       Object fetchOne(String fieldName);
<T>    T      fetchOne(String fieldName, Class<? extends T> type);
<U>    U      fetchOne(String fieldName, Converter<?, U> converter);



// Transform your Records into arrays, Results into matrices
       Object[][] fetchArrays();
       Object[]   fetchOneArray();
       
// Reduce your Result object into maps
<K>    Map<K, R>      fetchMap(Field<K> key);
<K, V> Map<K, V>      fetchMap(Field<K> key, Field<V> value);
<K, E> Map<K, E>      fetchMap(Field<K> key, Class<E> value);
       Map<Record, R> fetchMap(Field<?>[] key);
<E>    Map<Record, E> fetchMap(Field<?>[] key, Class<E> value);

// Transform your Result object into maps
       List<Map<String, Object>> fetchMaps();
       Map<String, Object>       fetchOneMap();

// Transform your Result object into groups
<K>    Map<K, Result<R>>      fetchGroups(Field<K> key);
<K, V> Map<K, List<V>>        fetchGroups(Field<K> key, Field<V> value);
<K, E> Map<K, List<E>>        fetchGroups(Field<K> key, Class<E> value);
       Map<Record, Result<R>> fetchGroups(Field<?>[] key);
<E>    Map<Record, List<E>>   fetchGroups(Field<?>[] key, Class<E> value);

// Transform your Records into custom POJOs
<E>    List<E> fetchInto(Class<? extends E> type);

// Transform your records into another table type
<Z extends Record> Result<Z> fetchInto(Table<Z> table);
```

#### 5.3.1. Record vs. TableRecord

```
// Use the selectFrom() method:
BookRecord book = create.selectFrom(BOOK).where(BOOK.ID.equal(1)).fetchOne();

// Join two tables
Record record = create.select()
                      .from(BOOK)
                      .join(AUTHOR).on(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
                      .where(BOOK.ID.equal(1))
                      .fetchOne();

// "extract" the two individual strongly typed TableRecord types from the denormalised Record:
BookRecord book = record.into(BOOK);
AuthorRecord author = record.into(AUTHOR);
```

#### 5.3.3. Arrays, Maps and Lists

```
// Fetching only book titles (the two calls are equivalent):
List<String> titles1 = create.select().from(BOOK).fetch().getValues(BOOK.TITLE);
List<String> titles2 = create.select().from(BOOK).fetch(BOOK.TITLE);
String[]     titles3 = create.select().from(BOOK).fetchArray(BOOK.TITLE);

// Fetching only book IDs, converted to Long
List<Long> ids1 = create.select().from(BOOK).fetch().getValues(BOOK.ID, Long.class);
List<Long> ids2 = create.select().from(BOOK).fetch(BOOK.ID, Long.class);
Long[]     ids3 = create.select().from(BOOK).fetchArray(BOOK.ID, Long.class);

// Fetching book IDs and mapping each ID to their records or titles
Map<Integer, BookRecord> map1 = create.selectFrom(BOOK).fetch().intoMap(BOOK.ID);
Map<Integer, BookRecord> map2 = create.selectFrom(BOOK).fetchMap(BOOK.ID);
Map<Integer, String>     map3 = create.selectFrom(BOOK).fetch().intoMap(BOOK.ID, BOOK.TITLE);
Map<Integer, String>     map4 = create.selectFrom(BOOK).fetchMap(BOOK.ID, BOOK.TITLE);

// Group by AUTHOR_ID and list all books written by any author:
Map<Integer, Result<BookRecord>> group1 = create.selectFrom(BOOK).fetch().intoGroups(BOOK.AUTHOR_ID);
Map<Integer, Result<BookRecord>> group2 = create.selectFrom(BOOK).fetchGroups(BOOK.AUTHOR_ID);
Map<Integer, List<String>>       group3 = create.selectFrom(BOOK).fetch().intoGroups(BOOK.AUTHOR_ID, BOOK.TITLE);
Map<Integer, List<String>>       group4 = create.selectFrom(BOOK).fetchGroups(BOOK.AUTHOR_ID, BOOK.TITLE);
```

#### 5.3.4. RecordHandler

```
// Write callbacks to receive records from select statements
create.selectFrom(BOOK)
      .orderBy(BOOK.ID)
      .fetch()
      .into(new RecordHandler<BookRecord>() {
          @Override
          public void next(BookRecord book) {
              Util.doThingsWithBook(book);
          }
      });
      
// Or more concisely
create.selectFrom(BOOK)
      .orderBy(BOOK.ID)
      .fetchInto(new RecordHandler<BookRecord>() {...});
      
// Or even more concisely with Java 8's lambda expressions:
create.selectFrom(BOOK)
      .orderBy(BOOK.ID)
      .fetchInto(book -> { Util.doThingsWithBook(book); }; );
```

#### 5.3.5. RecordMapper

```
// Or even more concisely with Java 8's lambda expressions:
create.selectFrom(BOOK)
      .orderBy(BOOK.ID)
      .fetch(book -> book.getId());
```

#### 5.3.6. POJOs

Using JPA-annotated POJOs
Using simple POJOs
Using "immutable" POJOs
Using proxyable types
Loading POJOs back into Records to store them

```
// A "mutable" POJO class
public class MyBook {
  public int id;
  public String title;
}

// Create a new POJO instance
MyBook myBook = new MyBook();
myBook.id = 10;
myBook.title = "Animal Farm";

// Load a jOOQ-generated BookRecord from your POJO
BookRecord book = create.newRecord(BOOK, myBook);

// Insert it (implicitly)
book.store();

// Insert it (explicitly)
create.executeInsert(book);

// or update it (ID = 10)
create.executeUpdate(book);
```

Interaction with DAOs

If you're using jOOQ's code generator, you can configure it to generate DAOs for you. Those DAOs operate on generated POJOs. An example of using such a DAO is given here:

```
// Initialise a Configuration
Configuration configuration = new DefaultConfiguration().set(connection).set(SQLDialect.ORACLE);

// Initialise the DAO with the Configuration
BookDao bookDao = new BookDao(configuration);

// Start using the DAO
Book book = bookDao.findById(5);

// Modify and update the POJO
book.setTitle("1984");
book.setPublishedIn(1948);
bookDao.update(book);

// Delete it again
bookDao.delete(book);
```

#### 5.3.7. POJOs with RecordMappers

例子 略

Using third party libraries

A couple of useful libraries exist out there, which implement custom, more generic mapping algorithms. Some of them have been specifically made to work with jOOQ. Among them are:
* ModelMapper (with an explicit jOOQ integration)
* SimpleFlatMapper (with an explicit jOOQ integration)
* Orika Mapper (without explicit jOOQ integration)


#### 5.3.8. Lazy fetching

```
// Obtain a Cursor reference:
try (Cursor<BookRecord> cursor = create.selectFrom(BOOK).fetchLazy()) {
    // Cursor has similar methods as Iterator<R>
    while (cursor.hasNext()) {
        BookRecord book = cursor.fetchOne();
        Util.doThingsWithBook(book);
    }
}
```

As a org.jooq.Cursor holds an internal reference to an open java.sql.ResultSet, it may need to be closed at the end of iteration. If a cursor is completely scrolled through, it will conveniently close the underlying ResultSet. However, you should not rely on that.

Cursors ship with all the other fetch features ...

#### 5.3.9. Lazy fetching with Streams

#### 5.3.11. Later fetching

#### 5.3.12. ResultSet fetching

#### 5.3.13. Data type conversion

```
// Define your Enum
public enum YNM {
    YES, NO, MAYBE
}

// Define your converter
public class YNMConverter extends EnumConverter<String, YNM> {
    public YNMConverter() {
        super(String.class, YNM.class);
    }
}

// And you're all set for converting records to your custom Enum:
for (BookRecord book : create.selectFrom(BOOK).fetch()) {
    switch (book.getValue(BOOK.I_LIKE, new YNMConverter())) {
        case YES:    System.out.println("I like this book             : " + book.getTitle()); break;
        case NO:     System.out.println("I didn't like this book      : " + book.getTitle()); break;
        case MAYBE:  System.out.println("I'm not sure about this book : " + book.getTitle()); break;
    }
}
```

### 5.5. Reusing a Query's PreparedStatement

Keeping open PreparedStatements with JDBC

With JDBC, you can easily reuse a java.sql.PreparedStatement by not closing it between subsequent executions. An example is given here:

```
try (PreparedStatement stmt = connection.prepareStatement("SELECT 1 FROM DUAL")) {
    // Fetch a first ResultSet
    try (ResultSet rs1 = stmt.executeQuery()) { ... }
    // Without closing the statement, execute it again to fetch another ResultSet
    try (ResultSet rs2 = stmt.executeQuery()) { ... }
}
```

Keeping open PreparedStatements with jOOQ

```
// Create a query which is configured to keep its underlying PreparedStatement open
try (ResultQuery<Record> query = create.selectOne().keepStatement(true)) {
    Result<Record> result1 = query.fetch(); // This will lazily create a new PreparedStatement
    Result<Record> result2 = query.fetch(); // This will reuse the previous PreparedStatement
}
```


### 5.6. JDBC flags

Using ResultSet concurrency with ExecuteListeners ...

### 5.7. Using JDBC batch operations

### 5.9. Stored procedures and functions

### 5.10. Exporting to XML, CSV, JSON, HTML, Text 
### 5.11. Importing data

```
// Fetch books and format them as JSON
String json = create.selectFrom(BOOK).fetch().formatJSON();

DSLContext create = DSL.using(connection, dialect);

// Load data into the BOOK table from an input stream
// holding the JSON data.
create.loadInto(BOOK)
      .loadJSON(inputstream, encoding)
      .fields(BOOK.ID, BOOK.AUTHOR_ID, BOOK.TITLE)
      .execute();
```

#### 5.11.3. Importing Records

```
Result<Record3<Integer, Integer, String>> result =
DSL.using(configuration1)
   .select(BOOK.ID, BOOK.AUTHOR_ID, BOOK.TITLE)
   .from(BOOK)
   .fetch();
   
Now, this result should be imported back into a database 2:

DSL.using(configuration2)
   .loadInto(BOOK)
   .loadRecords(result)
   .fields(BOOK.ID, BOOK.AUTHOR_ID, BOOK.TITLE)
   .execute();
```

### 5.12. CRUD with UpdatableRecords

#### 5.12.1. Simple CRUD

```
// Refresh a record from the database.
void refresh() throws DataAccessException;

// Store (insert or update) a record to the database.
int store() throws DataAccessException;

// Delete a record from the database
int delete() throws DataAccessException;
```

Some remarks about storing:
* jOOQ sets only modified values in INSERT statements or UPDATE statements. This allows for default values to be applied to inserted records, as specified in CREATE TABLE DDL statements.
* When store() performs an INSERT statement, jOOQ attempts to load any generated keys from the database back into the record. For more details, see the manual's section about IDENTITY values.
* When loading records from POJOs, jOOQ will assume the record is a new record. It will hence attempt to INSERT it.
* When you activate optimistic locking, storing a record may fail, if the underlying database record has been changed in the mean time.

#### 5.12.2. Records' internal flags

All of jOOQ's Record types and subtypes maintain an internal state for every column value. This state is composed of three elements:

* The value itself
* The "original" value, i.e. the value as it was originally fetched from the database or null, if the record was never in the database
* The "changed" flag, indicating if the value was ever changed through the Record API.

The purpose of the above information is for jOOQ's CRUD operations to know, which values need to be stored to the database, and which values have been left untouched.

#### 5.12.3. IDENTITY values

```
BookRecord book = create.newRecord(BOOK);
book.setTitle("1984");
book.store();

// The generated ID value is fetched after the above INSERT statement
System.out.println(book.getId());
```

#### 5.12.4. Navigation methods

```
BookRecord book = create.fetch(BOOK, BOOK.ID.equal(5));

// Find the author of a book (static imported from Keys)
AuthorRecord author = book.fetchParent(FK_BOOK_AUTHOR);

// Find other books by that author
Result<BookRecord> books = author.fetchChildren(FK_BOOK_AUTHOR);
```


#### 5.12.6. Optimistic locking

可以在codegenerate阶段制定version或timestamp字段

#### 5.12.7. Batch execution

```
// Fetch a bunch of books
Result<BookRecord> books = create.fetch(BOOK);

// Modify the above books, and add some new ones:
modify(books);
addMore(books);

// Batch-update and/or insert all of the above books
create.batchStore(books);
```

#### 5.12.8. CRUD SPI: RecordListener

crud切面

When performing CRUD, you may want to be able to centrally register one or several listener objects that receive notification every time CRUD is performed on an UpdatableRecord. Example use cases of such a listener are:

Adding a central ID generation algorithm, generating UUIDs for all of your records.
Adding a central record initialisation mechanism, preparing the database prior to inserting a new record.

An example of such a RecordListener is given here:

```
// Extending DefaultRecordListener, which provides empty implementations for all methods...
public class InsertListener extends DefaultRecordListener {

    @Override
    public void insertStart(RecordContext ctx) {

        // Generate an ID for inserted BOOKs
        if (ctx.record() instanceof BookRecord) {
            BookRecord book = (BookRecord) ctx.record();
            book.setId(IDTools.generate());
        }
    }
}
```

很重要！！！



### 5.13. DAOs

If you're using jOOQ's code generator, you can configure it to generate POJOs and DAOs for you.



### 5.14. Transaction management

重要！！！

### 5.16. ExecuteListeners

重要！！！ 	

Example: Query statistics ExecuteListener
Example: Custom Logging ExecuteListener
Example: Bad query execution ExecuteListener


### 5.20. Alternative execution models
#### 5.20.1. Using jOOQ with JPA
。。。


## 6. Code generation

### 6.2. Advanced generator configuration

a lot

### 6.3. Programmatic generator configuration

```
// Use the fluent-style API to construct the code generator configuration
import org.jooq.util.jaxb.*;

// [...]

Configuration configuration = new Configuration()
    .withJdbc(new Jdbc()
        .withDriver("org.postgresql.Driver")
        .withUrl("jdbc:postgresql:postgres")
        .withUser("postgres")
        .withPassword("test"))
    .withGenerator(new Generator()
        .withDatabase(new Database()
            .withName("org.jooq.util.postgres.PostgresDatabase")
            .withIncludes(".*")
            .withExcludes("")
            .withInputSchema("public"))
        .withTarget(new Target()
            .withPackageName("org.jooq.util.maven.example")
            .withDirectory("target/generated-sources/jooq")));

GenerationTool.generate(configuration);

// or Manually loading the XML file

import java.io.File;
import javax.xml.bind.JAXB;
import org.jooq.utils.jaxb.Configuration;

// [...]
// and then

Configuration configuration = JAXB.unmarshal(new File("jooq.xml"), Configuration.class);
configuration.getJdbc()
             .withUser("username")
             .withPassword("password");

GeberationTool.generate(configuration);

// ... and then, modify parts of your configuration programmatically, for instance the JDBC user / password:
```

For the above example, you will need all of jooq-3.8.3.jar, jooq-meta-3.8.3.jar, and jooq-codegen-3.8.3.jar, on your classpath.

### 6.4. Custom generator strategies

An out-of-the-box strategy to keep names as they are

By default, jOOQ's generator strategy will convert your database's UNDER_SCORE_NAMES to PascalCaseNames as this is a more common idiom in the Java ecosystem. If, however, you want to retain the names and the casing exactly as it is defined in your database, you can use the org.jooq.util.KeepNamesGeneratorStrategy, which will retain all names exactly as they are.

### 6.7. Generated global artefacts

Referencing global artefacts

When referencing global artefacts from your client application, you would typically static import them as such:

```
// Static imports for all global artefacts (if they exist)
import static com.example.generated.Keys.*;
import static com.example.generated.Routines.*;
import static com.example.generated.Sequences.*;
import static com.example.generated.Tables.*;

// You could then reference your artefacts as follows:
create.insertInto(MY_TABLE)
      .values(MY_SEQUENCE.nextval(), myFunction())
      
// as a more concise form of this:
create.insertInto(com.example.generated.Tables.MY_TABLE)
      .values(com.example.generated.Sequences.MY_SEQUENCE.nextval(), com.example.generated.Routines.myFunction())
```

### 6.16. Data type rewrites


Sometimes, the actual database data type does not match the SQL data type that you would like to use in Java. This is often the case for ill-supported SQL data types, such as BOOLEAN or UUID. jOOQ's code generator allows you to apply simple data type rewriting. The following configuration will rewrite IS_VALID columns in all tables to be of type BOOLEAN.

```
<database>

  <!-- Associate data type rewrites with database columns -->
  <forcedTypes>
    <forcedType>
      <!-- Specify any data type from org.jooq.impl.SQLDataType -->
      <name>BOOLEAN</name>

      <!-- Add a Java regular expression matching fully-qualified columns. Use the pipe to separate several expressions.
           
           If provided, both "expressions" and "types" must match. -->
      <expression>.*\.IS_VALID</expression>
      
      <!-- Add a Java regular expression matching data types to be forced to have this type.
      
           Data types may be reported by your database as:
           - NUMBER              regexp suggestion: NUMBER
           - NUMBER(5)           regexp suggestion: NUMBER\(5\)
           - NUMBER(5, 2)        regexp suggestion: NUMBER\(5,\s*2\)
           - any other form.
           
           It is thus recommended to use defensive regexes for types.
           
           If provided, both "expressions" and "types" must match. -->
      <types>.*</types>
    </forcedType>
  </forcedTypes>
</database>
```

### 6.17. Custom data types and type conversion
### 6.18. Custom data type binding

Consider the following trivial implementation of a binding for PostgreSQL's JSON data type, which binds the JSON string in PostgreSQL to a Google GSON object:

### 6.20. Code generation for large schemas

6.21. Code generation and version control
6.22. Generating code from JPA annotated entities
6.25. Running the code generator with Gradle


### 7.1. JDBC mocking for unit testing
### 8.5. Quality Assurance



































