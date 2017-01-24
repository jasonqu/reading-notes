https://github.com/google/guice

https://github.com/google/guice/wiki/Motivation
### Dependency Injection

public class RealBillingService implements BillingService {
  private final CreditCardProcessor processor;
  private final TransactionLog transactionLog;

  public RealBillingService(CreditCardProcessor processor, 
      TransactionLog transactionLog) {
    this.processor = processor;
    this.transactionLog = transactionLog;
  }

  public Receipt chargeOrder(PizzaOrder order, CreditCard creditCard) {
    try {
      ChargeResult result = processor.charge(creditCard, order.getAmount());
      transactionLog.logChargeResult(result);

      return result.wasSuccessful()
          ? Receipt.forSuccessfulCharge(order.getAmount())
          : Receipt.forDeclinedCharge(result.getDeclineMessage());
     } catch (UnreachableException e) {
      transactionLog.logConnectException(e);
      return Receipt.forSystemFailure(e.getMessage());
    }
  }
}

public class RealBillingServiceTest extends TestCase {

  private final PizzaOrder order = new PizzaOrder(100);
  private final CreditCard creditCard = new CreditCard("1234", 11, 2010);

  private final InMemoryTransactionLog transactionLog = new InMemoryTransactionLog();
  private final FakeCreditCardProcessor processor = new FakeCreditCardProcessor();

  public void testSuccessfulCharge() {
    RealBillingService billingService
        = new RealBillingService(processor, transactionLog);
    Receipt receipt = billingService.chargeOrder(order, creditCard);

    assertTrue(receipt.hasSuccessfulCharge());
    assertEquals(100, receipt.getAmountOfCharge());
    assertEquals(creditCard, processor.getCardOfOnlyCharge());
    assertEquals(100, processor.getAmountOfOnlyCharge());
    assertTrue(transactionLog.wasSuccessLogged());
  }
}

  public static void main(String[] args) {
    CreditCardProcessor processor = new PaypalCreditCardProcessor();
    TransactionLog transactionLog = new DatabaseTransactionLog();
    BillingService billingService
        = new RealBillingService(processor, transactionLog);
    ...
  }

### Dependency Injection with Guice

public class BillingModule extends AbstractModule {
  @Override 
  protected void configure() {
    bind(TransactionLog.class).to(DatabaseTransactionLog.class);
    bind(CreditCardProcessor.class).to(PaypalCreditCardProcessor.class);
    bind(BillingService.class).to(RealBillingService.class);
  }
}

public class RealBillingService implements BillingService {
  private final CreditCardProcessor processor;
  private final TransactionLog transactionLog;

  @Inject
  public RealBillingService(CreditCardProcessor processor,
      TransactionLog transactionLog) {
    this.processor = processor;
    this.transactionLog = transactionLog;
  }

  public Receipt chargeOrder(PizzaOrder order, CreditCard creditCard) {
    try {
      ChargeResult result = processor.charge(creditCard, order.getAmount());
      transactionLog.logChargeResult(result);

      return result.wasSuccessful()
          ? Receipt.forSuccessfulCharge(order.getAmount())
          : Receipt.forDeclinedCharge(result.getDeclineMessage());
     } catch (UnreachableException e) {
      transactionLog.logConnectException(e);
      return Receipt.forSystemFailure(e.getMessage());
    }
  }
}

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new BillingModule());
    BillingService billingService = injector.getInstance(BillingService.class);
    ...
  }


# https://github.com/google/guice/wiki/GettingStarted

So when you build an object, you really need to build an object graph.

# https://github.com/google/guice/wiki/Bindings

### Bindings

The injector's job is to assemble graphs of objects. You request an instance of a given type, and it figures out what to build, resolves dependencies, and wires everything together.

#### Linked Bindings

public class BillingModule extends AbstractModule {
  @Override 
  protected void configure() {
    bind(TransactionLog.class).to(DatabaseTransactionLog.class);
  }
}

Linked bindings can also be chained:

public class BillingModule extends AbstractModule {
  @Override 
  protected void configure() {
    bind(TransactionLog.class).to(DatabaseTransactionLog.class);
    bind(DatabaseTransactionLog.class).to(MySqlDatabaseTransactionLog.class);
  }
}
In this case, when a TransactionLog is requested, the injector will return a MySqlDatabaseTransactionLog.


#### Binding Annotations

Occasionally you'll want multiple bindings for a same type. For example, you might want both a PayPal credit card processor and a Google Checkout processor. To enable this, bindings support an optional binding annotation

The annotation and type together uniquely identify a binding. This pair is called a key.

Defining a binding annotation requires two lines of code plus several imports. Put this in its own .java file or inside the type that it annotates.

```
package example.pizza;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface PayPal {}
```

You don't need to understand all of these meta-annotations, but if you're curious:

* @BindingAnnotation tells Guice that this is a binding annotation. Guice will produce an error if ever multiple binding annotations apply to the same member.
* @Target({FIELD, PARAMETER, METHOD}) is a courtesy to your users. It prevents @PayPal from being accidentally being applied where it serves no purpose.
* @Retention(RUNTIME) makes the annotation available at runtime.

To depend on the annotated binding, apply the annotation to the injected parameter:

public class RealBillingService implements BillingService {

  @Inject
  public RealBillingService(@PayPal CreditCardProcessor processor,
      TransactionLog transactionLog) {
    ...
  }

Lastly we create a binding that uses the annotation. This uses the optional annotatedWith clause in the bind() statement:

    bind(CreditCardProcessor.class)
        .annotatedWith(PayPal.class)
        .to(PayPalCreditCardProcessor.class);

#####@Named

像spring一样注入

Guice comes with a built-in binding annotation @Named that uses a string:

public class RealBillingService implements BillingService {

  @Inject
  public RealBillingService(@Named("Checkout") CreditCardProcessor processor,
      TransactionLog transactionLog) {
    ...
  }
To bind a specific name, use Names.named() to create an instance to pass to annotatedWith:

    bind(CreditCardProcessor.class)
        .annotatedWith(Names.named("Checkout"))
        .to(CheckoutCreditCardProcessor.class);
Since the compiler can't check the string, we recommend using @Named sparingly.

### Instance Bindings

You can bind a type to a specific instance of that type. This is usually only useful only for objects that don't have dependencies of their own, such as value objects:

    bind(String.class)
        .annotatedWith(Names.named("JDBC URL"))
        .toInstance("jdbc:mysql://localhost/pizza");
    bind(Integer.class)
        .annotatedWith(Names.named("login timeout seconds"))
        .toInstance(10);

Avoid using .toInstance with objects that are complicated to create, since it can slow down application startup. You can use an @Provides method instead.

### @Provides Methods

When you need code to create an object, use an @Provides method. The method must be defined within a module, and it must have an @Provides annotation.

 The method's return type is the bound type. Whenever the injector needs an instance of that type, it will invoke the method.

public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
    ...
  }

  @Provides
  TransactionLog provideTransactionLog() {
    DatabaseTransactionLog transactionLog = new DatabaseTransactionLog();
    transactionLog.setJdbcUrl("jdbc:mysql://localhost/pizza");
    transactionLog.setThreadPoolSize(30);
    return transactionLog;
  }
}

If the @Provides method has a binding annotation like @PayPal or @Named("Checkout"), Guice binds the annotated type. Dependencies can be passed in as parameters to the method. The injector will exercise the bindings for each of these before invoking the method.

  @Provides @PayPal
  CreditCardProcessor providePayPalCreditCardProcessor(
      @Named("PayPal API key") String apiKey) {
    PayPalCreditCardProcessor processor = new PayPalCreditCardProcessor();
    processor.setApiKey(apiKey);
    return processor;
  }

### Provider Bindings

When your @Provides methods start to grow complex, you may consider moving them to a class of their own.

Our provider implementation class has dependencies of its own, which it receives via its @Inject-annotated constructor. It implements the Provider interface to define what's returned with complete type safety:

public class DatabaseTransactionLogProvider implements Provider<TransactionLog> {
  private final Connection connection;

  @Inject
  public DatabaseTransactionLogProvider(Connection connection) {
    this.connection = connection;
  }

  public TransactionLog get() {
    DatabaseTransactionLog transactionLog = new DatabaseTransactionLog();
    transactionLog.setConnection(connection);
    return transactionLog;
  }
}

Finally we bind to the provider using the .toProvider clause:

public class BillingModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TransactionLog.class)
        .toProvider(DatabaseTransactionLogProvider.class);
  }


### Untargeted Bindings

You may create bindings without specifying a target. This is most useful for concrete classes and types annotated by either @ImplementedBy or @ProvidedBy.

	bind(MyConcreteClass.class);
    bind(AnotherConcreteClass.class).in(Singleton.class);


    bind(MyConcreteClass.class)
        .annotatedWith(Names.named("foo"))
        .to(MyConcreteClass.class);
    bind(AnotherConcreteClass.class)
        .annotatedWith(Names.named("foo"))
        .to(AnotherConcreteClass.class)
        .in(Singleton.class);


#### Constructor Bindings

Occasionally it's necessary to bind a type to an arbitrary constructor. This comes up when the @Inject annotation cannot be applied to the target constructor: either because it is a third party class, or because multiple constructors that participate in dependency injection.

@Provides methods provide the best solution to this problem! By calling your target constructor explicitly, you don't need reflection and its associated pitfalls. But there are limitations of that approach: manually constructed instances do not participate in AOP.

#### Built-in Bindings

#### JustInTimeBindings

implicit bindings

@ImplementedBy(PayPalCreditCardProcessor.class)
public interface CreditCardProcessor {
  ChargeResult charge(String amount, CreditCard creditCard)
      throws UnreachableException;
}
The above annotation is equivalent to the following bind() statement:

    bind(CreditCardProcessor.class).to(PayPalCreditCardProcessor.class);



@ProvidedBy(DatabaseTransactionLogProvider.class)
public interface TransactionLog {
  void logConnectException(UnreachableException e);
  void logChargeResult(ChargeResult result);
}
The annotation is equivalent to a toProvider() binding:

    bind(TransactionLog.class)
        .toProvider(DatabaseTransactionLogProvider.class);


# https://github.com/google/guice/wiki/Scopes

By default, Guice returns a new instance each time it supplies a value. This behaviour is configurable via scopes. Scopes allow you to reuse instances: for the lifetime of an application (@Singleton), a session (@SessionScoped), or a request (@RequestScoped). 

@Singleton
public class InMemoryTransactionLog implements TransactionLog {
  /* everything here should be threadsafe! */
}

Scopes can also be configured in bind statements:

  bind(TransactionLog.class).to(InMemoryTransactionLog.class).in(Singleton.class);
And by annotating @Provides methods:

  @Provides @Singleton
  TransactionLog provideTransactionLog() {
    ...
  }

。。。


# https://github.com/google/guice/wiki/Injections

patterns TODO

# https://github.com/google/guice/wiki/AOP

TODO


# 最佳实践 Best Practices

* Minimize mutability
public class RealPaymentService implements PaymentService { 

   private final PaymentQueue paymentQueue; 
   private final Notifier notifier;  

   @Inject 
   RealPaymentRequestService( 
       PaymentQueue paymentQueue, 
       Notifier notifier) { 
     this.paymentQueue = paymentQueue; 
     this.notifier = notifier; 
   }

   ...
All fields of this class are final and initialized by a single @Inject-annotated constructor.

https://github.com/google/guice/wiki/InjectOnlyDirectDependencies
* Inject only direct dependencies
* Eliminate the cycle (Recommended)
* Avoid static state
* Use @Nullable 如果你允许注入null
* Modules should be fast and side-effect free

  public static void main(String[] args) throws Exception {
    Injector injector = Guice.createInjector(
        new DatabaseModule(),
        new WebserverModule(),
        ...
    );

    Service databaseConnectionPool = injector.getInstance(
        Key.get(Service.class, DatabaseService.class));
    databaseConnectionPool.start();
    addShutdownHook(databaseConnectionPool);

    Service webserver = injector.getInstance(
        Key.get(Service.class, WebserverService.class));
    webserver.start();
    addShutdownHook(webserver);
  }

* Be careful about I/O in Providers
* Avoid conditional logic in modules
* Keep constructors on Guice-instantiated classes as hidden as possible.


# https://github.com/google/guice/wiki/FrequentlyAskedQuestions

* How do I inject configuration parameters?
* How do I load configuration properties?
* How do I pass a parameter when creating an object via Guice?
* How do I build two similar but slightly different trees of objects?
* How can I inject an inner class?
* How to inject class with generic type?
* How can I inject optional parameters into a constructor?
* How do I inject a method interceptor?





Web TODO


Persist TODO







